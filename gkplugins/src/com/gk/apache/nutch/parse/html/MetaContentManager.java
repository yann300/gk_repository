package com.gk.apache.nutch.parse.html;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.avro.util.Utf8;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;
import com.gk.apache.nutch.parse.html.HtmlParserResult.HtmlParserResult;

public class MetaContentManager {
	public static final Logger LOG = LoggerFactory.getLogger(MetaContentManager.class);
	
	public final static String DATETIME_FIELD = "post_date";	
	public final static String TITLE_FIELD = "title";
	public final static String TYPE_FIELD = "type";
	public final static String SIGNATURE_FIELD = "signature";

	public HtmlParserResult logWriter;	
	
	//USED TO GET METADATA FROM WEBPAGE DURING INDEXINGFILTER
	public ByteBuffer getArticleDateField(WebPage webPage){		
		return webPage.getFromMetadata(new Utf8(DATETIME_FIELD));		
	}	
	
	//USED TO GET METADATA FROM WEBPAGE DURING INDEXINGFILTER
	public String getArticleTypeField(WebPage webPage){		
		ByteBuffer buffer = webPage.getFromMetadata(new Utf8(TYPE_FIELD));	
		if (buffer == null){
			return null;
		}
		else{
			return new String(buffer.array(), Charset.forName("UTF-8"));
		}
	}	
	
	//USED TO GET METADATA FROM WEBPAGE DURING INDEXINGFILTER
	public String getSignatureTypeField(WebPage webPage){		
		ByteBuffer buffer = webPage.getFromMetadata(new Utf8(SIGNATURE_FIELD));	
		if (buffer == null){
			return null;
		}
		else{
			return new String(buffer.array(), Charset.forName("UTF-8"));
		}
	}	

	//USED TO PUT METADATA ON WEBPAGE
	public void setMetadatas(WebPage page, ArticleMetadata metadatas) {
		// TODO Auto-generated method stub
		for (String key : metadatas.metadata.keySet()) {
			//WARN : DATE must be normalize. TODO : make by configuration
			String value = metadatas.metadata.get(key);			
			this.setMetadata(page, key, value);
		}
	}	
	
	//USED TO PUT METADATA TO INDEX DURING INDEXINGFILTER
	public void fillIndexWithMetadata(NutchDocument doc, WebPage webPage, String url) {
		// TODO Auto-generated method stub
		for (Utf8 key : webPage.getMetadata().keySet()) {
			ByteBuffer value = webPage.getMetadata().get(key);
			String value_metadata = new String(value.array(), Charset.forName("UTF-8"));
			String key_metadata = new String(key.getBytes(), Charset.forName("UTF-8"));	
			//if (key_metadata.equals(MetaContentManager.DATETIME_FIELD))
			//{
				doc.add(key_metadata, value_metadata);
				if (logWriter != null){
					logWriter.push(url, key_metadata, value_metadata);
				}
			//}
		}
	}
	
	public void setMetadata(WebPage webPage, String key, String value){
		webPage.putToMetadata(new Utf8(key), ByteBuffer.wrap(value.getBytes())); 	
	}
}
