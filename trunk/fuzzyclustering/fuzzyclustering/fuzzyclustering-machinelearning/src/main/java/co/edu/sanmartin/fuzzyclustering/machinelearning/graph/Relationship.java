package co.edu.sanmartin.fuzzyclustering.machinelearning.graph;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import co.edu.sanmartin.persistence.facade.PersistenceFacade;


public class Relationship {

	public void buildJson(){
		String titulo = "Matriz de Relacion";
		String dataFile = PersistenceFacade.getInstance().readFile("relationship.txt");
		String json = null;
		

			JSONArray jsArray = new JSONArray();
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(1);
			list.add(0);
			list.add(4);
			jsArray.add(list);
			ArrayList<Integer> list2 = new ArrayList<Integer>();
			list2.add(5);
			list2.add(7);
			list2.add(3);
			jsArray.add(list2);                



			JSONObject seriePrimera = new JSONObject();

			seriePrimera.put("name", "jane");

			seriePrimera.put("data", jsArray.get(0));                        



			JSONObject serieSegunda = new JSONObject();

			serieSegunda.put("name", "John");

			serieSegunda.put("data", jsArray.get(1));



			JSONArray series = new JSONArray(); 

			series.add(seriePrimera);

			series.add(serieSegunda);



			json = series.toJSONString();    
		
	}



	}