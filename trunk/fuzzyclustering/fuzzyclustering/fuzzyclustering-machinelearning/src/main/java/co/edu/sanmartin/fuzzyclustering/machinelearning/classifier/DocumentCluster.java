package co.edu.sanmartin.fuzzyclustering.machinelearning.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public void buildMembershipIndex(){
		List<Double[]> membershipMatrix = 
				this.workspace.getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, 
						"reduced_membership.txt");
		InvertedIndex invertedIndex = IRFacade.getInstance(this.workspace).getInvertedIndexZipf();
		invertedIndex.loadInvertedIndexDataZipf();

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
	 * Retorna el valor de pertenencia de cada termino del documento
	 * @param document
	 */
	public HashMap<String, ArrayList> getDocumentTermMembership(DocumentDTO document){
		String normalizedTerm = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyClusterTerm());
		document.setLazyClusterTerm(normalizedTerm.replace(",", ""));
		String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
		document.setLazyCleanData(normalizedText);
		
		String cleanData = document.getLazyCleanData();
		String[] documentTermArray = cleanData.split(",");
		HashMap<String, ArrayList> membeshipDocument = new HashMap<String,ArrayList>();
		for (String term : documentTermArray) {
			ArrayList array = this.membershipTermDocument.get(term);
			membeshipDocument.put(term, array);
		}
		return membeshipDocument;
	}

	/**
	 * Retorna los valores de pertenencia del documento en los diferentes clusteres
	 * @param document
	 * @return
	 */
	public HashMap<String,Double> getDocumentClusterMembership(DocumentDTO document){
		HashMap<String, ArrayList> documentTermMembership = this.getDocumentTermMembership(document);

		ArrayList<ArrayList> clusterMembership = new ArrayList<ArrayList>();
		Iterator iterator = documentTermMembership.entrySet().iterator();
		//Construimos la pareja cluster grado de pertenencia de los terminos de los documentos
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			ArrayList dataArray =(ArrayList) e.getValue();
			if(dataArray!=null){
				ArrayList arrayList = new ArrayList();
				arrayList.add((String)dataArray.get(0));
				arrayList.add(Double.parseDouble((String)dataArray.get(1)));
				clusterMembership.add(arrayList);
			}
		}

		//ordenamos la lista o  
		Collections.sort(clusterMembership, new Comparator() {  

			public int compare(Object o1, Object o2) {  
				ArrayList a1 = (ArrayList) o1;
				ArrayList a2 = (ArrayList) o2;
				String cluster1 = (String) a1.get(0);  
				String cluster2 = (String) a2.get(0);  
				return cluster1.compareToIgnoreCase(cluster2);  
			}  
		});

		String tempKey = "-1";
		Integer tempQuantity = 0;
		Double tempSummatory = 0.0;

		HashMap<String,Double> clusterAverage = new HashMap<String,Double>();

		for(ArrayList list : clusterMembership){

			String key = (String) list.get(0);
			Double value =(Double) list.get(1);

			if(!tempKey.equals(key)){
				if(tempQuantity!=0){
					clusterAverage.put(tempKey, tempSummatory/tempQuantity);
				}
				tempQuantity = 0;
				tempSummatory = 0.0;
			}
			tempKey = key;
			tempQuantity++;
			tempSummatory+=value;		    
		}

		System.out.println(clusterAverage.toString());
		return clusterAverage;
	}

	/**
	 * Retorna el cluster al que pertenece el documento y si el termino buscado 
	 * tambien pertenece al cluster
	 * @param document
	 * @param word
	 * @return
	 */
	public ArrayList getDocumentMembership(DocumentDTO document){
		HashMap<String,Double> documentMembership = this.getDocumentClusterMembership(document);
		Iterator iterator = documentMembership.entrySet().iterator();
		Double tmpMaxValue = 0.0;
		String tmpCluster = null;
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Double value =(Double) e.getValue();
			if(value>tmpMaxValue){
				tmpMaxValue = value;
				tmpCluster = key; 
			}
		}

		ArrayList result = new ArrayList();
		//Cluster del documento
		result.add(tmpCluster);
		//Pertenencia al cluster del documento
		result.add(tmpMaxValue);
		ArrayList termArray = this.membershipTermDocument.get(document.getLazyClusterTerm());
		if(termArray!=null){
			//Cluster Esperado segun el termino
			result.add(termArray.get(0));
			//Valor de pertenencia del termino
			result.add(termArray.get(1));
			//Verdadero Positivo son instancias pertenecientes a la clase C que se clasifican correctamente en la clase C
			if(tmpCluster.equals(termArray.get(0))){
				result.add("Verdadero-Positivo");
			}
			//Verdadero Negativo son instancias no pertenecientes a la clase C y que no se clasifican como clase C
			if(!tmpCluster.equals(termArray.get(0))){
				result.add("Verdadero-Negativo");
			}
		}
		return result;
	}


	/**
	 * Carga el Indice de Membresia de Terminos
	 */
	public void loadMembershipTermDocument(){
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
