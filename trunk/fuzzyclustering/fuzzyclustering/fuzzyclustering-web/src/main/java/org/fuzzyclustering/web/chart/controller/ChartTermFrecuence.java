package org.fuzzyclustering.web.chart.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.fuzzyclustering.web.managed.WorkspaceManagedBean;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;

import com.google.gson.Gson;

/**
 * Chart Controller
 *
 * @author Babji Prashanth, Chetty
 */
@ManagedBean(name = "termFrecuenceChartBean")
@ViewScoped
public class ChartTermFrecuence implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String chartData;
	private String categories;
	private List<String> categoryList = new ArrayList<String>();
	private List<Double> xAxisList = new ArrayList<Double>();
	private List<Double> yAxisList = new ArrayList<Double>();
	SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
	int index = 0;
	private Long[] longs;
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;

	@PostConstruct
	public void init(){
		this.loadDataOriginal();
	}


	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}


	/**
	 * Load Chart Data
	 */
	public void loadChartData() {
		System.out.print("init load chart data");

		List<Series> series = new ArrayList<Series>();

		long heapSize = Runtime.getRuntime().maxMemory();

		xAxisList.add(new Double(1));
		xAxisList.add(new Double(4));
		xAxisList.add(new Double(6));
		xAxisList.add(new Double(7));
		xAxisList.add(new Double(8));


		yAxisList.add(new Double(2));
		yAxisList.add(new Double(3));
		yAxisList.add(new Double(6));
		yAxisList.add(new Double(7));
		yAxisList.add(new Double(8));

		//series.add(new Series("Serie 1", xAxisList));
		//series.add(new Series("Serie 2", yAxisList));

		setChartData(new Gson().toJson(series));

		//categoryList.add(sdfDate.format(new Date()));

		setCategories(new Gson().toJson(categoryList));
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
	public void loadDataOriginal(){
		this.loadData("original");
	}
	
	public void loadDataZipf(){
		this.loadData("zipf");
	}
	
	private void loadData(String type){
		//TODO Habilitar la consulta de terminos via AJAX
				try{
					List<Series<Integer>> series = new ArrayList<Series<Integer>>();
					IRFacade irFacade = IRFacade.getInstance(this.workspaceBean.getWorkspace());
					InvertedIndex invertedIndexOriginal = irFacade.getInvertedIndexOriginal();
					List<Integer> original =this.getInvertedIndexList(invertedIndexOriginal);
					List<Integer> zipf =this.getInvertedIndexListZipf();
					List<Integer> left = new ArrayList<Integer>();
					List<Integer> right = new ArrayList<Integer>();
					
					//if(type.equals("original")){
					//	series.add(new Series<Integer>("Completo",original));
					//}
					//series.add(new Series<Integer>("Zipf",zipf));
					Integer transitionPointInteger = new Integer(new Double(invertedIndexOriginal.getTransitionPoint(1)).intValue());
					for (Integer frecuency : original) {
						if(frecuency>=transitionPointInteger){
							left.add(frecuency);
							right.add(null);
						}
						else{
							left.add(null);
							right.add(frecuency);
						}
					}
					
					series.add(new Series<Integer>("Mayor Frecuencia",left));
					series.add(new Series<Integer>("Menor Frecuencia",right));
					
					setChartData(new Gson().toJson(series));
					for (Object term : keys) {
						categoryList.add(term.toString());
					}
					//setCategories(new Gson().toJson(categoryList));
				}
				catch(Exception e){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Error", e.getMessage());
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
	}
	
	
	Object[] keys;
	/**
	 * Retorna la lista de
	 * @param invertedIndex
	 * @return
	 */
	public List<Integer> getInvertedIndexList(InvertedIndex invertedIndex){
		List<Integer> intList = new ArrayList<Integer>();
		
		
		String[] termList = invertedIndex.getTermList();
		LinkedHashMap<String,Integer> filterList = new LinkedHashMap<String,Integer>();
		for (int i = 0; i < termList.length; i++) {
			filterList.put(termList[i].split("\t")[0],Integer.parseInt(termList[i].split("\t")[2]));
		}
		//Ordenamos el HashMap
		filterList = invertedIndex.sortHashMapByValue(filterList);
		
		keys = (Object[]) filterList.keySet().toArray();
        Object[] values = (Object[]) filterList.values().toArray();
        
        //Integer[] integerArray = Arrays.copyOf(values, values.length, Integer[].class);
        
        for (Object object : values) {
        	intList.add((Integer) object);
		}
        return intList;
	}
	
	/**
	 * Retorna la lista de
	 * @param invertedIndex
	 * @return
	 */
	public List<Integer> getInvertedIndexListZipf(){
		List<Integer> intList = new ArrayList<Integer>();
		IRFacade irFacade = IRFacade.getInstance(this.workspaceBean.getWorkspace());
		InvertedIndex invertedIndexZipf = irFacade.getInvertedIndexZipf();
		
		HashMap<String,Integer> filterList = new HashMap<String,Integer>();
		for (Object object : keys) {
			String key = object.toString();
			if(invertedIndexZipf.getInvertedIndexMap().containsKey(key)){
				Integer keyLength = invertedIndexZipf.getInvertedIndexMap().get(key).length;
				intList.add(keyLength);
			}
			else{
				intList.add(null);
			}
		}
        return intList;
	}
}