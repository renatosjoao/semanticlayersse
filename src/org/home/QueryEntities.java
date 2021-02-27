package org.home;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryEntities {

	
	
	public static void main(String[] args) throws IOException {
		
		Query sparqlquery = QueryFactory.create(""+
	
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
				"prefix dc: <http://purl.org/dc/terms/> \n" + 
				"prefix owa: <http://l3s.de/owa#> \n" + 
				"prefix owl: <https://www.w3.org/2002/07/owl#> \n" + 
				"prefix schema: <http://schema.org/> \n" + 
				"prefix oae: <http://www.ics.forth.gr/isl/oae/core#> \n" + 
				"\n" + 
				"\n" + 
				"select DISTINCT ?sf ?entity FROM  <http://localhost:8890/occupy> WHERE  {\n" + 
				"    ?s  schema:mentions ?e .\n" + 
				"    ?e  rdf:type oae:Entity .\n" +
				"    ?e  oae:detectedAs ?sf . \n" +
				"    ?e  oae:hasMatchedURI ?entity .\n" + 
				"}LIMIT 1000\n");
		VirtGraph set = new VirtGraph ("jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2", "dba", "root");
	    

        TreeSet<String> entities = new TreeSet<String>(); 
        
//		Query sparql = QueryFactory.create(""+
//		"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
//		"prefix xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
//		"prefix oae: <http://www.ics.forth.gr/isl/oae/core#> \n" + 
//		"prefix schema: <http://schema.org/> \n" + 
//		"prefix dc: <http://purl.org/dc/terms/> \n" + 
//		"prefix twitter: <http://www.openlinksw.com/schemas/twitter#> \n" + 
//		"\n" + 
//		"\n" + 
//		"SELECT DISTINCT ?tweet \n"+
//		"    FROM  <http://localhost:8890/tweets> WHERE { \n" +
//		"	?tweet dc:creator \"Newsday\" .\n" + 
//		"	?tweet schema:mentions ?entity .\n" + 
//		"	?entity oae:detectedAs \"powerball\" .} ");
//	
	//+ "SELECT ?s ?p ?o FROM <http://localhost:8890/tweets> { ?s ?p ?o }  limit 100");
		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparqlquery, set);

		
		

 		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
		    RDFNode e = result.get("entity");
		    RDFNode s = result.get("sf");
//		    RDFNode o = result.get("o");
//		    System.out.println(" { " + s + " " + p+  " " + o +" . }");
		    String entity = e.toString();
		    entity = entity.replace("http://dbpedia.org/resource/","");
		    entity = entity.replaceAll("_"," ");
		    entity = entity.trim();
		    entities.add(entity);
//		    System.out.println("\"" + entity +  "\"");

		}
		
		
		
		OutputStreamWriter outPredictions = new OutputStreamWriter(new FileOutputStream("json/entities.json"), StandardCharsets.UTF_8);
		outPredictions.write("[");
		for(String s: entities) {
		    String result = java.net.URLDecoder.decode(s, StandardCharsets.UTF_8.name());

			System.out.println(result);
			outPredictions.write("\""+result+"\", ");
		}
		outPredictions.write("]");
		outPredictions.flush();
		outPredictions.close();
		
//		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:8890/occupy", sparqlquery);
//		//after it goes standard query execution and result processing which can
//		// be found in almost any Jena/SPARQL tutorial.
//	   ResultSet results = null; 
//		try {
//		    results = qexec.execSelect();
//		}catch(Exception e) {
//			System.out.println(e);
//		}
//		 while(results.hasNext() ) {
//		   	QuerySolution soln = results.nextSolution() ;
//            String x = soln.get("entity").toString();
////   		    pw.println("<br>");
////   		    pw.println("<br><a>"+x+"</a>");
//                System.out.print(x +"\n");
//		
//		 }
//		   qexec.close();
		
	}
	
	
	
	
}
