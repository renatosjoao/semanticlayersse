package org.home;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.home.model.CardObject;
import org.home.model.ResultDocument;
import org.home.test.WikipediaLookup;

//@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		System.out.println("foi get"); 
		 int currentPage = 0 ;
	        int recordsPerPage = 0;
	        
	        if(req.getParameter("currentPage") != null) {
	        	currentPage = Integer.parseInt(req.getParameter("currentPage"));
	        }
	        if(req.getParameter("recordsPerPage") != null) {
	        	recordsPerPage = Integer.parseInt(req.getParameter("recordsPerPage"));
	        }
	        
	        //        page = Integer.parseInt(req.getParameter("page"));
			String searchField = req.getParameter("searchField");
			String combineEntities = req.getParameter("foo1");
			String startDate = req.getParameter("startDate");
			String endDate = req.getParameter("endDate");
			String typeField = req.getParameter("typeField");
			String attributeField = req.getParameter("attributeField");
			String locationField = req.getParameter("locationField");

			
			//System.out.println(searchField.isEmpty());  	// works...
			System.out.println(typeField.isEmpty()); 	 	// works...
			System.out.println(attributeField.isEmpty()); 	// works...
			System.out.println(locationField.isEmpty()); 	// works...

			
//			if(searchField.isEmpty()) {
//				//searchField = "NONNOO";
//				System.out.println("vazio");
//				System.out.println("Empty search field. Searching for default entity.<<"+searchField+">>");
//			}
			
			if( (startDate.isEmpty()) || (startDate==null) ){
				startDate = "2011-12-03";
			}else {
				String[] dateElems = startDate.split("/");
				startDate = dateElems[2]+"-"+ dateElems[1]  +"-"+dateElems[0];

			}
			if(startDate.equalsIgnoreCase("today")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				startDate = dateFormat.format(today).toString();
				endDate  = today.toString();
			}
			if(startDate.equalsIgnoreCase("last_week")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				Date lastWeek =  DateUtils.addDays(today,-7);
				startDate = dateFormat.format(lastWeek).toString();
				endDate  = today.toString();
			}
			if(startDate.equalsIgnoreCase("last_month")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				Date lastmonth  = DateUtils.addDays(new Date(),-30);
				startDate = dateFormat.format(lastmonth).toString(); 
				endDate  = today.toString();
			}
			if(startDate.equalsIgnoreCase("last_year")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				Date lastyear  = DateUtils.addDays(new Date(),-365);
				startDate = dateFormat.format(lastyear).toString();
				endDate  = today.toString();
			}
			if((endDate.isEmpty()) || (endDate == null) ) {
				endDate = "2012-10-09";
			}else {
				String[] dateElems = endDate.split("/");
				endDate = dateElems[2]+"-"+ dateElems[1]  +"-"+dateElems[0];
			}
			
	        String[] searchElems = searchField.split(",");
//			
//		   CardObject CO = null ;
//			try {
//				CO =  DBPediaLookup.querydbPedia(searchElems[0]);
//			} catch (ParserConfigurationException e1) {
//				e1.printStackTrace();
//			} catch (SAXException e1) {
//				e1.printStackTrace();
//			}

	        
//	        
//	        WikipediaLookup wp = new WikipediaLookup();
//	        List<String> prefixes =  wp.queryPrefixes(searchElems[0]);
//	        if(prefixes.size() > 5) {
//	        	prefixes =  prefixes.subList(1,5);
//	        }
//	        String imgURL =  wp.fetchThumb(searchElems[0]);
//	        
	        
//		   String imgURL =  null;
	       SparqlService SS = new SparqlService();
	       SparqlService.setSearchElems(searchElems);
	       
	       int rows = SS.getNumberOfRows();
	       
	       List<ResultDocument> ldocs = SS.LocalQuery(currentPage,recordsPerPage);
	       
	       int noOfPages = rows / recordsPerPage;
//	       System.out.println("#pages :"+noOfPages);
//	       System.out.println("rows :"+rows);
//	       System.out.println("recordsPerPag :"+recordsPerPage);
	       if (noOfPages % recordsPerPage > 0) {
	    	   noOfPages++;
	       }
	       
	       
	   	
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			LocalDate ldt = LocalDate.parse(startDate,formatter);
			startDate = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);

			ldt = LocalDate.parse(endDate,formatter);
			endDate = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);

//			System.out.println("staaaart"+startDate);
		   req.getServletContext().setAttribute("searchField",searchField);
		   req.getServletContext().setAttribute("currentPage",currentPage);
		   req.getServletContext().setAttribute("recordsPerPage", recordsPerPage);
	       req.getServletContext().setAttribute("noOfPages", noOfPages);
//	       req.getServletContext().setAttribute("card", CO);
	       req.getServletContext().setAttribute("totalResults", rows);
	       req.getServletContext().setAttribute("startDate", startDate);
	       req.getServletContext().setAttribute("endDate", endDate);
//	       req.getServletContext().setAttribute("prefixes",prefixes);
	      
	       
	       if(ldocs!=null) {
	    	   req.getServletContext().setAttribute("resultsList",ldocs);
	       }
//	       if(imgURL!=null) {
//	    	   req.getServletContext().setAttribute("thumb",imgURL);
//	       }

		   req.getRequestDispatcher("result_google.jsp").forward(req, res);

	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		System.out.println("Esta entrando no doPost");
        int currentPage = 0 ;
        int recordsPerPage = 0;
        if(req.getParameter("currentPage") != null) {
        	currentPage = Integer.parseInt(req.getParameter("currentPage"));
        }
        if(req.getParameter("recordsPerPage") != null) {
        	recordsPerPage = Integer.parseInt(req.getParameter("recordsPerPage"));
        }
		String searchField = req.getParameter("searchField");
		String combineEntities = req.getParameter("foo1");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		String typeField = req.getParameter("typeField");
		String attributeField = req.getParameter("attributeField");
		String locationField = req.getParameter("locationField");

		
		System.out.println(searchField.isEmpty());  	// works...
		System.out.println(typeField.isEmpty()); 	 	// works...
		System.out.println(attributeField.isEmpty()); 	// works...
		System.out.println(locationField.isEmpty()); 	// works...

		
//		if(searchField.isEmpty()) {
//			//searchField = "NONNOO";
//			System.out.println("vazio");
//			System.out.println("Empty search field. Searching for default entity.<<"+searchField+">>");
//		}
		
		if( (startDate.isEmpty()) || (startDate==null) ){
			startDate = "2011-12-03";
		}else {
			String[] dateElems = startDate.split("/");
			startDate = dateElems[2]+"-"+ dateElems[1]  +"-"+dateElems[0];

		}
		if(startDate.equalsIgnoreCase("today")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			startDate = dateFormat.format(today).toString();
			endDate  = today.toString();
		}
		if(startDate.equalsIgnoreCase("last_week")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			Date lastWeek =  DateUtils.addDays(today,-7);
			startDate = dateFormat.format(lastWeek).toString();
			endDate  = today.toString();
		}
		if(startDate.equalsIgnoreCase("last_month")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			Date lastmonth  = DateUtils.addDays(new Date(),-30);
			startDate = dateFormat.format(lastmonth).toString(); 
			endDate  = today.toString();
		}
		if(startDate.equalsIgnoreCase("last_year")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			Date lastyear  = DateUtils.addDays(new Date(),-365);
			startDate = dateFormat.format(lastyear).toString();
			endDate  = today.toString();
		}
		if((endDate.isEmpty()) || (endDate == null) ) {
			endDate = "2012-10-09";
		}else {
			String[] dateElems = endDate.split("/");
			endDate = dateElems[2]+"-"+ dateElems[1]  +"-"+dateElems[0];
		}

        String[] searchElems = searchField.split(",");
        
       SparqlService SS = new SparqlService();
       SparqlService.setCombineEntities(combineEntities);
       SparqlService.setType(typeField);
       SparqlService.setAttribute(attributeField);
       SparqlService.setLocation(locationField);
       SparqlService.setSearchElems(searchElems);
       SparqlService.setStartDate(startDate);
       SparqlService.setEndDate(endDate);

      List<ResultDocument> ldocs = new ArrayList<ResultDocument>();
      int rows = 0;
	  if((typeField.isEmpty()) && (attributeField.isEmpty()) && (locationField.isEmpty()) ) {
		  ldocs = SS.LocalQuery(currentPage,recordsPerPage);
		  rows = SS.getNumberOfRows();
	  }
	  if((!typeField.isEmpty()) && (attributeField.isEmpty()) && (locationField.isEmpty()) ) {
		  //System.out.println("Fed Query");
		  ldocs = SS.FederatedQuery(currentPage,recordsPerPage);
		  rows = SS.getNumberOfFedRows();

	  }
	  
	  
	  
	
	  int noOfPages = rows / recordsPerPage;
//       System.out.println("#pages :"+noOfPages);
//       System.out.println("rows :"+rows);
//       System.out.println("recordsPerPag :"+recordsPerPage);
       if (noOfPages % recordsPerPage > 0) {
    	   noOfPages++;
       }
       
//       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
//		LocalDate ldt = LocalDate.parse(startDate,formatter);
//		startDate = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);
//
//		ldt = LocalDate.parse(endDate,formatter);
//		endDate = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);

//		System.out.println("staaaart"+startDate);
		
	   req.getSession().setAttribute("searchField",searchField);
       req.getSession().setAttribute("noOfPages", noOfPages);
	   req.getSession().setAttribute("currentPage",currentPage);
	   req.getSession().setAttribute("recordsPerPage", recordsPerPage);
//       req.getSession().setAttribute("card", CO);
       req.getSession().setAttribute("totalResults", rows);
       req.getSession().setAttribute("startDate", startDate);
       req.getSession().setAttribute("endDate", endDate);
//       req.getServletContext().setAttribute("prefixes",prefixes);
      
       
       if(ldocs!=null) {
    	   req.getSession().setAttribute("resultsList",ldocs);
       }else {
    	   //empty
    	   ldocs = new ArrayList<ResultDocument>(); 
    	   req.getSession().setAttribute("resultsList",ldocs);
       }
       
//       if(imgURL!=null) {
//    	   req.getSession().setAttribute("thumb",imgURL);
//       }

//       System.out.println(searchField);
	   req.getRequestDispatcher("result_google.jsp").forward(req, res);


	}

	


	

	
	
	public static void kb() {
//		
//		select str(?text) as ?text    {
//		    <http://dbpedia.org/resource/Akshay_Kumar> dbo:abstract  ?text 
//		    FILTER (lang(?text) = 'en')
//		    }
		
//		
//		SELECT DISTINCT ?player
//				FROM <http://dbpedia.org>
//				 WHERE{
//				?player dct:subject dbc:Grammy_Award_winners .
//
//				}
//		
//
//		
//		
//		SELECT DISTINCT ?player
//				FROM <http://dbpedia.org>
//				 WHERE{
//				?player dct:subject dbc:Presidents_of_Brazil .
//
//				}
//		
//		
//		SELECT DISTINCT ?player
//				FROM <http://dbpedia.org>
//				 WHERE{
//				?player dct:subject dbc:German_cuisine .
//
//				}
		
	}
	
	
}
