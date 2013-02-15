package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Clase que tiene el indice invertido
 * @author Ricardo Carvajal Salamanca
 *
 */
public class InvertedIndex {
	
	private static InvertedIndex instance;
	private ConcurrentLinkedDeque<String> index;
	
	private InvertedIndex(){
		index =new ConcurrentLinkedDeque<String>();
	}

	public static InvertedIndex getInstance(){
		if( instance == null ){
			instance = new InvertedIndex();
		}
		return instance;
	}
	
	
	public void addIndex(String term, String document){
		index.add(term+","+document);
	}
	public void printIndex(){
	
		for (String term : index) {
			System.out.print("term");
		}
	 } // fin
	
}
