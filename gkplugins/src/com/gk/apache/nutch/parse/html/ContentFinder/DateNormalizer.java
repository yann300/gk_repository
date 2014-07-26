package com.gk.apache.nutch.parse.html.ContentFinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateNormalizer {

	private String date;

	public DateNormalizer(String date){
		this.date = date;
	}
	
	public String normalize(String pattern){
		Date dateFormatted = this.format(pattern);		
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return outFormat.format(dateFormatted);
	}
	
	public String normalize(){
		Date dateFormatted = this.format(null);
		if (dateFormatted == null){
			dateFormatted = this.format("yyyy-MM-dd HH:mm:ss");
		}
		if (dateFormatted == null){
			dateFormatted = this.format("dd/MM/yyyy");
		}
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return outFormat.format(dateFormatted);
	}
	
	private Date format(String pattern)
	{
		SimpleDateFormat formatter = null;
		if (pattern == null)
		{
			formatter = new SimpleDateFormat();
		}
		else{
			formatter = new SimpleDateFormat(pattern);
		}
		try {
			return formatter.parse(this.date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}		 
	}
}
