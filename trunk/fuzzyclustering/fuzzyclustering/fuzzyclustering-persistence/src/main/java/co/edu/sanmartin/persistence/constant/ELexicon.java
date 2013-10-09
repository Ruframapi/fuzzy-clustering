package co.edu.sanmartin.persistence.constant;

import java.io.ObjectInputStream.GetField;


public enum ELexicon {
	ADVERBS("adverbs"),
	CONJUNCTIONS("conjunctions"),
	DETERMINANTS("determinants"),
	PREPOSITIONS("prepositions"),
	PRONOUNS("pronouns"),
	ARTICLES("articles"), 
	VERBS("verbs");
	
	private String fileName;
	
	/**
	 * Constructor de las enumeraciones de los archivos de lexicon. dependiendo del idioma
	 * @param fileName
	 */
	private ELexicon(String fileName){
		
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
}
