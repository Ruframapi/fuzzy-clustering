package org.fuzzyclustering.web.chart.controller;

import java.util.List;

public class Series<T> {
	private String name;
	private List<T> data;
	 
    public Series() {}
 
    public Series(String name, List<T> data) {
        this.name = name;
        this.data = data;
    }
}
