package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public  enum EDataFolder {
	
	
	DATA_ROOT("dataRoot",System.getProperty("file.separator")+"data"),
	CLEAN("clean",System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"clean"), 
	ORIGINAL_RSS("original",System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"original"+System.getProperty("file.separator")+"rss"),
	ORIGINAL_TWITTER("original",System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"original"+System.getProperty("file.separator")+"twitter"),
	INVERTED_INDEX("inverted index",System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"invertedindex"),
	MATRIX("matrix",System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"matrix"),
	MACHINE_LEARNING("machine learning", System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"machinelearning"),
	BACKUP("BACKUP",System.getProperty("file.separator")+"backup");
	
	private String name;
	private String path;

	EDataFolder(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public static Collection<EDataFolder> toList(){
		List<EDataFolder> list = Arrays.asList(EDataFolder.values()); 
		return list;
	}
}
