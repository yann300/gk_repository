package com.gk.tools.FocusRelevanceManager;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gk.helper.ElasticSearch.ElasticSearchBatchManager;

import org.apache.nutch.util.TableUtil;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

public class FocusRelevanceManager {
	public static void main(String[] args) throws Exception {		
		String action = args[0];
		String indexName = args[1];
		getLogger().log(Level.INFO,"start FocusRelevanceManager");
		if (action.equals("clean")){
			cleanIndex(indexName);
		}
		else if (action.equals("provision")){
			provisionIndexWithFocus(indexName);
		}
		getLogger().log(Level.INFO,"end FocusRelevanceManager");
	}
	
	private static void cleanIndex(String indexName){
		ElasticSearchBatchManager man = new ElasticSearchBatchManager(getLogger());
		ArrayList<SearchHit> results = man.getAllResultsLight(indexName);
		String script = "ctx._source.customRelevance_focus = 1.0";
		for (SearchHit searchHit : results) {			
			man.alterDocEntry(indexName, searchHit.field("id").getValue().toString(), script);
		}
	}
	
	private static void provisionIndexWithFocus(String indexName){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			ElasticSearchBatchManager man = new ElasticSearchBatchManager(getLogger());
			conn = DriverManager.getConnection("jdbc:mysql://localhost/global_k?" +
				                                   "user=root&password=azerty");	
			String script = "ctx._source.customRelevance_focus = ctx._source.customRelevance_focus + 0.4";
			stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT * FROM focus_has_sources");	
		    while (rs.next()) {
		        String sourceUrl = rs.getString(3);   
		        try {
					String id = TableUtil.reverseUrl(sourceUrl);					
					man.alterDocEntry(indexName, id.toLowerCase(), script);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
		    // it is a good idea to release
		    // resources in a finally{} block
		    // in reverse-order of their creation
		    // if they are no-longer needed

		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
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
