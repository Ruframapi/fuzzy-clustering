package co.edu.sanmartin.persistence.test;

import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.persistence.constant.properties.PropertiesLoader;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.QueueFacade;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

/**
 * Pruebas de persitencia
 * @author Ricardo
 *
 */
public class PersistenceTest {

	private WorkspaceDTO workspace = WorkspaceFacade.getInstance().getWorkspace("noticias");
	@Test
	@Ignore
	public void testInitialize(){
		try {
			this.workspace.getPersistence().initialize();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void propertiesTest(){
		PropertiesLoader loader = PropertiesLoader.getInstance();
		loader.getProperty("test");
	}
	
	@Test
	public void getServerDateTest(){
		System.out.print(QueueFacade.getInstance().getServerDate().toString());
	}
	
}
