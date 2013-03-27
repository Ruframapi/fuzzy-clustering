package co.edu.sanmartin.webscraping.execute.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

import co.edu.sanmartin.persistence.dto.SourceDTO;
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
	TwitterScraping twitterScraping;
	AtomicInteger sequence;
	
	public TwitterDownloadWorkerThread(SourceDTO sourceDTO, AtomicInteger sequence) throws TwitterException {
		logger.debug("Init TwitterDownloadWorkerThread");
		this.sequence = sequence;
		this.sourceDTO = sourceDTO;
		twitterScraping = new TwitterScraping();
	}

	public String call() throws Exception {
		logger.debug("Init TwitterDownloadWorkerThread Run Method");
		logger.debug("sourceDTO.getUrl()");
		twitterScraping.saveTwitterDocument(sourceDTO.getUrl(), sequence);
		return "true";
	}

}
