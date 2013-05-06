package co.edu.sanmartin.queryasynch.execute;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class CallRemoteServlet {
	private static String httpProtocol;
	private static String webServerURL;
	private static Logger logger = Logger.getLogger("CallRemoteServlet");
	private WorkspaceDTO workspace;
	
	public CallRemoteServlet(WorkspaceDTO workspace){
		this.workspace = workspace;
		
		try{
			webServerURL = this.workspace.getPersistence().getProperty(ESystemProperty.WEB_SERVER_CONTEXT).getValue();
			httpProtocol = this.workspace.getPersistence().getProperty(ESystemProperty.WEB_SERVER_PROTOCOL).getValue();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendDocument(DocumentDTO document) throws URISyntaxException{
		logger.trace("Init sendDocument Method" + document.getId());
		try {
			HttpClient client = new DefaultHttpClient();
			URIBuilder builder = new URIBuilder();
			builder.setScheme(this.httpProtocol).setHost(this.webServerURL).setPath("/AsynchServlet")
			//builder.setScheme("http").setHost("localhost:8085/fuzzyclustering-web").setPath("/AsynchServlet")
			.setParameter("documentName", document.getName())
			.setParameter("originalDocument", document.getLazyData())
			.setParameter("cleanDocument", document.getLazyCleanData());
			logger.info("Sending asynch document info to web server document:" + document.getId());
			URI uri = builder.build();
			HttpPost httppost = new HttpPost(uri);
			HttpResponse response = client.execute(httppost);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} 
		//HttpGet httpget = new HttpGet(uri);
		//System.out.println(httppost.getURI());
	}
	
	/**
	 * Envia un mensaje al cliente
	 * @param message
	 */
	public void sendMessage(String message) {
		// TODO Auto-generated method stub
		logger.trace("Init sendMessage Method" + message);
		try {
			HttpClient client = new DefaultHttpClient();
			URIBuilder builder = new URIBuilder();
			builder.setScheme(this.httpProtocol).setHost(this.webServerURL).setPath("/AsynchServlet")
			//builder.setScheme("http").setHost("localhost:8085/fuzzyclustering-web").setPath("/AsynchServlet")
			.setParameter("message", message);
			logger.info("Sending asynch message info to web server:" + message);
			URI uri = builder.build();
			HttpPost httppost = new HttpPost(uri);
			HttpResponse response = client.execute(httppost);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} 
	}
}
