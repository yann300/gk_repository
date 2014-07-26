package com.gk.apache.nutch.parse.html.ContentFinder;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomUtils {
	public static final Logger LOG = LoggerFactory.getLogger(DomUtils.class);
	
	public static Node findCommonAncestor(Node node1, Node node2){
		Node[] parentNode1 = getAllParents(node1);
		Node[] parentNode2 = getAllParents(node2);
		for (int i = 0; i < parentNode2.length; i++) {
			Node parentnode2 = parentNode2[i];
			for (int j = 0; j < parentNode1.length; j++) {
				Node parentnode1 = parentNode1[j];
				if (parentnode1.isSameNode(parentnode2)){
					return parentnode1;
				}
			}
		}
		return null;
	}
	
	public static boolean isParentNodeOf(Node testParentNode, Node[] nodes){
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			if (Node.DOCUMENT_POSITION_CONTAINS != node.compareDocumentPosition(testParentNode)  &&
				Node.DOCUMENT_TYPE_NODE != node.compareDocumentPosition(testParentNode)) {
				return false;
			}
		}
		return true;
	}
	
	public static  Node getCommonAncestor(Node node1, Node node2){
		Node [] parentNodes1 = getAllParents(node1);
		return findNodeInParents(node2, parentNodes1);
	}
	
	private static Node findNodeInParents(Node node, Node[] nodes)
	{		
		Node parentNode = node; //we check also the current node	
		while (parentNode != null){
			if (ContainNode(parentNode, nodes))
			{				
				return parentNode;
			}	
			else{
				parentNode = parentNode.getParentNode();
			}
		}
		return null;
	}
	
	private static Boolean ContainNode(Node node, Node[] nodes)
	{		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isSameNode(node))
			{				
				return true;
			}
		}
		return false;
	}
	
	private static Node[] getAllParents(Node node){
		Node parentNode = node.getParentNode();
		ArrayList<Node> parentNodes = new ArrayList<Node>();
		parentNodes.add(node); //we add also the current node
		while (parentNode != null){
			parentNodes.add(parentNode);
			parentNode = parentNode.getParentNode();
			if (parentNode != null && parentNode.getNodeName().toLowerCase() == "body")
			{
				parentNode = null;
			}
		}		
		return parentNodes.toArray(new Node[0]);
	}
	
	public static  Node getCommonAncestor(Node root, Node[] nodes1, Node[] nodes2){
		Node commonAncestor = null;
		for (int i = 0; i < root.getChildNodes().getLength(); i++) {
			Node node = root.getChildNodes().item(i);
			if (!DomUtils.isParentNodeOf(node, nodes1) ||
				!DomUtils.isParentNodeOf(node, nodes2)) {
				commonAncestor = node.getParentNode();
			}else{
				return getCommonAncestor(node, nodes1, nodes2);
			}
		}
		return commonAncestor;
	}
	
	public static Node[] filterChildTags(Node parent, Node[] nodes){
		ArrayList<Node> childTags = new ArrayList<Node>();
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			if (Node.DOCUMENT_POSITION_CONTAINS != node.compareDocumentPosition(parent)  &&
				Node.DOCUMENT_TYPE_NODE != node.compareDocumentPosition(parent)) {
					continue;
				}else{
					childTags.add(node);
				}
		}
		return childTags.toArray(new Node[0]);
	}
	
	public static Node getClosestNode(String nodeName, Node node){
		if (node == null){
			return null;
		}
		if (node.getNodeName().toLowerCase() == "body"){
			return null; //cannot find node from the root. use to get out recursive method.
		}
		ArrayList<Node> childNode = getChildTags(nodeName, node);
		if (childNode != null && childNode.size() > 0){
			return childNode.get(0);
		}
		else{
			return getClosestNode(nodeName, node.getParentNode());
		}
	}
	
	public static ArrayList<Node> getChildTags(String nodeName, Node node) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		if (node.getParentNode() == null){
			return nodes;
		}
		if (node != null && nodeName != ""){
			for (int i = 0; i < node.getParentNode().getChildNodes().getLength(); i++) {
				Node childNode = node.getParentNode().getChildNodes().item(i);
				if (nodeName.equals(childNode.getNodeName().toLowerCase()) &&
						!node.isSameNode(childNode)){
					nodes.add(childNode);
				}
			}
		}
		return nodes;
	}
	
	private static final String[] metaAndRessourceNodes = new String[] { "script", "link", "style", "#comment" };
	public static boolean isRessourceNode(Node node) {
		return Arrays.asList(metaAndRessourceNodes).contains(node.getNodeName().toLowerCase());
	}
}
