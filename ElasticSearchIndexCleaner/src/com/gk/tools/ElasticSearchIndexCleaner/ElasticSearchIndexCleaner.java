package com.gk.tools.ElasticSearchIndexCleaner;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.gk.helper.ElasticSearch.ElasticSearchBatchManager;

public class ElasticSearchIndexCleaner {
	
	private static ElasticSearchBatchManager man;
	public static void main(String[] args) throws Exception {		
		String sourceIndex = args[0];
		getLogger().log(Level.INFO,"start ElasticSearchBatchManager");
		man = new ElasticSearchBatchManager(getLogger());
		
		ArrayList<SearchHit> results = man.getAllResultsLight(sourceIndex);
		System.out.print("Continue to clean ElasTicSearch ? (y/n)");
		String input = "y"; //System.console().readLine();
		if (input.equals("y")){
		ArrayList<String> urls = new ArrayList<String>(); //list of url
		ArrayList<String> titles = new ArrayList<String>(); //list of url	
			for (SearchHit searchHit : results) {
				
				SearchHitField title = searchHit.field("title");
				SearchHitField url = searchHit.field("url");
				SearchHitField id = searchHit.field("id");
				
				if (id == null){
					getLogger().log(Level.SEVERE, "id null for " + url.getValue().toString().toLowerCase()); 
					continue;
				}
				
				if (url.getValue().toString().equals("http://www.voltairenet.org/article154396.html")){
					@SuppressWarnings("unused")
					String test = "gf";
					test = "k";
				}
				
				String urlStr = url.getValue().toString().toLowerCase();	
				String idStr = id.getValue().toString();
				String titleStr = title.getValue().toString().toLowerCase().replaceAll(" +","");
				if (!manageUrl(urls, urlStr, idStr)){
					manageTitle(titles, titleStr, idStr);			
				}
			}
		}
		getLogger().log(Level.INFO,"end ElasticSearchBatchManager");
	}
	
	public static boolean manageTitle(ArrayList<String> titles, String titleStr, String idStr){
		if (titleStr.equals("notitle")){
			return false;
		}
		if (!titles.contains(titleStr)){
			titles.add(titleStr); //first time we saw this title
			return false;
		}
		else {
			//not the first time : we delete if not already deleted
			man.deleteEntry(idStr);		
			return true;
		}
	}
	
	public static boolean manageUrl(ArrayList<String> urls, String urlStr, String idStr){
		if (!urls.contains(urlStr)){
			urls.add(urlStr); //first time we saw this url
			return false;
		}
		else {
			//not the first time : we delete if not already deleted
			man.deleteEntry(idStr);	
			return true;
		}
	}
	
	private static Logger log = null;
	public static Logger getLogger(){
		if (log == null) {
			log = Logger.getLogger("ElasticSearchIndexCleaner");
			log.setLevel(Level.ALL);			
		}
		return log;
	}
}
