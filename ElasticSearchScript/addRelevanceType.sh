curl -XPUT 'http://localhost:9200/index/_mapping/doc' -d '    
     { "properties" : {
        "customRelevance" : {
          "type" :    "float"
        }
      }
    }  
'




