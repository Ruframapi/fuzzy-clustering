package co.edu.sanmartin.webscraping.execute.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

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
	RssScraping rssScraping;
	
	public RssDownloadWorkerThread(SourceDTO sourceDTO, AtomicInteger sequence) {
		logger.debug("Init RssDownloadWorkerThread source" + sourceDTO.getUrl());
		this.sourceDTO = sourceDTO;
		this.rssScraping = new RssScraping(sequence);
	}

	public String call() throws Exception {
		logger.debug("Init RssDownloadWorkerThread Run Method");
		logger.debug("sourceDTO.getUrl()");
		rssScraping.saveRSSDocument(sourceDTO);
		
		return "true";
	}

}
