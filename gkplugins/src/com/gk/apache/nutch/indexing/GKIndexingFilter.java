package com.gk.apache.nutch.indexing;


import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gk.apache.nutch.parse.html.MetaContentManager;
import com.gk.apache.nutch.parse.html.HtmlParserResult.HtmlParserResult;
public class GKIndexingFilter implements IndexingFilter {

	public static final Logger LOG = LoggerFactory.getLogger(GKIndexingFilter.class);
	
	private Configuration conf;
	private static HtmlParserResult metadataIndexerLog;
	
	public static HtmlParserResult getMetadataIndexedLogger(Configuration conf){
		if (metadataIndexerLog == null)
		{
			metadataIndexerLog = new HtmlParserResult(conf, "GKIndexingFilter");
			metadataIndexerLog.setFileName("metadataIndexed.log");
		}
		return metadataIndexerLog;
	}

	@Override
	public Collection<Field> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Configuration getConf() {
		// TODO Auto-generated method stub
		return this.conf;
	}

	@Override
	public void setConf(Configuration conf) {
		// TODO Auto-generated method stub
		this.conf = conf;
	}

	@Override
	public NutchDocument filter(NutchDocument doc, String arg1, WebPage webPage)
			throws IndexingException {
		MetaContentManager man = new MetaContentManager();
		man.logWriter = GKIndexingFilter.getMetadataIndexedLogger(this.conf);		
		if (IndexHelper.shouldIndex(webPage, man)){
			man.fillIndexWithMetadata(doc, webPage, arg1);
			return doc;
		}
		return null;
	}
}
