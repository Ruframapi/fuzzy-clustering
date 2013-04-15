import org.junit.Test;

import co.edu.sanmartin.queryasynch.execute.CallRemoteServlet;


public class QueryAsynchTest {

	@Test
	public void sendMessageTest(){
		CallRemoteServlet call = new CallRemoteServlet();
		call.sendMessage("Test");
	}
}
