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

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

import com.sun.syndication.feed.synd.SyndContent;
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
	private WorkspaceDTO workspace;
	
	public RssScraping(WorkspaceDTO workspace,AtomicInteger atomicSequence){
		logger.debug("Init RssScraping Class");
		this.workspace = workspace;
		this.atomicSequence = atomicSequence;
	}
	/**
	 * Retorna la coleccion de documentos RSS de la fuente
	 * @return
	 */
	public Collection<DocumentDTO> getRssDocuments(SourceDTO source){
		logger.trace("Init getRssDocuments for source:" + source.getUrl());
		boolean ok = false;
		Collection<DocumentDTO> documentCol = new ArrayList<DocumentDTO>();
		try {
			URL feedUrl = new URL(source.getUrl());
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			feed.getPublishedDate();
			ok = true;
			if(source.getLastQuery()==null){
				source.setLastQuery(new Date(0));
			}
			Date lastPublishedDate = source.getLastQuery();

			for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
				logger.debug("Source Last Query:"+source.getLastQuery().toString() + 
						"PublishedDate:"+entry.getPublishedDate());
				if(source.getLastQuery().before(entry.getPublishedDate())){
					if(lastPublishedDate.before(entry.getPublishedDate())){
						lastPublishedDate = entry.getPublishedDate();
					}
					StringBuilder documentData = new StringBuilder();
					String title = entry.getTitle();
					documentData.append(title);
					documentData.append(".");
					documentData.append(" ");
					logger.debug("PublishedDate:"+entry.getPublishedDate().toString() + 
							" LastQuery:"+source.getLastQuery().toString());

					SyndContent description = entry.getDescription();
					if(description.getValue()!=null){
						documentData.append(description.getValue());
						List<SyndContentImpl> contents = (List<SyndContentImpl>) entry.getContents();
						documentData.append(".");
						documentData.append(" ");
						for (SyndContentImpl content : contents) {
							documentData.append(content.getValue());
						}
						DocumentDTO document = new DocumentDTO(String.valueOf(atomicSequence)+".txt");
						this.atomicSequence.incrementAndGet();
						document.setLazyData(documentData.toString());
						document.setDownloadDate(new Date());
						document.setPublishedDate(entry.getPublishedDate());
						document.setSource(source.getUrl());
						document.setSourceType(ESourceType.RSS);
						documentCol.add(document);
						
					}
				}
			}

			source.setLastQuery(lastPublishedDate);
			this.workspace.getPersistence().updateSource(source);

		} catch (Exception ex) {
			logger.info("ERROR: " + ex.getMessage() + " Source:" + source) ;
		}

		return documentCol;
	}

	public void saveRSSDocument(SourceDTO source) throws Exception{
		logger.debug("Init saveRSSDocument Source:" + source.getUrl());
		Collection<DocumentDTO> documentContent = this.getRssDocuments(source);
		logger.info("Documents to save Rss Source:" + source.getUrl() + 
				" Amount:" + documentContent.size());
		
		for (DocumentDTO documentDTO : documentContent) {
			logger.debug("Creating new file:"+ documentDTO.getName());
			this.workspace.getPersistence().writeFile(EDataFolder.DOWNLOAD, documentDTO.getName(), 
											documentDTO.getLazyData());
			this.workspace.getPersistence().insertDocument(documentDTO);
		}

	}


}
