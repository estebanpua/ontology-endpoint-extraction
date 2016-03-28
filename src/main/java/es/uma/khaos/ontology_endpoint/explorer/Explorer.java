package es.uma.khaos.ontology_endpoint.explorer;

import java.io.PrintStream;
import java.util.ArrayList;
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
	private int maxRetries = Integer.valueOf(Constants.QUERY_MAX_RETRIES);
	private int coolDown = Integer.valueOf(Constants.QUERY_COOLDOWN);
	private int limit = Integer.valueOf(Constants.QUERY_LIMIT);
	
	private final String limitQuerySufix = " LIMIT %d OFFSET %d";
	
	public Explorer(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public Explorer(String endpoint, String graph) {
		this(endpoint);
		this.graph = graph;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}
	
	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	private List<QuerySolution> executeQuery(String queryString, int offset) {
		queryString += String.format(limitQuerySufix, limit, offset);
		System.out.println("EXECUTING QUERY: " + queryString);
		int count = 0;
		while(count<maxRetries) {
			try {
				if (graph!=null)
					return SPARQLExecution.executeSelect(endpoint, queryString, graph);
				else 
					return SPARQLExecution.executeSelect(endpoint, queryString);
			} catch (Exception e) {
				e.printStackTrace();
				count++;
			}
			try { Thread.sleep(coolDown); } catch (InterruptedException e) {};
		}
		return new ArrayList<QuerySolution>();
	}
	
	@SuppressWarnings("unused")
	private List<QuerySolution> executeTimeoutQuery(String queryString, int offset) {
		queryString += String.format(limitQuerySufix, limit, offset);
		if (graph!=null)
			return SPARQLExecution.executeSelect(endpoint, queryString, graph, timeout);
		else 
			return SPARQLExecution.executeSelect(endpoint, queryString, timeout);
	}
	
	public List<QuerySolution> getClasses(int offset) {
		return executeQuery(Constants.CLASS_QUERY, offset);
	}
	
	public List<QuerySolution> getProperties(int offset) {
		return executeQuery(Constants.PROPERTY_QUERY, offset);
	}
	
	public List<QuerySolution> getDomainsFromProperty(String property, int offset) {
		return executeQuery(String.format(Constants.DOMAIN_QUERY, property), offset);
	}
	
	public List<QuerySolution> getRangesFromProperty(String property, int offset) {
		return executeQuery(String.format(Constants.RANGE_QUERY, property), offset);
	}
	
	public List<QuerySolution> getDataTypeFromProperty(String property, int offset) {
		return executeQuery(String.format(Constants.DATA_TYPE_QUERY, property), offset);
	}
	
	public OntologyData execute() {
		return execute(System.out);
	}
	
	public OntologyData execute(PrintStream ps) {
		
		System.setProperty("http.proxyHost", "proxy.uma.es");
        System.setProperty("http.proxyPort", "3128");
		
		int offset;
		OntologyData endpointOntology = new OntologyData();
		List<QuerySolution> list;
		
		offset = 0;
		do {
			list = getClasses(offset);
			for (QuerySolution qs : list) {
				String classUri = qs.getResource(Constants.CLASS_VAR).getURI();
				endpointOntology.addClass(classUri);
			}
			offset += limit;
			ps.println(endpointOntology.getClasses().size() + " classes obtained.");
		} while (list.size() == limit);
		
		offset = 0;
		do {
			list = getProperties(offset);
			for (QuerySolution qs : list) {
				String propertyUri = qs.getResource(Constants.PROPERTY_VAR).getURI();
				endpointOntology.addProperty(propertyUri);
			}
			offset += limit;
			ps.println(list.size() + " properties found.");
		} while (list.size() == limit);
		
		for (String propertyUri : endpointOntology.getProperties()) {
			offset = 0;
			do {
				list = getDomainsFromProperty(propertyUri, offset);
				for (QuerySolution qs : list) {
					String domainUri = qs.getResource(Constants.DOMAIN_VAR).getURI();
					endpointOntology.addDomain(propertyUri, domainUri);
					endpointOntology.addClass(domainUri);
				}
				offset += limit;
				ps.println(list.size()
						+ " domains obtained for "+propertyUri+".");
			} while (list.size() == limit);
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			offset = 0;
			do {
				list = getRangesFromProperty(propertyUri, offset);
				for (QuerySolution qs : list) {
					String rangeUri = qs.getResource(Constants.RANGE_VAR).getURI();
					endpointOntology.addRange(propertyUri, rangeUri);
					endpointOntology.addClass(rangeUri);
				}
				offset += limit;
				ps.println(list.size()
						+ " ranges obtained for "+propertyUri+".");
			} while (list.size() == limit);
		}
		
		for (String propertyUri : endpointOntology.getProperties()) {
			offset = 0;
			do {
				list = getDataTypeFromProperty(propertyUri, offset);
				for (QuerySolution qs : list) {
					Resource resource = qs.getResource(Constants.DATA_TYPE_VAR);
					if (resource!=null) {
						String rangeUri = resource.getURI();
						endpointOntology.addRange(propertyUri, rangeUri);
						endpointOntology.addDataType(rangeUri);
					}
					
				}
				offset += limit;
				ps.println(list.size()
						+ " datatypes obtained for "+propertyUri+".");
			} while (list.size() == limit);
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
