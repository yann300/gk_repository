package com.gk.engine;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class RessourceNode extends LightRessourceNode {	
	
	@JsonProperty
	private String[] associatedUrls;
	
	public RessourceNode(){		
	}
	
	public RessourceNode(String name, String[] urls) {
		super(name);
		// TODO Auto-generated constructor stub
		associatedUrls = urls;
	}

	public String[] getAssociatedUrls(){
		return associatedUrls;
	}
	
	public void setAssociatedUrls(String[] urls){
		associatedUrls = urls;
	}
	
	/*public RessourceNode[] getChild()
	{
		return null;
	}
	
	public RessourceNode getParent()
	{
		return null;
	}*/
}
