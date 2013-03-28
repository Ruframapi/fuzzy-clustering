package co.edu.sanmartin.fuzzyclustering.ir.normalize;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ELexicon;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de limpiar el texto descargado desde las fuentes de datos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class Cleaner {
	
	private static Logger logger = Logger.getRootLogger();
	
	/**
     * Metodo enargado de convertir los caracteres de escape de HTML que incluyen
     * acentos y caracteres especiales en caracteres UTF
     * @param data datos a convertir caracteres de escape
     * @return la cadena de caracteres convertida
     */
    public String unescapeHtml( String data, String fileName, boolean persist ){
	    StringBuilder unescape = new StringBuilder();
	    unescape.append(StringEscapeUtils.unescapeHtml(data));
	    if(persist){
	    	PersistenceFacade.getInstance().writeFile(EDataFolder.CLEAN, fileName, unescape.toString());
	    }
	    return unescape.toString();
    }
    
    /**
     * Metodo encargado de convertir los caracteres de mayusculas a minusculas
     * @param data textos a convertir
     * @return la cadena de texto convertido a minuscula
     */
    public String toLowerData( String data, String fileName, boolean persist ){
        StringBuilder lower = new StringBuilder();
        lower.append(data.toLowerCase());
        if(persist){
	    	PersistenceFacade.getInstance().writeFile(EDataFolder.CLEAN, fileName, lower.toString());
	    }
        return lower.toString();
    }
    
    /**
     * Metodo que aplica una regla de expresiones regulares sobre un texto
     * @param data datos a aplicar la regla de expresiones regulares
     * @param regexProperty expresion regular a aplicar
     * @return el texto con la expresion regular aplicada.
     */
    public String applyRegexExpression(String data, String fileName, boolean persist) throws PatternSyntaxException{
        
        StringBuilder cleanString = new StringBuilder();
        Collection<StopwordDTO> stopwordRulesCol = null;
		try {
			stopwordRulesCol = PersistenceFacade.getInstance().getAllStopword();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			logger.error(ex);
		}
        cleanString.append(data);
        try{
            for (StopwordDTO rule : stopwordRulesCol) {
            	if(rule.isEnabled()){
            		Pattern pattern = Pattern.compile(rule.getRegex());
            		Matcher matcher = pattern.matcher(cleanString.toString());
            		cleanString = new StringBuilder(matcher.replaceAll(rule.getRegexReplace()));
            	}
            }
        }
        catch (PatternSyntaxException e){
           logger.error("Error en aplicar expresion regular", e);
           throw e;
        }
        String result = cleanString.toString();
        if(persist){
	    	PersistenceFacade.getInstance().writeFile(EDataFolder.CLEAN, fileName, result);
	    }
        return result;
    }
    
    /**
     * Elimina las stop words del archivo realizando la busqueda en los archivos del lexicon
     * @param data datos del archivo
     * @param fileName nombre del archivo a almacenar
     * @param persist indica si persiste el proceso en un archivo
     * @return
     */
    public String deleteLexiconStopWords(String data, String fileName, boolean persist){
    	StringBuilder cleanString = new StringBuilder();
    	cleanString.append(data);
    	Lexicon lexicon = Lexicon.getInstance();
    	HashMap<ELexicon,String> lexiconMap = lexicon.getLexiconMap();
    	Iterator it = lexiconMap.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry e = (Map.Entry)it.next();
    		Pattern pattern = Pattern.compile((String) e.getValue());
    		Matcher matcher = pattern.matcher(cleanString.toString());
    		cleanString = new StringBuilder(matcher.replaceAll(" "));
    		logger.debug("Cleaning data Lexicon:" + ((ELexicon)e.getKey()).name());
    	}
    	return cleanString.toString();
    }
    
    
}
