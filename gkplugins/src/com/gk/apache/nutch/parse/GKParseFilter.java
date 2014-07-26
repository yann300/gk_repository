package com.gk.apache.nutch.parse;

import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseFilter;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;

public class GKParseFilter implements ParseFilter {

	public static final Logger LOG = LoggerFactory.getLogger(GKParseFilter.class);
	
	private Configuration conf;

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
	public Parse filter(String arg0, WebPage arg1, Parse arg2,
			HTMLMetaTags arg3, DocumentFragment arg4) {
		// TODO Auto-generated method stub
		LOG.info("in GKParseFilter Nothing to do ...");		
		return arg2;
	}

}
