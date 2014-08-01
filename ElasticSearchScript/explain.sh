

curl -XGET 'http://localhost:9200/gkindex/doc/fr.lemonde.www:http%2fplanete%2farticle%2f2014%2f07%2f31%2fvirus%2Debola%2Dla%2Dsierra%2Dleone%2Ddecrete%2Dl%2Detat%2Dd%2Durgence%5F4465166%5F3244.html/_explain' -d '{
      "query" : {
        "term" : { "url" : "ebola" }
      }
}'
