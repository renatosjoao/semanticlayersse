package org.home.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.home.model.CardObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DBPediaLookup {
	
public static CardObject querydbPedia(String searchElem) throws IOException, ParserConfigurationException, SAXException {
	CardObject co = new CardObject();
	ArrayList<String> rTerms = new ArrayList<String>();
	System.out.println("Entered here");
	searchElem = searchElem.replaceAll(" ", "_");
	String url = "http://lookup.dbpedia.org/api/search/KeywordSearch?QueryString="+searchElem;
	URL obj = new URL(url);
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	// optional default is GET
	con.setRequestMethod("GET");

	//add request header
	con.setRequestProperty("Accept", "application/xml");
	con.setRequestProperty("Content-Type", "application/xml");

	int responseCode = con.getResponseCode();
	System.out.println("\nSending 'GET' request to URL : " + url);
	System.out.println("Response Code : " + responseCode);
	if(responseCode!=200) {
		return null;
		
	}
	InputStream xml = con.getInputStream();

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db = dbf.newDocumentBuilder();
	Document doc = db.parse(xml);
	
	Element root = doc.getDocumentElement();
	System.out.println(root.getNodeName());
    
	//Get all Results
	NodeList nList = doc.getElementsByTagName("Result");
	
	Node node = nList.item(0);
    Element eElement = (Element) node;
    String title = eElement.getElementsByTagName("Label").item(0).getTextContent();
    String description = eElement.getElementsByTagName("Description").item(0).getTextContent();
    CardObject.setTitle(title);
    CardObject.setDescription(description);
    
	System.out.println("============================");
	for (int temp = 0; temp < nList.getLength(); temp++){
	 node = nList.item(temp);
	 System.out.println();    //Just a separator
	 if (node.getNodeType() == Node.ELEMENT_NODE){
	    eElement = (Element) node;
	    String relTerm = eElement.getElementsByTagName("Label").item(0).getTextContent();
	    rTerms.add(relTerm);
	 }
	 CardObject.setRelatedTerms(rTerms);
	 
	}
	xml.close();
	return co;
	}
	
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		String url = "http://lookup.dbpedia.org/api/search/KeywordSearch?QueryString=BERLIN";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("Accept", "application/xml");
		con.setRequestProperty("Content-Type", "application/xml");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		InputStream xml = con.getInputStream();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xml);
		
		Element root = doc.getDocumentElement();
		System.out.println(root.getNodeName());
        
		//Get all Results
		NodeList nList = doc.getElementsByTagName("Result");
		System.out.println("============================");
		for (int temp = 0; temp < nList.getLength(); temp++){
		 Node node = nList.item(temp);
		 System.out.println();    //Just a separator
		 if (node.getNodeType() == Node.ELEMENT_NODE){
		    //Print each employee's detail
		    Element eElement = (Element) node;
		    //System.out.println("Label : "    + eElement.getElementsByTagName("Label"));
		    System.out.println("Label : "  + eElement.getElementsByTagName("Label").item(0).getTextContent());
		    System.out.println("Description : "   + eElement.getElementsByTagName("Description").item(0).getTextContent());
//		    System.out.println("Location : "    + eElement.getElementsByTagName("location").item(0).getTextContent());
		 }
		}
		xml.close();
	}
}
