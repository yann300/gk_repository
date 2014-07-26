package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;
import com.gk.apache.nutch.parse.html.ContentFinder.DateNormalizer;

public class MetaDataExtractorHelper {
	
	private static Node getNode(String targetLevel, Node node){
		if (targetLevel == null || Integer.parseInt(targetLevel) == 0){
			return node;
		}
		int realIncr = 0;
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node targetNode = node.getChildNodes().item(i);
			if (targetNode.getNodeName().toLowerCase() != "#text"){
				realIncr++;
				if (realIncr == Integer.parseInt(targetLevel)){
					return targetNode;
				}
			}
		}
		return null;
	}
	
	public static String getAttributeExtendedContent(String targetLevel, Node node, String AttributeKey){
		Node targetNode = getNode(targetLevel, node);
		if (targetNode != null){
			return targetNode.getAttributes().getNamedItem(AttributeKey).getNodeValue();
		}
		return null;
    }
	
	public static String getHtmlExtendedContent(String targetLevel, Node node){
		Node targetNode = getNode(targetLevel, node);
		if (targetNode != null){
			return targetNode.getTextContent();
		}
		return null;
	}	
	
	public static void resolveAndSetDateMetadata(ArticleMetadata metaData, String content, String RegExMatcher, String dateFormat)
	{
		Pattern pattern = Pattern.compile(RegExMatcher);
		Matcher matcher = pattern.matcher(content);
		
		if (matcher.find()){
			String metadata =  matcher.group();
			DateNormalizer norm_date = new DateNormalizer(metadata);	
			String urlNormalized = "";
			try{
				urlNormalized =	norm_date.normalize(dateFormat);
			}
			catch (Exception ex){
			}
			if (urlNormalized != ""){
				metaData.metadata.put(MetaContentManager.DATETIME_FIELD, urlNormalized);
			}
		}
	}
	
	public static void setBasicMetadata(ArticleMetadata metaData, String key, String Content){	
		metaData.metadata.put(key, Content);	
	}
	
	public static void resolveAndSetTitleMetadata(ArticleMetadata metaData, String Content, String RegExMatcher){
		Content = Content.replace("\n", "").replace("-", " ");
		Pattern pattern = Pattern.compile(RegExMatcher);
		Matcher matcher = pattern.matcher(Content);		
		if (matcher.find()){
			String metadata =  matcher.group();					
			metaData.metadata.put(MetaContentManager.TITLE_FIELD, metadata);		
		}
	}
}
