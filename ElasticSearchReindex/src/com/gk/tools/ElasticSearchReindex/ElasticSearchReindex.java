
package com.gk.tools.ElasticSearchReindex;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.gk.helper.ElasticSearch.ElasticSearchBatchManager;

public class ElasticSearchReindex {
	
	private static ElasticSearchBatchManager man;
	
	public static void main(String[] args) throws Exception {		
			String sourceIndex = args[0];
			String destIndex = args[1];
			getLogger().log(Level.INFO,"start ElasticSearchBatchManager");
			man = new ElasticSearchBatchManager(getLogger());
			
			man.getAllResultsAndReInsert(sourceIndex, destIndex);
			
			getLogger().log(Level.INFO,"end ElasticSearchReindex");
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
