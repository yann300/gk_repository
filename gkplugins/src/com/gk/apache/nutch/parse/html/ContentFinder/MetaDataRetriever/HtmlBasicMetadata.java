package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public class HtmlBasicMetadata extends ContentMetadataExtractor {

	public String MetaDataName;
	@Override
	public String getContent(Node node) {
		// TODO Auto-generated method stub
		return node.getTextContent();
	}

	@Override
	public void resolveAndSetMetadata(ArticleMetadata metaData, Node refNode,
			String regExPattern) {
		// TODO Auto-generated method stub
		MetaDataExtractorHelper.setBasicMetadata(metaData, MetaDataName, this.getContent(refNode));
	}

	@Override
	public String getManagedMetadataName() {
		// TODO Auto-generated method stub
		return MetaDataName;
	}

}
