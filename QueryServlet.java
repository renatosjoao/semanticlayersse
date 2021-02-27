package org.home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	
	protected void executeQuery() {

		/**
		 * Executes a SPARQL query against a virtuoso url and prints results.
		 */
//			try {
//				Connection conn = DriverManager.getConnection("jdbc:virtuoso://localhost:1111/", "dba", "root");
//				} catch (SQLException ex) {
//				ex.printStackTrace();
//			}
			Query sparqlquery = QueryFactory.create(""+
			"	PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
			"	PREFIX res: <http://dbpedia.org/resource/> \n" +
			"	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
			"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
			"	SELECT DISTINCT ?uri ?string  \n" +
			"	WHERE { \n" +
			"		?uri rdf:type dbo:Film . \n" +
			"	        ?uri dbo:starring res:Julia_Roberts . \n" +
			"	        ?uri dbo:starring res:Richard_Gere. \n" +
			"		OPTIONAL {?uri rdfs:label ?string . FILTER (lang(?string) = 'en') } \n" +
			"	}				 ");
			

	/*			STEP 1			*/
//			VirtGraph set = new VirtGraph ("jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2", "dba", "root");

	/*			STEP 3			*/
	/*		Select all data in virtuoso	*/
//			Query sparql = QueryFactory.create(""+
//					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
//					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
//					"prefix oae: <http://www.ics.forth.gr/isl/oae/core#> \n" + 
//					"prefix schema: <http://schema.org/> \n" + 
//					"prefix dc: <http://purl.org/dc/terms/> \n" + 
//					"prefix twitter: <http://www.openlinksw.com/schemas/twitter#> \n" + 
//					"\n" + 
//					"\n" + 
//					"SELECT DISTINCT ?tweet \n"+
//					"    FROM  <http://localhost:8890/tweets> WHERE { \n" +
//					"	?tweet dc:creator \"Newsday\" .\n" + 
//					"	?tweet schema:mentions ?entity .\n" + 
//					"	?entity oae:detectedAs \"powerball\" .} ");
//				
				//+ "SELECT ?s ?p ?o FROM <http://localhost:8890/tweets> { ?s ?p ?o }  limit 100");
	//
	///*			STEP 4			*/
//			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);
	////

//			ResultSet results = vqe.execSelect();
//			while (results.hasNext()) {
//				QuerySolution result = results.nextSolution();
//			    RDFNode s = result.get("tweet");
//			    RDFNode p = result.get("p");
//			    RDFNode o = result.get("o");
//			    System.out.println(" { " + s + " " + p+  " " + o +" . }");
//			}
			
			
			
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", sparqlquery);
			//after it goes standard query execution and result processing which can
			// be found in almost any Jena/SPARQL tutorial.
			try {
			    ResultSet results = qexec.execSelect();
			    //ResultSetFormatter.outputAsCSV(results);
			    while(results.hasNext() ) {

			    	QuerySolution soln = results.nextSolution() ;
		            String x = soln.get("uri").toString();
		            String y = soln.get("string").toString();
		            System.out.print(x +'\t'+ y +"\n");

//			    }
			    }
			}
			finally {
			   qexec.close();
			}
	}

}
