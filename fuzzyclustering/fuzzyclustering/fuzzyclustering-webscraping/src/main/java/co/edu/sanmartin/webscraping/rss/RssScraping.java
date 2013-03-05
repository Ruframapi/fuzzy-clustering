package co.edu.sanmartin.webscraping.rss;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Clase que realiza la descarga de documentos desde fuentes RSS
 * @author Ricardo Carvajal Salamanca
 *
 */
public class RssScraping{
	
	private static Logger logger = Logger.getRootLogger();
	
    Set urlSet = new HashSet();
    private AtomicInteger atomicSequence;
    
    public RssScraping(){
    	logger.info("Init RssScraping Class");
    	this.atomicSequence = new AtomicInteger();
    }
    /**
     * Retorna la coleccion de documentos RSS de la fuente
     * @return
     */
    public Collection<String> getRssDocuments(String source){
    	boolean ok = false;
    	Collection<String> documentCol = new ArrayList<String>();
        try {
            
            URL feedUrl = new URL(source);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            
            // System.out.println(feed);
            ok = true;
            for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
            	StringBuilder documentData = new StringBuilder();
                String title = entry.getTitle();
                documentData.append(title);
                documentData.append(".");
                documentData.append(" ");
                String uri = entry.getUri();
                String date = entry.getPublishedDate().toString();            
                // Get the Contents
                for (SyndContentImpl content : (List<SyndContentImpl>) entry.getContents()) {
                    documentData.append(content.getValue());
                    System.out.println("Content: " + documentData.toString());
                    documentCol.add(documentData.toString());
                }
                
                feed.createWireFeed();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }

        return documentCol;
    }

    public void saveRSSDocument(String data){
    	logger.info("Init saveRSSDocument");
    	Collection<String> documentContent = this.getRssDocuments(data);
    	
    	for (String content : documentContent) {
    		logger.info("Creating new file number:"+ atomicSequence);
    		String fileName = String.valueOf(atomicSequence)+".txt";
    		int atomicSequence = this.atomicSequence.incrementAndGet();
			PersistenceFacade.getInstance().writeFile(EDataFolder.ORIGINAL_RSS, fileName, content);
		}
    }
    

}
