package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public abstract class ContentMetadataExtractor implements IArticleMetaDataRetriever, INodeMetadataRetriever {
	
	protected String TagName = null;
	protected String AttributeKey = null;
	protected String AttributeValue = null;
	protected String RegExContent = null;	
	
	public abstract String getContent(Node node);
	public abstract void resolveAndSetMetadata(ArticleMetadata metaData, Node refNode, String regExPattern);
	public abstract String getManagedMetadataName();
	
	public void tryResolveMetadataFromNode(ArticleMetadata metaData, Node refNode) {
		// TODO Auto-generated method stub
		if (metaData.metadata.containsKey(this.getManagedMetadataName())){
			return;
		}
		if (refNode.getNodeName().toLowerCase().equals(TagName)){
			
			if ((this.AttributeKey == "" &&
				this.AttributeValue == "") || 
				(refNode.getAttributes().getNamedItem(AttributeKey) != null &&
			    refNode.getAttributes().getNamedItem(AttributeKey).getNodeValue().equals(AttributeValue))){
				
				this.resolveAndSetMetadata(metaData, refNode, RegExContent);
			}
		}
	}
	
	public MetadataContextType getContextType() {
		// TODO Auto-generated method stub
		return MetadataContextType.HtmlContent;
	}
}
