package co.edu.sanmartin.fuzzyclustering.ir.normalize;

import java.util.HashMap;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ELanguage;
import co.edu.sanmartin.persistence.constant.ELexicon;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
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
	private static HashMap<ELexicon,String> lexiconMap;
	private WorkspaceDTO workspace;
	private static Lexicon instance;
	
	
	
	private Lexicon(WorkspaceDTO workspace){
		this.workspace = workspace;
		this.initLexiconinMemory();
	}
	
	public static Lexicon getInstance(WorkspaceDTO workspace){
		
		if(instance==null){
			instance = new Lexicon(workspace);
		}
		else{
			try {
				if(!instance.workspace.getPersistence().getProperty(EProperty.LANGUAGE).equals(workspace.getPersistence().getProperty(EProperty.LANGUAGE))){
					instance.workspace = workspace;
				}
			} catch (PropertyValueNotFoundException e) {
				logger.error(e);
			}
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
	private  void initLexiconinMemory() {
		lexiconMap = new HashMap<ELexicon,String>();
    	try {
			if (this.workspace.getPersistence().getProperty(EProperty.IR_DELETE_ADVERBS).getValue().equals("true")){
        		Lexicon.lexiconMap.put(ELexicon.ADVERBS, this.getLexiconPattern(ELexicon.ADVERBS));
			}
			if (this.workspace.getPersistence().getProperty(EProperty.IR_DELETE_CONJUNCTIONS).getValue().equals("true")){
				Lexicon.lexiconMap.put(ELexicon.CONJUNCTIONS, this.getLexiconPattern(ELexicon.CONJUNCTIONS));
			}
			if (this.workspace.getPersistence().getProperty(EProperty.IR_DELETE_DETERMINANTS).getValue().equals("true")){
				Lexicon.lexiconMap.put(ELexicon.DETERMINANTS, this.getLexiconPattern(ELexicon.DETERMINANTS));
			}
			if (this.workspace.getPersistence().getProperty(EProperty.IR_DELETE_PREPOSITIONS).getValue().equals("true")){
				Lexicon.lexiconMap.put(ELexicon.PREPOSITIONS, this.getLexiconPattern(ELexicon.PREPOSITIONS));
			}
			if (this.workspace.getPersistence().getProperty(EProperty.IR_DELETE_PRONOUNS).getValue().equals("true")){
				Lexicon.lexiconMap.put(ELexicon.PRONOUNS, this.getLexiconPattern(ELexicon.PRONOUNS));
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

    	
    	ELanguage language = null;
    	try {
			PropertyDTO property = this.workspace.getPersistence().getProperty(EProperty.LANGUAGE);
			language = ELanguage.valueOf(property.getValue());
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
    	
    	String word = this.workspace.getPersistence().readRootFile(EDataFolder.LEXICON,
    													lexicon.getFileName()+"-"+language.getInitials()+".txt");
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
