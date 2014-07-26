curl -XPUT 'http://localhost:9200/index/doc/_mapping' -d '  
      {
	"doc" : {
		"properties" : {
			"customRelevance" : {
			  "type" :    "float"
			},
			"title.fr": {
				"boost": 6,
				"index_analyzer": "custom_analyzer_fr",
				"search_analyzer": "custom_search_analyzer",
				"type" :    "String"
			},
			  "content.fr": {
				"index_analyzer": "custom_analyzer_fr",
				"search_analyzer": "custom_search_analyzer",
				"type" : "String",
				"term_vector" : "with_positions_offsets"
			},
			"title.en": {
				"boost": 6,
				"index_analyzer": "custom_analyzer_en",
				"search_analyzer": "custom_search_analyzer",
				"type" :    "String"
			},
			  "content.en": {
				"index_analyzer": "custom_analyzer_en",
				"search_analyzer": "custom_search_analyzer",
				"type" : "String",
				"term_vector" : "with_positions_offsets"
			},
			"title.de": {
				"boost": 6,
				"index_analyzer": "custom_analyzer_de",
				"search_analyzer": "custom_search_analyzer",
				"type" :    "String"
			},
			  "content.de": {
				"index_analyzer": "custom_analyzer_de",
				"search_analyzer": "custom_search_analyzer",
				"type" : "String",
				"term_vector" : "with_positions_offsets"
			}
      		}
	}
}   
'




