package co.edu.sanmartin.fuzzyclustering.machinelearning.reuters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ESourceType;
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
	
	public void classyfyReutersStemmed1(){
		DocumentCluster documentCluster = new DocumentCluster(this.workspace);
		documentCluster.loadMembershipTermDocument();
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (DocumentDTO documentDTO : testReutersDocument) {
			documentCluster.getDocumentMembership(documentDTO);
		}
	}
	
	/**
	 * Metodo encargado de validar el archivo con el modelo
	 */
	public void classifyDocumentsInFile(){
		logger.info("init classifyDocumentsInFile");
		StringBuilder stringBuilder = new StringBuilder();
		DocumentClustering documentCluster = new DocumentClustering(this.workspace);
		//Almacen el termino y la cuenta del cluster
		HashMap<String,ArrayList<Integer>> clusterCounter = new HashMap<String,ArrayList<Integer>>();
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (DocumentDTO documentDTO : testReutersDocument) {
			documentCluster.clustering(documentDTO);
			Double[] weightedAverage = documentCluster.getMembershipAverage();
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
			ArrayList clusterDocumentArray = clusterCounter.get(documentDTO.getLazyClusterTerm());
			if(clusterDocumentArray==null){
				clusterDocumentArray = new ArrayList<Integer>();
			}
			clusterDocumentArray.add(clusterNumber);
			clusterCounter.put(documentDTO.getLazyClusterTerm(), clusterDocumentArray);
			
			stringBuilder.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MACHINE_LEARNING, 
				"clusterdocument.txt", stringBuilder.toString());
		logger.info("init classifyDocumentsInFile Ended");
	}
	
	
	/**
	 * Retorna los documentos de Test de Reuters
	 * @return
	 */
	private ArrayList<DocumentDTO> testReutersDocumentsCol(){
		String fileReuters = this.workspace.getPersistence().readRootFile(EDataFolder.TRAIN, "r8-test-all-terms.txt");
		String[] row = fileReuters.split(System.getProperty("line.separator"));
		
		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setName(String.valueOf(i));
			String validationTerm = row[i].split("\t")[0];
			String normalizedTerm = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(validationTerm);
			document.setLazyClusterTerm(normalizedTerm.replace(",", ""));
			document.setLazyData(row[i].split("\t")[1]);
			String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
			document.setLazyCleanData(normalizedText);
			fileCol.add(document);
		}
		return fileCol;
	}
	
	
	
}
