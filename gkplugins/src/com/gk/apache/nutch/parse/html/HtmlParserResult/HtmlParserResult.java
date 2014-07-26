package com.gk.apache.nutch.parse.html.HtmlParserResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlParserResult {
	public static final Logger LOG = LoggerFactory.getLogger("org.apache.nutch.parse.html.HtmlParserResult");
	
	public static final String PARSER_RESULT_LOCATION = "gk.parser.log.location";
	public static final String PARSER_RESULT_FILENAME = "parseResult.log"; //DefaultName
	public String fileName = "";
	
	private String location;	
	private String relativePath;
	
	public HtmlParserResult(Configuration conf, URL url) {
		if (conf != null){
			this.location = conf.get(PARSER_RESULT_LOCATION);
		}
		this.relativePath = url.getHost().replaceAll("http://", "");		
		File file = new File(getFullPath());
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	public HtmlParserResult(Configuration conf, String relativefilePath) {
		if (conf != null){
			this.location = conf.get(PARSER_RESULT_LOCATION);
		}
		this.relativePath = relativefilePath;
		File file = new File(getFullPath());
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	private String getFullPath()
	{
		return this.location + relativePath ;
	}
	
	public void setFileName(String name){
		this.fileName = name;
	}
	
	public void push(HtmlParserInfo info)
	{
		try
		{
			if (fileName == ""){
				fileName = PARSER_RESULT_FILENAME;
			}
		    FileWriter fw = new FileWriter(getFullPath() + "/" + fileName, true); //true will append the new data
		    String toWrite = info.Url.toString() + ";" + info.typeOfParse + ";" + info.nbWordToIndex + ";";
		    for (String iterable_element : info.metadatas.metadata.values()) {
		    	toWrite = toWrite + iterable_element.replace(';', ' ') + ";";
		    }
		    fw.write(toWrite + "\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
			LOG.error("Unable to log parse result", ioe);
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public void push(String url, String key, String value){
		try
		{
			if (fileName == ""){
				fileName = PARSER_RESULT_FILENAME;
			}
		    FileWriter fw = new FileWriter(getFullPath() + "/" + fileName, true); //the true will append the new data	    
		    fw.write(url + ";" + key + ";" + value.replace(';', ' ') + "\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
			LOG.error("Unable to log parse result", ioe);
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
}

