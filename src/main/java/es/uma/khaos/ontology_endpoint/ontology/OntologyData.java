package es.uma.khaos.ontology_endpoint.ontology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OntologyData {
	
	private Set<String> classes;
	
	private Set<String> properties;
	
	private Map<String, Set<String>> domains;
	
	private Map<String, Set<String>> ranges;
	
	public OntologyData() {
		classes = new HashSet<String>();
		properties = new HashSet<String>();
		domains = new HashMap<String, Set<String>>();
		ranges = new HashMap<String, Set<String>>();
	}

	public Set<String> getClasses() {
		return classes;
	}

	public Set<String> getProperties() {
		return properties;
	}

	public Map<String, Set<String>> getDomains() {
		return domains;
	}

	public Map<String, Set<String>> getRanges() {
		return ranges;
	}
	
	public void addClass(String class_) {
		classes.add(class_);
	}
	
	public void addProperty(String property) {
		properties.add(property);
	}
	
	public void addDomain(String property, String class_) {
		if (!domains.containsKey(property)) {
			domains.put(property, new HashSet<String>());
		}
		domains.get(property).add(class_);
	}
	
	public void addRange(String property, String class_) {
		if (!ranges.containsKey(property)) {
			ranges.put(property, new HashSet<String>());
		}
		ranges.get(property).add(class_);
	}
	
}
