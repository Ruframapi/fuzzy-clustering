package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans;

import org.apache.log4j.Logger;

public class DistanceFunctions {

	private static Logger logger = Logger.getLogger("DistanceFunctions");
	/**
	 * Metodo encargado de determinar la distancia entre dos vectores utilizando la 
	 * norma euclidiana
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double getEuclideanDistance(double[] vector1, double[] vector2){
		if(vector1.length!=vector2.length){
			logger.error("Error en getEuclideanDistance: Los vectores son de dimensiones diferentes");
		}
		double diference = 0.0;
		for (int i = 0; i < vector1.length; i++) {
			for (int j = 0; j < vector2.length; j++) {
				diference += Math.pow((vector1[i]-vector2[j]),2);
			}
		}
		diference = Math.sqrt(diference);
		return diference;
	}
}
