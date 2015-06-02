package com.twitternlg.templates;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathExpression;
import org.xml.sax.SAXException;

public class NLGTemplateProcessor {
	String emailpath = "//emailAddress";
	String emailvalue = ".//value";

	XPathFactory xPathFactory = XPathFactory.newInstance();
	XPath xpath = xPathFactory.newXPath();
	Document document;
	
	/*public NLGTemplateParser(String filePath) throws ParserConfigurationException, IOException, SAXException {
		
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = docFactory.newDocumentBuilder();
	    
		Document doc = builder.parse(filePath);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
	    NodeList nodeList = getNodeList(doc, emailpath);
	    for(int i = 0; i < nodeList.getLength(); i++){
	        System.out.println(getValue(nodeList.item(i), emailvalue));
	    }
	    bis.close();        
	}

	public NodeList getNodeList(Document doc, String expr) {
	    try {
	        XPathExpression pathExpr = xpath.compile(expr);
	        return (NodeList) pathExpr.evaluate(doc, XPathConstants.NODESET);
	    } catch (XPathExpressionException e) {
	        e.printStackTrace();
	    }
	    return null;
	}


	//extracts the String value for the given expression
	private String getValue(Node n, String expr) {
	    try {
	        XPathExpression pathExpr = xpath.compile(expr);
	        return (String) pathExpr.evaluate(n,
	                XPathConstants.STRING);
	    } catch (XPathExpressionException e) {
	        e.printStackTrace();
	    }
	    return null;
	}*/
}

