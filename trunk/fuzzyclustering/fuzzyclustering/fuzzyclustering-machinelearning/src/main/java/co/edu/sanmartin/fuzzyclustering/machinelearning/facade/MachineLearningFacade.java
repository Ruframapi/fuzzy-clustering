package co.edu.sanmartin.fuzzyclustering.machinelearning.facade;

import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;

public class MachineLearningFacade {
	
	/**
	 * Genera los conjuntos difusos a partir de la matrix de datos.
	 * @param data matrix de datos de los documentos descargados
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de fuzzyficacion
	 */
	public void calculateCMeans(double[][] data, int centroidsAmount, int iterationsAmount, int mValue){
		FuzzyCMeans cmeans = new FuzzyCMeans(data, centroidsAmount, iterationsAmount, mValue);
		cmeans.calculateFuzzyCmeans();
	}

}
