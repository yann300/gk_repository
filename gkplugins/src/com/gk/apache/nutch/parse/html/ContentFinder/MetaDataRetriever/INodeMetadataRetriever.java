package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public interface INodeMetadataRetriever {
	void tryResolveMetadataFromNode(ArticleMetadata metaData, Node refNode);
}
