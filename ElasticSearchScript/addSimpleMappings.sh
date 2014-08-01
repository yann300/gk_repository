curl -XPUT 'http://localhost:9200/index/doc/_mapping' -d '  
      {
	"doc" : {
		"properties" : {
			"customRelevance" : {
			  "type" :    "float"
			}
      		}
	}
}   
'




