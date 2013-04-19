package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;

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

	/**
	 * Metodo encargado de ordenar y crear el indice invertido
	 * @param minQuantityTerms
	 */
	private void buildIndex(int minQuantityTerms){
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
				e.printStackTrace();
			}
			String[] termData = termItem.split(splitToken);
			if(termData.length==2){
				String term = termData[0];
				
				String document = termData[1];

				if (tempTerm== null){
					stringBuilder.append(term);
					stringBuilder.append("\t");
					stringBuilder.append(document);
					tempTerm = term;
				}
				else{
					if (term.equals(tempTerm)){
						stringBuilder.append(",");
						stringBuilder.append(document);
					}
					else{
						if(counter >= minQuantityTerms){
							stringBuilder.append("\t");
							stringBuilder.append(counter);
							invertedIndex.add(stringBuilder.toString());
						}
						counter = 0;
						stringBuilder = new StringBuilder();
						stringBuilder.append(term);
						stringBuilder.append("\t");
						stringBuilder.append(document);
					}
				}
				tempTerm = term;
				counter++;
			}
			
		}
	}
	
	
	/**
	 * Metodo encargado de salvar el archivo de texto plano
	 */
	public void saveIndex(int minQuantityTerms){
		
		StringBuilder stringBuilder = new StringBuilder();
		//Realiza la construccion del indice invertido
		this.buildIndex(minQuantityTerms);
		for (String word : invertedIndex) {
			stringBuilder.append(word);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.INVERTED_INDEX,
													 "invertedIndex.txt", stringBuilder.toString());
		
	}
	
	
}
