package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.io.IOException;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeansBigData;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager;


public class CMeansBigDataTest {

		
	@Test
	public void testGenerateCMeansBigData() throws Exception{
		double[][] data ={{0.58,0.33},{0.90,0.11},{0.68,0.17},{0.11,0.44},{0.47,0.81},{0.24,0.83},
							{0.09,0.18},{0.82,0.11},{0.65,0.50},{0.09,0.63},{0.98,0.24}};
		this.saveMatrix(data, "test.txt");
		BigMatrixFileManager bigMatrixFileManager = new BigMatrixFileManager();
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "test.txt");
		this.printBigMatrix(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(bigMatrixFileManager, 2, 200, 2);
		fuzzyCmeans.initMembershipMatrix();
		//double centroids[][] ={{0.2,0.5},{0.8,0.5}};
		//fuzzyCmeans.setCentroids(centroids);
		fuzzyCmeans.calculateFuzzyCmeans();
		bigMatrixFileManager.close();
	};
	
	private void printBigMatrix(BigMatrixFileManager data){
		for (int i = 0; i < data.height(); i++) {
			for (int j = 0; j < data.width(); j++) {
				System.out.print(data.get(i, j)+",");
			}
			System.out.println();
		}
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
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrix(double[][] matrix, String fileName) throws Exception{
		BigMatrixFileManager bigMatrixFileManager = new BigMatrixFileManager();
		bigMatrixFileManager.loadReadWrite(EDataFolder.MATRIX, "test.txt",matrix.length,matrix[0].length);
		System.out.println("Init savematrix");
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				bigMatrixFileManager.set(i, j, matrix[i][j]);
			}
			
		}
		bigMatrixFileManager.close();
	}
}
