package org.fuzzyclustering.web.chart.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

import com.google.gson.Gson;

@ManagedBean(name = "membershipBean")
@ViewScoped
public class ChartMembership implements Serializable{

		private String chartData;
	    private String categories;
	    private List<String> categoryList = new ArrayList<String>();
	    private List<Double> xAxisList = new ArrayList<Double>();
	    private List<Double> yAxisList = new ArrayList<Double>();
	    int index = 0;
	    private Long[] longs;
	    private WorkspaceDTO workspace;
	     
	    public ChartMembership(WorkspaceDTO workspace){
	    	this.loadData2D();
	    	this.workspace = workspace;
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

	    	List<Series<Double>> series = new ArrayList<Series<Double>>();
	    	
	    	
	    	List<Double[]> membershipMatrix = this.workspace.getPersistence().readFileMatrix(EDataFolder.MACHINE_LEARNING, "membership.txt");
	    	InvertedIndex invertedIndex = IRFacade.getInstance().getInvertedIndex(this.workspace);
	    	
	    	String[] termList = invertedIndex.getTermList();
	    	
	    	//for (int i = 0; i < membershipMatrix.get(0).length; i++) {
	    	for (int i = 0; i < 10; i++) {
	    		List membershipSerie = new ArrayList<Double>();
	    		for (int j = 0; j < membershipMatrix.size(); j++) {
	    			membershipSerie.add(membershipMatrix.get(j)[i]);
				}
	    		Collections.sort(membershipSerie);
	    		series.add(new Series<Double>(String.valueOf(i),membershipSerie));
			}

	        setChartData(new Gson().toJson(series));
	        ArrayList wordList = invertedIndex.getWordList();
	        setCategories(new Gson().toJson(wordList));
	    }
}
