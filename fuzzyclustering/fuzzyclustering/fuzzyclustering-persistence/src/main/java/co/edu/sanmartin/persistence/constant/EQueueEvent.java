package co.edu.sanmartin.persistence.constant;

/**
 * Enumeracion de los Eventos de la cola de procesos
 * @author Ricardo
 *
 */
public enum EQueueEvent {
	
	DOWNLOAD_RSS,
	DOWNLOAD_TWITTER,
	CANCEL_DOWNLOAD, 
	CLEAN_DOWNLOAD,
	GENERATE_INVERTED_INDEX,
	GENERATE_TERM_DOCUMENT_MATRIX,
	RELOAD_DATA_MEMORY,
	QUERY_DOCUMENT;
	
}
