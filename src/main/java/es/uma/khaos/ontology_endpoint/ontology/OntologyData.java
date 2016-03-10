package es.uma.khaos.ontology_endpoint.ontology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OntologyData {
	
	private Set<String> classes;
	
	private Set<String> properties;
	
	private Set<String> objectProperties;
	
	private Set<String> dataProperties;
	
	private Map<String, Set<String>> domains;
	
	private Map<String, Set<String>> ranges;
	
	private Set<String> datatypes;// Create set of data types
	
	public OntologyData() {
		classes = new HashSet<String>();
		properties = new HashSet<String>();
		domains = new HashMap<String, Set<String>>();
		ranges = new HashMap<String, Set<String>>();
		datatypes = new HashSet<String>(); //HashSet for data types
		objectProperties = new HashSet<String>();
		dataProperties = new HashSet<String>();
		
	}

	public Set<String> getClasses() {
		return classes;
	}

	public Set<String> getProperties() {
		return properties;
	}

	public Set<String> getObjectproperties() {
		return objectProperties;
	}

	public Set<String> getDataproperties() {
		return dataProperties;
	}

	public Map<String, Set<String>> getDomains() {
		return domains;
	}
	
	public Map<String, Set<String>> getRanges() {
		return ranges;
	}

	public Set<String> getDatatype() {
		return datatypes;
	}
	
	public Set<String> getDomain(String property) {
		if (domains.containsKey(property))
			return domains.get(property);
		else
			return new HashSet<String>();
	}
	
	public Set<String> getRange(String property) {
		if (ranges.containsKey(property))
			return ranges.get(property);
		else
			return new HashSet<String>();
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
	
	public void addRange(String property, String range) {
		if (!ranges.containsKey(property)) {
			ranges.put(property, new HashSet<String>());
		}
		ranges.get(property).add(range);
	}
	
	public void addDataType(String datatype_) {
		datatypes.add(datatype_);
	}
	
	public void addDataProperty(String dataproperty_) {
		dataProperties.add(dataproperty_);
	}
	
	public void addObjectProperty(String objectproperty_) {
		objectProperties.add(objectproperty_);
	}
	
	public Set<String> getPropertiesFromClass(String class_) {
		Set<String> res = new HashSet<String>();
		for (String property : domains.keySet()) {
			if (domains.get(property).contains(class_)) {
				res.add(property);
			}
		}
		return res;
	}
	
	public Set<String> getPropertiesToClass(String class_) {
		Set<String> res = new HashSet<String>();
		for (String property : ranges.keySet()) {
			if (ranges.get(property).contains(class_)) {
				res.add(property);
			}
		}
		return res;
	}
	
}
