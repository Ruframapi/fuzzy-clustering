package co.edu.sanmartin.persistence.constant;

import java.io.ObjectInputStream.GetField;


public enum ELexicon {
	ADVERBS("adverbs-es.txt"),
	CONJUNCTIONS("conjunctions-es.txt"),
	DETERMINANTS("determinants-es.txt"),
	PREPOSITIONS("prepositions-es.txt"),
	PRONOUNS("pronouns-es.txt");
	
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
