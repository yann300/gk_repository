package com.gk.apache.nutch.parse.html.ContentFinder;

import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.gk.apache.nutch.parse.html.DOMContentUtils;
import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.HtmlParserResult.HtmlParserResult;

public class ContentFinder {
	
	private DocumentFragment doc;
	private HtmlParseInfo parseInfo;
	private Configuration conf;
	private DOMContentUtils utils;
	private URL url;
	public static final Logger LOG = LoggerFactory.getLogger(ContentFinder.class);
	
	public ContentFinder(DocumentFragment doc, Configuration conf, URL url)
	{
		this.url = url;
		this.doc = doc;
		this.conf = conf;
		this.utils = new DOMContentUtils(conf);
		
		this.parseInfo = new HtmlParseInfo(conf, url);	
		
	}
	
	public void init(){
		this.parseInfo.build(this.doc.getChildNodes());
	}
	
	public ArticleMetadata getMetadatas(){
		return this.parseInfo.getMetadata();
	}
	
	public String getTitle()
	{		
		if (this.parseInfo.getMetadata().metadata.containsKey(MetaContentManager.TITLE_FIELD)){
			String title = this.parseInfo.getMetadata().metadata.get(MetaContentManager.TITLE_FIELD);
			if ("".equals(title.trim())) {
				title = "NO TITLE";
			}
			return title;
		}
		return "NO TITLE";
	}
	
	public String getPostedDate()
	{		
		if (this.parseInfo.getMetadata().metadata.containsKey(MetaContentManager.DATETIME_FIELD)){
			String date = this.parseInfo.getMetadata().metadata.get(MetaContentManager.DATETIME_FIELD);				
			return date;
		}
		return null;
	}
	
	/*public boolean isOKForIndex()
	{		
		if (this.parseInfo.getMetadata().metadata.containsKey(MetaContentManager.TYPE_FIELD)){
			return this.parseInfo.getMetadata().metadata.get(MetaContentManager.TYPE_FIELD).toLowerCase().contains("article");
		}
		return false;
	}*/
	
	public String getText()
	{				
		String text = " ";		
		if (this.parseInfo.ParagraphParentNodes.size() == 0) {
			return "NO CONTENT"; //not an article !!
		}
		new HtmlParserResult(this.conf, this.url);
		
		Node node = this.parseInfo.getParentWithMostWord(); //could be part of an article.
		Node copy = node.cloneNode(true);
		this.purgeNode(copy.getChildNodes());
		text = this.utils.getText(copy);
		
		if (this.parseInfo.H1Tags.size() == 1){
			Node h1Node = this.parseInfo.H1Tags.get(0);
			text = this.utils.getText(h1Node) + " " + text;
		}
		/*if (this.isOKForIndex()){
			//if null this doc will not be indexed
			//log parse information
			HtmlParserInfo infoParse = new HtmlParserInfo(this.parseInfo, this.url, text, this.getMetadatas());
			parserResult.push(infoParse);	
		}*/
		return text;
	}
	
	private void purgeNode(NodeList nodes)
	{
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (!node.hasChildNodes()){
				if ("ul".equals(node.getNodeName().toLowerCase()) ||
						("span".equals(node.getNodeName().toLowerCase()) ||
								"li".equals(node.getNodeName().toLowerCase()))) {

					if (node.getParentNode() != null)
					{
						node.getParentNode().removeChild(node);
						continue;
					}
				}
			}
			else{
				this.purgeNode(node.getChildNodes());
			}

		}	
	}
}
