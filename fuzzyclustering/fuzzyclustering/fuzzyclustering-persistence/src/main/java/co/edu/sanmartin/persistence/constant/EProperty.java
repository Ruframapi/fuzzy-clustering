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
	
	TWITTER_CONSUMER_KEY("twitter.consumer.key", "Twitter Consumer KEY"), 
	TWITTER_CONSUMER_SECRET("twitter.consumer.secret", "Twitter Consumer Secret"), 
	TWITTER_ACCESS_TOKEN("twitter.access.token", "Twitter Access Token"), 
	TWITTER_ACCESS_TOKEN_SECRET("twitter.access.token.secret", "Twitter Access Token Secret"),
	TWITTER_HOME_TIMELINE_PAGES("twitter.home.timeline.pages","Cantidad de paginas de descarga de twitter"),
	WEB_SCRAPPING_THREAD_NUMBER("websrapin.thread.number", "Numero de Hilos de Descarga"),
	INVERTED_INDEX_THREAD_NUMBER("inverted.index.thread.number", "Numero de Hilos de Indices Invertidos"),
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
