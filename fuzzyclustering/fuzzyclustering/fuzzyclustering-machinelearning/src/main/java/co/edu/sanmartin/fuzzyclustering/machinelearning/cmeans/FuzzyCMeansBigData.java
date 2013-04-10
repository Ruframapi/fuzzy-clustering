package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans;

import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;

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
public class FuzzyCMeansBigData {

	private static Logger logger = Logger.getLogger("FuzzyCMeansBigData");
	private BigDoubleMatrixFileManager data;
	private int centroidsAmount;
	private int iterationAmount;
	private double mValue;
	
	private double[][] previousCentroids;
	private double[][] centroids;
	private double[][] initMembershipMatrix;
	private double[][] membershipMatrix;
	private double[] centroidsDistances;
	
	
	/**
	 * Constructor de la clase
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de m generalmente se utiliza 2
	 */
	public FuzzyCMeansBigData(BigDoubleMatrixFileManager data, int centroidsAmount, int iterationsAmount, double mValue){
		
		this.data = data;
		this.centroidsAmount = centroidsAmount;
		this.centroidsDistances = new double[centroidsAmount];
		this.iterationAmount = iterationsAmount;
		this.mValue = mValue;
		
	}
	
	/**
	 * Constructor de la clase
	 * @param centroidsAmount cantidad de centroides
	 * @param iterationsAmount cantidad de iteraciones
	 * @param mValue valor de m generalmente se utiliza 2
	 * @param buildMatrix indica si realiza la construccion de las matrices de terminos previa
	 */
	public FuzzyCMeansBigData(String fileName, int centroidsAmount, int iterationsAmount, 
								double mValue, boolean buildMatrix){	
		
		try {
			if(buildMatrix){
				try {
					IRFacade.getInstance().buildCmeanMatrix();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Error in FuzzyCMeansBigData",e);
				}
			}
			
			this.data  = new BigDoubleMatrixFileManager();
			data.loadReadOnly(EDataFolder.MATRIX, fileName);
			this.centroidsAmount = centroidsAmount;
			this.iterationAmount = iterationsAmount;
			this.centroidsDistances = new double[centroidsAmount];
			this.mValue = mValue;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error in load FuzzyCMeansBigData",e);
		}
		
	}
	

	public int getCentroidsAmount() {
		return centroidsAmount;
	}
	
	public double getmValue() {
		return mValue;
	}
	
	
	public void setCentroids(double[][] centroids) {
		this.centroids = centroids;
	}
	
	
	public void setMembershipMatrix(double[][] membershipMatrix) {
		this.membershipMatrix = membershipMatrix;
	}
	/**
	 * Inicicializa la matrix de numeros aletorios y genera los
	 * centroides para comenzar el proceso
	 */
	public void init(){
		try {
			this.initMembershipMatrix();
			this.calculateCentroids();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Metodo encargado de realizar el calculo de los conjuntos difusos
	 */
	public void calculateFuzzyCmeans(){
		logger.info("Inicializando proceso de clusterizacion C-Means");
		this.sendMessageAsynch("Inicializando proceso de clusterizacion C-Means");
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		this.membershipMatrix = new double[data.height()][centroidsAmount];
		for (int i = 0; i < this.iterationAmount; i++) {
			this.saveDoubleMatrix(this.initMembershipMatrix, "relationship"+i+".txt");
			this.calculateMembershipMatrix();
			this.initMembershipMatrix = membershipMatrix.clone();
			this.membershipMatrix = new double[data.height()][centroidsAmount];
			this.calculateCentroids();
			
		}
		
		this.saveDoubleMatrix(this.centroids, "centroids.txt");
		time_end = System.currentTimeMillis();
		
		String result = "El proceso de clusterizacion Tomo "+ 
		( time_end - time_start )/1000 +" segundos" + 
		(( time_end - time_start )/1000)/60 +" minutos";
		
		logger.info(result);
		this.sendMessageAsynch(result);
	}

	
	/**
	 * Guarda los centroides en disco.
	 */
	public void printCentroides(){
		logger.info("Nuevos Centroides Calculados");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.getProperty("line.separator"));
		for (int j = 0; j < centroids.length; j++) {
			stringBuilder.append("Centroide:" + j +" - " );
			for (int j2 = 0; j2 < centroids[j].length; j2++) {
				stringBuilder.append(centroids[j][j2]+",");
			}
			stringBuilder.append(System.getProperty("line.separator"));
		}
		logger.info("Nuevos Centroides:"+stringBuilder.toString());
	}

	/**
	 * Metodo encargado de crear la matriz de pertenencia con valores aleatorios
	 * 
	 */
	public void initMembershipMatrix(){
		this.initMembershipMatrix = new double[data.height()][centroidsAmount];
		Random random = new Random();
		for(int i = 0; i< initMembershipMatrix.length; i++){
			for (int j = 0; j < initMembershipMatrix[i].length; j++) {
				random.setSeed(System.nanoTime());
				double randomValue = random.nextDouble()*random.nextInt(100);
				if(randomValue== 0.0){
					randomValue = random.nextDouble();
				}
				this.initMembershipMatrix[i][j]=randomValue;
			}
			
		}
		logger.trace("Finish Init Membership Matrix");
	}
	
	/**
	 * Metodo encargado de cargar los centroides basado en la matrix de pertenencia
	 */
	public void calculateCentroids(){
		if(centroids!=null && centroids.length>0){
			this.previousCentroids = centroids.clone();
		}
		else{
			this.previousCentroids = new double[centroidsAmount][data.width()];
		}
		this.centroids = new double[centroidsAmount][data.width()];
		for (int h = 0; h < centroidsAmount; h++) {
			int i = 0;
			double dividend = 0.00;
			double divisor = 0.00;
			for (i = 0; i < data.width(); i++) {
				int j = 0;
				for ( j=0; j < data.height(); j++) {
					double membership = Math.pow(initMembershipMatrix[j][h],mValue);
					double centroidComponent = data.get(j, i)*membership;
					dividend+=centroidComponent;
					divisor+= membership;
				}
				this.centroids[h][i]=dividend/divisor;
			}
		}
		this.calculateDistances();
		//this.printCentroides();
	}
	/**
	 * Metodo encargado de calcular las distancias entre dos vectores
	 * Si la distancia es mayor a la tolerancia continua de lo contrario
	 * deja las iteraciones en -1
	 */
	private void calculateDistances() {
		double tolerance = 0.0000001;
		double[] newCentroidsDistances = new double[this.centroidsAmount];
		for (int i = 0; i < centroids.length; i++) {
			double distanceCentroid = DistanceFunctions.getEuclideanDistance(this.centroids[i], this.previousCentroids[i]);
			logger.info("Centroid " + i + " - Resultado de la diferencia entre vectores:" + distanceCentroid);
			newCentroidsDistances[i]= distanceCentroid;
		}
		
		//Se verifica el cambio de la distancia euclidiana de los centroids
		boolean finishProcess = true;
		for (int i = 0; i < centroidsDistances.length; i++) {
			double diferenceCentroidsIteration = Math.abs(centroidsDistances[i]-newCentroidsDistances[i]);
			logger.info("Previos Centroid vs new Centroid " + i + " - Distance:" + diferenceCentroidsIteration);
			if(diferenceCentroidsIteration>tolerance){
				finishProcess = false;
				break;
			}
		}
		if(finishProcess == true){
			this.iterationAmount=-1;
			logger.info("Proceso Finalizado Los Centroides de la iteracion " +
						"anterior y la actual son menores a :" + tolerance);
		}
		
		this.centroidsDistances = newCentroidsDistances.clone();
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
		this.membershipMatrix = new double[data.height()][centroidsAmount];
		this.centroids = new double[centroidsAmount][data.width()];
		for (int i = 0; i < centroidsAmount; i++) {
			for (int j = 0; j < data.height(); j++) {
				double[] vector = new double[data.width()];
				for (int j2 = 0; j2 < data.width(); j2++) {
					vector[j2]=data.get(j, j2);
				}
				centroids[i][j] = this.calculateInitCentroidsVector(vector,initMembershipMatrix[i]);
			}
		}
	}
	
	/**
	 * Metodo encargado de calcular la funcion de pertenencia que generara
	 * los nuevos centroides
	 */
	public void calculateMembershipMatrix(){
		for (int i = 0; i < data.height(); i++) {
			double[] dataVector = new double[data.width()];
			for (int j2 = 0; j2 < data.width(); j2++) {
				dataVector[j2]=data.get(i, j2);
			}
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
				//TODO REVISAR
				xiVi[i]+=Math.pow((Math.abs(dataValue-centroidValue)),this.mValue);
			}
		}
		return xiVi;
		
	}
	
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveDoubleMatrix(double[][] matrix, String fileName){
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {
		for (int j = 0; j < matrix[i].length; j++) {
			data.append(matrix[i][j]);
			if(j+1<matrix[i].length){
				data.append(";");
			}
		}
		data.append(System.getProperty("line.separator"));
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.MACHINE_LEARNING, 
				fileName, data.toString());
	}
	
	public void sendMessageAsynch(String message){
		QueueDTO queue = new QueueDTO();
		queue.setInitDate(PersistenceFacade.getInstance().getServerDate().getTime());
		queue.setEvent(EQueueEvent.SEND_MESSAGE);
		queue.setModule(EModule.QUERYASYNCH);
		queue.setParams(message);
		queue.setStatus(EQueueStatus.ENQUEUE);
	}
	
}
