package co.edu.sanmartin.persistence.constant;

/**
 * Enumeracion de los lenguajes soportados por el sistema
 * @author Ricardo Carvajal Salamanca
 *
 */
public enum ELanguage {
	SPANISH("es"),
	ENGLISH("en");

	private String initials;
	
	/**
	 * Constructor de las enumeraciones de los archivos de lexicon. dependiendo del idioma
	 * @param fileName
	 */
	private ELanguage(String initials){
		
		this.initials = initials;
	}

	public String getInitials() {
		return initials;
	}
	
}
