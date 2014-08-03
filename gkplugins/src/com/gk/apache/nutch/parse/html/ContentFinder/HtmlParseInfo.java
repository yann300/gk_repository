package com.gk.apache.nutch.parse.html.ContentFinder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HtmlParseInfo {
	private static final int PARAGRAPH_WORD_COUNT = 10; 
	public ArrayList<Node> TimeTags;
	public ArrayList<Node> TitleTags;
	public ArrayList<Node> H1Tags;
	public ArrayList<Node> H2Tags;
	public ArrayList<Node> H3Tags;
	public ArrayList<Node> H4Tags;
	public ArrayList<Node> H5Tags;
	public ArrayList<Node> H6Tags;
	public ArrayList<Node> ArticleTags;
	public ArrayList<Node> MainTags;
	public ArrayList<Node> ContentNodes;
	public Map<Node,MutableInt>  ParagraphParentNodes;
	private HashMap<Node, Integer> ParagraphParentNodesWordCount;
	private MetaDataFinder metadata_finder;
	
	public HtmlParseInfo(Configuration conf, URL url)
	{
		this.ParagraphParentNodes = new HashMap<Node,MutableInt> ();
		this.ParagraphParentNodesWordCount = new HashMap<Node,Integer> ();
		this.H1Tags = new ArrayList<Node>();
		this.H2Tags = new ArrayList<Node>();
		this.H3Tags = new ArrayList<Node>();
		this.H4Tags = new ArrayList<Node>();
		this.H5Tags = new ArrayList<Node>();
		this.H6Tags = new ArrayList<Node>();
		this.TitleTags = new ArrayList<Node>();
		this.ArticleTags = new ArrayList<Node>();
		this.MainTags = new ArrayList<Node>();
		this.ContentNodes = new ArrayList<Node>();
		this.TimeTags = new ArrayList<Node>();
		
		this.metadata_finder = new MetaDataFinder(conf, url);
	}
	
	public ArticleMetadata getMetadata(){
		 return metadata_finder.getResolvedMetadata();
	 }
	
	public ArrayList<Node> getHtags()
	{
		ArrayList<Node> Htags = new ArrayList<Node>();
		Htags.addAll(this.H1Tags);
		Htags.addAll(this.H2Tags);
		Htags.addAll(this.H3Tags);
		Htags.addAll(this.H4Tags);
		Htags.addAll(this.H5Tags);
		Htags.addAll(this.H6Tags);
		return Htags;
	}
	
	private void manageParagraphParent(Node parentnode){
		for (Iterator<Node> iterator = this.ParagraphParentNodes.keySet().iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			if (node.isSameNode(parentnode))
			{
				this.ParagraphParentNodes.get(node).inc();
				return;
			}
		}
		this.ParagraphParentNodes.put(parentnode, new MutableInt());
		
	}
	
	private void manageParagraphParent(Node parentnode, Integer textLength){
		for (Iterator<Node> iterator = this.ParagraphParentNodesWordCount.keySet().iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			if (node.isSameNode(parentnode))
			{
				Integer value = this.ParagraphParentNodesWordCount.get(node);
				this.ParagraphParentNodesWordCount.put(node, value + textLength);
				return;
			}
		}		
		this.ParagraphParentNodesWordCount.put(parentnode, textLength);
	}
	
	public void build(NodeList nodes)
	{	
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (DomUtils.isRessourceNode(node)){
				continue;
			}
			if ("main".equals(node.getNodeName().toLowerCase())) {
				this.MainTags.add(node);
			}
			if ("article".equals(node.getNodeName().toLowerCase())) {
				this.ArticleTags.add(node);
			}
			if ("h1".equals(node.getNodeName().toLowerCase())) {
				this.H1Tags.add(node);
			}
			if ("h2".equals(node.getNodeName().toLowerCase())) {
				this.H2Tags.add(node);
			}	
			if ("h3".equals(node.getNodeName().toLowerCase())) {
				this.H3Tags.add(node);
			}
			if ("h4".equals(node.getNodeName().toLowerCase())) {
				this.H4Tags.add(node);
			}
			if ("h5".equals(node.getNodeName().toLowerCase())) {
				this.H5Tags.add(node);
			}
			if ("h6".equals(node.getNodeName().toLowerCase())) {
				this.H6Tags.add(node);
			}
			
			/*if ("time".equals(node.getNodeName().toLowerCase())) {
				this.TimeTags.add(node);
			}*/
			this.metadata_finder.tryResolveMetaData(node);
			if (!node.hasChildNodes()){
				String text = node.getTextContent();
				Integer textLength = text.replace("\t"," ").replace("\n", " ").replaceAll(" +", " ").split(" ").length;
				
				if (textLength >= PARAGRAPH_WORD_COUNT)
				{
					Node parentnode = node.getParentNode().getParentNode();
					this.manageParagraphParent(parentnode);
					this.manageParagraphParent(parentnode, textLength);
					this.ContentNodes.add(node.getParentNode());					
				}
			}
			else{
				build(node.getChildNodes());
			}
		}
	}
	
	public Node getParentWithMostWord(){
		Integer wordCount = 0;
		Node nodeWithMostWord = null;
		for (Iterator<Node> iterator = this.ParagraphParentNodesWordCount.keySet().iterator(); iterator.hasNext();) {
			Node currentnode = (Node) iterator.next();
			if (this.ParagraphParentNodesWordCount.get(currentnode) > wordCount) {
				wordCount =  this.ParagraphParentNodesWordCount.get(currentnode);
				nodeWithMostWord = currentnode;
			}
		}
		return nodeWithMostWord;
	}

}

class MutableInt {
	  int value = 0;
	  public void inc () { ++value; }
	  public int get () { return value; }
	}
