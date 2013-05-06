package co.edu.sanmartin.fuzzyclustering.machinelearning.facade;

import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;
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

}
