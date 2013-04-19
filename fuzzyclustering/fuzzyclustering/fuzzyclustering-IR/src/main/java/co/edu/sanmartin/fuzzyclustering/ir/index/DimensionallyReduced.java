package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.Random;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;

public class DimensionallyReduced {

	private static Logger logger = Logger.getLogger("DimensionallyReduced");
	public static final String REDUCED_FILE_NAME = "reduced.dat"; 
	/**
	 * Metodo encargado de realiza la reduccion de dimensionalidad de la matrix ppmi
	 * @param fileName nombre del archivo
	 * @param newDimension nuevo numero de dimensiones a crear
	 * @param saveReadable indica si se almacena el archivo en texto plano para lectura
	 * @param reableRowsAmount maxima cantidad de filas a almacenar
	 */
	public void reducedDimensionDoubleMatrix(String fileName, int newDimension, 
												boolean saveReadable, int reableRowsAmount){
		SendMessageAsynch.sendMessage("Iniciando Proceso de Reduccion de Dimensionalidad para " + 
									newDimension+ " Dimensiones." );
		try{
			BigDoubleMatrixFileManager ppmiMatrix = 
					new BigDoubleMatrixFileManager();
			ppmiMatrix.loadReadOnly(EDataFolder.MATRIX,MutualInformation.PPMI_FILE_NAME);
			double[][] matrixReduced = new double[ppmiMatrix.height()][newDimension];
			double[][] newMatrix = new double[ppmiMatrix.height()][matrixReduced[0].length];
			Random random = new Random();
			for (int i = 0; i < matrixReduced.length; i++) {
				random.setSeed(System.nanoTime());
				for (int j = 0; j < matrixReduced[i].length; j++) {
					int multiplier = 1;
					//if (random.nextBoolean()==false)multiplier = -1;
					matrixReduced[i][j]=random.nextInt(2)*multiplier;
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

			PersistenceFacade.getInstance().saveDoubleMatrixNio(newMatrix, REDUCED_FILE_NAME);
			if(saveReadable){
				PersistenceFacade.getInstance().saveMatrixDouble(newMatrix, EDataFolder.MATRIX, 
																"reduced.txt",reableRowsAmount,2);
			}
			ppmiMatrix.close();
		}
		catch(Exception e){
			logger.error("Error in reducedDimension",e);
		}
		
		SendMessageAsynch.sendMessage("Proceso de Reduccion de Dimensionalidad finalizado.");
	}



}
