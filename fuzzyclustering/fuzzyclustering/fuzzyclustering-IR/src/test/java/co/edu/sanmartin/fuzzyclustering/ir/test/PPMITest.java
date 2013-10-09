package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

public class PPMITest {

	public PPMITest(){
		BasicConfigurator.configure();
	}
	@Test
	public void intersectionTermsTest(){
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.loadInvertedIndexDataZipf();
		ArrayList<Integer> intersection = invertedIndex.intersectionDocumentTerms("porcion", "sals");
		System.out.print("Intersection: " + intersection.toString());
	}
	
	@Test
	public void calculateMutualInformation(){
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		MutualInformation mutualInformation = new MutualInformation(workspace);
		mutualInformation.buildMutualInformation(true);
	}
	@Test
	public void calculateMutualInformationContext(){
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		MutualInformation mutualInformation = new MutualInformation(workspace);
		mutualInformation.buildContextMutualInformation(true, new String[]{"petrol","telecom","inform","aero"});
	}
}
