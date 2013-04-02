package co.edu.sanmartin.webscraping.execute;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class CallRemoteServlet {
	private static String webServerURL;
	
	public CallRemoteServlet(){
		PersistenceFacade persistence = PersistenceFacade.getInstance();
		StringBuilder url = new StringBuilder();
		try {
			url.append(persistence.getProperty(ESystemProperty.WEB_SERVER_IP).getValue());
			url.append(":");
			url.append(persistence.getProperty(ESystemProperty.WEB_SERVER_PORT).getValue());
			url.append("/");
			url.append(persistence.getProperty(ESystemProperty.WEB_SERVER_CONTEXT).getValue());
			webServerURL = url.toString();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendDocument() throws URISyntaxException{

		try {
			HttpClient client = new DefaultHttpClient();
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("fuzzyclustering-springtree.rhcloud.com/fuzzyclustering-web-1.0").setPath("/AsynchServlet")
			.setParameter("status", "2q");
			URI uri = builder.build();
			HttpPost httppost = new HttpPost(uri);
			HttpResponse response = client.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//HttpGet httpget = new HttpGet(uri);
		//System.out.println(httppost.getURI());

	}
}
