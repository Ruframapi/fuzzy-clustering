package co.edu.sanmartin.fuzzyclustering.machinelearning.facade;

import java.util.ArrayList;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

public class MachineLearningFacade {
	
	/**
	 * Genera los conjuntos difusos a partir de la matrix de datos.
	 * @param data matrix de datos de los documentos descargados
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de fuzzyficacion
	 */
	public void calculateCMeans(WorkspaceDTO workspace, double[][] data, int centroidsAmount, int iterationsAmount, int mValue){
		FuzzyCMeans cmeans = new FuzzyCMeans(workspace, data, centroidsAmount, iterationsAmount, mValue);
		cmeans.calculateFuzzyCmeans();
	}
	
	/**
	 * Genera el indice de pertenencia de cada termino en los clusteres
	 * @param workspace
	 */
	public void generatePertenenceIndex(WorkspaceDTO workspace){
		DocumentCluster documentCluster = new DocumentCluster(workspace);
		documentCluster.buildMembershipIndex();
	}
	
	
	/**
	 * Retorna el cluster al que pertenece el documento y si el termino buscado 
	 * tambien pertenece al cluster
	 * @param document
	 * @param word
	 * @return
	 */
	public DocumentClustering getDocumentClustering(WorkspaceDTO workspace, DocumentDTO document){
		DocumentClustering documentClustering = new DocumentClustering(workspace);
		documentClustering.clustering(document);
		return documentClustering;
	}
	
	

}
