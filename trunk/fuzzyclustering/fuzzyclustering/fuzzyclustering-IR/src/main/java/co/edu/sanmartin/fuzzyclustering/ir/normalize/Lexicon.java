package co.edu.sanmartin.fuzzyclustering.ir.normalize;

import java.util.HashMap;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ELexicon;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de cargar los lexicones en memoria para su uso en el proceso
 * de normalizacion de archivos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class Lexicon {
	private static Logger logger = Logger.getLogger(Lexicon.class);
	private HashMap<ELexicon,String> lexiconMap;
	private static Lexicon instance;
	
	private Lexicon(){
		this.initLexiconinMemory();
	}
	
	public static Lexicon getInstance(){
		if (instance == null){
			instance = new Lexicon();
		}
		return instance;
	}
	
	/**
	 * Vuelve a cargar los lexicones en memoria
	 */
	public void refresh(){
		this.initLexiconinMemory();
	}
	
	/**
	 * Inicializa los lexicones en memoria para realizar el proceso de normalizacion de archivos
	 */
	private void initLexiconinMemory() {
		lexiconMap = new HashMap<ELexicon,String>();
    	PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
    	try {
			if (persistenceFacade.getProperty(EProperty.IR_DELETE_ADVERBS).getValue().equals("true")){
        		this.lexiconMap.put(ELexicon.ADVERBS, this.getLexiconPattern(ELexicon.ADVERBS));
			}
			if (persistenceFacade.getProperty(EProperty.IR_DELETE_CONJUNCTIONS).getValue().equals("true")){
				this.lexiconMap.put(ELexicon.CONJUNCTIONS, this.getLexiconPattern(ELexicon.CONJUNCTIONS));
			}
			if (persistenceFacade.getProperty(EProperty.IR_DELETE_DETERMINANTS).getValue().equals("true")){
				this.lexiconMap.put(ELexicon.DETERMINANTS, this.getLexiconPattern(ELexicon.DETERMINANTS));
			}
			if (persistenceFacade.getProperty(EProperty.IR_DELETE_PREPOSITIONS).getValue().equals("true")){
				this.lexiconMap.put(ELexicon.PREPOSITIONS, this.getLexiconPattern(ELexicon.PREPOSITIONS));
			}
			if (persistenceFacade.getProperty(EProperty.IR_DELETE_PRONOUNS).getValue().equals("true")){
				this.lexiconMap.put(ELexicon.PRONOUNS, this.getLexiconPattern(ELexicon.PRONOUNS));
			}
		} catch (PropertyValueNotFoundException e) {
			logger.error("No se encuentra la propiedad", e);
		}
	}

    
    /**
     * Retorna el patron con la expresion regular que realiza el proceso de limpieza de los stop words de los archivos
     * descargados
     * @param lexicon el lexicon a construir
     * @return La cadena con la construccion del lexicon como expresion regular
     */
    private String getLexiconPattern(ELexicon lexicon){
    	PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
    	String word = persistenceFacade.readFile(EDataFolder.LEXICON,lexicon.getFileName());
		String[] adverbsList = word.split(",");
		StringBuilder regexPattern = new StringBuilder();
		
		for (int i = 0; i < adverbsList.length; i++) {
			regexPattern.append("\\b");
			regexPattern.append(adverbsList[i]);
			regexPattern.append("\\b");
			if(i+1<adverbsList.length){
				regexPattern.append("|");
			}
		}

		return regexPattern.toString();
    }

	public HashMap<ELexicon, String> getLexiconMap() {
		return lexiconMap;
	}
    
    
	
}
