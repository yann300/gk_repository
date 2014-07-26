package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public class HtmlTitleMetadata extends TitleContentMetadataExtractor {	
	
	private String TargetLevel = null;
	
	public String getContent(Node node)
	{
		return MetaDataExtractorHelper.getHtmlExtendedContent(TargetLevel, node);
	}

	public String getManagedMetadataName(){
		return MetaContentManager.TITLE_FIELD;
	}
	
}
