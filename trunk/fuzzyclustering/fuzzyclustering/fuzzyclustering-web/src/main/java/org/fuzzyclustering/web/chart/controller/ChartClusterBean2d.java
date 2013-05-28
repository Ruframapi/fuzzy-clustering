package org.fuzzyclustering.web.chart.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.fuzzyclustering.web.managed.WorkspaceManagedBean;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

import com.google.gson.Gson;

@ManagedBean(name = "clusterChartBean")
@ViewScoped
public class ChartClusterBean2d implements Serializable{

		private String chartData;
	    private String categories;
	    private List<String> categoryList = new ArrayList<String>();
	    private List<Double> xAxisList = new ArrayList<Double>();
	    private List<Double> yAxisList = new ArrayList<Double>();
	   
	    @ManagedProperty(value = "#{workspace}") 
		private WorkspaceManagedBean workspaceBean;
	    
	    int index = 0;
	    private Long[] longs;
	     
	    @PostConstruct
	    public void init(){
	    	this.loadData2D();
	    }
	    
	    
	    public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
			this.workspaceBean = workspaceBean;
		}


		/**
	     * @return the chartData
	     */
	    public String getChartData() {
	        return chartData;
	    }
	 
	    /**
	     * @param chartData the chartData to set
	     */
	    public void setChartData(String chartData) {
	        this.chartData = chartData;
	    }
	 
	    /**
	     * @return the categories
	     */
	    public String getCategories() {
	        return categories;
	    }
	     
	    /**
	     * @param categories the categories to set
	     */
	    public void setCategories(String categories) {
	        this.categories = categories;
	    }
	    
	    /**
	     * Crea las series leyendo el archivo de matrix reducida
	     */
	    private void loadData2D(){
	    	try{
	    	System.out.print("Init Load 2D Cluster Graph");
	    	List<Series<Double[]>> series = new ArrayList<Series<Double[]>>();
	    	
	    	List<Double[]> termList = this.workspaceBean.getWorkspace().getPersistence().readFileMatrix(EDataFolder.MATRIX, "reduced2D.txt");
	    	List<Double[]> centroids = this.workspaceBean.getWorkspace().getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, "centroids.txt");
	    	List<Double[]> membership = this.workspaceBean.getWorkspace().getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, "membership.txt");
	    	
	    	
	    	ArrayList<List <Double[]>> clusterList = new ArrayList<List <Double[]>>(centroids.size());
	    	
	    	for (int i = 0; i < centroids.size(); i++) {
				clusterList.add( new ArrayList<Double[]>());
			}
	    	
	    	
	    	for (int i = 0; i < termList.size(); i++) {
				Double[] membershipVector = membership.get(i);
				int clusterNumber = -1;
				Double tmpMinorValue = 0.00;
				
				for (int j = 0; j < membershipVector.length; j++) {
					if(membershipVector[j]>tmpMinorValue){
						tmpMinorValue = membershipVector[j];
						clusterNumber = j;
					}
				}
				
				List <Double[]> cluster = clusterList.get(clusterNumber);
				cluster.add(termList.get(i));
			}
	    	
	    	
	    	for (int i = 0; i < clusterList.size(); i++) {
	    		List <Double[]> termsCluster = clusterList.get(i);
	    		series.add(new Series<Double[]>(String.valueOf(i),termsCluster));
			}
	    	series.add(new Series<Double[]>("Centroides",centroids));
	    	//series.add(new Series<Double[]>("Terminos",termList));
	        setChartData(new Gson().toJson(series));
	        setCategories(new Gson().toJson(categoryList));
	    	}
	    	catch(Exception e){
	    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	    				"Error", e.getMessage());
	    		FacesContext.getCurrentInstance().addMessage(null, msg);
	    	}
	    }
	    

	   
}
