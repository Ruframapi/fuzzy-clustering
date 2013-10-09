package org.fuzzyclustering.web.managed.clustering;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.fuzzyclustering.web.managed.WorkspaceManagedBean;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.fuzzyclustering.machinelearning.facade.MachineLearningFacade;
import co.edu.sanmartin.persistence.dto.DocumentDTO;

@ManagedBean( name = "tableBean")
@RequestScoped
public class ClusteringResultTableManagedBean implements Serializable{

	private static final long serialVersionUID = 7745848137950723128L;
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;

	private String documentText = "china daily says vermin eat pct grain stocks a survey of provinces and seven cities showed vermin consume between seven and pct of china s grain stocks the china daily said it also said that each year mln tonnes or pct of china s fruit output are left to rot and mln tonnes or up to pct of its vegetables the paper blamed the waste on inadequate storage and bad preservation methods it said the government had launched a national programme to reduce waste calling for improved technology in storage and preservation and greater production of additives the paper gave no further details reuter";
	private String term = "crude";
	private DocumentClustering documentClustering;
	private List<ColumnModel> columns = new ArrayList<ColumnModel>();
	private ArrayList<DataTable> data = new ArrayList<DataTable>();
	public String getDocumentText() {
		return documentText;
	}



	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}



	public void setDocumentText(String documentText) {
		this.documentText = documentText;
	}


	public String getTerm() {
		return term;
	}


	public void setTerm(String term) {
		this.term = term;
	}



	public List<ColumnModel> getColumns() {
		return columns;
	}



	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}


	public ArrayList<DataTable> getData() {
		return data;
	}



	public void setData(ArrayList<DataTable> data) {
		this.data = data;
	}



	public void buildDocumentMembership(){
		DocumentDTO document = new DocumentDTO();
		document.setLazyData(this.documentText);
		document.setLazyClusterTerm(this.term);
		MachineLearningFacade machineLearning = new MachineLearningFacade();
		this.documentClustering = machineLearning.getDocumentClustering(this.workspaceBean.getWorkspace(), document);
		this.createMembershipData();
		this.addResume();
		this.createDynamicColumns();


	}
	public void createMembershipData() {  
		HashMap<String, Double[]> membership= this.documentClustering.getMembershipMatrix();
		Iterator iterator = membership.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Double[] value =(Double[]) e.getValue();
			Double maxValue = this.getMaxValue(value);
			DataTable dataTable = new DataTable();
			dataTable.term = key; 
			dataTable.maxValue = maxValue;
			Class<?> c = dataTable.getClass();

			//Valores de pertenencia de la tabla
			if(value!=null){
				for (int i = 0; i < value.length; i++) {
					try {
						Field field = dataTable.getClass().getDeclaredField("m"+i);
						Double doubleValue = value[i];
						field.set(dataTable, doubleValue);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				this.data.add(dataTable);
			}
			
		}
	}  
	
	public void addResume(){
		//Resumen Promedio 
		DataTable dataTable = new DataTable();
		
		dataTable.term = "Promedio"; 
		Double[] average = documentClustering.getMembershipAverage();
		Double maxValue = this.getMaxValue(average);
		dataTable.setMaxValue(maxValue);
		for (int i = 0; i < average.length; i++) {
			try {
				Field field = dataTable.getClass().getDeclaredField("m"+i);
				Double doubleValue = average[i];
				field.set(dataTable, doubleValue);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		this.data.add(dataTable);
		//Resumen Votos
		dataTable = new DataTable();
		dataTable.term = "Votos"; 
		Double[] vote = documentClustering.getVoteArray();
		maxValue = this.getMaxValue(vote);
		dataTable.setMaxValue(maxValue);
		for (int i = 0; i < vote.length; i++) {
			try {
				Field field = dataTable.getClass().getDeclaredField("m"+i);
				Double doubleValue = vote[i];
				field.set(dataTable, doubleValue);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		this.data.add(dataTable);
		//Resumen Promedio Ponderado
		dataTable = new DataTable();
		dataTable.term = "Promedio Ponderado"; 
		Double[] weightAverage = documentClustering.getWeightAverage();
		maxValue = this.getMaxValue(weightAverage);
		dataTable.setMaxValue(maxValue);
		for (int i = 0; i < weightAverage.length; i++) {
			try {
				Field field = dataTable.getClass().getDeclaredField("m"+i);
				Double doubleValue = weightAverage[i];
				field.set(dataTable, doubleValue);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		this.data.add(dataTable);
		
	}

	public Double getMaxValue(Double[] values){
		double maxValue = 0.0;
		if(values!=null){
			for (Double value : values) {
				if (value>maxValue){
					maxValue = value;
				}
			}
		}
		return maxValue;
	}

	public void createDynamicColumns() { 
		HashMap<String, Double[]> membership= this.documentClustering.getMembershipMatrix();
		Iterator iterator = membership.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry e = (Map.Entry)iterator.next();
			String key = (String) e.getKey();
			Double[] value =(Double[]) e.getValue();
			if(value!=null){
				columns.add(new ColumnModel("TERM", "term", "maxValue")); 
				for (int i = 0; i < value.length; i++) {
					columns.add(new ColumnModel("m"+i, "m"+i, "maxValue")); 
				}
				break;
			}
		}
	}  

	static public class ColumnModel implements Serializable {  

		private String header;  
		private String property;  
		private String maxValue;

		public ColumnModel(String header, String property, String maxValue) {  
			this.header = header;  
			this.property = property; 
			this.maxValue = maxValue;
		}  

		public String getHeader() {  
			return header;  
		}  

		public String getProperty() {  
			return property;  
		}

		public String getMaxValue() {
			return maxValue;
		}
		
		

	}  
}
