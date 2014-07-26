package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;


import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;

public class HtmlDateMetadata extends DateContentMetadataExtractor {	

	private String TargetLevel = null;
	
	public String getContent(Node node)
	{
		return MetaDataExtractorHelper.getHtmlExtendedContent(TargetLevel, node);
	}
	
	public String getManagedMetadataName(){
		return MetaContentManager.DATETIME_FIELD;
	}
}
