package co.edu.sanmartin.queryasynch.test;
import java.sql.SQLException;

import org.junit.Test;

import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;
import co.edu.sanmartin.queryasynch.execute.CallRemoteServlet;


public class QueryAsynchTest {

	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias");
	@Test
	public void sendMessageTest(){
		WorkspaceDTO workspace;
		CallRemoteServlet call = new CallRemoteServlet(this.workspace);
		call.sendMessage("Test");
	}
}
