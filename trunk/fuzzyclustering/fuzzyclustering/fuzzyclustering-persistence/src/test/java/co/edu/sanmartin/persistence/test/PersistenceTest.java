package co.edu.sanmartin.persistence.test;

import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.persistence.constant.properties.PropertiesLoader;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Pruebas de persitencia
 * @author Ricardo
 *
 */
public class PersistenceTest {

	@Test
	@Ignore
	public void testInitialize(){
		try {
			PersistenceFacade.getInstance().initialize();
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
		System.out.print(PersistenceFacade.getInstance().getServerDate().toString());
	}
	
}
