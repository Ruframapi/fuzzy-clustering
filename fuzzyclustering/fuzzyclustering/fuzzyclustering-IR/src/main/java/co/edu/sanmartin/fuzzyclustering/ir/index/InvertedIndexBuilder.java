package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que genera el indice invertido a partir de los documentos previamente
 * Normalizados.
 * @author Ricardo Carvajal Salamanca
 *
 */
public class InvertedIndexBuilder {
	
	Logger logger = Logger.getRootLogger();
	private ConcurrentLinkedDeque<String> index;
	private ArrayList<String> invertedIndex;
	
	
	public InvertedIndexBuilder(ConcurrentLinkedDeque<String> index){
		logger.debug("Init Inverted Index");
		this.index = index;
	}
	
	public void addIndex(String term, String document){
		index.add(term+","+document);
	}
	public ConcurrentLinkedDeque<String> getIndexCol(){
		return index;
	}

	public void printIndex(){
	
		for (String term : index) {
			System.out.println("term");
		}
	 } // fin
	
	private void buildIndex(){
		//Ordena Alfabeticamente el indice
		this.invertedIndex = new ArrayList<String>();
		Collection<String> termCol =  index;
		ArrayList<String> termList =new ArrayList<String>(termCol);
		Collections.sort(termList);
		String tempTerm = null;
		StringBuilder stringBuilder = new StringBuilder();
		int counter = 0;
		for (String termItem : termList) {
			String splitToken = null;
			try {
				splitToken = PersistenceFacade.getInstance().getProperty(EProperty.TEXT_SPLIT_TOKEN).getValue();
			} catch (PropertyValueNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] termData = termItem.split(splitToken);
			if(termData.length==2){
				String term = termData[0];
				//Si el termino es vacio o si no son caracteres o letras no se tiene en cuenta
				if(term.length()==0){
					continue;
				}
				if(!Character.isLetterOrDigit(term.charAt(0))){
					continue;
				}
				
				String document = termData[1];

				if (tempTerm== null){
					stringBuilder.append(term);
					stringBuilder.append(";");
					stringBuilder.append(document);
					tempTerm = term;
				}
				else{
					if (term.equals(tempTerm)){
						stringBuilder.append(",");
						stringBuilder.append(document);
					}
					else{
						stringBuilder.append(";");
						stringBuilder.append(counter);
						invertedIndex.add(stringBuilder.toString());
						counter = 0;
						stringBuilder = new StringBuilder();
						stringBuilder.append(term);
						stringBuilder.append(";");
						stringBuilder.append(document);
					}
				}
				tempTerm = term;
				counter++;
			}
			
		}
		
		
	}
	public void saveIndex(){
		
		StringBuilder stringBuilder = new StringBuilder();
		this.buildIndex();
		for (String word : invertedIndex) {
			stringBuilder.append(word);
			stringBuilder.append("\n");
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.INVERTED_INDEX,
													 "invertedIndex.txt", stringBuilder.toString());
		
	}
	
	
}
