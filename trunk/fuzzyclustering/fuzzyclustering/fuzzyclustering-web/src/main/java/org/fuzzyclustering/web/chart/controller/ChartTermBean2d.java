package org.fuzzyclustering.web.chart.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

import com.google.gson.Gson;

/**
 * Chart Controller
 *
 * @author Babji Prashanth, Chetty
 */
@ManagedBean(name = "termChartBean")
@ViewScoped
public class ChartTermBean2d implements Serializable {
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
		this.loadData2D();
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
	private void loadData2D(){
		try{
			List<Series<Double[]>> series = new ArrayList<Series<Double[]>>();

			List<Double[]> doubleArray = new ArrayList<Double[]>();
			WorkspaceDTO workspace = this.workspaceBean.getWorkspace();
			doubleArray = workspace.getPersistence().readFileMatrix(EDataFolder.MATRIX, "reduced2D.txt");
			
			IRFacade irFacade = IRFacade.getInstance(workspace);
			InvertedIndex invertedIndex = new InvertedIndex(workspace);
			invertedIndex.loadInvertedIndexDataZipf();
			String[] terms = invertedIndex.getTermList();
			series.add(new Series<Double[]>("Serie 1",doubleArray));
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