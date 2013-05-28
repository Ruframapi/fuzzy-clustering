package co.edu.sanmartin.fuzzyclustering.machinelearning.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

/**
 * Clase encargada de determinar el grado de pertenencia del documento
 * @author Ricardo Carvajal Salamanca
 *
 */
public class DocumentClusteringFunctions {

	private HashMap<String,Double[]>  membershipTerm = new HashMap<String,Double[]>();
	private WorkspaceDTO workspace;

	public DocumentClusteringFunctions(WorkspaceDTO workspace){
		this.workspace = workspace;
	}

	/**
	 * Carga la tabla termino-Pertenencia de todos los terminos del indice
	 */
	public void loadMembershipTermMatrix(){
		List<Double[]> membershipMatrix = 
				this.workspace.getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, 
						"membership.txt");
		InvertedIndex invertedIndex = IRFacade.getInstance(this.workspace).getInvertedIndexZipf();
		invertedIndex.loadInvertedIndexDataZipf();
		for (int i = 0; i < invertedIndex.getTermCount(); i++) {
			String term = invertedIndex.getWordList().get(i);

			Double[] membershipTermCluster = membershipMatrix.get(i);
			this.membershipTerm.put(term, membershipTermCluster);
		}
	}

	/**
	 * Carga los valores de termino pertenencia de los terminos que componen el documento
	 */
	public HashMap<String,Double[]> getDocumentMembershipMatrix(DocumentDTO document){

		String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
		document.setLazyCleanData(normalizedText);

		String cleanData = document.getLazyCleanData();
		String[] documentTermArray = cleanData.split(",");
		HashMap<String, Double[]> membeshipDocument = new HashMap<String,Double[]>();
		for (String term : documentTermArray) {
			Double[] membershipValues = this.getMembershipTerm(term);
			membeshipDocument.put(term, membershipValues);
		}
		return membeshipDocument;
	}


	/**
	 * Retorna los valores de pertenencia de un termino
	 * @param term
	 * @return
	 */
	public Double[] getMembershipTerm(String term){
		return this.membershipTerm.get(term);
	}

	/**
	 * Retorna los promedios de la matrix de pertenencia
	 * @return
	 */
	public Double[] getAvgMembershipMatrix(HashMap<String, Double[]> membershipMatrix){
		Iterator iterator = membershipMatrix.entrySet().iterator();

		ArrayList<Double[]> matrix = new ArrayList<Double[]>();

		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Double[] values = (Double[]) e.getValue();
			if(values!=null){
				matrix.add(values);
			}
		}

		Double[] avgColl = null;
		if(!matrix.isEmpty()){
			avgColl = new Double[matrix.get(0).length];

			for (int i = 0; i < matrix.get(0).length; i++) {
				Double avg = 0.0;
				for (int j = 0; j < matrix.size(); j++) {
					avg+=matrix.get(j)[i];
				}
				avg = avg/matrix.size();
				avgColl[i]=avg;
			}

		}
		return avgColl;
	}


	/**
	 * Retorna los votos de la matrix de pertenencia
	 * @return
	 */
	public Double[] getVoteMembershipMatrix(HashMap<String, Double[]> membershipMatrix){
		Iterator iterator = membershipMatrix.entrySet().iterator();

		ArrayList<Double[]> matrix = new ArrayList<Double[]>();

		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Double[] values = (Double[]) e.getValue();
			if(values!=null){
				matrix.add(values);
			}
		}

		double[] voteColl = null;
		if(!matrix.isEmpty()){
			voteColl = new double[matrix.get(0).length];

			for (int i = 0; i < matrix.size(); i++) {
				Double maxClusterMembershipValue = 0.00;
				Integer maxCluster  = -1;
				for (int j = 0; j < matrix.get(i).length; j++) {
					if(matrix.get(i)[j]>maxClusterMembershipValue){
						maxClusterMembershipValue=matrix.get(i)[j];
						maxCluster = j;
					}
				}
				voteColl[maxCluster]++;
			}

		}
		return ArrayUtils.toObject(voteColl);
	}
	/**
	 * Retorna el promedio ponderado de los valores difusos del documento
	 * @param avgMembershipMatrix
	 * @param voteMembershipMatrix
	 * @return
	 */
	public Double[] getFuzzyWeightedAverage(Double[] avgMembershipMatrix,Double[] voteMembershipMatrix){
		Double[] weightedAverage = new Double[avgMembershipMatrix.length];
		Double voteSum = 0.00;
		for (Double vote : voteMembershipMatrix) {
			voteSum+=vote;
		}
		for (int i = 0; i < voteMembershipMatrix.length; i++) {
			if(voteSum!=0){
				weightedAverage[i]=((avgMembershipMatrix[i]*voteMembershipMatrix[i])/voteSum);
			}
			else{
				weightedAverage[i]=0.0;
			}
		}
		return weightedAverage;
	}

	/**
	 * Retorna el cluster al que pertenece el documento
	 * @param fuzzyWeightedAverage
	 * @return
	 */
	public int getDocumentCluster(Double[] fuzzyWeightedAverage){
		int cluster = -1;
		double clusterAverage = 0;
		for (int i = 0; i < fuzzyWeightedAverage.length; i++) {
			if(clusterAverage<fuzzyWeightedAverage[i]){
				clusterAverage = fuzzyWeightedAverage[i];
				cluster = i;
			}
		}
		return cluster;
	}

	/**
	 * Retorna el cluster al que pertenene un termino
	 * @param noStemmedTerm
	 * @return
	 */
	public int getTermCluster(String noStemmedTerm){
		String normalizedTerm = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(noStemmedTerm);
		normalizedTerm = normalizedTerm.replace(",", "");
		Double[] membershipArray = membershipTerm.get(normalizedTerm);
		int cluster = -1;
		if(membershipArray!=null){
			double clusterAverage = 0;
			for (int i = 0; i < membershipArray.length; i++) {
				if(clusterAverage<membershipArray[i]){
					clusterAverage = membershipArray[i];
					cluster = i;
				}
			}
		}
		return cluster;
	}





}
