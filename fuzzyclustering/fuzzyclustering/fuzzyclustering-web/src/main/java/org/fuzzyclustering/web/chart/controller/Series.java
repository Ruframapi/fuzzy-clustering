package org.fuzzyclustering.web.chart.controller;

import java.util.List;

public class Series {
	private String name;
	private List<Long> data;
	 
    public Series() {}
 
    public Series(String name, List<Long> data) {
        this.name = name;
        this.data = data;
    }
}
