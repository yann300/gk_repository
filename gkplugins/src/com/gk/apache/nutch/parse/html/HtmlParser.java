package com.gk.apache.nutch.parse.html;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.metadata.Nutch;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseFilters;
import org.apache.nutch.parse.ParseStatusCodes;
import org.apache.nutch.parse.ParseStatusUtils;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.storage.ParseStatus;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.util.Bytes;
import org.apache.nutch.util.EncodingDetector;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.util.TableUtil;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.xbib.elasticsearch.common.langdetect.Detector;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gk.apache.nutch.parse.html.ContentFinder.ContentFinder;


public class HtmlParser implements Parser {
  public static final Logger LOG = LoggerFactory.getLogger("org.apache.nutch.parse.html");

  // I used 1000 bytes at first, but  found that some documents have
  // meta tag well past the first 1000 bytes.
  // (e.g. http://cn.promo.yahoo.com/customcare/music.html)
  private static final int CHUNK_SIZE = 2000;

  // NUTCH-1006 Meta equiv with single quotes not accepted
  private static Pattern metaPattern =
    Pattern.compile("<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
        Pattern.CASE_INSENSITIVE);
  private static Pattern charsetPattern =
    Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)",
        Pattern.CASE_INSENSITIVE);

  private static Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

  static {
    FIELDS.add(WebPage.Field.BASE_URL);
  }

  /**
   * Given a <code>ByteBuffer</code> representing an html file of an
   * <em>unknown</em> encoding,  read out 'charset' parameter in the meta tag
   * from the first <code>CHUNK_SIZE</code> bytes.
   * If there's no meta tag for Content-Type or no charset is specified,
   * <code>null</code> is returned.  <br />
   * FIXME: non-byte oriented character encodings (UTF-16, UTF-32)
   * can't be handled with this.
   * We need to do something similar to what's done by mozilla
   * (http://lxr.mozilla.org/seamonkey/source/parser/htmlparser/src/nsParser.cpp#1993).
   * See also http://www.w3.org/TR/REC-xml/#sec-guessing
   * <br />
   *
   * @param content <code>ByteBuffer</code> representation of an html file
   */

  private static String sniffCharacterEncoding(ByteBuffer content) {
    int length = Math.min(content.remaining(), CHUNK_SIZE);

    // We don't care about non-ASCII parts so that it's sufficient
    // to just inflate each byte to a 16-bit value by padding.
    // For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
    // {U+0041, U+0082, U+00B7}.
    String str = "";
    try {
      str = new String(content.array(), content.arrayOffset() + content.position(),
          length, Charset.forName("ASCII").toString());
    } catch (UnsupportedEncodingException e) {
      // code should never come here, but just in case...
      return null;
    }

    Matcher metaMatcher = metaPattern.matcher(str);
    String encoding = null;
    if (metaMatcher.find()) {
      Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
      if (charsetMatcher.find())
        encoding = new String(charsetMatcher.group(1));
    }

    return encoding;
  }

  private String defaultCharEncoding;

  private Configuration conf;

  private DOMContentUtils utils;

  private ParseFilters htmlParseFilters;

  private String cachingPolicy;

@Override
public Parse getParse(String url, WebPage page) {
	LOG.info("in GK HtmlParser : " + url);
    HTMLMetaTags metaTags = new HTMLMetaTags();

    String baseUrl = TableUtil.toString(page.getBaseUrl());
    URL base;
    try {
      base = new URL(baseUrl);
    } catch (MalformedURLException e) {
      return ParseStatusUtils.getEmptyParse(e, getConf());
    }

    String text = "";
    String title = "";
    Outlink[] outlinks = new Outlink[0];
    Metadata metadata = new Metadata();

    // parse the content
    DocumentFragment root;
    try {
      ByteBuffer contentInOctets = page.getContent();
      
      InputSource input = new InputSource(new ByteArrayInputStream(contentInOctets.array(),
          contentInOctets.arrayOffset() + contentInOctets.position(), contentInOctets.remaining()));    
      
      String encoding = defaultCharEncoding;
      if (conf != null)
      {
    	  EncodingDetector detector = new EncodingDetector(conf);
    	  detector.autoDetectClues(page, true);
          detector.addClue(sniffCharacterEncoding(contentInOctets), "sniffed");
          encoding = detector.guessEncoding(page, defaultCharEncoding);
      }

      metadata.set(Nutch.ORIGINAL_CHAR_ENCODING, encoding);
      metadata.set(Nutch.CHAR_ENCODING_FOR_CONVERSION, encoding);

      input.setEncoding(encoding);
      
      if (LOG.isTraceEnabled()) { LOG.trace("Parsing..."); }
      root = parse(input);
    } catch (IOException e) {
      LOG.error("Failed with the following IOException: ", e);
      return ParseStatusUtils.getEmptyParse(e, getConf());
    } catch (DOMException e) {
      LOG.error("Failed with the following DOMException: ", e);
      return ParseStatusUtils.getEmptyParse(e, getConf());
    } catch (SAXException e) {
      LOG.error("Failed with the following SAXException: ", e);
      return ParseStatusUtils.getEmptyParse(e, getConf());
    } catch (Exception e) {
      LOG.error("Failed with the following Exception: ", e);
      return ParseStatusUtils.getEmptyParse(e, getConf());
    }

    // get meta directives
    HTMLMetaProcessor.getMetaTags(metaTags, root, base);
    if (LOG.isTraceEnabled()) {
      LOG.trace("Meta tags for " + base + ": " + metaTags.toString());
    }
    
    if (!metaTags.getNoFollow()) {              // okay to follow links
        ArrayList<Outlink> l = new ArrayList<Outlink>();   // extract outlinks
        URL baseTag = utils.getBase(root);
        if (LOG.isTraceEnabled()) { LOG.trace("Getting links..."); }
        utils.getOutlinks(baseTag!=null?baseTag:base, l, root);
        outlinks = l.toArray(new Outlink[l.size()]);
        if (LOG.isTraceEnabled()) {
          LOG.trace("found "+outlinks.length+" outlinks in "+ url);
        }
      }
    
    try{
	    // check meta directives	  
	    if (!metaTags.getNoIndex()) { // okay to index   
	      new StringBuilder();
	      if (LOG.isTraceEnabled()) { LOG.trace("Getting text..."); }
	      ContentFinder finder = new ContentFinder(root, this.conf, base);
	      finder.init();
	      // extract text
	      text = finder.getText();
	      if (LOG.isTraceEnabled()) { LOG.trace("Getting title..."); }
	      // extract title	      
	      title = finder.getTitle();
	      //if (finder.isOKForIndex()){
	      MetaContentManager meta_man = new MetaContentManager();	    	  
	    	  //if post date != null, we can add all other article metadatas (post date will be included):
	      meta_man.setMetadatas(page, finder.getMetadatas());
	      //}
	      
	      this.ManagelangContent("content", text, meta_man, page);
	      this.ManagelangContent("title", title, meta_man, page);
	    }
    }
    catch (Exception ex)
    {
    	LOG.error("Error while parsing content of " +  base.toString() + " host:" + base.getHost(), ex);
    }

    ParseStatus status = new ParseStatus();
    status.setMajorCode(ParseStatusCodes.SUCCESS);
    status.setMinorCode(ParseStatusCodes.SUCCESS);
    if (metaTags.getRefresh()) {
      //status.setMinorCode(ParseStatusCodes.SUCCESS_REDIRECT);
      status.addToArgs(new Utf8(metaTags.getRefreshHref().toString()));
      status.addToArgs(new Utf8(Integer.toString(metaTags.getRefreshTime())));
    }

    Parse parse = new Parse(text, title, outlinks, status);
    //Log.info("before filter");
    //logOutlinks(outlinks);
    //Log.info("before filter");
    //logOutlinks(outlinks);
    if (getConf() != null)
    {
    	parse = htmlParseFilters.filter(url, page, parse, metaTags, root);
    	//Log.info("after filter");
    	//logOutlinks(parse.getOutlinks());
    	 //Log.info("after filter");
    	    //logOutlinks(outlinks);
    }
    
    if (metaTags.getNoCache()) {             // not okay to cache
      page.putToMetadata(new Utf8(Nutch.CACHING_FORBIDDEN_KEY),
          ByteBuffer.wrap(Bytes.toBytes(cachingPolicy)));
    }
    
    return parse;
  }
	private String[] lang_supported = new String[] { "en","fr", "de" };

	private static Detector detector_lang = null;
	private void ManagelangContent(String key, String Content, MetaContentManager contentManager, WebPage page) {
		
		if (detector_lang == null){
			detector_lang = new org.xbib.elasticsearch.common.langdetect.Detector();
			try {
				detector_lang.loadDefaultProfiles();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String lang = detector_lang.detect(Content);
		for (int i = 0; i < lang_supported.length; i++) {
			String langcurrent = lang_supported[i];
			if (langcurrent.equals(lang)){
				contentManager.setMetadata(page, key + "_" + langcurrent, Content);
			}
			else{
				contentManager.setMetadata(page, key + "_" + langcurrent, "");
			}
		}
		
	}

/*
private DocumentFragment parse(InputSource input) throws Exception {
    if (parserImpl.equalsIgnoreCase("tagsoup"))
      return parseTagSoup(input);
    else return parseNeko(input);
  }

  private DocumentFragment parseTagSoup(InputSource input) throws Exception {
    HTMLDocumentImpl doc = new HTMLDocumentImpl();
    DocumentFragment frag = doc.createDocumentFragment();
    DOMBuilder builder = new DOMBuilder(doc, frag);
    org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
    reader.setContentHandler(builder);
    reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature, true);
    reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature, false);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
    reader.parse(input);
    return frag;
  }

  private DocumentFragment parseNeko(InputSource input) throws Exception {
    DOMFragmentParser parser = new DOMFragmentParser();
    try {
      parser.setFeature("http://cyberneko.org/html/features/augmentations",
          true);
      parser.setProperty("http://cyberneko.org/html/properties/default-encoding",
          defaultCharEncoding);
      parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset",
          true);
      parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
          false);
      parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",
          true);
      parser.setFeature("http://cyberneko.org/html/features/report-errors",
          LOG.isTraceEnabled());
    } catch (SAXException e) {}
    // convert Document to DocumentFragment
    HTMLDocumentImpl doc = new HTMLDocumentImpl();
    doc.setErrorChecking(false);
    DocumentFragment res = doc.createDocumentFragment();
    DocumentFragment frag = doc.createDocumentFragment();
    parser.parse(input, frag);
    res.appendChild(frag);

    try {
      while(true) {
        frag = doc.createDocumentFragment();
        parser.parse(input, frag);
        if (!frag.hasChildNodes()) break;
        if (LOG.isInfoEnabled()) {
          LOG.info(" - new frag, " + frag.getChildNodes().getLength() + " nodes.");
        }
        res.appendChild(frag);
      }
    } catch (Exception x) { 
      LOG.error("Failed with the following Exception: ", x);
      };
    return res;
  }*/
  

  private DocumentFragment parse(InputSource input) throws Exception {
	  return parseNeko(input);	
  }

  private DocumentFragment parseNeko(InputSource input) throws Exception {
	  
	  DOMFragmentParser parser = new DOMFragmentParser();
	  HTMLDocumentImpl doc = new HTMLDocumentImpl();
	  DocumentFragment res = doc.createDocumentFragment();
	  parser.parse(input,res);
	  return res;
  }
  public static Configuration conf_static;
  
  @Override  
  public void setConf(Configuration conf) {
    this.conf = conf;
    conf_static = conf;
    if (getConf() != null)
    {
    	this.htmlParseFilters = new ParseFilters(getConf());
    	getConf().get("parser.html.impl", "neko");
    	this.defaultCharEncoding = getConf().get(
    	        "parser.character.encoding.default", "windows-1252");
    	this.cachingPolicy = getConf().get("parser.caching.forbidden.policy",
    	        Nutch.CACHING_FORBIDDEN_CONTENT);
    	this.utils = new DOMContentUtils(conf);
    }
    
  }


@Override
public Configuration getConf() {
    return this.conf;
  }


@Override
  public Collection<WebPage.Field> getFields() {
    return FIELDS;
  }

  public static void main(String[] args) throws Exception {
    //LOG.setLevel(Level.FINE);
    String name = "/home/yann/workspace/nutch2.2.1/gk nutch plugins/src/com/gk/apache/nutch/parse/html/package.html";
    String url = "http://rue89.nouvelobs.com";
    File file = new File(name);
    byte[] bytes = new byte[(int)file.length()];
    DataInputStream _in = new DataInputStream(new FileInputStream(file));
    _in.readFully(bytes);
    _in.close();
    Configuration conf = NutchConfiguration.create();
    HtmlParser parser = new HtmlParser();
    //parser.setConf(conf);
    parser.utils = new DOMContentUtils(conf);
    WebPage page = new WebPage();
    page.setBaseUrl(new Utf8(url));
    page.setContent(ByteBuffer.wrap(bytes));
    page.setContentType(new Utf8("text/html"));
    Parse parse = parser.getParse(url, page);
    System.out.println("title: "+parse.getTitle());
    System.out.println("text: "+parse.getText());
    System.out.println("outlinks: " + Arrays.toString(parse.getOutlinks()));

  }

}