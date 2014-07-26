package com.gk.apache.nutch.parse.html.HtmlParserResult;

import java.net.URL;
import com.gk.apache.nutch.parse.html.ContentFinder.ArticleMetadata;
import com.gk.apache.nutch.parse.html.ContentFinder.HtmlParseInfo;

public class HtmlParserInfo {
	public enum ParsingType{
		H1Alone,
		H1Multi,
		NoH1,
		ArticleTagAlone
	}
	
	public URL Url;
	public ParsingType typeOfParse;
	public Integer nbWordToIndex = 0;
	public ArticleMetadata metadatas;
	
	public HtmlParserInfo(HtmlParseInfo parseInfo, URL url, String textToIndex, ArticleMetadata articleMetadata) {
		
		if (textToIndex != null)
		{
			this.nbWordToIndex = textToIndex.split(" ").length;
		}
		this.Url = url;
		// TODO Auto-generated constructor stub
		if (parseInfo.H1Tags.size() == 1)
		{
			this.typeOfParse = ParsingType.H1Alone;
		}
		else if (parseInfo.H1Tags.size() == 0)
		{
			this.typeOfParse = ParsingType.NoH1;
		}
		else if (parseInfo.H1Tags.size() > 1)
		{
			this.typeOfParse = ParsingType.H1Multi;
		}
		
		this.metadatas = articleMetadata;
	}	
}

