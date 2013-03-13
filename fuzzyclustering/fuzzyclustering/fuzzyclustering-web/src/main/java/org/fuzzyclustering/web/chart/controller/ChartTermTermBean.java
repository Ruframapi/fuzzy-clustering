package org.fuzzyclustering.web.chart.controller;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.BubbleChartModel;
import org.primefaces.model.chart.BubbleChartSeries;
 
/**
 * Chart Controller
 *
 * @author Babji Prashanth, Chetty
 */
@ManagedBean(name = "chartTermTerm")
@ViewScoped
public class ChartTermTermBean implements Serializable {
	 private BubbleChartModel bubbleModel; 
     
    public ChartTermTermBean(){
    	this.loadChartData();
    }
    public BubbleChartModel getBubbleModel() {
		return bubbleModel;
	}
	public void setBubbleModel(BubbleChartModel bubbleModel) {
		this.bubbleModel = bubbleModel;
	}
	/**
     * Load Chart Data
     */
    public void loadChartData() {
    	IRFacade irFacade = IRFacade.getInstance();
    	int[][] termTermMatrix = irFacade.getTermTermMatrix();
    	
		 bubbleModel = new BubbleChartModel();  
		 //bubbleModel.add(new BubbleChartSeries("Acura", 70, 183,55));  
		 for (Integer i = 0; i < 30; i++) {
			for (int j = 0; j < termTermMatrix[i].length; j++) {
				bubbleModel.add(new BubbleChartSeries(i.toString(), i, j, termTermMatrix[i][j]));
			}
		}
		 
		 
		// bubbleModel.add(new BubbleChartSeries("Alfa Romeo", 45, 92, 36));  
		// bubbleModel.add(new BubbleChartSeries("AM General", 24, 104, 40)); 
    }
 
}