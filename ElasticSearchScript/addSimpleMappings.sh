curl -XPUT 'http://localhost:9200/gkindex/doc/_mapping' -d '  
      {
	"doc" : {
		"properties" : {
			"customRelevance" : {
			  "type" :    "float"
			},
			"customRelevance_focus" : {
			  "type" :    "float"
			}
      		}
	}
}   
'




