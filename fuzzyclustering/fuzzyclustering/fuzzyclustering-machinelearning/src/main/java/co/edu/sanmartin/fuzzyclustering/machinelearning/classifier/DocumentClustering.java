package co.edu.sanmartin.fuzzyclustering.machinelearning.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

/**
 * Clase que define los valores de clusterizacion del documento para consulta
 * @author Ricardo
 *
 */
public class DocumentClustering {

	private DocumentDTO document;
	private int termQuantity = 0;
	//Promedio de la matrix de pertenencia
	private Double[] membershipAverage;
	//Cantidad de maximos
	private Double[] voteArray;
	//Promedio ponderado difuso
	private Double[] weightAverage;
	//Tabla de pertenencia
	LinkedHashMap<String,Double[]> membershipMatrix;
	//Categoria de los cluster por voto
	private Double[] clusterCategory;

	private int documentCluster;
	private int termCluster;
	
	private WorkspaceDTO workspace;
	private DocumentClusteringFunctions documentClusteringFunction;
	private static Logger logger = Logger.getLogger("DocumentClustering");
	
	public DocumentClustering(WorkspaceDTO workspace){
		this.workspace = workspace;
		documentClusteringFunction = new DocumentClusteringFunctions(workspace);
		documentClusteringFunction.loadMembershipTermMatrix();
	}
	
	/**
	 * Realiza los calculos del grado de pertenencia del documento
	 * @param document
	 */
	public void clustering(DocumentDTO document){
		this.document = document;
		
		this.membershipMatrix = documentClusteringFunction.getDocumentMembershipMatrix(document);
		this.membershipAverage = documentClusteringFunction.getAvgMembershipMatrix(membershipMatrix);
		this.termQuantity = documentClusteringFunction.getDocumentTermCount(document);
		if(membershipMatrix==null){
			logger.info("El documento: " + document.getName() + "no tiene matrix de pertenencia");
		}
		else{
			
			if(this.membershipAverage== null){
				logger.info("Todos los terminos del documento" + document.getName() + "no se encuentran en el indice");
			}
			else{
				this.voteArray = documentClusteringFunction.getVoteMembershipMatrix(membershipMatrix);
				
				this.weightAverage = documentClusteringFunction.getFuzzyWeightedAverage(membershipAverage, voteArray, termQuantity);
				this.documentCluster = documentClusteringFunction.getDocumentCluster(weightAverage);
				this.termCluster = documentClusteringFunction.getTermCluster(document.getLazyClusterTerm());
			}
		}
	}
	
	

	public DocumentDTO getDocument() {
		return document;
	}
	public void setDocument(DocumentDTO document) {
		this.document = document;
	}
	public Double[] getMembershipAverage() {
		return membershipAverage;
	}
	public Double[] getVoteArray() {
		return voteArray;
	}
	
	public HashMap<String, Double[]> getMembershipMatrix() {
		return membershipMatrix;
	}

	public Double[] getWeightAverage() {
		return weightAverage;
	}

	public int getDocumentCluster() {
		return documentCluster;
	}

	public int getTermCluster() {
		return termCluster;
	}


	
}
