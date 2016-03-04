package es.uma.khaos.ontology_endpoint;

import java.io.File;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class ExecuteMain {
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		String endpoint = "http://150.214.214.5/virtuoso/sparql";
		String graph = "www.khaos.uma.es/metabolic-pathways-app";
		
//		String endpoint = "http://150.214.214.6/sparql";
//		String graph = "http://khaos.uma.es/olivedb";
		
		Explorer explorer = new Explorer(endpoint, graph);
		EndpointOntology ontology = explorer.buildOntologyFromEndpoint();
		ontology.print(System.out);
		File file = new File("endpoint.owl");
		ontology.buildOwlFile(file);
	}

}
