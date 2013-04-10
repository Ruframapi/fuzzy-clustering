package org.fuzzyclustering.web.chart.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
 
/**
 * Chart Controller
 *
 * @author Babji Prashanth, Chetty
 */
@ManagedBean(name = "chart")
@ViewScoped
public class ChartTermBean implements Serializable {
	
	public ChartTermBean(){
		this.loadChartData();
	}
	private String data;
	
	
	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	/**
     * Load Chart Data
     */
    public void loadChartData() {
    	/*IRFacade irFacade = IRFacade.getInstance();
    	int[][] termTermMatrix = irFacade.getTermTermMatrix();
    	
		StringBuilder stringBuilder = new StringBuilder();
		//stringBuilder.append("[[161.2, 51.6], [167.5, 59.0], [159.5, 49.2]]");
		stringBuilder.append("[");
		 for (Integer i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if(i>0 && j>0){
					stringBuilder.append("[");
					stringBuilder.append(i);
					stringBuilder.append(",");
					stringBuilder.append(termTermMatrix[i][j]);
					stringBuilder.append("],");
				}
			}
		}
		stringBuilder.replace(stringBuilder.length()-1, stringBuilder.length(), "]");
		this.data = stringBuilder.toString();
		 
		// bubbleModel.add(new BubbleChartSeries("Alfa Romeo", 45, 92, 36));  
		// bubbleModel.add(new BubbleChartSeries("AM General", 24, 104, 40)); 
		 * */
		
    }
 
}