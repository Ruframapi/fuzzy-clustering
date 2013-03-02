package co.edu.sanmartin.webscraping.execute.worker;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.webscraping.rss.RssScraping;

/**
 * Worker encargado de la descarga de los documentos de internet
 * Cada fuente es un worker diferente
 * @author Ricardo Carvajal Salamanca
 *
 */
public class RssDownloadWorkerThread implements Callable<String>{

	private static Logger logger = Logger.getRootLogger();
	private SourceDTO sourceDTO;
	RssScraping rssScraping = new RssScraping();
	
	public RssDownloadWorkerThread(SourceDTO sourceDTO) {
		logger.info("Init RssDownloadWorkerThread");
		this.sourceDTO = sourceDTO;
	}

	public String call() throws Exception {
		logger.info("Init RssDownloadWorkerThread Run Method");
		logger.info("sourceDTO.getUrl()");
		rssScraping.saveRSSDocument(sourceDTO.getUrl());
		return "true";
	}

}
