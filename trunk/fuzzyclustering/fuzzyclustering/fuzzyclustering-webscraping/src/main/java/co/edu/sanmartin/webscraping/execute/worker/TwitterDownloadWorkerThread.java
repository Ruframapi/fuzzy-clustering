package co.edu.sanmartin.webscraping.execute.worker;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

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
	TwitterScraping twitterScraping = new TwitterScraping();
	
	public TwitterDownloadWorkerThread(SourceDTO sourceDTO) {
		logger.info("Init TwitterDownloadWorkerThread");
		this.sourceDTO = sourceDTO;
	}

	public String call() throws Exception {
		logger.info("Init TwitterDownloadWorkerThread Run Method");
		logger.info("sourceDTO.getUrl()");
		twitterScraping.saveTwitterDocument(sourceDTO.getUrl());
		return "true";
	}

}
