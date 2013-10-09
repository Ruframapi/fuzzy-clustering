package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClusteringFunctions;
import co.edu.sanmartin.fuzzyclustering.machinelearning.reuters.ReutersTrainer;
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
		document.setLazyData("murphy raises crude oil postings cts a bbl yesterday wti to dlrs");
		document.setLazyClusterTerm("Crude");
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("reuters_nostemmed");
		DocumentClusteringFunctions documentClustering = new DocumentClusteringFunctions(workspace);
		documentClustering.loadMembershipTermMatrix();
		LinkedHashMap<String,Double[]> membershipMatrix = documentClustering.getDocumentMembershipMatrix(document);
		Double[] avg = documentClustering.getAvgMembershipMatrix(membershipMatrix);
		Double[] vote = documentClustering.getVoteMembershipMatrix(membershipMatrix);
		Double[] weightAverage = documentClustering.getFuzzyWeightedAverage(avg, vote, 3);
		System.out.println(Arrays.toString(weightAverage));
		
	}
	

	
}
