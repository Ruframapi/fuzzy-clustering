package co.edu.sanmartin.webscraping.rss;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.file.FileManager;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;


/**
 * Clase encargada de crear un archivo Rss a partir de un archivo de texto
 * @author Ricardo Carvajal Salamanca
 *
 */
public class RssBuilder {

	public WorkspaceDTO workspace;


	public RssBuilder(WorkspaceDTO workspace){
		this.workspace = workspace;
	}



	public void createRss(Collection<DocumentDTO> documentsCol, String fileName){
		SyndFeed feed = new SyndFeedImpl();
		List entries = this.getEntriesFromDocuments(documentsCol);
		feed = this.buildRss(entries);
		this.writeFeed(feed, "reutersr8.xml");
		//this.workspace.getPersistence().writeFile(EDataFolder.TRAIN, "reuters.xml", feed.);
	}

	/**
	 * Retorna la lista de entries a partir de los documentos
	 * @param documentsCol
	 * @return
	 */
	public List getEntriesFromDocuments(Collection<DocumentDTO> documentsCol){
		List entries = new ArrayList();
		for (DocumentDTO documentDTO : documentsCol) {
			SyndEntry entry;
			SyndContent description;
			entry = new SyndEntryImpl();
			entry.setTitle("test");
			entry.setLink("https://fuzzyclustering-springtree.rhcloud.com/fuzzyclustering-web-1.0/");
			entry.setPublishedDate(new Date());
			description = new SyndContentImpl();
			description.setType("text/plain");
			description.setValue(documentDTO.getLazyData());
			entry.setDescription(description);
			entries.add(entry);
		}
		return entries;

	}

	public SyndFeed buildRss(List entries){
		String feedType = "rss_2.0";
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(feedType);
		feed.setTitle("Reuters Test News");
		feed.setLink("https://fuzzyclustering-springtree.rhcloud.com/fuzzyclustering-web-1.0");
		feed.setDescription("Reuters R8Test");
		feed.setEntries(entries);
		return feed;
	}

	public void writeFeed(SyndFeed feed, String fileName){
		Writer writer;
		try {
			FileManager fileManager = new FileManager(this.workspace);
			StringBuilder path = new StringBuilder();
			path.append(fileManager.getFolderPath(EDataFolder.TRAIN));
			path.append(System.getProperty("file.separator"));
			path.append(fileName);
			writer = new FileWriter(path.toString());
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed,writer);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
