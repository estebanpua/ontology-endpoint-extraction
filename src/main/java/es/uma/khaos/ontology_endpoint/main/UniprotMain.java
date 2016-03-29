package es.uma.khaos.ontology_endpoint.main;

import java.io.File;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.uma.khaos.ontology_endpoint.explorer.Explorer;
import es.uma.khaos.ontology_endpoint.ontology.OntologyData;
import es.uma.khaos.ontology_endpoint.ontology.OntologyUtils;

public class UniprotMain {
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		long timeStart, timeEnd;
		timeStart = System.currentTimeMillis();
		
		String endpoint = "http://sparql.uniprot.org/sparql";
		String graph = "http://sparql.uniprot.org/taxonomy/";
		Explorer explorer = new Explorer(endpoint, graph);
		
		OntologyData ontology = explorer.execute();
		OntologyUtils.print(ontology);
		OntologyUtils.buildOwlFile(ontology, new File("uniprot.owl"));
		
		timeEnd = System.currentTimeMillis();
		System.out.println("the task has taken "+ ( timeEnd - timeStart ) +" milliseconds");
		
	}

}
