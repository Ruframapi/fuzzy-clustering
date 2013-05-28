package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.util.HashMap;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClusteringFunctions;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

public class DocumentClusteringTest {

	@Test
	public void buildMembershipTermMatrixTest(){
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		DocumentClusteringFunctions documentClustering = new DocumentClusteringFunctions(workspace);
		documentClustering.loadMembershipTermMatrix();
		
	}
	
	@Test
	public void getDocumentMembershipMatrixTest(){
		DocumentDTO document = new DocumentDTO();
		document.setLazyData("Venezuela afirmó ayer que en lo que va de este año ha exportado 626.000 barriles diarios de petróleo a China");
		document.setLazyClusterTerm("Petroleo");
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		DocumentClusteringFunctions documentClustering = new DocumentClusteringFunctions(workspace);
		documentClustering.loadMembershipTermMatrix();
		HashMap<String,Double[]> membershipMatrix = documentClustering.getDocumentMembershipMatrix(document);
		Double[] avg = documentClustering.getAvgMembershipMatrix(membershipMatrix);
		Double[] vote = documentClustering.getVoteMembershipMatrix(membershipMatrix);
		Double[] weightAverage = documentClustering.getFuzzyWeightedAverage(avg, vote);
		
		
	}
	
}
