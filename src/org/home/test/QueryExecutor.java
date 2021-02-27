package org.home.test;

import java.io.IOException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class QueryExecutor {

	
public static void main(String[] args) throws IOException {
		
		Query sparqlquery = QueryFactory.create(""+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
		"PREFIX dc: <http://purl.org/dc/terms/> \n" + 
		"PREFIX owa: <http://l3s.de/owa#> \n" + 
		"PREFIX owl: <https://www.w3.org/2002/07/owl#>\n" +  
		"PREFIX schema: <http://schema.org/> \n" + 
		"PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#>\n" +  
		"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +  
		"PREFIX res: <http://dbpedia.org/resource/>\n" +  
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +  
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +  
		"SELECT ?document (MIN(?version) AS ?S)  (MIN(?title) AS ?T) (MIN(?documentFirstCapture) AS ?DFC) (MAX(?documentLastCapture) AS ?DLC) (MAX(?documentNumOfCaptures) AS ?DNC) FROM <http://localhost:8890/occupy>\n" +   
		"WHERE {\n" +  
		"?version dc:date ?date FILTER(?date >=\"1900-01-01\"^^xsd:dateTime && ?date <=\"2019-07-31\"^^xsd:dateTime) .\n" + 
		"?version schema:mentions ?entity0 .\n" +  
		"?entity0 rdf:type oae:Entity ; oae:hasMatchedURI  res:Bill_Clinton .\n" +  
		"?version dc:title ?title .\n" +  
		"?document dc:hasVersion ?version .\n" +  
		"?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures .\n" +  
		"}GROUP BY ?document\n" +  
		"LIMIT 5 OFFSET 0\n" ); 
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/sparql", sparqlquery);
		 ResultSet results = null; 
			try {
			    results = qexec.execSelect();
			}catch(Exception e) {
				System.out.println(e);
			}
			 while(results.hasNext() ) {
			   	QuerySolution soln = results.nextSolution() ;
	            String x = soln.get("document").toString();
	                System.out.print(x +"\n");
			 }
			   qexec.close();
}
}
