package com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever;

import org.w3c.dom.Node;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;

public class OpenGraphMetadatas implements IArticleMetaDataRetriever, INodeMetadataRetriever {	

	public final String PREFIX = "og:";
	public final String TITLE = "title";
	public final String TYPE = "type";
	public final String IMAGE = "image";
	public final String URL = "url";
	
	@Override
	public MetadataContextType getContextType() {
		return MetadataContextType.HtmlContent;
	}

	@Override
	public void tryResolveMetadataFromNode(ArticleMetadata metaData,
			Node refNode) {
		// TODO Auto-generated method stub
		if (refNode.getNodeName().toLowerCase().equals("meta")){
			if ((refNode.getAttributes().getNamedItem("property") != null &&
			    refNode.getAttributes().getNamedItem("property").getNodeValue().startsWith(PREFIX))){
				
				String metaKeyValue = refNode.getAttributes().getNamedItem("property").getNodeValue();
				metaKeyValue = metaKeyValue.replace(PREFIX, "");
				String metaValue = refNode.getAttributes().getNamedItem("content").getNodeValue();					
				metaData.metadata.put(metaKeyValue, metaValue);
			}
		}
	}
}
