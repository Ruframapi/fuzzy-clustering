package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public  enum EDataFolder {
	CLEAN("clean","\\data\\clean"), 
	ORIGINAL_RSS("original","\\data\\original\\rss"),
	ORIGINAL_TWITTER("original","\\data\\original\\twitter"),
	INVERTED_INDEX("inverted index","\\data\\invertedindex"),
	MATRIX("matrix","\\data\\matrix"),
	BACKUP("BACKUP","\\backup");
	
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
