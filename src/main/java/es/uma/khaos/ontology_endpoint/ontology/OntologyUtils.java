package es.uma.khaos.ontology_endpoint.ontology;

import java.io.File;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public final class OntologyUtils {
	
	private OntologyUtils() { }
	
	public static void print(OntologyData ontologyData) {
		print(ontologyData, System.out);
	}
	
	public static void print(OntologyData ontologyData, PrintStream ps) {
		ps.println("Classes:");
		for (String class_ : ontologyData.getClasses()) {
			ps.println("\t"+class_);
		}
		ps.println();
		
		ps.println("Properties:");
		for (String property : ontologyData.getProperties()) {
			ps.println("\t"+property);
		}
		ps.println();
		
		ps.println("Domains:");
		for (String property : ontologyData.getDomains().keySet()) {
			ps.println("\t"+property+" :");
			for (String domain : ontologyData.getDomains().get(property)) {
				ps.println("\t\t"+domain);
			}
		}
		ps.println();
		
		ps.println("Ranges:");
		for (String property : ontologyData.getRanges().keySet()) {
			ps.println("\t"+property+" :");
			for (String range : ontologyData.getRanges().get(property)) {
				ps.println("\t\t"+range);
			}
		}
		ps.println();
		
		
		ps.println("DataTypes:");
		for (String datatype: ontologyData.getDatatype()) {
			ps.println("\t"+datatype);
		}
		ps.println();
		
		
		ps.println("ObjectProperties:");
		for (String objectproperties: ontologyData.getObjectproperties()) {
			ps.println("\t"+objectproperties);
		}
		ps.println();
		
		ps.println("DataProperties:");
		for (String dataproperties: ontologyData.getDataproperties()) {
			ps.println("\t"+dataproperties);
		}
		ps.println();
		
		
		
	}
	
	public static void buildOwlFile(OntologyData ontologyData, File file) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//IRI ontologyIRI = IRI.create("http://khaos.uma.es/endpoint.owl#");
		OWLOntology ont = manager.createOntology();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		
		System.out.println("Create classes");

		for (String class_ : ontologyData.getClasses()) { // Getting all classes to create classes in ontology.
			OWLClass owlClass = factory.getOWLClass(IRI.create(class_));
			OWLAxiom declareClass = factory.getOWLDeclarationAxiom(owlClass);
			manager.addAxiom(ont, declareClass);
		}
		
		
		System.out.println("Create properties");
		
		for (String property : ontologyData.getProperties()) {
			OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(IRI.create(property));
			OWLAxiom declareObjectProperty = factory.getOWLDeclarationAxiom(owlObjectProperty);
			manager.addAxiom(ont, declareObjectProperty);
			
			if (ontologyData.getDomains().containsKey(property)) {
				for (String domain : ontologyData.getDomains().get(property)) {
					OWLClass domainClass = factory.getOWLClass(IRI.create(domain));
			        OWLObjectPropertyDomainAxiom domainAxiom =
			        		factory.getOWLObjectPropertyDomainAxiom(owlObjectProperty, domainClass);
			        manager.addAxiom(ont, domainAxiom);
				}
			}
			
			if (ontologyData.getRanges().containsKey(property)) {
				for (String range : ontologyData.getRanges().get(property)) {
					OWLClass rangeClass = factory.getOWLClass(IRI.create(range));
			        OWLObjectPropertyRangeAxiom rangeAxiom =
			        		factory.getOWLObjectPropertyRangeAxiom(owlObjectProperty, rangeClass);
			        manager.addAxiom(ont,  rangeAxiom);
				}
			}
			
		}
		
		/*for (String property : ontologyData.getProperties()) {
			
			OWLDataProperty owlDataProperty = factory.getOWLDataProperty(IRI.create(property));
			OWLAxiom declareDataProperty = factory.getOWLDeclarationAxiom(owlDataProperty);
			manager.addAxiom(ont, declareDataProperty);

			if (ontologyData.getDomains().containsKey(property)) {
				for (String domain : ontologyData.getDomains().get(property)) {
					OWLClass domainClass = factory.getOWLClass(IRI.create(domain));
			        OWLDataPropertyDomainAxiom domainAxiom =
			        		factory.getOWLDataPropertyDomainAxiom(owlDataProperty, domainClass);
			        manager.addAxiom(ont, domainAxiom);
				}
			}

			if (ontologyData.getDatatype().contains(property)) {
				for (String range : ontologyData.getDatatype()) {
					OWLDataRange rangeDataType = factory.getOWLDatatype(IRI.create(range));
			        OWLDataPropertyRangeAxiom rangeAxiomDataType =
			        		factory.getOWLDataPropertyRangeAxiom(owlDataProperty, rangeDataType);
			        manager.addAxiom(ont,  rangeAxiomDataType);
				}
			}

		
		
		}*/
		
		manager.saveOntology(ont, IRI.create(file.toURI()));
		
	}
	
	public static JSONObject buildJson(OntologyData ontologyData) {
		
		JSONObject classes = new JSONObject();
		for (String class_ : ontologyData.getClasses()) {

			JSONArray domains = new JSONArray();
			for (String property : ontologyData.getPropertiesFromClass(class_)) {
				if (ontologyData.getObjectproperties().contains(property)) {
					domains.put(new JSONObject()
							.put("type", "object_property")
							.put("uri", property));
				}
				if (ontologyData.getDataproperties().contains(property)) {
					domains.put(new JSONObject()
							.put("type", "object_property")
							.put("uri", property));
				}
				
			}
			
			JSONArray ranges = new JSONArray();
			for (String property : ontologyData.getPropertiesToClass(class_)) {
				ranges.put(new JSONObject()
						.put("type", "property")
						.put("uri", property));
			}
			
			classes.put(class_, new JSONObject()
					.put("domain", domains)
					.put("range", ranges)
					.put("uri", class_));
		}
		
		JSONObject properties = new JSONObject();
		for (String property : ontologyData.getProperties()) {
			
			JSONArray domains = new JSONArray();
			for (String domain : ontologyData.getDomain(property)) {
				domains.put(new JSONObject()
						.put("type", "class")
						.put("uri", domain));
			}
			
			JSONArray ranges = new JSONArray();
			for (String range : ontologyData.getRange(property)) {
				ranges.put(new JSONObject()
						.put("type", "class")
						.put("uri", range));
			}
			
			properties.put(property, new JSONObject()
					.put("domain", domains)
					.put("range", ranges)
					.put("uri", property));;
		}
		
		JSONObject res = new JSONObject()
				.put("classes", classes)
				.put("properties", properties);
		
		return res;

	}

}
