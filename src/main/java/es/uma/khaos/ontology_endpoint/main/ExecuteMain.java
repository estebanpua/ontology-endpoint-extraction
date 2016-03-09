package es.uma.khaos.ontology_endpoint.main;

import java.io.File;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.uma.khaos.ontology_endpoint.explorer.Explorer;
import es.uma.khaos.ontology_endpoint.ontology.OntologyData;
import es.uma.khaos.ontology_endpoint.ontology.OntologyUtils;

public class ExecuteMain {
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		String endpoint = "http://150.214.214.5/virtuoso/sparql";
		String graph = "www.khaos.uma.es/metabolic-pathways-app";
		
//		String endpoint = "http://150.214.214.6/sparql";
//		String graph = "http://khaos.uma.es/olivedb";
		
		Explorer explorer = new Explorer(endpoint, graph);
		OntologyData ontology = explorer.execute();
		OntologyUtils.print(ontology);
	//	OntologyUtils.buildOwlFile(ontology, new File("endpoint.owl"));
	}

}
