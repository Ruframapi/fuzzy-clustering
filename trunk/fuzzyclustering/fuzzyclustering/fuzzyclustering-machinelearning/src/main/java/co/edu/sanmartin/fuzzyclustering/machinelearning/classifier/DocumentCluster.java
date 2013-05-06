package co.edu.sanmartin.fuzzyclustering.machinelearning.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de determinar la categoria del documento
 * @author Ricardo Carvajal Salamanca
 *
 */
public class DocumentCluster {
	private HashMap<String,ArrayList>  membershipTermDocument = new HashMap<String,ArrayList>();
	private WorkspaceDTO workspace;
	
	public DocumentCluster(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	
	/**
	 * Metodo encargado de crear el indice de pertenencia de los terminos 
	 * 
	 */
	public void buildMembershipIndex(String dataRoot){
		List<Double[]> membershipMatrix = 
				this.workspace.getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, 
																"membership.txt");
		InvertedIndex invertedIndex = IRFacade.getInstance().getInvertedIndex(this.workspace);
		invertedIndex.loadInvertedIndexData();

		for (int i = 0; i < invertedIndex.getTermCount(); i++) {
			String term = invertedIndex.getWordList().get(i);
			Double membershipMaxValue =  0.0;
			Integer cluster = 0;
			for (int j = 0; j < membershipMatrix.get(i).length; j++) {
				if(membershipMatrix.get(i)[j]>membershipMaxValue){
					membershipMaxValue = membershipMatrix.get(i)[j];
					cluster = j;
				}
			}
			ArrayList arrayDocument = new ArrayList(); 
			arrayDocument.add(cluster);
			arrayDocument.add(membershipMaxValue);
			membershipTermDocument.put(term, arrayDocument);
		}
		this.persistMembershipIndex();
	}
	
	/**
	 * Retorna el valor de pertenencia del documento
	 * @param document
	 */
	public HashMap<String, ArrayList> getDocumentMembership(DocumentDTO document){
		String cleanData = document.getLazyCleanData();
		String[] documentTermArray = cleanData.split(" ");
		HashMap<String, ArrayList> membeshipDocument = new HashMap<String,ArrayList>();
		for (String term : documentTermArray) {
			ArrayList array = this.membershipTermDocument.get(term);
			membeshipDocument.put(term, array);
		}
		return membeshipDocument;

	}
	
	/**public ArrayList getMembershipTerm(String term){
		int termIndex = this.membershipTermDocument.indexOf(term);
		if(termIndex>=0){
			return this.membershipTermDocument;
		}
	}*/
	
	/**
	 * Carga el Indice de Membresia de Terminos
	 */
	public void loadMembershipTermDocument(String dataRoot){
		String membershipTerm = this.workspace.getPersistence().readFile(EDataFolder.MACHINE_LEARNING, 
																			"membershipt_term.txt");
		String[] membershipTermSplit = membershipTerm.split(System.getProperty("line.separator"));
		this.membershipTermDocument = new HashMap<String,ArrayList>();
		for (String string : membershipTermSplit) {
			String[] rowData = string.split("\t");
			ArrayList arrayDocument = new ArrayList(); 
			arrayDocument.add(rowData[1]);
			arrayDocument.add(rowData[2]);
			membershipTermDocument.put(rowData[0], arrayDocument);
		}

	}

	/**
	 * Almacena el indice de pertenencia
	 */
	public void persistMembershipIndex(){
		StringBuilder stringBuilder = new StringBuilder();
		//Ordenamos el HashMap con un treeMap
		Map sortedMap = new TreeMap(this.membershipTermDocument);
		Iterator iterator = sortedMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
		    String key = (String) e.getKey();
		    ArrayList dataArray =(ArrayList) e.getValue();
		    
		    stringBuilder.append(key);
			stringBuilder.append("\t");
			stringBuilder.append((Integer)dataArray.get(0));
			stringBuilder.append("\t");
			stringBuilder.append((Double)dataArray.get(1));
			stringBuilder.append(System.getProperty("line.separator"));
		}
			
		this.workspace.getPersistence().writeFile(EDataFolder.MACHINE_LEARNING, 
												"membershipt_term.txt", 
												stringBuilder.toString());
	}
	
	
}
