package es.uma.khaos.ontology_endpoint;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class SPARQLExecution {
	
	public static List<QuerySolution> executeSelect(String endpoint, String queryString) {
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint, query);
	    ResultSet results =  qe.execSelect();
	    
	    List<QuerySolution> resultList = ResultSetFormatter.toList(results);

	    qe.close();

	    return resultList;
		
	}
	
	public static List<QuerySolution> executeSelect(String endpoint, String queryString, String graph) {
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint, query, graph);
	    ResultSet results =  qe.execSelect();
	    
	    List<QuerySolution> resultList = ResultSetFormatter.toList(results);

	    qe.close();

	    return resultList;
		
	}
	
	public static List<QuerySolution> executeSelect(
			String endpoint, String queryString, int timeout) {
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint, query);
		qe.setTimeout(timeout);
	    ResultSet results =  qe.execSelect();
	    
	    List<QuerySolution> resultList = ResultSetFormatter.toList(results);

	    qe.close();

	    return resultList;
		
	}
	
	public static List<QuerySolution> executeSelect(
			String endpoint, String queryString, String graph, int timeout) {
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint, query, graph);
		qe.setTimeout(timeout);
	    ResultSet results =  qe.execSelect();
	    
	    List<QuerySolution> resultList = ResultSetFormatter.toList(results);
	    

	    qe.close();

	    return resultList;
		
	}

}
