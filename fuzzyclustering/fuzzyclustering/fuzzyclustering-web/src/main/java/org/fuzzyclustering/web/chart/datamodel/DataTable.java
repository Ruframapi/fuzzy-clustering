package org.fuzzyclustering.web.chart.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataTable {

	private int columnCount;
	private ArrayList<DataRow> rowData = new ArrayList<DataRow>();

	public int getColumnCount() {
		return columnCount;
	}
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public ArrayList<DataRow> getRowData() {
		return rowData;
	}
	public void setRowData(ArrayList<DataRow> rowData) {
		this.rowData = rowData;
	}
	public void loadData(HashMap<String,Double[]> dataMap){
		rowData = new ArrayList<DataRow>();
		if(dataMap!=null){
			Iterator it = dataMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				String key = (String)e.getKey();
				Double[] values = (Double[])e.getValue();
				DataRow dataRow = new DataRow();
				dataRow.getRowData().add(key);
				if(values!=null){
					for (Double double1 : values) {
						if(double1!=null){
							dataRow.getRowData().add(double1.toString());
						}
					}
				}
				this.rowData.add(dataRow);
			}
		}
	}


}
