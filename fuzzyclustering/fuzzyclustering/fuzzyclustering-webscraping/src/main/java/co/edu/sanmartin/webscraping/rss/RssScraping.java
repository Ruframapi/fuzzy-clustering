package co.edu.sanmartin.webscraping.rss;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.SourceDTO;
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
    
    public RssScraping(AtomicInteger atomicSequence){
    	logger.info("Init RssScraping Class");
    	this.atomicSequence = atomicSequence;
    }
    /**
     * Retorna la coleccion de documentos RSS de la fuente
     * @return
     */
    public Collection<String> getRssDocuments(SourceDTO source){
    	boolean ok = false;
    	Collection<String> documentCol = new ArrayList<String>();
        try {
            
            URL feedUrl = new URL(source.getUrl());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            feed.getPublishedDate();
            // System.out.println(feed);
            ok = true;
            boolean isNew = false;
            if (source.getLastQuery()==null){
            	source.setLastQuery(new Date());
            	isNew = true;
            }
            for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
            	StringBuilder documentData = new StringBuilder();
                String title = entry.getTitle();
                documentData.append(title);
                documentData.append(".");
                documentData.append(" ");
                String uri = entry.getUri();
                Date publishedDate = entry.getPublishedDate();
                
                if (isNew || publishedDate.after(source.getLastQuery())){
	                // Get the Contents
	                for (SyndContentImpl content : (List<SyndContentImpl>) entry.getContents()) {
	                    documentData.append(content.getValue());
	                    System.out.println("Content: " + documentData.toString());
	                    documentCol.add(documentData.toString());
	                }
	                
	                feed.createWireFeed();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }

        return documentCol;
    }

    public void saveRSSDocument(SourceDTO source) throws Exception{
    	logger.info("Init saveRSSDocument");
    	Collection<String> documentContent = this.getRssDocuments(source);
    	
    	for (String content : documentContent) {
    		logger.info("Creating new file number:"+ atomicSequence);
    		String fileName = String.valueOf(atomicSequence)+".txt";
    		this.atomicSequence.incrementAndGet();
			PersistenceFacade.getInstance().writeFile(EDataFolder.ORIGINAL_RSS, fileName, content);
		}
    	source.setLastQuery(new Date());
    	PersistenceFacade.getInstance().updateSource(source);
    }
    

}
