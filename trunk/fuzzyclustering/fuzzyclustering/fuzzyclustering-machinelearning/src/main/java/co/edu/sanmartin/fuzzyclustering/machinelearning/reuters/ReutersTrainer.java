package co.edu.sanmartin.fuzzyclustering.machinelearning.reuters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
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
	
	public void classyfyReutersStemmed2(){
		StringBuilder stringBuilder = new StringBuilder();
		DocumentCluster documentCluster = new DocumentCluster(this.workspace);
		documentCluster.loadMembershipTermDocument();
		ArrayList<DocumentDTO> testReutersDocument = this.testReutersDocumentsCol();
		for (DocumentDTO documentDTO : testReutersDocument) {
			
			ArrayList result = documentCluster.getDocumentMembership(documentDTO, "acq" );
			stringBuilder.append(result.get(0));
			stringBuilder.append("\t");
			stringBuilder.append(result.get(1));
			stringBuilder.append("\t");
			stringBuilder.append(result.get(2));
			stringBuilder.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MACHINE_LEARNING, 
				"clusterdocument.txt", stringBuilder.toString());
	}
	
	
	/**
	 * Retorna los documentos de Test de Reuters
	 * @return
	 */
	private ArrayList<DocumentDTO> testReutersDocumentsCol(){
		String fileReuters = this.workspace.getPersistence().readRootFile(EDataFolder.TRAIN, "r8-test-stemmed.txt");
		String[] row = fileReuters.split(System.getProperty("line.separator"));
		
		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setLazyCleanData(row[i].split("\t")[1]);
			fileCol.add(document);
		}
		return fileCol;
	}
	
	
	
}
