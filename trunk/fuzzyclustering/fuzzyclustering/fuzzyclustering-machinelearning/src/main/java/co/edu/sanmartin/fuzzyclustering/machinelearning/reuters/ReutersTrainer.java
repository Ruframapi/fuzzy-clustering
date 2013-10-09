package co.edu.sanmartin.fuzzyclustering.machinelearning.reuters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class ReutersTrainer {

	private static Logger logger = Logger.getLogger("ReutersTrainer");
	private WorkspaceDTO workspace;


	public ReutersTrainer(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	public void classyfyReutersStemmed(){
		DocumentCluster documentCluster = new DocumentCluster(this.workspace);
		documentCluster.loadMembershipTermDocument();
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (DocumentDTO documentDTO : testReutersDocument) {
			documentCluster.getDocumentTermMembership(documentDTO);
		}
	}

	/**
	 * Metodo encargado de validar el archivo con el modelo
	 */
	public void classifyDocumentsInFile(){
		logger.info("init classifyDocumentsInFile");
		StringBuilder stringBuilder = new StringBuilder();
		DocumentClustering documentCluster = new DocumentClustering(this.workspace);
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (DocumentDTO documentDTO : testReutersDocument) {
			documentCluster.clustering(documentDTO);
			Double[] weightedAverage = documentCluster.getWeightAverage();
			//Si no se encuentra ningun termino
			if(weightedAverage==null){
				stringBuilder.append("\t");
				stringBuilder.append("No se encuentra Pertenencia");
				stringBuilder.append(System.getProperty("line.separator"));
				continue;
			}
			stringBuilder.append(documentDTO.getLazyClusterTerm());
			double tmpMaxAverage = 0.0;
			int clusterNumber = 0;
			for (int i = 0; i < weightedAverage.length; i++) {
				stringBuilder.append("\t");
				stringBuilder.append(weightedAverage[i]);
				if(tmpMaxAverage<weightedAverage[i] && weightedAverage[i]<1){
					tmpMaxAverage = weightedAverage[i];
					clusterNumber = i;
				}
			}
			stringBuilder.append("\t");
			stringBuilder.append(clusterNumber);

			stringBuilder.append(System.getProperty("line.separator"));
		}
		this.buildCategory(stringBuilder.toString());
		this.workspace.getPersistence().writeFile(EDataFolder.MACHINE_LEARNING, 
				"clusterdocument.txt", stringBuilder.toString());
		logger.info("init classifyDocumentsInFile Ended");
	}

	/**
	 * Metodo encargado de validar el archivo con el modelo
	 */
	public void classifyDocumentsInFile1() throws Exception{
		logger.info("init classifyDocumentsInFile 1");
		double[][] documentsTable = null; 
		DocumentClustering documentCluster = new DocumentClustering(this.workspace);
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (int i = 0; i < testReutersDocument.size(); i++) {
			DocumentDTO documentDTO = testReutersDocument.get(i);
			documentCluster.clustering(documentDTO);
			Double[] weightedAverage = documentCluster.getWeightAverage();
			if(documentsTable==null){
				documentsTable = new double[testReutersDocument.size()][weightedAverage.length];
			}
			documentsTable[i]=ArrayUtils.toPrimitive(weightedAverage);
		}
		this.workspace.getPersistence().saveMatrixDouble(documentsTable, EDataFolder.TRAIN, "halvector.txt", 0, 5);
		this.workspace.getPersistence().saveDoubleMatrixNio(documentsTable,"halvector.dat");
	}
	
	/**
	 * Construye las categorias del documento para calcular el precision  y recall
	 * @param documentCluster
	 */
	public void buildCategory(String documentCluster){

		HashMap<String,Integer[]> testCluster = new HashMap<String,Integer[]>(); 
		HashMap<String,Integer> resume = new HashMap<String,Integer>(); 
		String[] documentClusterRow = documentCluster.split(System.getProperty("line.separator"));
		
		for (String row : documentClusterRow) {
			String[] rowSplit = row.split("\t");
			String category = rowSplit[0];
			Integer cluster = 	Integer.valueOf(rowSplit[rowSplit.length-1]);	
			Integer[] categoryCluster = testCluster.get(rowSplit[0]);
			if(categoryCluster==null){
				categoryCluster = new Integer[rowSplit.length-2];
				for (int i = 0; i < categoryCluster.length; i++) {
					categoryCluster[i]=0;			
				}
			}
			categoryCluster[cluster]++;
			testCluster.put(category, categoryCluster);
		}
		//Definimos el mayor de cada cluster por conteo
		Iterator iterator = testCluster.entrySet().iterator();
		Integer tmpMaxValue = 0;
		Integer tmpCluster = 0;
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Integer[] value =(Integer[]) e.getValue();
			for (int i = 0; i < value.length; i++) {
				if(value[i]>tmpMaxValue){
					tmpMaxValue = value[i];
					tmpCluster=i;
				}
			}
			resume.put(key, tmpCluster);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for (String row : documentClusterRow) {
			String[] rowSplit = row.split("\t");
			Integer rowCluster = Integer.valueOf(rowSplit[rowSplit.length-1]);
			Integer categoryCluster = resume.get(rowSplit[0]);
			if(rowCluster.equals(categoryCluster)){
				stringBuilder.append(row);
				stringBuilder.append("\t");
				stringBuilder.append(categoryCluster);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MACHINE_LEARNING, 
				"clusterdocumentRecall.txt", stringBuilder.toString());
		logger.info("init classifyDocumentsInFile Ended");

	}

	/**
	 * Retorna los documentos de Test de Reuters
	 * @return
	 */
	private ArrayList<DocumentDTO> testReutersDocumentsCol(){
		String fileReuters = this.workspace.getPersistence().readFile(EDataFolder.TRAIN, "test-all-terms.txt");
		String[] row = fileReuters.split(System.getProperty("line.separator"));

		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setName(String.valueOf(i));
			String validationTerm = row[i].split("\t")[0];
			document.setLazyClusterTerm(validationTerm);
			//String normalizedTerm = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(validationTerm);
			//document.setLazyClusterTerm(normalizedTerm.replace(",", ""));
			document.setLazyData(row[i].split("\t")[1]);
			//String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
			//document.setLazyCleanData(normalizedText);
			fileCol.add(document);
		}
		return fileCol;
	}

	
	/**
	 * Retorna los documentos de Test de Reuters
	 * @return
	 */
	private ArrayList<DocumentDTO> testDocumentsCol(){
		String fileReuters = this.workspace.getPersistence().readFile(EDataFolder.TRAIN, "test-all-terms.txt");
		String[] row = fileReuters.split(System.getProperty("line.separator"));

		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setName(String.valueOf(i));
			String validationTerm = row[i].split("\t")[0];
			document.setLazyClusterTerm(validationTerm);
			//String normalizedTerm = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(validationTerm);
			//document.setLazyClusterTerm(normalizedTerm.replace(",", ""));
			document.setLazyData(row[i].split("\t")[1]);
			//String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
			//document.setLazyCleanData(normalizedText);
			fileCol.add(document);
		}
		return fileCol;
	}


}
