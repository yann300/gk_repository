package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;
import com.gk.apache.nutch.parse.html.ContentFinder.DateNormalizer;

public class UrlDateMetadata implements IArticleMetaDataRetriever, IUrlMetadataRetriever{	
	
	private String RegExContent = null;
	private String Dateformat = null;	
	
	public void resolveMetaDataValue(ArticleMetadata metaData, URL url) {
		// TODO Auto-generated method stub
		MetaDataExtractorHelper.resolveAndSetDateMetadata(metaData, url.toString(), this.RegExContent, this.Dateformat);
	}
	
	public MetadataContextType getContextType() {
		// TODO Auto-generated method stub
		return MetadataContextType.URL;
	}
}
