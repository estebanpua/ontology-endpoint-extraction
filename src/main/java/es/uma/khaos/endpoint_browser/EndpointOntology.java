package es.uma.khaos.endpoint_browser;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class EndpointOntology {
	
	private Set<String> classes;
	
	private Set<String> properties;
	
	private Map<String, Set<String>> domains;
	
	private Map<String, Set<String>> ranges;
	
	public EndpointOntology() {
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
	
	public void print(PrintStream ps) {
		ps.println("Classes:");
		for (String class_ : classes) {
			ps.println("\t"+class_);
		}
		ps.println();
		
		ps.println("Properties:");
		for (String property : properties) {
			ps.println("\t"+property);
		}
		ps.println();
		
		ps.println("Domains:");
		for (String property : domains.keySet()) {
			ps.println("\t"+property+" :");
			for (String domain : domains.get(property)) {
				ps.println("\t\t"+domain);
			}
		}
		ps.println();
		
		ps.println("Ranges:");
		for (String property : ranges.keySet()) {
			ps.println("\t"+property+" :");
			for (String range : ranges.get(property)) {
				ps.println("\t\t"+range);
			}
		}
		ps.println();
		
	}
	
	public void buildOwlFile(File file) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//IRI ontologyIRI = IRI.create("http://khaos.uma.es/endpoint.owl#");
		OWLOntology ont = manager.createOntology();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		for (String class_ : classes) {
			OWLClass owlClass = factory.getOWLClass(IRI.create(class_));
			OWLAxiom declareClass = factory.getOWLDeclarationAxiom(owlClass);
			manager.addAxiom(ont, declareClass);
		}
		
		for (String property : properties) {
			OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(IRI.create(property));
			OWLAxiom declareObjectProperty = factory.getOWLDeclarationAxiom(owlObjectProperty);
			manager.addAxiom(ont, declareObjectProperty);
			
			if (domains.containsKey(property)) {
				for (String domain : domains.get(property)) {
					OWLClass domainClass = factory.getOWLClass(IRI.create(domain));
			        OWLObjectPropertyDomainAxiom domainAxiom =
			        		factory.getOWLObjectPropertyDomainAxiom(owlObjectProperty, domainClass);
			        manager.addAxiom(ont, domainAxiom);
				}
			}
			
			if (ranges.containsKey(property)) {
				for (String range : ranges.get(property)) {
					OWLClass rangeClass = factory.getOWLClass(IRI.create(range));
			        OWLObjectPropertyRangeAxiom rangeAxiom =
			        		factory.getOWLObjectPropertyRangeAxiom(owlObjectProperty, rangeClass);
			        manager.addAxiom(ont,  rangeAxiom);
				}
			}
			
		}
		
		manager.saveOntology(ont, IRI.create(file.toURI()));
		
		
	}

}
