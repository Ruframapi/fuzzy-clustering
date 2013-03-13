package org.fuzzyclustering.web.chart.controller;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
 
/**
 * Chart Controller
 *
 * @author Babji Prashanth, Chetty
 */
@ManagedBean(name = "chartController")
@ViewScoped
public class ChartTermController implements Serializable {
    private String chartData;
    private String categories;
    private List<String> categoryList = new ArrayList<String>();
    private List<Long> heapSizeList = new ArrayList<Long>();
    private List<Long> usedHeapSizeList = new ArrayList<Long>();
    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
    private static final long MB = 1024*1024;
    int index = 0;
    private Long[] longs;
     
    public ChartTermController(){
    	this.loadChartData();
    }
    /**
     * Load Chart Data
     */
    public void loadChartData() {
    	System.out.print("init load chart data");
        if(heapSizeList.size() > 10) {
            heapSizeList.remove(0);
            usedHeapSizeList.remove(0);
            categoryList.remove(0);
        }
        List<Series> series = new ArrayList<Series>();
         
        malloc();
        long heapSize = Runtime.getRuntime().maxMemory();
        heapSizeList.add(heapSize/MB);
        usedHeapSizeList.add((heapSize - Runtime.getRuntime().freeMemory())/MB);
         
        series.add(new Series("Heap Size", heapSizeList));
        series.add(new Series("Used Heap", usedHeapSizeList));
 
        setChartData(new Gson().toJson(series));
         
        categoryList.add(sdfDate.format(new Date()));
         
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
     
    private void malloc() {
        if(index%2 == 0) {
            longs = new Long[100000];
            for(int i=0;i<1000;i++) {
                longs[i] = Long.valueOf(i);
            }
        } else {
            longs = null;
        }
        index++;
    }
}