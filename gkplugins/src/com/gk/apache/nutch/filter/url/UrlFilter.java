package com.gk.apache.nutch.filter.url;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.net.URLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlFilter implements URLFilter {
  public static final Logger LOG = LoggerFactory.getLogger(UrlFilter.class);
  private Configuration conf;
  
 
	@Override
	  public String filter(String urlString) 
	{	
		//LOG.info("in GK URLFilter : " + urlString);
		return urlString;
	}
	
	@Override
	public void setConf(Configuration conf) {
	    this.conf = conf;
	}
	
	@Override
	public Configuration getConf() {
	    return this.conf;
	}
	  
	public static void main(String[] args) throws Exception {
	    /*String url = "http://rue89.nouvelobs.com";
	    UrlFilter filter = new UrlFilter();
	    String result = filter.filter(url);*/
	}
}
