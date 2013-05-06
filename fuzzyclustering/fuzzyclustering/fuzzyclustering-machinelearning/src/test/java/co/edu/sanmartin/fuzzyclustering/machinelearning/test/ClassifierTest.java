package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.reuters.ReutersTrainer;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

public class ClassifierTest {
	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias");
	
	@Test
	public void testReutersClassifier(){
		
		DocumentCluster classifier = new DocumentCluster(workspace);
		classifier.buildMembershipIndex("train");
	}
	
	@Test
	public void classyfyReutersStemmedTest(){
		ReutersTrainer reuters = new ReutersTrainer(this.workspace);
		reuters.classyfyReutersStemmed();
	}

}
