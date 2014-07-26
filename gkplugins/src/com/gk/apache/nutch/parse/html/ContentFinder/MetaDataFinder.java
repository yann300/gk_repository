package com.gk.apache.nutch.parse.html.ContentFinder;

import java.io.IOException;


import com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever.IArticleMetaDataRetriever;
import com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever.INodeMetadataRetriever;
import com.gk.apache.nutch.parse.html.ContentFinder.MetaDataRetriever.IUrlMetadataRetriever;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MetaDataFinder {

		private final String METADATADESCRIPTION_FILEKEY = "gk.parser.metadata.description.file";
		private ArticleMetadata metadata = null;
		private Document xdoc;				
		private IArticleMetaDataRetriever[] managed_types;
		
		public static final Logger LOG = LoggerFactory.getLogger(MetaDataFinder.class);
		
		public MetaDataFinder(Configuration conf, URL url)
		{	
			String fileRules = conf.get(METADATADESCRIPTION_FILEKEY);
			LOG.info("MetaDataFinder file " + fileRules);
		    InputStream metadataStream = conf.getConfResourceAsInputStream(fileRules);	
		    
		    //GET xml configuration for medata retrieving
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
			try {
				xdoc = dBuilder.parse(metadataStream);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// get all managed types : 			
			 managed_types = null;
			NodeList sitesNode = xdoc.getElementsByTagName("Site");
			for (int i = 0; i < sitesNode.getLength(); i++) {
				Node node = sitesNode.item(i);
				String urlSite = node.getAttributes().getNamedItem("url").getNodeValue();				
				if (url.toString().toLowerCase().contains(urlSite.toLowerCase())){					 
					managed_types = this.getAllMetadataRetrievers(node, url);					
					break;
				}
			}
			
			metadata = new ArticleMetadata();
			metadata.url = url;		
			
			// send url to all url metadataretriever
			for (int i = 0; i < managed_types.length; i++) {
				IArticleMetaDataRetriever articleMetaDataRetriever = managed_types[i];
				if (articleMetaDataRetriever instanceof IUrlMetadataRetriever){
					((IUrlMetadataRetriever)articleMetaDataRetriever).resolveMetaDataValue(metadata, url);
				}
			}
		}
		
		private IArticleMetaDataRetriever[] getAllMetadataRetrievers(Node siteNode, URL url){
			NodeList ArticleDefs = ((Element)siteNode).getElementsByTagName("ArticleDefinition");
			Node ArticleDef = ArticleDefs.item(0);
			NodeList metadatadefs = ((Element)ArticleDef).getElementsByTagName("Metadata");
			ArrayList<IArticleMetaDataRetriever> types = new ArrayList<IArticleMetaDataRetriever>();
			for (int i = 0; i < metadatadefs.getLength(); i++) {
				Node metadatadef = metadatadefs.item(i);
				//we instanciate all type
				try {
					IArticleMetaDataRetriever metadataretriever = (IArticleMetaDataRetriever)Class.forName(metadatadef.getAttributes().getNamedItem("type").getNodeValue()).newInstance();
					this.loadContext(metadataretriever, metadatadef);
					types.add(metadataretriever);
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return types.toArray(new IArticleMetaDataRetriever[types.size()]);
		}
		
		private void loadContext(IArticleMetaDataRetriever metadataretriever,
				Node metadatadef) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			NodeList nodes = ((Element)metadatadef).getElementsByTagName("Parameters");
			if (nodes != null && nodes.getLength() > 0){
				Node params = nodes.item(0);
				for (int i = 0; i < params.getChildNodes().getLength(); i++) {
					Node param_node =  params.getChildNodes().item(i);
					String param = param_node.getNodeName();
					if (param.equals("#text") || param.equals("#comment"))
					{
						continue;
					}
					String value = param_node.getAttributes().getNamedItem("value").getNodeValue();
					Field f = null;
					try {
						f = this.getClassField(metadataretriever.getClass(), param);
						f.setAccessible(true);
						try {
							f.set(metadataretriever, value);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
			}
		}

		public void tryResolveMetaData(Node node) {			
			// send node to all metadataretriever
			for (int i = 0; i < managed_types.length; i++) {
				IArticleMetaDataRetriever articleMetaDataRetriever = managed_types[i];
				if (articleMetaDataRetriever instanceof INodeMetadataRetriever){
					((INodeMetadataRetriever)articleMetaDataRetriever).tryResolveMetadataFromNode(metadata, node);
				}
			}
		}		
		
		public ArticleMetadata getResolvedMetadata()
		{			
			return metadata;
		}
		
		private Field getClassField(Class<?> _class, String fieldName){
			Class<?> tempClass = _class;
			Field field = null;
			while (tempClass != null){
				try {
					field = tempClass.getDeclaredField(fieldName);					
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					this.getClassField(_class.getSuperclass(), fieldName);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					this.getClassField(_class.getSuperclass(), fieldName);
				}
				if (field != null || tempClass.getSuperclass() == null){
					tempClass = null;
				}
				else if (tempClass.getSuperclass() != null){
					tempClass = tempClass.getSuperclass();
				}
			}
			return field;
		}
}
