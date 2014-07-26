package com.gk.engine.front.ws;

import java.io.IOException;
import java.util.Hashtable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.gk.engine.LightRessourceNode;
import com.gk.engine.RessourceNode;
import com.gk.engine.back.RessourceNodeManager;

@Path("/RessourceNodesWS/")
public class RessourceNodesWS {	
	@Context ServletContext ctx;
	@Context private HttpServletResponse response;
	
	@POST
	@Path("/getRessourceNode/{nodename}/") 
	@Produces({MediaType.APPLICATION_JSON})
	public RessourceNode getRessourceNode(@PathParam("nodename") String nodeName) throws Exception{
		RessourceNodeManager man = new RessourceNodeManager();	
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			return man.getRessourceNode(nodeName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	@POST
	@Path("/getRessourceNodesFrom") 
	@Produces({MediaType.APPLICATION_JSON})
	//@Consumes({MediaType.APPLICATION_JSON})
	public JerseyMap getRessourceNodesFrom(JSONObject urls) throws JSONException, IOException{
		RessourceNodeManager man = new RessourceNodeManager();		
		Hashtable<String, LightRessourceNode[]> items = man.getRessourceNodesFromUrl(urls.getJSONObject("urls").getJSONArray("items"));
		JerseyMap map = new JerseyMap();
		map.setItems(items);
		return map;
	}
	
	@POST
	@Path("/addRessourceNode/{nodename}") 
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public void addRessourceNode(@PathParam("nodename") String nodeName, String url){
		RessourceNodeManager man = new RessourceNodeManager();
		try {
			man.addRessource(nodeName, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/addNode/{nodename}") 
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public void addRessourceNode(@PathParam("nodename") String nodeName){
		RessourceNodeManager man = new RessourceNodeManager();
		try {
			man.addNode(nodeName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
