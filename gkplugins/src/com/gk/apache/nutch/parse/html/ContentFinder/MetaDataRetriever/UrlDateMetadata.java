package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.net.URL;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

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
