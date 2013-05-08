package co.edu.sanmartin.webscraping.test;

import org.junit.Test;

import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;
import co.edu.sanmartin.webscraping.train.ReutersRssBuilder;

public class ReutersRssBuilderTest {

	@Test
	public void createRssReutersR8(){;
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("prueba");
		ReutersRssBuilder rssBuilder = new ReutersRssBuilder(workspace);
		rssBuilder.createRssReutersR8();
	}
}
