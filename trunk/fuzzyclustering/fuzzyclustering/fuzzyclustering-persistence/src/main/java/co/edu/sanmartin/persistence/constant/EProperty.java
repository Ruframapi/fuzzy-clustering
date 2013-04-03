package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Enumeración de las propiedades de configuraciones del sistema
 * @author Ricardo Carvajal Salamanca
 *
 */
public enum EProperty {
	
	LANGUAGE("language", "Idioma a procesar"),
	TWITTER_CONSUMER_KEY("twitter.consumer.key", "Twitter Consumer KEY"), 
	TWITTER_CONSUMER_SECRET("twitter.consumer.secret", "Twitter Consumer Secret"), 
	TWITTER_ACCESS_TOKEN("twitter.access.token", "Twitter Access Token"), 
	TWITTER_ACCESS_TOKEN_SECRET("twitter.access.token.secret", "Twitter Access Token Secret"),
	TWITTER_HOME_TIMELINE_PAGES("twitter.home.timeline.pages","Cantidad de paginas de descarga de twitter"),
	WEB_SCRAPPING_RSS_DOWNLOAD_TIME("webscraping.rss.download.time","tiempo entre busquedas de noticicas RSS"),
	WEB_SCRAPPING_TWITTER_DOWNLOAD_TIME("webscraping.twitter.download.time","tiempo entre busquedas de noticicas RSS"),
	WEB_SCRAPPING_THREAD_NUMBER("webscrapin.thread.number", "Numero de Hilos de Descarga"),
	CLEAN_THREAD_NUMBER("clean.thread.number", "Numero de Hilos de Proceso de Limpieza"),
	INVERTED_INDEX_THREAD_NUMBER("inverted.index.thread.number", "Numero de Hilos de Indices Invertidos"),
	IR_DELETE_ADVERBS("ir.delete.adverbs", "Elimina Adverbios"),
	IR_DELETE_CONJUNCTIONS("ir.delete.conjunctions", "Elimina Conjunciones"),
	IR_DELETE_DETERMINANTS("ir.delete.determinants", "Elimina Determinantes"),
	IR_DELETE_PREPOSITIONS("ir.delete.prepositions", "Elimina Preposiciones"),
	IR_DELETE_PRONOUNS("ir.delete.pronouns", "Elimina Pronombres"),
	TEXT_SPLIT_TOKEN("text.split.token", "Token para separacion de textos"),
	TEXT_STEMMER_ON("text.stemmer.on","Indica si realiza lematizacion(stemming) al texto");
	
	private String propertyName;

	EProperty(String propertyName, String label) {
		this.propertyName = propertyName;
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	public static Collection<EProperty> toList(){
		List<EProperty> list = Arrays.asList(EProperty.values()); 
		return list;
	}
}
