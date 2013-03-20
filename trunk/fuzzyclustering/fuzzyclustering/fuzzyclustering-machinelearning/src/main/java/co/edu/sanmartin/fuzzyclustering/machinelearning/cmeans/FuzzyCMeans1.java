package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans;

import java.util.Random;

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
public class FuzzyCMeans1 {

	private double[][] data;
	private int centroidsAmount;
	private int iterationAmount;
	private int mValue;
	private double centroidsSet[][];
	
	public int getCentroidsAmount() {
		return centroidsAmount;
	}

	public void setCentroidsAmount(int centroidsAmount, int iterationsAmount) {
		this.centroidsAmount = centroidsAmount;
	}
	
	public int getmValue() {
		return mValue;
	}

	/**
	 * Constructos de la clase
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de m generalmente se utiliza 2
	 */
	public FuzzyCMeans1(int centroidsAmount, int iterationsAmount, int mValue){
		this.centroidsAmount = centroidsAmount;
		this.iterationAmount = iterationsAmount;
		this.mValue = mValue;
		
		data = new double[1][6];
		data[0][0]=1.0;
		data[0][1]=3.0;
		data[0][2]=4.0;
		data[0][3]=6.0;
		data[0][4]=7.0;
		data[0][5]=10.0;
	}
	
	
	public void generateCMean(){
		double[][] centroidSet = new double[data.length][centroidsAmount];
		double[] centroides = {2.71,7.7};
		//Genera los centroides iniciales
		for (int i = 0; i < data.length; i++) {
			double[] vectorData = data[i];
			//double[] centroides = this.getCentroids(vectorData);
			
 		}

		for (int i = 0; i < this.iterationAmount; i++) {
			for (int j = 0; j < data.length; j++) {
				double[] vectorData = data[j];
				centroidSet = this.calculateMembershipFunction(vectorData, centroides);
			}
		}
	}
	
	/**
	 * Retorna una matrix con el valor de los centroides para el vector dado
	 * @param vectorData
	 * @return
	 */
	public double[][] calculateMembershipFunction(double[] vectorData, double[] centroids){
		double membershipCentroide = 0.00;
		double[][] vectorCentroideColl = new double[vectorData.length][centroids.length];
		
		for (int i = 0; i < this.centroidsAmount; i++) {
			
			for (int j = 0; j < vectorData.length; j++) {
				membershipCentroide = this.calculateCentroidMembershipFunction(vectorData[j], centroids, centroids[i]);
				vectorCentroideColl[j][i] = membershipCentroide;
			}
		}
		return vectorCentroideColl;
	}
	
	/**
	 * Calcula la funcion de pertentencia del conjunto
	 * @param dataMatrix el dato del componente del vector
	 */
	public double calculateCentroidMembershipFunction(double dataMatrix, double[] centroidColl, double centroidValue){
		double membership = 0.00;
		double membershipCentroide = 0.00;
		for (int j = 0; j < this.centroidsAmount; j++) {
			membershipCentroide+=Math.pow(((dataMatrix-centroidValue)/(dataMatrix-centroidColl[j])),this.mValue);
		}
			
		membership = 1/membershipCentroide;
		return  membership;
	}

	
	/**
	 * Metodo encargado de calcular los centroides de los conjuntos Fuzzy-CMeans
	 * @param vectorData vector con los componentes de la cantidad de terminos
	 * @param initCentroid verdadero si va a generar el centroide inicial utilizando numeros Randomicos
	 * @return
	 */
	public double[] getCentroids(double[] vectorData){
		double[] centroids = new double[this.centroidsAmount];
		double centroidValue = 0.00;
		Random random = new Random();
		double divider = 0.00;
		for(int i = 0; i< this.centroidsAmount; i++){
			for (int j = 0; j < vectorData.length; j++) {
		
				double randomValue = random.nextDouble();
				centroidValue+=(randomValue*vectorData[j]);
				divider+=randomValue;
			}
			centroids[i]=centroidValue/divider;
		}
		return centroids;
	}
	
	/**
	 * Metodo que recalcula el valor de los centroides
	 * @param vectorData
	 * @return
	 */
	public double[] recalculateCentroids(double[] centroidsValue,double[] vectorData){
		double[] centroids = new double[this.centroidsAmount];
		double centroidValue = 0.00;
		double divider = 0.00;
		for(int i = 0; i< this.centroidsAmount; i++){
			for (int j = 0; j < vectorData.length; j++) {
				centroidValue+=(centroidsValue[j]*vectorData[j]);
				divider+=centroidsValue[j];
			}
			centroids[i]=centroidValue/divider;
		}
		return centroids;
	}
	
	/**
	 * Retorna un vector de numeros aleatorios entre 0 y 1
	 * @param vectorSize
	 * @return
	 */
	public double[] getRandomVector( int vectorSize ){
		double[] randomVector = new double[vectorSize];
		Random random = new Random();
		for (int i = 0; i < randomVector.length; i++) {
			randomVector[i]=random.nextDouble();
		}
		return randomVector;
	}
	
}
