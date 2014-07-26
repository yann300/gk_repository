package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public abstract class DateContentMetadataExtractor extends ContentMetadataExtractor  {
	
	protected String Dateformat = null;	
	
	public void resolveAndSetMetadata(ArticleMetadata metaData,
			Node refNode, String regExPattern) {
		// TODO Auto-generated method stub
		MetaDataExtractorHelper.resolveAndSetDateMetadata(metaData, this.getContent(refNode), 
				this.RegExContent, this.Dateformat);
	}
}
