 curl -XPOST 'localhost:9200/index/_search' -d '
    {
       "query" : {
           "term" : {
                "content.lang" : "en"
           }
       }
    }'
