package co.edu.sanmartin.fuzzyclustering.ir.test;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.cmeans.FuzzyCMeans;

public class CMeansTest {

		
	@Test
	public void testGenerateCMeans(){
		double[][] data ={{0.58,0.33},{0.90,0.11},{0.68,0.17},{0.11,0.44},{0.47,0.81},{0.24,0.83},
							{0.09,0.18},{0.82,0.11},{0.65,0.50},{0.09,0.63},{0.98,0.24}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,2,200,2);
		fuzzyCmeans.initMembershipMatrix();
		//double centroids[][] ={{0.2,0.5},{0.8,0.5}};
		//fuzzyCmeans.setCentroids(centroids);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	@Test
	public void testGenerateCMeans1(){
		double[][] data ={{1.00},{3.00},{4.00},{6.0},{7.0},{10.0}};
		double[][] initMatriz = {{0.9,0.1},{0.8,0.2},{0.7,0.3},{0.3,0.7},{0.2,0.8},{0.1,0.9}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,2,2000,2);
		//fuzzyCmeans.setCentroids(centroids);
		//fuzzyCmeans.init();
		fuzzyCmeans.setInitMembershipMatrix(initMatriz);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
}
