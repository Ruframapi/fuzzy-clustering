package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager;


public class CMeansTest {

		
	@Test
	public void testGenerateCMeans(){
		double[][] data ={{0.58,0.33},{0.90,0.11},{0.68,0.17},{0.11,0.44},{0.47,0.81},{0.24,0.83},
							{0.09,0.18},{0.82,0.11},{0.65,0.50},{0.09,0.63},{0.98,0.24}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,2,200,2);
		this.printMatrix(data);
		fuzzyCmeans.init();
		//double centroids[][] ={{0.2,0.5},{0.8,0.5}};
		//fuzzyCmeans.setCentroids(centroids);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	@Test
	public void testGenerateCMeans1(){
		double[][] data ={{1.00},{3.00},{4.00},{6.0},{7.0},{10.0}};
		double[][] initMatriz = {{0.9,0.1},{0.8,0.2},{0.7,0.3},{0.3,0.7},{0.2,0.8},{0.1,0.9}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,2,2000,2);
		this.printMatrix(data);
		fuzzyCmeans.init();
		//fuzzyCmeans.setCentroids(centroids);
		//fuzzyCmeans.init();
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	@Test
	public void testGenerateCMeans2(){
		double[][] data ={{1.00},{1.10},{1.20},{1.30},{1.40},{1.50},{1.60},{1.70},{2.0},{3.0}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,3,200,2);
		//fuzzyCmeans.setCentroids(centroids);
		//fuzzyCmeans.init();
		fuzzyCmeans.init();
		fuzzyCmeans.setInitMembershipMatrix(data);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	@Test
	public void testGenerateCMeans3(){
		double[][] data ={{0.58,0.33,0.12},{0.90,0.11,0.12},{0.68,0.17,0.12},{0.11,0.44,0.12},{0.47,0.81,0.12},{0.24,0.83,0.12},
							{0.09,0.18,0.12},{0.82,0.11,0.12},{0.65,0.50,0.12},{0.09,0.63,0.12},{0.98,0.24,0.12}};
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(data,2,200,2);
		this.printMatrix(data);
		fuzzyCmeans.init();
		//double centroids[][] ={{0.2,0.5},{0.8,0.5}};
		//fuzzyCmeans.setCentroids(centroids);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	private void printMatrix(double[][] data){
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				System.out.print(data[i][j]+",");
			}
			System.out.println();
		}
	}
}