package co.edu.sanmartin.fuzzyclustering.ir.normalize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EProperty;
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
	    	PersistenceFacade.getInstance().writeFile(EProperty.FILE_CLEAN_SOURCE_PATH, fileName, unescape.toString());
	    }
	    return unescape.toString();
    }
    
    /**
     * Metodo encargado de convertir los caracteres de mayusculas a minusculas
     * @param data textos a convertir
     * @return la cadena de texto convertido a minuscula
     */
    public String toLowerData( String data ){
        StringBuilder lower = new StringBuilder();
        lower.append(data.toLowerCase());
        return lower.toString();
    }
    
    /**
     * Metodo que aplica una regla de expresiones regulares sobre un texto
     * @param data datos a aplicar la regla de expresiones regulares
     * @param regexProperty expresion regular a aplicar
     * @return el texto con la expresion regular aplicada.
     */
    public String applyRegexExpression(String data, String regexProperty){
        
       /* StringBuilder cleanString = new StringBuilder();
        NormalizerPropertiesLoader properties = new NormalizerPropertiesLoader();
        String regexRules = properties.getProperty(regexProperty);
        String[] regexRuleColl = regexRules.split("¥");
        cleanString.append(data);
        try{
            for (String rule : regexRuleColl) {
                String[] regexExpression = rule.split(";");
                Pattern pattern = Pattern.compile(regexExpression[0]);
                Matcher matcher = pattern.matcher(cleanString.toString());
                cleanString = new StringBuilder(matcher.replaceAll(regexExpression[1]));

            }
        }
        catch (PatternSyntaxException e){
           log.error("Error en aplicar expresion regular", e); 
        }
        return cleanString.toString();
        */
    	return null;
    }
    
    
    

}
