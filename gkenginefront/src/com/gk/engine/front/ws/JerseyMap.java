package com.gk.engine.front.ws;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gk.engine.LightRessourceNode;

@XmlRootElement
public class JerseyMap {
	
	@JsonProperty
	private Map<String, LightRessourceNode[]> items;
	
	public JerseyMap(){
		
	}
	
	public void setItems(Map<String, LightRessourceNode[]> items){
		this.items = items;
	}
	
	public Map<String, LightRessourceNode[]> getItems(){
		return this.items;
	}
}
