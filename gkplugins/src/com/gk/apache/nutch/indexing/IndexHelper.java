package com.gk.apache.nutch.indexing;

import java.nio.ByteBuffer;

import org.apache.nutch.storage.WebPage;

import com.gk.apache.nutch.parse.html.MetaContentManager;

public class IndexHelper {
	public static boolean shouldIndex(WebPage webPage, MetaContentManager man){
		boolean typeStatus = typeIndexStatus(webPage, man);
		boolean signatureStatus = signatureIndexStatus(webPage, man);
		boolean dateStatus = dateIndexStatus(webPage, man);
		return typeStatus && signatureStatus && dateStatus;
	}
	
	public static boolean dateIndexStatus(WebPage webPage, MetaContentManager man){
		ByteBuffer dateField = man.getArticleDateField(webPage);
		return dateField != null;
	}
	
	public static boolean typeIndexStatus(WebPage webPage, MetaContentManager man){
		
		String typeField = man.getArticleTypeField(webPage);
		if (typeField == null){
			if (man.getArticleDateField(webPage) != null){
				return true;
			}
			return false;
		}
		else if (typeField != null && typeField.toLowerCase().contains("article") || typeField.toLowerCase().contains("web"))
		{	
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean signatureIndexStatus(WebPage webPage, MetaContentManager man){
		String signatureField = man.getSignatureTypeField(webPage);
		if (signatureField == null){
			return true;
		}
		else{
			return !containUnwantedSignature(signatureField);
		}
	}
	
	private static String[] unwantedSignature = new String[] { "afp", "reuters" };
	private static boolean containUnwantedSignature(String signature){
		for (int i = 0; i < unwantedSignature.length; i++) {
			String unwanted = unwantedSignature[i];
			if (signature.toLowerCase().contains(unwanted)){
				return true;
			}
		}
		return false;
	}
}
