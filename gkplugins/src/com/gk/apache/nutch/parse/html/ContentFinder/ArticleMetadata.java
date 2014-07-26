package com.gk.apache.nutch.parse.html.ContentFinder;

import java.net.URL;
import java.util.Hashtable;

public class ArticleMetadata {
	
	public URL url;
	public Hashtable<String, String> metadata;
	
	public ArticleMetadata() {
		 this.metadata  = new Hashtable<String, String>();
	}
}
