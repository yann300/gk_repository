package com.gk.engine.front.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONArray;

@XmlRootElement
public class JerseyArray {
		
		@JsonProperty
		private JSONArray items;
		
		public JerseyArray(){
			
		}
		
		public void setItems(JSONArray items){
			this.items = items;
		}
		
		public JSONArray getItems(){
			return this.items;
		}
}
