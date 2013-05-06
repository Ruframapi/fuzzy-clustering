package co.edu.sanmartin.webscraping.execute.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.webscraping.rss.RssScraping;
import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

/**
 * Worker encargado de la descarga de los documentos de internet
 * Cada fuente es un worker diferente
 * @author Ricardo Carvajal Salamanca
 *
 */
public class TwitterDownloadWorkerThread implements Callable<String>{

	private static Logger logger = Logger.getRootLogger();
	private SourceDTO sourceDTO;
	private TwitterScraping twitterScraping;
	private AtomicInteger sequence;
	private String dataRoot;
	private WorkspaceDTO workspace;
	
	public TwitterDownloadWorkerThread(WorkspaceDTO workspace, SourceDTO sourceDTO, AtomicInteger sequence) throws TwitterException {
		logger.debug("Init TwitterDownloadWorkerThread");
		this.workspace = workspace;
		this.sequence = sequence;
		this.sourceDTO = sourceDTO;
		twitterScraping = new TwitterScraping(workspace,sequence);
	}

	public String call() throws Exception {
		logger.debug("Init TwitterDownloadWorkerThread Run Method");
		logger.debug("sourceDTO.getUrl()");
		twitterScraping.saveTwitterDocument(this.dataRoot, this.sourceDTO);
		return "true";
	}

}
