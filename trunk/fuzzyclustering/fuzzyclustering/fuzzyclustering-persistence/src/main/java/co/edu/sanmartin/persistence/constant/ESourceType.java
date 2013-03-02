package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Tipos de origines de la fuentes de datos
 * @author Ricardo Carvajal Salamanca
 *
 */
public enum ESourceType {
	RSS("rss"), 
	TWITTER("twitter");
	
	private String name;

	ESourceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	
}
