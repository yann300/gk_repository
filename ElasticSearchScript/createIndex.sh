curl -XPUT 'http://localhost:9200/index/' -d '
{ 
"analysis": {
  "analyzer": {
	"custom_analyzer_fr": {
	  "type": "custom",
	  "tokenizer": "standard",
	  "filter": [
		"stopwords_fr",
		"asciifolding",
		"lowercase",
		"snowball_fr",
		"elision",
		"worddelimiter"
	  ]
	},
	"custom_analyzer_en": {
	  "type": "custom",
	  "tokenizer": "standard",
	  "filter": [
		"stopwords_en",
		"asciifolding",
		"lowercase",
		"snowball_en",
		"elision",
		"worddelimiter"
	  ]
	},
	"custom_analyzer_de": {
	  "type": "custom",
	  "tokenizer": "standard",
	  "filter": [
		"stopwords_de",
		"asciifolding",
		"lowercase",
		"snowball_de",
		"elision",
		"worddelimiter"
	  ]
	},
	"custom_search_analyzer": {
	  "type": "custom",
	  "tokenizer": "standard",
	  "filter": [
		"stopwords",
		"asciifolding",
		"lowercase",
		"snowball",
		"elision",
		"worddelimiter"
	  ]
	}
  },
  "tokenizer": {
	"nGram": {
	  "type": "nGram",
	  "min_gram": 2,
	  "max_gram": 20
	}
  },
  "filter": {
	"snowball_fr": {
	  "type": "snowball",
	  "language": "French"
	},
	"snowball_en": {
	  "type": "snowball",
	  "language": "English"
	},
	"snowball_de": {
	  "type": "snowball",
	  "language": "German"
	}
	"elision": {
	  "type": "elision",
	  "articles": [
		"l",
		"m",
		"t",
		"qu",
		"n",
		"s",
		"j",
		"d"
	  ]
	},
	"stopwords_fr": {
	  "type": "stop",
	  "stopwords": [
		"_french_"
	  ],
	  "ignore_case": true
	},
	"stopwords_en": {
	  "type": "stop",
	  "stopwords": [
		"_english_"
	  ],
	  "ignore_case": true
	},
	"stopwords_de": {
	  "type": "stop",
	  "stopwords": [
		"_german_"
	  ],
	  "ignore_case": true
	},
	"worddelimiter": {
	  "type": "word_delimiter"
	}
  }
}
}
'
