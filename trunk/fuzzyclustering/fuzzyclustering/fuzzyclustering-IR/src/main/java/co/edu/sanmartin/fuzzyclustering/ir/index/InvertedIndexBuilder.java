package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;

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
	private WorkspaceDTO workspace;

	public InvertedIndexBuilder(WorkspaceDTO workspace, ConcurrentLinkedDeque<String> index){
		logger.info("Init Inverted Index");
		this.index = index;
		this.workspace = workspace;
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
		logger.info("Iniciando Creacion de indice invertido");
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
				splitToken = this.workspace.getPersistence().getProperty(EProperty.TEXT_SPLIT_TOKEN).getValue();
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
		invertedIndex.add(stringBuilder.toString());
		logger.info("Indice Invertido Creado");
	}


	/**
	 * Metodo encargado de salvar el archivo de texto plano
	 */
	public void saveIndex(int minQuantityTerms){

		StringBuilder stringBuilder = new StringBuilder();
		//Realiza la construccion del indice invertido
		this.buildIndex(minQuantityTerms);
		ArrayList<String> sortedIndex = this.sortInvertedIndexDocuments();
		for (String word : sortedIndex) {
			stringBuilder.append(word);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile(EDataFolder.INVERTED_INDEX,
				"invertedIndex.txt", stringBuilder.toString());

	}

	public ArrayList<String> sortInvertedIndexDocuments(){
		ArrayList<String> sortedIndex = new ArrayList<String>();
		for (String termIndex : invertedIndex) {
			String[] data = termIndex.split("\t");
			String[] documents = data[1].split(",");
			ArrayList<Integer> documentsArray = new ArrayList<Integer>();
			for (String document : documents) {
				documentsArray.add(Integer.parseInt(document));
			}
			Collections.sort(documentsArray);
			StringBuilder sortedString = new StringBuilder();
			sortedString.append(data[0]);
			sortedString.append("\t");
			for (int i = 0; i < documents.length; i++) {
				sortedString.append(documentsArray.get(i));
				if(i+1<documentsArray.size()){
					sortedString.append(",");
				}
			}
			sortedString.append("\t");
			sortedString.append(documentsArray.size());
			sortedString.append("\t");
			sortedString.append("100.00");
			sortedIndex.add(sortedString.toString());
		}

		return sortedIndex;
	}




}
