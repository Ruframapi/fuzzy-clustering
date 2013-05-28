package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.util.ArrayList;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.fuzzyclustering.machinelearning.facade.MachineLearningFacade;
import co.edu.sanmartin.fuzzyclustering.machinelearning.reuters.ReutersTrainer;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

public class ClassifierTest {
	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("reuters_nostemmed");
	
	@Test
	public void testReutersClassifier(){
		DocumentCluster classifier = new DocumentCluster(workspace);
		classifier.buildMembershipIndex();
	}
	
	@Test
	public void classyfyReutersStemmedTest(){
		ReutersTrainer reuters = new ReutersTrainer(this.workspace);
		reuters.classyfyReutersStemmed();
	}
	
	@Test
	public void classyfyReutersStemmedTest1(){
		ReutersTrainer reuters = new ReutersTrainer(this.workspace);
		reuters.classyfyReutersStemmed1();
	}
	
	@Test
	public void classyfyReutersStemmedTest2(){
		ReutersTrainer reuters = new ReutersTrainer(this.workspace);
		reuters.classifyDocumentsInFile();
	}
	
	@Test
	public void classyfyText(){
		DocumentDTO document = new DocumentDTO();
		workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		document.setLazyData("Venezuela afirm� ayer que en lo que va de este a�o ha exportado 626.000 barriles diarios de petr�leo a China");
		document.setLazyClusterTerm("Petroleo");
		MachineLearningFacade machineLearning = new MachineLearningFacade();
		DocumentClustering result = machineLearning.getDocumentClustering(workspace, document);
		String membership = result.toString();
		System.out.print(membership);
	}

}
