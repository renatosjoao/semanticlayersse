package org.home.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.management.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import arq.query;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class WikipediaLookup {

	
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date today = new Date();
		System.out.println("Today "+dateFormat.format(today).toString()); 
		
		Date lastWeek =  DateUtils.addDays(today,-7);
		System.out.println("Last Week "+dateFormat.format(lastWeek).toString());
		
		
		Date lastmonth  = DateUtils.addDays(new Date(),-30);
		System.out.println("Last Month  "+dateFormat.format(lastmonth).toString()); 
		
		Date lastyear  = DateUtils.addDays(new Date(),-365);
		System.out.println("Last Year "+dateFormat.format(lastyear).toString()); 
		
		
	}
	
	
	

	public List<String> queryImages(String searchElem) throws IOException{
		List<String> imgsList = new ArrayList<String>();
//		String url = "http://lookup.dbpedia.org/api/search/KeywordSearch?QueryString=BERLIN";
//		https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Barack%20Obama&utf8=&format=json		
		searchElem = searchElem.replaceAll(" ","%20");
		String URL = "https://en.wikipedia.org/w/api.php?action=query&titles="+searchElem+"&format=json&prop=images";
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Angela-merkel-ebw-01.jpg
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Billy_Tipton.jpg
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Angela_Merkel._Tallinn_Digital_Summit.jpg
			

//		/w/api.php?action=query&format=json&list=search&utf8=1&srsearch=Nelson%20Mandela
//		/w/api.php?action=query&format=json&prop=images&titles=Albert%20Einstein
				
				
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");

		int responseCode = con.getResponseCode();
		
		if(responseCode!=200) {
			return null;
		}else {
//		System.out.println("\nSending 'GET' request to URL : " + URL);
		
			InputStream json = con.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(json, "UTF-8"));
			
			StringBuilder responseStrBuilder = new StringBuilder(); 
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null) {
		        	responseStrBuilder.append(inputStr);
		    	}
			//		        
			String jsonData = responseStrBuilder.toString();
			JSONObject Jobject = new JSONObject(jsonData);
			JSONObject Jcontinue = Jobject.getJSONObject("continue");
			String pid = (String)Jcontinue.get("imcontinue");
			String[] p = pid.split("\\|");
			pid = p[0].trim();
			JSONObject JqueryObj = Jobject.getJSONObject("query");
			JSONObject JpageObj =  JqueryObj.getJSONObject("pages");
			JSONObject pageObj = JpageObj.getJSONObject(pid);
			JSONArray Jimages =  pageObj.getJSONArray("images");

//		for (int i = 0; i < Jimages.length()/2; i++) {
			for (int i = 0; i < 3; i++) {
			
				JSONObject object = Jimages.getJSONObject(i);
				String title = object.getString("title");
				String imgUrl = fetchImagesURL(title);
				if(imgUrl!=null) {
					if( (imgUrl.endsWith(".jpg")) || ((imgUrl.endsWith(".jpeg"))) || ((imgUrl.endsWith(".png"))) )
					{	
						imgsList.add(imgUrl);
					}
				}
			}
			return imgsList;
		}
		
	}
	
	public String fetchThumb(String fileQuery) throws IOException{
		fileQuery = fileQuery.replaceAll(" ","%20");
		String URL  = "https://en.wikipedia.org/api/rest_v1/page/summary/"+fileQuery;
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");

		int responseCode = con.getResponseCode();
		
		if(responseCode!=200) {
			return null;
		}else {
		
		InputStream json = con.getInputStream();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(json, "UTF-8"));
		
		StringBuilder responseStrBuilder = new StringBuilder(); 
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null) {
	        	responseStrBuilder.append(inputStr);
	    	}
		//		        
		String jsonData = responseStrBuilder.toString();
		JSONObject Jobject = new JSONObject(jsonData);
//		System.out.println(Jobject);

		//		        
		JSONObject Jcontinue = Jobject.getJSONObject("thumbnail");
		String thumURL = Jcontinue.getString("source");
//		System.out.println(thumURL);
		return thumURL;
		}	
		
				
	}
	
	public static String fetchImagesURL(String fileQuery) throws IOException{
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Angela-merkel-ebw-01.jpg
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Billy_Tipton.jpg
//		https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:Angela_Merkel._Tallinn_Digital_Summit.jpg
//		fileQuery = URLEncoder.encode(fileQuery, "UTF-8"); 
		String imgURL = "";
		fileQuery = fileQuery.replaceAll(" ","%20");
		String URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles="+fileQuery;
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");

		int responseCode = con.getResponseCode();
		if(responseCode!=200) {
			return null;
		}else {
			InputStream json = con.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(json, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder(); 
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null) {
		        	responseStrBuilder.append(inputStr);
		    	}
			String jsonData = responseStrBuilder.toString();
			JSONObject Jobject = new JSONObject(jsonData);
			JSONObject JqueryObj = Jobject.getJSONObject("query");
			JSONObject JpageObj =  JqueryObj.getJSONObject("pages");
			Iterator<String> keys = JpageObj.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				if (JpageObj.get(key) instanceof JSONObject) {
					JSONObject Jobj = (JSONObject) JpageObj.get(key);
					JSONArray Jarray = null;
		    		try {
		    			Jarray = Jobj.getJSONArray("imageinfo");
		    		} catch (Exception e) {
					
		    		}
		    		JSONObject object = Jarray.getJSONObject(0);
		    		imgURL = object.getString("url");
					}
				break;
			}
			return imgURL;
		}
	}
	
	
	
	
	

		
	
	/**
	 * 						Perform a prefix search for page titles.
	 *
	 * @param searchElem
	 * @return
	 * @throws IOException
	 */
	public List<String> queryPrefixes(String searchElem) throws IOException{
		searchElem = searchElem.replaceAll(" ", "_");
		String URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&pslimit=10&list=prefixsearch&pssearch="+searchElem;
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + URL);
		if(responseCode!=200) {
//			System.out.println("Response Code : " + responseCode);
			return null;
		}
		InputStream json = con.getInputStream();
	    BufferedReader streamReader = new BufferedReader(new InputStreamReader(json, "UTF-8"));
	    StringBuilder responseStrBuilder = new StringBuilder(); 
        String inputStr;
	    while ((inputStr = streamReader.readLine()) != null) {
//		    	System.out.println(inputStr);
		        responseStrBuilder.append(inputStr);
		}
	    String jsonData = responseStrBuilder.toString();
		JSONObject Jobject = new JSONObject(jsonData);
		JSONObject JqueryObj = Jobject.getJSONObject("query");
		JSONArray Jarray = null;
		try {
			Jarray = JqueryObj.getJSONArray("prefixsearch");
		} catch (Exception e) {
			
		}
		List<String> entitiesList = new ArrayList<String>();
		for (int i = 0; i < Jarray.length(); i++) {
			JSONObject object = Jarray.getJSONObject(i);
			String title = object.getString("title");
//			String id = object.getString("id");
			entitiesList.add(title);
		}
		return entitiesList;
	}
}
