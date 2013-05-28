package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeans;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeansBigData;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager1;


public class CMeansBigDataTest {

	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias");
	
	public CMeansBigDataTest() {
		BasicConfigurator.configure();
	}
	
	
	@Test
	public void testGenerateCMeansBigData() throws Exception{
		double[][] data ={{0.58,0.33},{0.90,0.11},{0.68,0.17},{0.11,0.44},{0.47,0.81},{0.24,0.83},
							{0.09,0.18},{0.82,0.11},{0.65,0.50},{0.09,0.63},{0.98,0.24}};
		this.workspace.getPersistence().deleteFile(EDataFolder.MATRIX, "test.dat");
		this.saveMatrix(data, "test.dat");
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "test.dat");
		this.printBigMatrix(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"test.dat",2, 200, 2, false);
		fuzzyCmeans.init();
		double[][] centroids = {{0.2,0.5},{0.8,0.5}};
		fuzzyCmeans.setCentroids(centroids);
		double[][] initMembershipMatrix = new double[11][2];
		fuzzyCmeans.setMembershipMatrix(initMembershipMatrix);
		fuzzyCmeans.calculateFuzzyCmeans();
	};
	
	@Test
	public void testGenerateCMeansBigDataA() throws Exception{
		double[][] data ={{0.58,0.33},{0.90,0.11},{0.68,0.17},{0.11,0.44},{0.47,0.81},{0.24,0.83},
							{0.09,0.18},{0.82,0.11},{0.65,0.50},{0.09,0.63},{0.98,0.24}};
		this.workspace.getPersistence().deleteFile(EDataFolder.MATRIX, "test.dat");
		this.saveMatrix1(data, "test.dat");
		BigMatrixFileManager1 bigMatrixFileManager = new BigMatrixFileManager1(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "test.dat");
		this.printBigMatrix1(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"test.dat",2, 200, 2, false);
		fuzzyCmeans.init();
		double[][] centroids = {{0.2,0.5},{0.8,0.5}};
		fuzzyCmeans.setCentroids(centroids);
		double[][] initMembershipMatrix = new double[11][2];
		fuzzyCmeans.setMembershipMatrix(initMembershipMatrix);
		fuzzyCmeans.calculateFuzzyCmeans();
	};
	
	
	@Test
	public void testGenerateCMeansBigData1() throws Exception{
		double[][] data ={{1.00},{3.00},{4.00},{6.0},{7.0},{10.0}};
		this.saveMatrix(data, "test.dat");
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "test.dat");
		this.printBigMatrix(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"test.dat",2, 200, 2, false);
		fuzzyCmeans.init();
		double[][] centroids = {{2.71},{7.7}};
		fuzzyCmeans.setCentroids(centroids);
		
		//double[][] initMembershipMatrix = {{0.9,0.1},{0.8,0.2},{0.7,0.3},{0.3,0.7},{0.2,0.8},{0.1,0.9}};
		//fuzzyCmeans.setMembershipMatrix(initMembershipMatrix);
		fuzzyCmeans.calculateFuzzyCmeans();
	};
	
	@Test
	public void testGenerateCMeansBigData2() throws Exception{
		//double[][] data ={{1.00},{2.00},{3.00},{4.0},{5.0},{6.0},{7.0},{8.0}
		//,{9.0},{10.0},{11.0},{12.0},{13.0},{14.0},{15.0},{16.0},{16.0},{18.0},{19.0},{20.0}};
		double[][] data ={{0.00},{0.00},{0.00},{0.0},{0.0},{0.0},{0.0},{0.0}
		,{0.0},{0.0},{0.0},{0.0},{0.0},{0.0},{15.0},{16.0},{16.0},{18.0},{19.0},{20.0}};
		this.saveMatrix(data, "test.txt");
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "test.txt");
		this.printBigMatrix(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"test.txt",3, 200, 1.5, false);
		fuzzyCmeans.init();
		fuzzyCmeans.calculateFuzzyCmeans();
	};
	
	@Test
	public void testGenerateCMeansReducedNio() throws Exception{
		
		FuzzyCMeansBigData cmeans = new FuzzyCMeansBigData(this.workspace,DimensionallyReduced.REDUCED_FILE_NAME, 
				8, 20000, 1.5, true);
		cmeans.init();
		cmeans.calculateFuzzyCmeans();
	};
	
	
	@Test
	public void testGenerateCMeansReducedSample() throws Exception{
		String dataFile = this.workspace.getPersistence().readFile(EDataFolder.MATRIX,"reducida.txt");
		String[] rowVector = dataFile.split(System.getProperty("line.separator"));
		int height = rowVector.length;
		int width = rowVector[0].split(";").length;
		double [][] matrix = new double[height][width];
		for (int i = 0; i < height; i++) {
			String[] columnVector = rowVector[i].split(";");
			for (int j = 0; j < width; j++) {
				matrix[i][j] = Double.parseDouble(columnVector[j]);
			}
		}
		
		this.saveMatrix(matrix, "reducidaNio.txt");
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"reducidaNio.txt",10, 200, 2, false);
		fuzzyCmeans.init();
		fuzzyCmeans.calculateFuzzyCmeans();
	};
	@Test
	public void testGenerateCMeansBigDataFile() throws Exception{
		String dataFile = this.workspace.getPersistence().readFile(EDataFolder.MATRIX,"test.txt");
		String[] rowVector = dataFile.split(System.getProperty("line.separator"));
		int height = rowVector.length;
		int width = rowVector[0].split(",").length;
		double [][] matrix = new double[height][width];
		for (int i = 0; i < height; i++) {
			String[] columnVector = rowVector[i].split(",");
			for (int j = 0; j < width; j++) {
				matrix[i][j] = Double.parseDouble(columnVector[j]);
			}
		}
		
		this.saveMatrix(matrix, "testNio.txt");
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "testNio.txt");
		this.printBigMatrix(bigMatrixFileManager);
		
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"testNio.txt",10, 200, 2, false);
		fuzzyCmeans.init();
		fuzzyCmeans.calculateFuzzyCmeans();
	};

	
	private void printBigMatrix(BigDoubleMatrixFileManager data){
		for (int i = 0; i < data.height(); i++) {
			for (int j = 0; j < data.width(); j++) {
				System.out.print(data.get(i, j)+",");
			}
			System.out.println();
		}
	}
	
	private void printBigMatrix1(BigMatrixFileManager1 data){
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
		FuzzyCMeans fuzzyCmeans = new FuzzyCMeans(this.workspace,data,2,2000,2);
		//fuzzyCmeans.setCentroids(centroids);
		//fuzzyCmeans.init();
		fuzzyCmeans.setInitMembershipMatrix(initMatriz);
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrix(double[][] matrix, String fileName) throws Exception{
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadWrite(EDataFolder.MATRIX, fileName,matrix.length,matrix[0].length);
		System.out.println("Init savematrix");
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				bigMatrixFileManager.set(i,j, matrix[i][j]);
			}
			
		}
		bigMatrixFileManager.close();
	}
	
	
	
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrix1(double[][] matrix, String fileName) throws Exception{
		BigMatrixFileManager1 bigMatrixFileManager = new BigMatrixFileManager1(this.workspace);
		bigMatrixFileManager.loadReadWrite(EDataFolder.MATRIX, fileName,matrix.length,matrix[0].length);
		System.out.println("Init savematrix");
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				bigMatrixFileManager.set(i,j, matrix[i][j]);
			}
		}
		bigMatrixFileManager.close();
	}
	
	
	@Test
	public void calculateCmeansPpmiTest(){
		FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"ppmi.txt",3, 200, 2, false);
		fuzzyCmeans.init();
		fuzzyCmeans.calculateFuzzyCmeans();
	}
	
	/**
	 * Realiza la creacion de archivos para pruebas de clustezacion
	 * @throws Exception
	 */
	@Test
	public void testGenerateCMeansClusteringTest() throws Exception{
		this.workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		String dataFile = this.workspace.getPersistence().readRootFile(EDataFolder.TRAIN,
				System.getProperty("file.separator")+"clustering"+System.getProperty("file.separator")+"R15ClusteringTest.txt");
		String[] rowVector = dataFile.split(System.getProperty("line.separator"));
		int height = rowVector.length;
		int width = rowVector[0].split("\t").length;
		double [][] matrix = new double[height][width];
		for (int i = 0; i < height; i++) {
			String[] columnVector = rowVector[i].split("\t");
			for (int j = 0; j < width; j++) {
				matrix[i][j] = Double.parseDouble(columnVector[j]);
			}
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MATRIX, "reduced.txt",dataFile);
		this.saveMatrix(matrix, "reduced.dat");
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		bigMatrixFileManager.loadReadOnly(EDataFolder.MATRIX, "reduced.dat");
		this.printBigMatrix(bigMatrixFileManager);
		
		//FuzzyCMeansBigData fuzzyCmeans = new FuzzyCMeansBigData(this.workspace,"testNio.txt",10, 200, 2, false);
		//fuzzyCmeans.init();
		//fuzzyCmeans.calculateFuzzyCmeans();
	};
}


