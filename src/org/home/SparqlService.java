package org.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.home.model.ResultDocument;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;


import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import virtuoso.rdf4j.driver.VirtuosoRepository;


public class SparqlService implements ISparqlService {

	private static int numRows;
	private static String[] searchElems;
	private static String combineEntities;
	private static String startDate;
	private static String endDate;
	private static String type;
	private static String attribute;
	private static String location;
	private static List<String> relatedEntities;


	public SparqlService() {
		super();
	}

	
	
	
	public static void main(String[] args) {
		searchElems = new String[]{ "Hillary_Clinton"};
		SparqlService ss = new SparqlService();
		startDate = "2011-12-03";
		endDate = "2012-12-03";
		type = "BaseballPlayer";
		attribute="";
		location="";
//		System.out.println(ss.getNumberOfRows());
//		System.out.println(ss.getNumberOfFedRows());
		ss.FederatedQuery(1,10);
		//ss.getLayerMaxDate();
		//ss.getLayerMinDate();
	}


	public List<ResultDocument> FederatedQuery(int currentPage, int recordsPerPage){
		int start = currentPage * recordsPerPage - recordsPerPage;
		List<ResultDocument> ldocs = new ArrayList<ResultDocument>();
		StringBuffer querybff = new StringBuffer();
		
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		querybff.append("PREFIX dc: <http://purl.org/dc/terms/> \n");
		querybff.append("PREFIX owa: <http://l3s.de/owa#> \n");
		querybff.append("PREFIX owl: <https://www.w3.org/2002/07/owl#> \n");
		querybff.append("PREFIX schema: <http://schema.org/> \n");
		querybff.append("PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> \n");
		querybff.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");
		querybff.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		querybff.append("PREFIX yago:	<http://dbpedia.org/class/yago/> \n");
		querybff.append(" \n");
//		querybff.append("SELECT ?document (SAMPLE(?version) AS ?ver) (SAMPLE(?title) AS ?tit) (SAMPLE(?date) AS ?dat) (SAMPLE(?documentNumOfCaptures) AS ?dnc) (SAMPLE(?documentFirstCapture)AS ?dfc) (SAMPLE(?documentLastCapture)AS ?dlc) \n");

		querybff.append("SELECT ?document (MAX(?version) AS ?ver) (MAX(?title) AS ?tit) (MAX(?date) AS ?dat) (MAX(?documentNumOfCaptures) AS ?dnc) (MIN(?documentFirstCapture)AS ?dfc) (MAX(?documentLastCapture)AS ?dlc) \n");
//		for(int i = 0; i< searchElems.length; i++){
//			querybff.append(" (MIN(?entity"+i+") AS ?ent"+i+") " );
//		}						
//		querybff.append("SELECT ?document ?version ?title ?documentNumOfCaptures ?documentFirstCapture ?documentLastCapture \n"); 
		querybff.append("    WHERE { \n");
		querybff.append("        SERVICE <http://dbpedia.org/sparql> {\n");
//			     ?person dbo:birthPlace dbr:Berlin .
		if(!type.isEmpty()) {
			querybff.append("			?person rdf:type dbo:"+type+" .\n");
		}
		if((!attribute.isEmpty()) && (!location.isEmpty()) ) {
			querybff.append("			?person dbo:"+attribute+" dbr:"+location+" .\n");
		}
//		    	 ?person rdf:type dbo:SoccerManager .
//		querybff.append("            ?person rdf:type yago:Journalist110224578 .\n");
		querybff.append("          }\n");
		
		if((startDate!=null) && (endDate!=null)) {
			querybff.append("    ?version dc:date ?date FILTER(?date >=\""+startDate+"\"^^xsd:dateTime && ?date <=\""+endDate+"\"^^xsd:dateTime) .\n");
		}
		
		querybff.append("    ?version schema:mentions ?entity . \n");
	    querybff.append("    ?entity rdf:type oae:Entity ; oae:hasMatchedURI  ?person . \n");
		querybff.append("    ?version dc:title ?title . \n");
		querybff.append("    ?document dc:hasVersion ?version . \n");
		querybff.append("    ?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures . \n");
		
	
		
		querybff.append("}GROUP BY ?document \n");
    	querybff.append("  LIMIT "+recordsPerPage+" OFFSET "+start+"\n");

		Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
    	
//		System.out.println(querybff.toString());
		long startTime = System.nanoTime();

		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println("Broke here");
			return ldocs;
		}
		long endTime = System.nanoTime();
		// get difference of two nanoTime values
		long timeElapsed = endTime - startTime;

		System.out.println("Execution time in nanoseconds  : " + timeElapsed);

		System.out.println("Execution time in milliseconds : " +	timeElapsed / 1000000);

		if(results.hasNext()) {
			while(results.hasNext() ) {
				QuerySolution soln = results.nextSolution() ;
				String version = soln.get("ver").toString();
				version = version.replace("https://wayback.archive-it.org/2950/","https://web.archive.org/web/");
				String date = soln.get("dat").toString();
				date = date.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
//				date = date.split("T")[0];	
				String title = soln.get("tit").toString();
				ArrayList<String> entity = new ArrayList<String>();
//				if(combineEntities!=null) {
//					for(int i = 0; i< searchElems.length; i++){
//						String ent = soln.get("ent"+i).toString();
//						entity.add(ent);
//					}	
//				}else {
//					for(int i = 0; i< searchElems.length; i++){
//						
//						String ent = "";
//						try {
//							ent = soln.get("ent"+i).toString();
//						}catch(Exception e) {
//							ent = "null";
//						}
//						entity.add(ent);
//					}
//				}
//				
				String document = soln.get("document").toString();
				document = document.replace("https://wayback.archive-it.org/2950/","https://web.archive.org/web/");
				String documentFirstCapture = soln.get("dfc").toString();
				documentFirstCapture = documentFirstCapture.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				documentFirstCapture = documentFirstCapture.split("T")[0];
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDate ldt = LocalDate.parse(documentFirstCapture,formatter);
				documentFirstCapture = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);

				String documentLastCapture = soln.get("dlc").toString();
				documentLastCapture = documentLastCapture.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				documentLastCapture = documentLastCapture.split("T")[0];
				
				ldt = LocalDate.parse(documentLastCapture,formatter);
				documentLastCapture = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);
				
				String documentNumOfCaptures = soln.get("dnc").toString();
				
				documentNumOfCaptures = documentNumOfCaptures.replace("^^http://www.w3.org/2001/XMLSchema#integer","");
				ResultDocument rd = new ResultDocument(version, date, title, entity, document, documentFirstCapture, documentLastCapture, documentNumOfCaptures);
				ldocs.add(rd);
			}
			}else {
				System.out.println("There is no results");
				return ldocs;
			}
		System.out.println(ldocs.size());
		System.out.println();
		return ldocs;
		
		
		//federated query about documents that speaks about journalists
//		PREFIX schema: <http://schema.org/>
//		PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> 
//		PREFIX yago:	<http://dbpedia.org/class/yago/>
//		PREFIX dc2:   <http://purl.org/dc/terms/>
//		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

//		SELECT ?document ?person  WHERE {
//
//		SERVICE <http://dbpedia.org/sparql> {
//		    ?person rdf:type yago:Journalist110224578 .
//		}
//		    ?version schema:mentions    ?entity .
//		    ?entity rdf:type           oae:Entity .
//		    ?entity oae:hasMatchedURI  ?person .
//		    ?document  dc2:hasVersion     ?version .
//		  } GROUP BY ?document LIMIT 10

	     
	
		
////	
//	    PREFIX owl: <http://www.w3.org/2002/07/owl#>
//		PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
//		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
//		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//		PREFIX foaf: <http://xmlns.com/foaf/0.1/>
//		PREFIX dc: <http://purl.org/dc/elements/1.1/>
//		PREFIX : <http://dbpedia.org/resource/>
//		PREFIX dbpedia2: <http://dbpedia.org/property/>
//		PREFIX dbpedia: <http://dbpedia.org/>
//		PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
//
//		SELECT ?person 
//		WHERE { 
//		     ?person dbo:birthPlace dbr:Berlin .
//		     ?person rdf:type dbo:SoccerPlayer .
//	    	 ?person rdf:type dbo:SoccerManager .
//		     ?person rdf:type yago:Journalist110224578 .
//		} 
//	
		
		
	}
	
	
//	retrieving the most discussed journalists in web pages of the Occupy Movement collection.

//	SELECT ?journalist (COUNT(DISTINCT ?page) AS ?num) WHERE {
//		SERVICE <http://dbpedia.org/sparql> {
//		?journalist a yago:Journalist110224578 }
//		?page a owa:ArchivedDocument ;
//		dc:hasVersion ?version .
//		?version schema:mentions ?entity .
//		?entity oae:hasMatchedURI ?journalist .
//		} GROUP BY ?journalist ORDER BY DESC(?num)
//	
	
	
	public List<ResultDocument> LocalQuery(int currentPage, int recordsPerPage) {
	   int start = currentPage * recordsPerPage - recordsPerPage;
	   List<ResultDocument> ldocs = new ArrayList<ResultDocument>();
	   StringBuffer querybff = new StringBuffer();
//		
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		querybff.append("PREFIX dc: <http://purl.org/dc/terms/> \n");
		querybff.append("PREFIX owa: <http://l3s.de/owa#> \n");
		querybff.append("PREFIX owl: <https://www.w3.org/2002/07/owl#> \n");
		querybff.append("PREFIX schema: <http://schema.org/> \n");
		querybff.append("PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> \n");
		querybff.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");
		querybff.append("PREFIX res: <http://dbpedia.org/resource/> \n");
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		querybff.append("SELECT ?document  (MAX(?version) AS ?ver) (MAX(?title) AS ?tit) (MAX(?date) AS ?dat) (MAX(?documentNumOfCaptures) AS ?dnc) (MIN(?documentFirstCapture)AS ?dfc) (MAX(?documentLastCapture)AS ?dlc) \n");
		for(int i = 0; i< searchElems.length; i++){
			querybff.append(" (MIN(?entity"+i+") AS ?ent"+i+") " );
		}	
		querybff.append("    FROM <http://localhost:8890/occupy>  \n");

		querybff.append("WHERE { \n");

		if((startDate!=null) && (endDate!=null)) {
			querybff.append("    ?version dc:date ?date FILTER(?date >=\""+startDate+"\"^^xsd:dateTime && ?date <=\""+endDate+"\"^^xsd:dateTime) .\n");
		}
		if(combineEntities!=null) {
			for(int i = 0; i< searchElems.length; i++){
				String entity = searchElems[i];
				entity  = entity.trim();
				entity = entity.replaceAll(" ", "_");
				querybff.append("    ?version schema:mentions ?entity"+i+" . \n");
				querybff.append("    ?entity"+i+" oae:position ?pos"+i+" . \n");
				querybff.append("    ?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");

			}
		}else {
			for(int i = 0; i< searchElems.length; i++){
				String entity = searchElems[i];
				entity  = entity.trim();
				entity = entity.replaceAll(" ", "_");
				querybff.append("{\n");       
				querybff.append("    ?version schema:mentions ?entity"+i+" . \n");
//				querybff.append("?version schema:mentions ?entity"+i+" . \n");
			    querybff.append("    ?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");
//			    querybff.append("?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");
				querybff.append("}\n");			

             if (i != searchElems.length - 1) {
             	querybff.append(" UNION \n");
             }
			}
		}
		querybff.append("    ?version dc:title ?title . \n");
		querybff.append("    ?document dc:hasVersion ?version . \n");
		querybff.append("    ?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures . \n");
		querybff.append("}GROUP BY ?document \n");
    	querybff.append("  LIMIT "+recordsPerPage+" OFFSET "+start+"\n");
		Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
//
		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println("Broke here");
			return ldocs;
		}
		if(results.hasNext()) {
			while(results.hasNext() ) {
				QuerySolution soln = results.nextSolution() ;
				String version = soln.get("ver").toString();
				version = version.replace("https://wayback.archive-it.org/2950/","https://web.archive.org/web/");
				String date = soln.get("dat").toString();
				date = date.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
//				date = date.split("T")[0];	
				String title = soln.get("tit").toString();
				ArrayList<String> entity = new ArrayList<String>();
				if(combineEntities!=null) {
					for(int i = 0; i< searchElems.length; i++){
						String ent = soln.get("ent"+i).toString();
						entity.add(ent);
					}	
				}else {
					for(int i = 0; i< searchElems.length; i++){
						
						String ent = "";
						try {
							ent = soln.get("ent"+i).toString();
						}catch(Exception e) {
							ent = "null";
						}
						entity.add(ent);
					}
				}
				
				String document = soln.get("document").toString();
				document = document.replace("https://wayback.archive-it.org/2950/","https://web.archive.org/web/");
				String documentFirstCapture = soln.get("dfc").toString();
				documentFirstCapture = documentFirstCapture.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				documentFirstCapture = documentFirstCapture.split("T")[0];
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDate ldt = LocalDate.parse(documentFirstCapture,formatter);
				documentFirstCapture = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);

				String documentLastCapture = soln.get("dlc").toString();
				documentLastCapture = documentLastCapture.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				documentLastCapture = documentLastCapture.split("T")[0];
				
				
				ldt = LocalDate.parse(documentLastCapture,formatter);
				documentLastCapture = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH).format(ldt);
				
				String documentNumOfCaptures = soln.get("dnc").toString();
				
				documentNumOfCaptures = documentNumOfCaptures.replace("^^http://www.w3.org/2001/XMLSchema#integer","");
				ResultDocument rd = new ResultDocument(version, date, title, entity, document, documentFirstCapture, documentLastCapture, documentNumOfCaptures);
				ldocs.add(rd);
				}
			}else {
				System.out.println("There is no results");
				return ldocs;
			}
		return ldocs;
	}



	

	public int getNumberOfFedRows() {

		StringBuffer querybff = new StringBuffer();
		
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		querybff.append("PREFIX dc: <http://purl.org/dc/terms/> \n");
		querybff.append("PREFIX owa: <http://l3s.de/owa#> \n");
		querybff.append("PREFIX owl: <https://www.w3.org/2002/07/owl#> \n");
		querybff.append("PREFIX schema: <http://schema.org/> \n");
		querybff.append("PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> \n");
		querybff.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");
		querybff.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		querybff.append("PREFIX yago:	<http://dbpedia.org/class/yago/> \n");
		querybff.append(" \n");
		
		querybff.append("SELECT (COUNT(DISTINCT ?document) AS ?no_document) \n");
		querybff.append("    WHERE { \n");
		querybff.append("        SERVICE <http://dbpedia.org/sparql> {\n");
//			     ?person dbo:birthPlace dbr:Berlin .
		querybff.append("			?person rdf:type dbo:"+type+" .\n");
//		querybff.append("			?person dbo:birthPlace dbr:Berlin .\n");
//		    	 ?person rdf:type dbo:SoccerManager .
//		querybff.append("            ?person rdf:type yago:Journalist110224578 .\n");
		querybff.append("          }\n");
		
		if((startDate!=null) && (endDate!=null)) {
			querybff.append("    ?version dc:date ?date FILTER(?date >=\""+startDate+"\"^^xsd:dateTime && ?date <=\""+endDate+"\"^^xsd:dateTime) .\n");
		}
		
		querybff.append("    ?version schema:mentions ?entity . \n");
	    querybff.append("    ?entity rdf:type oae:Entity ; oae:hasMatchedURI  ?person . \n");
		querybff.append("    ?version dc:title ?title . \n");
		querybff.append("    ?document dc:hasVersion ?version . \n");
		querybff.append("    ?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures . \n");
		
	
		
		querybff.append("}  \n");
    	System.out.println(querybff.toString());
    	Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
//
		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println("Broke here");
		}
		if(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			String count = soln.get("no_document").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer","");
			int numOfRows = Integer.parseInt(count);
			return numOfRows;
			
		}else {
			return 0;
		}
	}
	
	
	
	
	public int getNumberOfRows() {
		StringBuffer querybff = new StringBuffer();
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		querybff.append("PREFIX dc: <http://purl.org/dc/terms/> \n");
		querybff.append("PREFIX owa: <http://l3s.de/owa#> \n");
		querybff.append("PREFIX owl: <https://www.w3.org/2002/07/owl#> \n");
		querybff.append("PREFIX schema: <http://schema.org/> \n");
		querybff.append("PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> \n");
		querybff.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");
		querybff.append("PREFIX res: <http://dbpedia.org/resource/> \n");
		querybff.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		querybff.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		
		
		querybff.append("SELECT (COUNT(DISTINCT ?document) AS ?no_document)");
//		for(int i = 0; i< searchElems.length; i++){
//			querybff.append(" ?entity"+i);
//		}	
		
		querybff.append(" FROM <http://localhost:8890/occupy>  \n");
//		querybff.append(" ?documentNumOfCaptures ?documentFirstCapture ?documentLastCapture  FROM <http://localhost:8890/occupy>  \n");

//		querybff.append("SELECT ?document ?version ?date ?title ?documentNumOfCaptures ?documentFirstCapture ?documentLastCapture  FROM <http://localhost:8890/occupy>  \n");
		
//		querybff.append("SELECT DISTINCT * FROM <http://localhost:8890/occupy>  \n");
		querybff.append("WHERE { \n");
//		if((startDate!=null) && (endDate==null)) {
//			querybff.append("    ?version dc:date ?date FILTER(?date >=\""+startDate+"\"^^xsd:dateTime) .\n");
//		}
//		if((startDate==null) && (endDate!=null)) {
//			querybff.append("    ?version dc:date ?date FILTER(?date <=\""+endDate+"\"^^xsd:dateTime) .\n");
//		}
//		if((startDate!=null) && (endDate!=null)) {
			querybff.append("    ?version dc:date ?date FILTER(?date >=\""+startDate+"\"^^xsd:dateTime && ?date <=\""+endDate+"\"^^xsd:dateTime) .\n");
//		}
		
		if(combineEntities!=null) {
			for(int i = 0; i< searchElems.length; i++){
				String entity = searchElems[i];
				entity  = entity.trim();
				entity = entity.replaceAll(" ", "_");
				querybff.append("    ?version schema:mentions ?entity"+i+" . \n");
//				querybff.append("    ?entity"+i+" oae:position ?pos"+i+" . \n");
				querybff.append("    ?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");
				//System.out.println("?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" .");
			}
		}else {
			for(int i = 0; i< searchElems.length; i++){
				String entity = searchElems[i];
				entity  = entity.trim();
				entity = entity.replaceAll(" ", "_");
				querybff.append("{\n");       
				querybff.append("    ?version schema:mentions ?entity . \n");
//				querybff.append("?version schema:mentions ?entity"+i+" . \n");
//				querybff.append("    ?entity"+i+" oae:position ?pos"+i+" . \n");
			    querybff.append("    ?entity rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");
//			    querybff.append("?entity"+i+" rdf:type oae:Entity ; oae:hasMatchedURI  res:"+entity+" . \n");
				querybff.append("}\n");			

             if (i != searchElems.length - 1) {
             	querybff.append(" UNION \n");
             }
			}
		}
		querybff.append("    ?version dc:title ?title . \n");
		querybff.append("    ?document dc:hasVersion ?version . \n");
		querybff.append("    ?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures . \n");
		querybff.append("}\n");
		
		Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
//
		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println("Broke here");
		}
		if(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			String count = soln.get("no_document").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer","");
			int numOfRows = Integer.parseInt(count);
			return numOfRows;
			
		}else {
			return 0;
		}
	}

	
	
	
	/**
	 * 
	 * 	Get the maximum date from Semantic layer
	 * 
	 */
	private void getLayerMaxDate(){
		StringBuffer querybff = new StringBuffer();
		querybff.append("PREFIX  dc:   <http://purl.org/dc/terms/> \n");
		querybff.append("");	
		querybff.append("SELECT  ?version ?date \n");
		querybff.append("FROM <http://localhost:8890/occupy> \n");
		querybff.append("WHERE \n");
		querybff.append("	  { ?version  dc:date  ?date } \n");
		querybff.append("	ORDER BY DESC(?date) LIMIT 1 \n");	
		Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println(e);
		}
		if(results.hasNext()) {
			while(results.hasNext() ) {
				QuerySolution soln = results.nextSolution() ;
				String date = soln.get("date").toString();
				date = date.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				date = date.split("T")[0];
				System.out.println(date);
			}
		}
		
	}
	/**
	 * 
	 * 	Get the minimum date from Semantic layer
	 * 
	 */
	private void getLayerMinDate(){
		StringBuffer querybff = new StringBuffer();
		querybff.append("PREFIX  dc:   <http://purl.org/dc/terms/> \n");
		querybff.append("");	
		querybff.append("SELECT  ?version ?date \n");
		querybff.append("FROM <http://localhost:8890/occupy> \n");
		querybff.append("WHERE \n");
		querybff.append("	  { ?version  dc:date  ?date } \n");
		querybff.append("	ORDER BY ASC(?date) LIMIT 1 \n");
		Query sparqlquery = QueryFactory.create( querybff.toString()	);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
		ResultSet results = null; 
		try {
			results = qexec.execSelect();
		}catch(Exception e) {
			System.out.println(e);
		}
		if(results.hasNext()) {
			while(results.hasNext() ) {
				QuerySolution soln = results.nextSolution() ;
				String date = soln.get("date").toString();
				date = date.replace("^^http://www.w3.org/2001/XMLSchema#dateTime","");
				date = date.split("T")[0];
				System.out.println(date);
			}
		}
	}	
	
	
	
	
	public static String[] getSearchElems() {
		return searchElems;
	}


	public static void setSearchElems(String[] searchElems) {
		for(int i=0; i < searchElems.length; i++) {
			try {
				searchElems[i] = searchElems[i].replace(" ","_");
				searchElems[i] = URLEncoder.encode(searchElems[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
		}
		
		//searchField=searchField.replace(" ","_");
		SparqlService.searchElems = searchElems;
	}

	

	public static String getCombineEntities() {
		return combineEntities;
	}




	public static void setCombineEntities(String combineEntities) {
		SparqlService.combineEntities = combineEntities;
	}




	public static String getStartDate() {
		return startDate;
	}




	public static void setStartDate(String startDate) {
		SparqlService.startDate = startDate;
	}




	public static String getEndDate() {
		return endDate;
	}




	public static void setEndDate(String endDate) {
		SparqlService.endDate = endDate;
	}




	public static int getNumRows() {
		return numRows;
	}




	public static void setNumRows(int numRows) {
		SparqlService.numRows = numRows;
	}




	public static String getType() {
		return type;
	}




	public static void setType(String type) {
		SparqlService.type = type;
	}




	public static String getAttribute() {
		return attribute;
	}




	public static void setAttribute(String attribute) {
		SparqlService.attribute = attribute;
	}




	public static String getLocation() {
		return location;
	}




	public static void setLocation(String location) {
		SparqlService.location = location;
	}
	
	
	
	
	
	
	
}