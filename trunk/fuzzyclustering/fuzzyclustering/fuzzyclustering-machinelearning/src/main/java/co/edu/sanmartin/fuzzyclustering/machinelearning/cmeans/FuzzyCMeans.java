package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans;

import java.util.Random;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Paso1  Inicializar los centroides ci,i=1,..c. 
 * Paso2  Crear los puntos aleatorios para los cantidad de centroides
 * Paso3. Determinar la matrix de pertenencia U
 * Paso4. Procesar la funcion de disimilitud. 
 * Paso5. Deternerse si la iteracion es menor al umbral
 * Paso6. Procesar de nuevo los centroides.

 * @author Ricardo Carvajal Salamanca
 *
 */
public class FuzzyCMeans {

	private double[][] data;
	private int centroidsAmount;
	private int iterationAmount;
	private int mValue;
	
	private double[][] centroids;
	private double[][] initMembershipMatrix;
	private double[][] membershipMatrix;
	
	
	/**
	 * Constructor de la clase
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de m generalmente se utiliza 2
	 */
	public FuzzyCMeans(double[][] data, int centroidsAmount, int iterationsAmount, int mValue){
		
		this.data = data;
		this.centroidsAmount = centroidsAmount;
		this.iterationAmount = iterationsAmount;
		this.mValue = mValue;
	}
	
	/**
	 * Metodo encargado de cargar la matriz double[][] de datos de los conjuntos difusos.
	 * @param fileNameMatrix
	 * @param centroidsAmount
	 * @param iterationsAmount
	 * @param mValue
	 */
	public FuzzyCMeans( String fileNameMatrix, int centroidsAmount, int iterationsAmount, int mValue){
		//TODO Terminar Metodo
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		String[] data = persistenceFacade.readFile(fileNameMatrix).split("\r\n");
		String[][] dataMatrix = null; 
	//	double dataMatrix = null;
		for (int i = 0; i < data.length; i++) {
			String[] components = data[i].split(",");
			if(i==0){
		//		dataMatrix = new double[][];
			}
		}
		
		//String[][] filedata = data.split("\r\n");
		
	}
	public int getCentroidsAmount() {
		return centroidsAmount;
	}
	
	public int getmValue() {
		return mValue;
	}
	
	
	public void setCentroids(double[][] centroids) {
		this.centroids = centroids;
	}
	
	
	public void setInitMembershipMatrix(double[][] initMembershipMatrix) {
		this.initMembershipMatrix = initMembershipMatrix;
	}
	/**
	 * Inicicializa la matrix de numeros aletorios y genera los
	 * centroides para comenzar el proceso
	 */
	public void init(){
		try {
			this.initMembershipMatrix();
			this.calculateCentroids();
			//this.initCentroids();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Metodo encargado de realizar el calculo de los conjuntos difusos
	 */
	public void calculateFuzzyCmeans(){
		
		this.membershipMatrix = new double[data.length][centroidsAmount];
		for (int i = 0; i < this.iterationAmount; i++) {
			this.calculateMembershipMatrix();
			this.initMembershipMatrix = membershipMatrix.clone();
			this.membershipMatrix = new double[data.length][centroidsAmount];
			this.saveCentroides();
			this.calculateCentroids();
			
		}
		System.out.print("Finalizo");
	}
	
	/**
	 * Guarda los centroides en disco.
	 */
	public void saveCentroides(){
		StringBuilder stringBuilder = new StringBuilder();
		for (int j = 0; j < centroids.length; j++) {
			for (int j2 = 0; j2 < centroids[j].length; j2++) {
				stringBuilder.append(centroids[j][j2]+",");
			}
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.MACHINE_LEARNING, 
				stringBuilder.toString(), data.toString());
		
	}
	
	/**
	 * Guarda los centroides en disco.
	 */
	public void printCentroides(){
		for (int j = 0; j < centroids.length; j++) {
			System.out.print("Centroide:" + j +" - " );
			for (int j2 = 0; j2 < centroids[j].length; j2++) {
				System.out.print(centroids[j][j2]+",");
			}
			System.out.print("\r\n");
		}
		
	}

	/**
	 * Metodo encargado de crear la matriz de pertenencia con valores aleatorios
	 * 
	 */
	public void initMembershipMatrix(){
		this.initMembershipMatrix = new double[data.length][centroidsAmount];
		Random random = new Random();
		for(int i = 0; i< initMembershipMatrix.length; i++){
			for (int j = 0; j < initMembershipMatrix[i].length; j++) {
				this.initMembershipMatrix[i][j]=random.nextDouble();
				
			}
		}
	}
	
	/**
	 * Metodo encargado de cargar los centroides basado en la matrix de pertenencia
	 */
	public void calculateCentroids(){
		this.centroids = new double[centroidsAmount][data[0].length];
		for (int h = 0; h < centroidsAmount; h++) {
			int i = 0;
			double dividend = 0.00;
			double divisor = 0.00;
			for (i = 0; i < data[0].length; i++) {
				int j = 0;
				for ( j=0; j < data.length; j++) {
					double membership = Math.pow(initMembershipMatrix[j][h],mValue);
					double centroidComponent = data[j][i]*membership;
					dividend+=centroidComponent;
					divisor+= membership;
				}
				this.centroids[h][i]=dividend/divisor;
			}
		}
		this.printCentroides();
	}
	
	/**
	 * Calcula los centroides iniciales del conjunto difuso
	 * @param dataVector
	 * @param membershipVector
	 * @return
	 */
	public double calculateInitCentroidsVector(double[] dataVector, double membershipVector[]){
			double vectorSumatory = 0.0;
			for (int j = 0; j < dataVector.length; j++) {
				double dataValue =dataVector[j];
				double randomValue = Math.pow(membershipVector[j],this.mValue);
				vectorSumatory+=dataValue*randomValue;
			}
			return vectorSumatory;
	}
	
	public void recalculeCentroids(){
		this.initMembershipMatrix = membershipMatrix.clone();
		this.membershipMatrix = new double[data.length][centroidsAmount];
		this.centroids = new double[centroidsAmount][data[0].length];
		for (int i = 0; i < centroidsAmount; i++) {
			for (int j = 0; j < data.length; j++) {
				centroids[i][j] = this.calculateInitCentroidsVector(data[j],initMembershipMatrix[i]);
			}
		}
	}
	
	/**
	 * Metodo encargado de calcular la funcion de pertenencia que generara
	 * los nuevos centroides
	 */
	public void calculateMembershipMatrix(){
		for (int i = 0; i < data.length; i++) {
			double[] dataVector = data[i];
			double[] xiVi = this.calculateXiVi(dataVector);
				for (int k = 0; k < xiVi.length; k++) {
					double newCentroidComponent=0.00;
					double xValue=xiVi[k];
					for (int l = 0; l < xiVi.length; l++) {
						double x1Value=xiVi[l];
						newCentroidComponent+=(xValue/x1Value);
					}
					newCentroidComponent = 1/newCentroidComponent;
					membershipMatrix[i][k]=newCentroidComponent;
				}
		}
	}
	
	/**
	 * Retorna n-centroides valores escalares que corresponden a la resta de componentes vectoriales
	 * entre la matriz de terminos y la matriz de pertenencia
	 * @param dataVector
	 * @return
	 */
	public double[] calculateXiVi(double[] dataVector){
		double[] xiVi = new double[centroidsAmount];
		
		for (int i = 0; i < centroidsAmount; i++) {
			for (int j = 0; j < dataVector.length; j++) {
				double dataValue = dataVector[j];
				double centroidValue = centroids[i][j];
				xiVi[i]+=Math.pow(dataValue-centroidValue,this.mValue);
			}
		}
		return xiVi;
		
	}	
	
}
