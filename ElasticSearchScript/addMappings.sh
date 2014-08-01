curl -XPUT 'http://localhost:9200/gkindex/doc/_mapping' -d '  
      {
	"doc" : {
		"properties" : {
				"customRelevance" : {
			  		"type" :    "float"
				},
				"content_fr": {
					"index_analyzer": "custom_analyzer_fr",
					"search_analyzer": "custom_search_analyzer",
					"type" : "String",
					"term_vector" : "with_positions_offsets"
				},
				"content_en": {
					"index_analyzer": "custom_analyzer_en",
					"search_analyzer": "custom_search_analyzer",
					"type" : "String",
					"term_vector" : "with_positions_offsets"
				},
				"content_de": {
					"index_analyzer": "custom_analyzer_de",
					"search_analyzer": "custom_search_analyzer",
					"type" : "String",
					"term_vector" : "with_positions_offsets"
				},
				"content" : {
			  		"type" :    "String"
			  	},
				"title" : {
			     		"type" :    "String"
						
				},
				"title_fr": {					
					"index_analyzer": "custom_analyzer_fr",
					"search_analyzer": "custom_search_analyzer",
					"type" :    "String"
				},
				"title_en": {					
					"index_analyzer": "custom_analyzer_en",
					"search_analyzer": "custom_search_analyzer",
					"type" :    "String"
				},		  
				"title_de": {					
					"index_analyzer": "custom_analyzer_de",
					"search_analyzer": "custom_search_analyzer",
					"type" :    "String"
				}
      		}
	}
}   
'




