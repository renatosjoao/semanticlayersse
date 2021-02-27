package org.home;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryBuilder {
	   private String startDate;
	    private String endDate;
	    private ArrayList entities;
	    private Semantics semantics;
	    private int confidence;
	    private HashMap<Integer, Double> confidenceScoreMatching;

	    private StringBuilder queryBuilder;

	    private final String PREFIX = "PREFIX oa: <http://www.w3.org/ns/oa#> \n"
	            + "PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> \n"
	            + "PREFIX dc: <http://purl.org/dc/terms/> \n"
	            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
	            + "PREFIX tw: <http://www.openlinksw.com/schemas/twitter#> \n"
	            + "PREFIX schema: <http://schema.org/> \n"
	            + "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
	            + "PREFIX yago: <http://dbpedia.org/class/yago/> \n"
	            + "PREFIX dbr: <http://dbpedia.org/resource/> \n"
	            + "PREFIX owa: <http://l3s.de/owa#> \n\n";

	    public QueryBuilder(String startDate, String endDate, ArrayList entities, Semantics semantics, int confidence) {
	        this.startDate = startDate;
	        this.endDate = endDate;
	        this.entities = entities;
	        this.semantics = semantics;
	        this.confidence = confidence; // FEL values from -4.0 to -0.0

	        confidenceScoreMatching = new HashMap<>();
	        confidenceScoreMatching.put(1, -4.0);
	        confidenceScoreMatching.put(2, -3.5);
	        confidenceScoreMatching.put(3, -3.0);
	        confidenceScoreMatching.put(4, -2.5);
	        confidenceScoreMatching.put(5, -2.0);
	        confidenceScoreMatching.put(6, -1.5);
	        confidenceScoreMatching.put(7, -1.0);
	        confidenceScoreMatching.put(8, -0.5);
	        confidenceScoreMatching.put(9, -0.0);
	    }

	    public void createQuery() {
	        queryBuilder = new StringBuilder(PREFIX);
	        queryBuilder.append("SELECT * WHERE { \n"); 
	        //?version ?title ?date ?document ?documentFirstCapture ?documentLastCapture ?documentNumOfCaptures ?detectedAs ?position ?confidence

	        queryBuilder.append(" ?version dc:date ?date FILTER(?date >= \"").append(startDate).append("-01\"^^xsd:date && ?date <= \"").append(endDate).append("-31\"^^xsd:date) . \n");
	        
	        if (entities.size() == 1 || semantics == Semantics.AND) {
	            for (int i = 0; i < entities.size(); i++) {
	                queryBuilder.append(" ?version schema:mentions ?entity").append(i).append(" . \n");

	                if (confidence == 1) {
	                    queryBuilder.append(" ?entity").append(i).append(" oae:hasMatchedURI ").append("dbr:").append(entities.get(i)).append(" . \n");
	                    queryBuilder.append(" ?entity").append(i).append(" oae:confidence ?confidence").append(i).append(" ; oae:detectedAs ?detectedAs ; oae:position ?position . \n");
	                } else {
	                    queryBuilder.append(" ?entity").append(i).append(" oae:confidence ?confidence").append(i).append(" FILTER (?confidence").append(i).append(" > ").append(confidenceScoreMatching.get(confidence)).append(") . \n");
	                    queryBuilder.append(" ?entity").append(i).append(" oae:hasMatchedURI ").append("dbr:").append(entities.get(i)).append(" . \n");
	                    queryBuilder.append(" ?entity").append(i).append(" oae:detectedAs ?detectedAs").append(i).append(" ; oae:position ?position").append(i).append(" . \n");
	                }

	            }
	        } else {
	            for (int i = 0; i < entities.size(); i++) {
	                queryBuilder.append(" { \n   ?version schema:mentions ?entity").append(" . \n");
	                if (confidence == 1) {
	                    queryBuilder.append("   ?entity").append(" oae:hasMatchedURI ").append("dbr:").append(entities.get(i)).append(" . \n");
	                    queryBuilder.append("   ?entity").append(" oae:confidence ?confidence ; oae:detectedAs ?detectedAs ; oae:position ?position \n } \n");
	                } else {
	                    queryBuilder.append("   ?entity").append(" oae:confidence ?confidence FILTER (?confidence > ").append(confidenceScoreMatching.get(confidence)).append(") . \n");
	                    queryBuilder.append("   ?entity").append(" oae:hasMatchedURI ").append("dbr:").append(entities.get(i)).append(" . \n");
	                    queryBuilder.append("   ?entity").append(" oae:detectedAs ?detectedAs ; oae:position ?position \n } \n");
	                }

	                if (i != entities.size() - 1) {
	                    queryBuilder.append(" UNION \n");
	                }
	            }
	        }

	        queryBuilder.append(" ?version dc:title ?title . \n");
	        queryBuilder.append(" ?document dc:hasVersion ?version . \n");
	        queryBuilder.append(" ?document owa:firstCapture ?documentFirstCapture ; owa:lastCapture ?documentLastCapture ; owa:numOfCaptures ?documentNumOfCaptures . \n");

//	        queryBuilder.append(" ?version a owa:VersionedDocument . \n");
//	        queryBuilder.append(" ?document a owa:ArchivedDocument . \n");
	        queryBuilder.append("}");

	    }

	    public String getStartDate() {
	        return startDate;
	    }

	    public void setStartDate(String startDate) {
	        this.startDate = startDate;
	    }

	    public String getEndDate() {
	        return endDate;
	    }

	    public void setEndDate(String endDate) {
	        this.endDate = endDate;
	    }

	    public ArrayList getEntities() {
	        return entities;
	    }

	    public void setEntities(ArrayList entities) {
	        this.entities = entities;
	    }

	    public Semantics getSemantics() {
	        return semantics;
	    }

	    public void setSemantics(Semantics semantics) {
	        this.semantics = semantics;
	    }

	    public int getConfidence() {
	        return confidence;
	    }

	    public void setConfidence(int confidence) {
	        this.confidence = confidence;
	    }

	    public StringBuilder getQueryBuilder() {
	        return queryBuilder;
	    }

	    public void setQueryBuilder(StringBuilder queryBuilder) {
	        this.queryBuilder = queryBuilder;
	    }

	    public static void main(String args[]) {
	        ArrayList<String> entities = new ArrayList<>();
	        entities.add("Barack_Obama");
	        entities.add("Hillary_Clintion");
	        QueryBuilder q = new QueryBuilder("2012-01", "2017-10", entities, Semantics.OR, 3);
	        q.createQuery();
	        System.out.println(q.getQueryBuilder());
	    }
	    

	    
// Listing 2 	    
//SELECT ?article ?title ?date ?nylawyer ?bdate ?abstr WHERE {
//SERVICE <http://dbpedia.org/sparql> {
//?nylawyer dc:subject dbc:New_York_lawyers ;
//dbo:birthPlace dbr:Brooklyn .
//OPTIONAL {
//?nylawyer dbo:birthDate ?bdate ;
//dbo:abstract ?abstr FILTER(lang(?abstr)="fr")}}
//?article schema:mentions ?entity .
//?entity oae:hasMAtchedURI ?nylawyer .
//?article dc?title ?title
//}ORDER BY ?nylawyer
//					
	
//Listing 3
//SELECT DISTINCT ?tweet ?count ?date ?entityUri WHERE {
//	SERVICE <http://dbpedia.org/sparql> {
//		?entityUri dc:subject dbc:Los_Angeles_Lakers_players}
//}t a tw:Tweet ;
//dc:date ?date FILTER(?date>="2016-06-01"^^xsd:dateTime && ?date<="2016-08-31"^^xsd:dateTime)
//		?t tw:retweetCount ?count FILTER (?count >50).
//				?t schema:text ?tweet ; schema:mentions ?entity .
//						?entity oae:hasMAtchedURI ?entityUri }
	    
	    
//Listing 4
//SELECT DISTINCT ?player ?tweet WHERE {
//SERVICE <http://dbpedia.org/sparql> {    
// ?player dc:subject dbc:Los_Angeles_Lakers_players}
// ?article dc:date ?date FILTER(?date>="2016-06-01"^^xsd:date 
//	    && ?date<="2016-08-31"^^xsd:date)
//?article schema:mentions ?articleEntity .
//?articleEntity oae:hasMatchedURI ?player .
//?tweet schema:mentions ?tweetEntity .
//?tweetEntity oae:hasMatchedURI ?player }
//


// Listing 5 	    
//SELECT ?journ (COUNT(DISTINCT ?page) AS ?num)  WHERE {
//	SERVICE <http://dbpedia.org/sparql> {
//	    ?journ a yago:Journalist110224578}
//?page a owa:ArchivedDocument ;
//	    dc:hasVersion ?version .
//?version schema:mentions ?entity .
//?entity oae:hasMatchedURI ?journ .
//}GROUP BY ?journ ORDER BY DESC(?num)
//			
	    
//Listing 6 
//SELECT ?year (COUNT(DISTICT ?article) AS ?num) WHERE {
// ?article dc:date ?date ;
// schema:mentions ?entity .
// ?entity oae:hasMatchedURI dbr:Nelson_Mandela
// GROUP BY (year(?date) AS ?year) order by ?year
	    
	    

//Listing 7 
//SELECT ?drug (COUNT(DISTICT ?article) AS ?numOfArticles) WHERE {
//		SERVICE <http://dbpedia.org/sparql> {
// 		?drug a dbo:Drug }
// ?article dc:date ?date FILTER(year(?date) = "1987") .
// ?article schema:mentions ?ent .
// ?ent oae:hasMatchedURI ?drug .	    
// GROUP BY ?drug ORDER BY DESC(?numOfArticles)
//}
	    

//Listing 8 
//SELECT ?politician (COUNT(DISTICT ?article) AS ?num) WHERE {
//SERVICE <http://dbpedia.org/sparql> {
//?politician a dbo:Politician }   
//?article dc:date ?date FILTER(?date >= "2007-06-01"^^xsd:date && ?date <= "2007-08-30"^^xsd:date) .
	  // ?article schema:mentions ?entity .
	  // ?entity oae:hasMatchedURI dbr:Barack_Obama .
	  // ?article schema:mentions ?entityPolit .
      // ?entityPolit oae:hasMatchedURI ?politician FILTER(?politician != dbr:Barack_Obama)}	    
	  // GROUP BY ?politician ORDER BY DESC(?num) LIMIT 5

//Listing 9
//SELECT ?month xsd:double(?cEnt)/xsd:double(?cAll)
//WHERE {
//{SELECT(month(?date) AS ?month) (count(?tweet) AS ?cAll) WHERE {
//?tweet dc:date ?date FILTER(year(?date) = 2016)
//}GROUP BY month(?date)}
//{SELECT (month(?date) AS ?month) (COUNT(?tweet) AS ?cEnt) WHERE {
//	?tweet dc:date ?date FILTER(year(?date) = 2016) .
//	?tweet schema:mentions ?entity .
//	?entity oae:hasMatchedURI dbr:Barack_Obama
//}GROUP BY month(?date)}
//}ORDER BY ?month
//	    		

//Listing 10
//SELECT ?article2 (count?(?entUri2) as ?numOfCommon) WHERE {
//nyt:9504E4D71530F932A35755C0A9619C8B63 schema:mentions ?entity1 .
//?entity1 oae:hasMatchedURI ?entUri1 .
//?article2 schema:mentions ?entity2
//FILTER (?article2 != nyt:9504E4D71530F932A35755C0A9619C8B63 )
//?entity2 oae:hasMatchedURI ?entUri2 FILTER(?entUri2 = ?entUri1 ) .
// GROUP BY ?erticle2 ORDER BY DESC(?numOfCommon) LIMIT 5

	    
}




/**
 * 
 * -------------------+---------
 * Resource (overall)	4,233,000
 * -------------------+---------
	Place	735,000
	Person	1,450,000
	Work	411,000
	Species	251,000
	Organisation	241,000
	--------------------------
 */


/**
 * 
 * DBPedia prefixes
 * 
 *  * -------------------+---------
 *  Prefix	URI
 *  * -------------------+---------	
 *  bif	bif:
	category-en	http://dbpedia.org/resource/Category:
	category-eo	http://eo.dbpedia.org/resource/Kategorio:
	dawgt	http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#
	dbpedia	http://dbpedia.org/resource/
	dbpedia-cs	http://cs.dbpedia.org/resource/
	dbpedia-de	http://de.dbpedia.org/resource/
	dbpedia-el	http://el.dbpedia.org/resource/
	dbpedia-eo	http://eo.dbpedia.org/resource/
	dbpedia-es	http://es.dbpedia.org/resource/
	dbpedia-fr	http://fr.dbpedia.org/resource/
	dbpedia-it	http://it.dbpedia.org/resource/
	dbpedia-ja	http://ja.dbpedia.org/resource/
	dbpedia-ko	http://ko.dbpedia.org/resource/
	dbpedia-nl	http://nl.dbpedia.org/resource/
	dbpedia-owl	http://dbpedia.org/ontology/
	dbpedia-pl	http://pl.dbpedia.org/resource/
	dbpedia-pt	http://pt.dbpedia.org/resource/
	dbpedia-ru	http://ru.dbpedia.org/resource/
	dbpprop	http://dbpedia.org/property/
	dc	http://purl.org/dc/elements/1.1/
	dcterms	http://purl.org/dc/terms/
	fn	http://www.w3.org/2005/xpath-functions/#
	foaf	http://xmlns.com/foaf/0.1/
	freebase	http://rdf.freebase.com/ns/
	geo	http://www.w3.org/2003/01/geo/wgs84_pos#
	geonames	http://www.geonames.org/ontology#
	georss	http://www.georss.org/georss/
	go	http://purl.org/obo/owl/GO#
	ldp	http://www.w3.org/ns/ldp#
	math	http://www.w3.org/2000/10/swap/math#
	mesh	http://purl.org/commons/record/mesh/
	mf	http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#
	nci	http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#
	obo	http://www.geneontology.org/formats/oboInOwl#
	ogc	http://www.opengis.net/
	ogcgml	http://www.opengis.net/ont/gml#
	ogcgs	http://www.opengis.net/ont/geosparql#
	ogcgsf	http://www.opengis.net/def/function/geosparql/
	ogcgsr	http://www.opengis.net/def/rule/geosparql/
	ogcsf	http://www.opengis.net/ont/sf#
	opencyc	http://sw.opencyc.org/2008/06/10/concept/
	owl	http://www.w3.org/2002/07/owl#
	product	http://www.buy.com/rss/module/productV2/
	prop-eo	http://eo.dbpedia.org/property/
	protseq	http://purl.org/science/protein/bysequence/
	rdf	http://www.w3.org/1999/02/22-rdf-syntax-ns#
	rdfa	http://www.w3.org/ns/rdfa#
	rdfdf	http://www.openlinksw.com/virtrdf-data-formats#
	rdfs	http://www.w3.org/2000/01/rdf-schema#
	sc	http://purl.org/science/owl/sciencecommons/
	scovo	http://purl.org/NET/scovo#
	sd	http://www.w3.org/ns/sparql-service-description#
	sioc	http://rdfs.org/sioc/ns#
	skos	http://www.w3.org/2004/02/skos/core#
	sql	sql:
	template-en	http://dbpedia.org/resource/Template:
	template-eo	http://eo.dbpedia.org/resource/Template:
	umbel-ac	http://umbel.org/umbel/ac/
	umbel-sc	http://umbel.org/umbel/sc/
	units	http://dbpedia.org/units/
	vcard	http://www.w3.org/2001/vcard-rdf/3.0#
	vcard2006	http://www.w3.org/2006/vcard/ns#
	virtcxml	http://www.openlinksw.com/schemas/virtcxml#
	virtrdf	http://www.openlinksw.com/schemas/virtrdf#
	void	http://rdfs.org/ns/void#
	wiki-en	http://en.wikipedia.org/wiki/
	wiki-eo	http://eo.wikipedia.org/wiki/
	wikicompany	http://dbpedia.openlinksw.com/wikicompany/
	xf	http://www.w3.org/2004/07/xpath-functions
	xml	http://www.w3.org/XML/1998/namespace
	xsd	http://www.w3.org/2001/XMLSchema#
	xsl10	http://www.w3.org/XSL/Transform/1.0
	xsl1999	http://www.w3.org/1999/XSL/Transform
	xslwd	http://www.w3.org/TR/WD-xsl
	yago	http://dbpedia.org/class/yago/
	yago-res	http://mpii.de/yago/resource/
 	*/
