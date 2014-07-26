package com.gk.engine;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class LightRessourceNode {
	
	@JsonProperty
	private String name;
	
	public LightRessourceNode(){
	}
	
	public LightRessourceNode(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
