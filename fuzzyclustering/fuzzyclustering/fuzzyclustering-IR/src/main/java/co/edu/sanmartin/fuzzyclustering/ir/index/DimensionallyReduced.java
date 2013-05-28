package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.Random;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;

/**
 * Clase que realiza la reduccion de dimensionalidad
 * Existen dos estrategias
 * rij = ±1 with probability of 0.5 each
 * rij =raiz(3)*(±1) with probability of 1/6 each, or 0 with a probability of 2/3

 * @author Ricardo
 *
 */
public class DimensionallyReduced {
	
	
	private static Logger logger = Logger.getLogger("DimensionallyReduced");
	public static final String REDUCED_FILE_NAME = "reduced.dat"; 
	private WorkspaceDTO workspace;
	
	public DimensionallyReduced(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	/**
	 * Metodo encargado de realiza la reduccion de dimensionalidad de la matrix ppmi
	 * @param fileName nombre del archivo
	 * @param newDimension nuevo numero de dimensiones a crear
	 * @param saveReadable indica si se almacena el archivo en texto plano para lectura
	 * @param reableRowsAmount maxima cantidad de filas a almacenar
	 */
	public void reducedDimensionDoubleMatrix(String fileName, int newDimension, 
												boolean saveReadable, int reableRowsAmount){
		SendMessageAsynch.sendMessage(this.workspace, "Iniciando Proceso de Reduccion de Dimensionalidad para " + 
									newDimension+ " Dimensiones." );
		try{
			
			BigDoubleMatrixFileManager ppmiMatrix = new BigDoubleMatrixFileManager(this.workspace);
			ppmiMatrix.loadReadOnly(EDataFolder.MATRIX,MutualInformation.PPMI_FILE_NAME);
			double[][] matrixReduced = new double[ppmiMatrix.height()][newDimension];
			double[][] newMatrix = new double[ppmiMatrix.height()][matrixReduced[0].length];
			Random random = new Random();
			for (int i = 0; i < matrixReduced.length; i++) {
				for (int j = 0; j < matrixReduced[i].length; j++) {
					random.setSeed(System.nanoTime());
					
					//---------Con 1 y -1
					//int multiplier = 1;
					//if (random.nextBoolean()==false)multiplier = -1;
					//matrixReduced[i][j]=multiplier;
					
					//---------Con 1 y 0
					//int multiplier = 1;
					//if (random.nextBoolean()==false)multiplier = -1;
					//matrixReduced[i][j]=random.nextInt(2)*multiplier;
					
					//---------con raiz de 3
					//probabilidad de 1/6
					int multiplier = 1;
					int randomValue = random.nextInt(7);
					if(randomValue==6){
						multiplier = 1;
					}
					else if(random.nextInt(7)==1){
						multiplier = -1;
					}
					else{
						multiplier=0;
					}
					matrixReduced[i][j]=Math.sqrt(3)*multiplier;
					//fin con raiz de 3
					
				}
			}

			//se necesitan tres instrucciones for para multiplicar cada
			//fila de la una matriz por las columnas de la otra
			for(int i = 0; i < ppmiMatrix.height(); i++){
				for (int j = 0; j < matrixReduced.length; j++){
					for (int k = 0; k < matrixReduced[0].length; k++){
						newMatrix [i][k] += ppmiMatrix.get(i,j)*matrixReduced[j][k];
					}
				}
			}
			
			//Si es de dos dimensiones guarda una copia para la generacion de las graficas
			if(newDimension==2){
				this.workspace.getPersistence().saveDoubleMatrixNio(newMatrix,"reduced2D.dat");
				if(saveReadable){
					this.workspace.getPersistence().saveMatrixDouble(newMatrix, EDataFolder.MATRIX, 
																	"reduced2D.txt",reableRowsAmount,2);
				}
			}

			this.workspace.getPersistence().saveDoubleMatrixNio(newMatrix,REDUCED_FILE_NAME);
			if(saveReadable){
				this.workspace.getPersistence().saveMatrixDouble(newMatrix, EDataFolder.MATRIX, 
																"reduced.txt",reableRowsAmount,2);
			}
			ppmiMatrix.close();
		}
		catch(Exception e){
			logger.error("Error in reducedDimension",e);
		}
		
		SendMessageAsynch.sendMessage(this.workspace,"Proceso de Reduccion de Dimensionalidad finalizado.");
	}
	


}
