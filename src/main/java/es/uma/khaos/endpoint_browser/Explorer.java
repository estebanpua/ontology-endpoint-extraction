package es.uma.khaos.endpoint_browser;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;


public class Explorer {
	
	private final String CLASS_QUERY =
			"select distinct ?class "
			+ "where {"
			+ "?s a ?class"
			+ "}";
	
	private final String PROPERTY_QUERY =
			"select distinct ?property "
			+ "where {"
			+ "?s ?property ?o"
			+ "}";
	
	private final String DOMAIN_QUERY =
			"select distinct ?domain "
			+ "where {"
			+ "?o <%s> ?range."
			+ "?o a ?domain."
			+ "}";
	
	private final String RANGE_QUERY =
			"select distinct ?range "
			+ "where {"
			+ "?domain <%s> ?o."
			+ "?o a ?range."
			+ "}";
	
	
	private final String endpoint;
	private final String graph;
	private final int timeout = 60000;
	
	public Explorer(String endpoint) {
		this.endpoint = endpoint;
		this.graph = null;
	}
	
	public Explorer(String endpoint, String graph) {
		this.endpoint = endpoint;
		this.graph = graph;
	}
	
	private List<QuerySolution> executeQuery(String queryString) {
		if (graph!=null)
			return SPARQLExecution.executeSelect(endpoint, queryString, graph);
		else 
			return SPARQLExecution.executeSelect(endpoint, queryString);
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
		return executeQuery(CLASS_QUERY);
	}
	
	public List<QuerySolution> getProperties() {
		return executeQuery(PROPERTY_QUERY);
	}
	
	public List<QuerySolution> getDomainsFromProperty(String property) {
		return executeQuery(String.format(DOMAIN_QUERY, property));
	}
	
	public List<QuerySolution> getRangesFromProperty(String property) {
		return executeQuery(String.format(RANGE_QUERY, property));
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
	
	public EndpointOntology buildOntologyFromEndpoint() {
		
		EndpointOntology endpointOntology = new EndpointOntology();
		List<QuerySolution> list;
		
		list = getClasses();
		for (QuerySolution qs : list) {
			String classUri = qs.getResource("class").getURI();
			endpointOntology.addClass(classUri);
		}
		System.out.println(list.size() + " clases obtenidas.");
		
		list = getProperties();
		for (QuerySolution qs : list) {
			String propertyUri = qs.getResource("property").getURI();
			endpointOntology.addProperty(propertyUri);
		}
		System.out.println(list.size() + " propiedades encontradas.");
		
		for (String propertyUri : endpointOntology.getProperties()) {
			list = getDomainsFromProperty(propertyUri);
			for (QuerySolution qs : list) {
				String domainUri = qs.getResource("domain").getURI();
				endpointOntology.addDomain(propertyUri, domainUri);
			}
			System.out.println(list.size()
					+ " dominios obtenidos para "+propertyUri+".");
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			list = getRangesFromProperty(propertyUri);
			for (QuerySolution qs : list) {
				String rangeUri = qs.getResource("range").getURI();
				endpointOntology.addRange(propertyUri, rangeUri);
			}
			System.out.println(list.size()
					+ " rangos obtenidos para "+propertyUri+".");
		}
		
		return endpointOntology;
		
	}

}
