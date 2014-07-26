package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public abstract class TitleContentMetadataExtractor extends ContentMetadataExtractor  {
	
	public void resolveAndSetMetadata(ArticleMetadata metaData,
			Node refNode, String regExPattern) {
		// TODO Auto-generated method stub
		MetaDataExtractorHelper.resolveAndSetTitleMetadata(metaData, this.getContent(refNode), 
				this.RegExContent);
	}
}
