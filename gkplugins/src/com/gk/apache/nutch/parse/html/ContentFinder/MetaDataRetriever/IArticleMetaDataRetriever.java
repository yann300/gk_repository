package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.net.URL;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public interface IArticleMetaDataRetriever {	
	MetadataContextType getContextType();
}
