package es.uma.khaos.ontology_endpoint.explorer;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;

import es.uma.khaos.ontology_endpoint.config.Constants;
import es.uma.khaos.ontology_endpoint.ontology.OntologyData;
import es.uma.khaos.ontology_endpoint.sparql.SPARQLExecution;

public class Explorer {
	
	private final String endpoint;
	private String graph = null;
	
	private final int timeout = Integer.valueOf(Constants.QUERY_TIMEOUT);
	private final int maxRetries = Integer.valueOf(Constants.QUERY_MAX_RETRIES);
	private final int coolDown = Integer.valueOf(Constants.QUERY_COOLDOWN);
	
	public Explorer(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public Explorer(String endpoint, String graph) {
		this(endpoint);
		this.graph = graph;
	}
	
	public void setGraph(String graph) {
		this.graph = graph;
	}
	
	private List<QuerySolution> executeQuery(String queryString) {
		int count = 0;
		while(true) {
			try {
				if (graph!=null)
					return SPARQLExecution.executeSelect(endpoint, queryString, graph);
				else 
					return SPARQLExecution.executeSelect(endpoint, queryString);
			} catch (Exception e) {
				e.printStackTrace();
				if (++count == maxRetries) throw e;
			}
			try { Thread.sleep(coolDown); } catch (InterruptedException e) {};
		}
	}
	
	@SuppressWarnings("unused")
	private List<QuerySolution> executeTimeoutQuery(String queryString) {
		if (graph!=null)
			return SPARQLExecution.executeSelect(endpoint, queryString, graph, timeout);
		else 
			return SPARQLExecution.executeSelect(endpoint, queryString, timeout);
	}
	
//	private List<String> getListFromQuerySolution(List<QuerySolution> list, String object) {
//		List<String> res = new ArrayList<String>();
//		for (QuerySolution qs : list) {
//			String classUri = qs.getResource(object).getURI();
//			endpointOntology.addClass(classUri);
//	}
	
	public List<QuerySolution> getClasses() {
		return executeQuery(Constants.CLASS_QUERY);
	}
	
	public List<QuerySolution> getProperties() {
		return executeQuery(Constants.PROPERTY_QUERY);
	}
	
	public List<QuerySolution> getDomainsFromProperty(String property) {
		return executeQuery(String.format(Constants.DOMAIN_QUERY, property));
	}
	
	public List<QuerySolution> getRangesFromProperty(String property) {
		return executeQuery(String.format(Constants.RANGE_QUERY, property));
	}
	
	public List<QuerySolution> getDataTypeFromProperty(String property) {
		return executeQuery(String.format(Constants.DATA_TYPE_QUERY, property));
	}
	
	/*
	public List<QuerySolution> getPredicatesFromClass(String classUri, int limit) {
		String queryString =
				"select distinct ?p "
				+ "where {"
				+ "?s a <" + classUri +"> ."
				+ "?s ?p []"
				+ "} LIMIT " + limit;
		//System.out.println(queryString);
		//return SPARQLExecution.executeSelect(endpoint, queryString);
		return SPARQLExecution.executeSelect(endpoint, queryString, graph, timeout);
 	}
	
	public getInstancesFromClass(String classUri, int limit) {
		String queryString =
				"select distinct ?Concept "
				+ "where {"
				+ "[] a ?Concept"
				+ "}";
		
		return SPARQLExecution.executeSelect(endpoint, queryString, graph);
	}
	*/
	
	public OntologyData execute() {
		return execute(System.out);
	}
	
	public OntologyData execute(PrintStream ps) {
		
		OntologyData endpointOntology = new OntologyData();
		List<QuerySolution> list;
		
		list = getClasses();
		for (QuerySolution qs : list) {
			String classUri = qs.getResource(Constants.CLASS_VAR).getURI();
			endpointOntology.addClass(classUri);
		}
		ps.println(list.size() + " classes obtained.");
		
		list = getProperties();
		for (QuerySolution qs : list) {
			String propertyUri = qs.getResource(Constants.PROPERTY_VAR).getURI();
			endpointOntology.addProperty(propertyUri);
		}
		ps.println(list.size() + " properties found.");
		
		for (String propertyUri : endpointOntology.getProperties()) {
			list = getDomainsFromProperty(propertyUri);
			for (QuerySolution qs : list) {
				String domainUri = qs.getResource(Constants.DOMAIN_VAR).getURI();
				endpointOntology.addDomain(propertyUri, domainUri);
			}
			ps.println(list.size()
					+ " domains obtained for "+propertyUri+".");
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			list = getRangesFromProperty(propertyUri);
			for (QuerySolution qs : list) {
				String rangeUri = qs.getResource(Constants.RANGE_VAR).getURI();
				endpointOntology.addRange(propertyUri, rangeUri);
			}
			ps.println(list.size()
					+ " ranges obtained for "+propertyUri+".");
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			list = getDataTypeFromProperty(propertyUri);
			for (QuerySolution qs : list) {
				Resource resource = qs.getResource(Constants.DATA_TYPE_VAR);
				if (resource!=null) {
					String rangeUri = resource.getURI();
					endpointOntology.addRange(propertyUri, rangeUri);
					endpointOntology.addDataType(rangeUri);
				}
				
			}
			ps.println(list.size()
					+ " datatypes obtained for "+propertyUri+".");
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			
			Set<String> ranges = endpointOntology.getRange(propertyUri);
			
			for (String range : ranges) {
				
				if (endpointOntology.getClasses().contains(range)) {
					endpointOntology.addObjectProperty(propertyUri);
				}
				
				if (endpointOntology.getDatatype().contains(range)) {
					endpointOntology.addDataProperty(propertyUri);
				}
			}
		}
		
		return endpointOntology;
		
	}

}
