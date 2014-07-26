package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;

public class AttributeDateMetadata extends DateContentMetadataExtractor {	

	private String AttributeContent = null;
	private String TargetLevel = null;
	
	public String getContent(Node node)
	{
		return MetaDataExtractorHelper.getAttributeExtendedContent(TargetLevel, node, AttributeContent);	
	}
	
	public String getManagedMetadataName(){
		return MetaContentManager.DATETIME_FIELD;
	}	
}
