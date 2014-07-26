package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import java.net.URL;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public interface IUrlMetadataRetriever {
	void resolveMetaDataValue(ArticleMetadata metaData, URL url);
}
