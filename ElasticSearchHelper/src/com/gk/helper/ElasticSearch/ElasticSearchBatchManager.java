package com.gk.helper.ElasticSearch;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import static org.elasticsearch.node.NodeBuilder.*;

public class ElasticSearchBatchManager {	
	
	private  Client client;
	private Logger logger;
	
	public ElasticSearchBatchManager(Logger logger){
		this.logger = logger;
		Node node = nodeBuilder().clusterName("gk-elasticsearch").node();
		client = node.client();
	}
	
	public void deleteEntry(String url){
		//url = java.net.URLEncoder.encode(url, "utf-8");
		//url = url.replaceAll("/", "%3F");
		this.logger.log(Level.INFO,"deleting " + url);
		DeleteResponse response = client.prepareDelete("index", "doc", url)
		        .execute()
		        .actionGet();
		this.logger.log(Level.INFO,"deletion result " + response.isFound());
	}
	
	public void ReinsertEntry(SearchHit entry, String indexName){
		//url = java.net.URLEncoder.encode(url, "utf-8");
		//url = url.replaceAll("/", "%3F");
		//ElasticSearchIndexCleaner.getLogger().log(Level.INFO,"reinsert " + url);
		IndexResponse response = null;
		try {
			String image = "";
			if (entry.field("image") != null){
				image = entry.field("image").getValue().toString();
			}
			response = client.prepareIndex(indexName, "doc").setSource(jsonBuilder()
			        .startObject()
			        .field("customRelevance", entry.field("customRelevance").getValue().toString())
			        .field("content_fr", entry.field("content_fr").getValue().toString())
			        .field("content_en", entry.field("content_en").getValue().toString())
			        .field("content_de", entry.field("content_de").getValue().toString())
			        .field("url", entry.field("url").getValue().toString())
			        .field("post_date", entry.field("post_date").getValue().toString())
			        .field("content", entry.field("content").getValue().toString())
			        .field("title", entry.field("title").getValue().toString())
			        .field("title_fr", entry.field("title_fr").getValue().toString())
			        .field("title_en", entry.field("title_en").getValue().toString())
			        .field("title_de", entry.field("title_de").getValue().toString())
			        .field("image", image)
			    .endObject()
			  )
			  .execute()
			  .actionGet();
		} catch (ElasticsearchException e) {
			// TODO Auto-generated catch block
			this.logger.log(Level.SEVERE,"indexinx failed " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.logger.log(Level.SEVERE,"indexinx failed " + e.getMessage());
			e.printStackTrace();
		}
		if (!response.isCreated()){
			this.logger.log(Level.WARNING,"indexinx failed " + response.isCreated());
		}
	}
	
public void getAllResultsAndReInsert(String sourceIndex, String destIndex) throws IOException{		
		
		
	this.logger.log(Level.INFO,"get all entries ...");
		//QueryBuilder qb = termQuery("content", "*");
		
		SearchResponse scrollResp = client.prepareSearch()
		        .setSearchType(SearchType.SCAN)
		        .setIndices(sourceIndex)
		        .setScroll(new TimeValue(60000))			        
		        .addField("customRelevance")
		        .addField("id")
			        .addField("content_fr")
			        .addField("content_en")
			        .addField("content_de")
			        .addField("url")
			        .addField("post_date")
			        .addField("content")
			        .addField("title")
			        .addField("image")
			        .addField("title_fr")
			        .addField("title_en")
			        .addField("title_de")
		        .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll
		//Scroll until no hits are returned
		int hitsnb = 0;
		while (true) {
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
		    
		    BulkRequestBuilder bulkRequest = client
		    		.prepareBulk();
		    SearchHits hits = scrollResp.getHits();
		    this.logger.log(Level.INFO, "reindex " + hits.getHits().length); 
		    if (scrollResp.getHits().getHits().length == 0) {
		    	this.logger.log(Level.INFO, "reindex end"); 
		        break;
		    }
		    for (SearchHit entry : hits) {
		        //Handle the hit...
		    	hitsnb++;
				
				XContentBuilder jSON = jsonBuilder()
		        .startObject()
		        .field("id", entry.field("id").getValue().toString())
		        .field("customRelevance", entry.field("customRelevance").getValue().toString())
		        .field("content_fr", entry.field("content_fr").getValue().toString())
		        .field("content_en", entry.field("content_en").getValue().toString())
		        .field("content_de", entry.field("content_de").getValue().toString())
		        .field("url", entry.field("url").getValue().toString())
		        .field("content", entry.field("content").getValue().toString())
		        .field("title", entry.field("title").getValue().toString())
		        .field("title_fr", entry.field("title_fr").getValue().toString())
		        .field("title_en", entry.field("title_en").getValue().toString())
		        .field("title_de", entry.field("title_de").getValue().toString());
				
				if (entry.field("post_date") != null){
					jSON = jSON.field("post_date", entry.field("post_date").getValue().toString());
				}
				
				if (entry.field("image") != null){
					jSON = jSON.field("image", entry.field("image").getValue().toString());
				}
				
				jSON = jSON.endObject();
				
		    	bulkRequest.add(client.prepareIndex(destIndex, "doc", entry.field("id").getValue().toString()).setSource(jSON));
		    }		    
		    BulkResponse response =  bulkRequest.execute().actionGet();
		    this.logger.log(Level.INFO, hitsnb + " doc reindexed");
		    this.logger.log(Level.INFO,"failure : " + response.hasFailures());
		    this.logger.log(Level.INFO,"failure : " + response.buildFailureMessage());
		    
		    //Break condition: No hits are returned
		    
		}	
		this.logger.log(Level.INFO,"reindex finished ");
	}

	public ArrayList<SearchHit> getAllResultsLight(String index){		
		
		
		this.logger.log(Level.INFO,"get all entries ...");
		//QueryBuilder qb = termQuery("content", "*");
		
		SearchResponse scrollResp = client.prepareSearch()
				.setIndices(index)
		        .setSearchType(SearchType.SCAN)
		        .setScroll(new TimeValue(60000))			        
		        .addField("title")
		        .addField("id")
		        .addField("url")
		        .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll
		//Scroll until no hits are returned
		ArrayList<SearchHit> list = new ArrayList<SearchHit>();
		while (true) {
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
		    for (SearchHit hit : scrollResp.getHits()) {
		        //Handle the hit...
		    	list.add(hit);
		    }
		    //Break condition: No hits are returned
		    if (scrollResp.getHits().getHits().length == 0) {
		        break;
		    }
		}
		this.logger.log(Level.INFO,"found " + list.size() + " entries");
		return list;
	}
}
