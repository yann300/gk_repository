package com.gk.engine.back;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import com.gk.engine.LightRessourceNode;
import com.gk.engine.RessourceNode;
import com.kenai.jffi.Array;


public class RessourceNodeManager {

	public final String TABLENAME = "ressources_themes";	
	public final String COLUMNINFO = "b"; 
	public final String COLUMNINFO_SUB1 = "b1";
	
	private final String STOP_KEY = "z";
	private final String START_KEY = "a";
	
	public void createTable() throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor tableDescriptor = new HTableDescriptor(TABLENAME);
		tableDescriptor.addFamily(new HColumnDescriptor(COLUMNINFO));
		admin.createTable(tableDescriptor);
	}
	
	public void addNode(String nodeName) throws IOException {
		Configuration conf = HBaseConfiguration.create();		 
		HTable table = new HTable(conf, TABLENAME);		
		Put put = new Put(Bytes.toBytes(nodeName + "_" + START_KEY));	
		Put putStop = new Put(Bytes.toBytes(nodeName + "_" + STOP_KEY));	
		put.add(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1), Bytes.toBytes(nodeName));	
		putStop.add(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1), Bytes.toBytes(STOP_KEY));
		table.put(put);
		table.put(putStop);		
		table.flushCommits();
		table.close();
	}
	
	public void addRessource(String ressourceNodeName, String url) throws IOException{
		Configuration conf = HBaseConfiguration.create();		 
		HTable table = new HTable(conf, TABLENAME);		
		Put put = new Put(Bytes.toBytes(ressourceNodeName + "_" + url));	
		Put put2 = new Put(Bytes.toBytes(url + "_" + ressourceNodeName));		
	
		
		put.add(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1), Bytes.toBytes(url));	
		put2.add(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1), Bytes.toBytes(ressourceNodeName));
			
		table.put(put);
		table.put(put2);
		
		table.flushCommits();
		table.close();
	}
	
	public Hashtable<String, LightRessourceNode[]> getRessourceNodesFromUrl(JSONArray urls) throws IOException, JSONException{
		
		Hashtable<String, LightRessourceNode[]> urls_themes = new Hashtable<String, LightRessourceNode[]>();
		if (urls == null){
			return urls_themes;	
		}
		Configuration conf = HBaseConfiguration.create();		 
		
		HTable table = new HTable(conf, TABLENAME);		
		for (int i = 0; i < urls.length(); i++) {
			String url = (String) urls.get(i);
			Scan scan = new Scan(Bytes.toBytes(url + "_"));
			scan.setStopRow(Bytes.toBytes(url + "_" + STOP_KEY));
			
			scan.addColumn(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1));						
			ResultScanner scanner = table.getScanner(scan);
			ArrayList<LightRessourceNode> ressourceNodes = new ArrayList<LightRessourceNode>();
			for (Result result : scanner) {
				KeyValue val = result.getColumnLatest(
						Bytes.toBytes(COLUMNINFO), 
						Bytes.toBytes(COLUMNINFO_SUB1));
				String ressourceName = Bytes.toString(val.getValue());	
				ressourceNodes.add(new LightRessourceNode(ressourceName));
			}
			urls_themes.put(url, (LightRessourceNode[])ressourceNodes.toArray(new LightRessourceNode[0]));
		}
		return urls_themes;
	}
	
	public RessourceNode getRessourceNode(String theme) throws IOException{
		ArrayList<String> urls = new ArrayList<String>();
		Configuration conf = HBaseConfiguration.create();		 
		HTable table = new HTable(conf, TABLENAME);		
		Scan scan = new Scan(Bytes.toBytes(theme + "_"));
		scan.setStopRow(Bytes.toBytes(theme + "_" + STOP_KEY));
		scan.addColumn(Bytes.toBytes(COLUMNINFO), Bytes.toBytes(COLUMNINFO_SUB1));						
		ResultScanner scanner = table.getScanner(scan);		
		for (Result result : scanner) {
			KeyValue val = result.getColumnLatest(
					Bytes.toBytes(COLUMNINFO), 
					Bytes.toBytes(COLUMNINFO_SUB1));
			urls.add(Bytes.toString(val.getValue()));
		}
		if (urls.size() > 0){
			return new RessourceNode(theme, (String[])urls.toArray(new String[0]));
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		RessourceNodeManager man = new RessourceNodeManager();
		//man.createTable();
		man.addRessource("france", "http://www.lemonde.fr/sport/article/2014/06/16/tom-boonen-renonce-au-tour-de-france_4438951_3242.html");
		
		/*man.addRessource("pollution", "http://monde.fr");
		man.addRessource("test2", "http://urltest2");
		man.addRessource("test3", "http://urltest3");
		man.addRessource("ressourcesName4", "http://rue89.com");*/
		
		//Hashtable<String, LightRessourceNode[]> result = man.getRessourceNodesFromUrl(new String[] { "http://rue89.com", "http://urltest3" });
		//result.get("http://rue89.com");
		
		RessourceNode node = man.getRessourceNode("berlin");
	}

	
}
