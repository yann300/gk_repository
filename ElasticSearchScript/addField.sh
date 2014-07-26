curl -XPOST 'localhost:9200/index/doc/com.nouvelobs.rue89:http%2f/_update' -d '{
    "script" : "ctx._source.customRelevance = ctx._source.customRelevance + 0.1" }'


