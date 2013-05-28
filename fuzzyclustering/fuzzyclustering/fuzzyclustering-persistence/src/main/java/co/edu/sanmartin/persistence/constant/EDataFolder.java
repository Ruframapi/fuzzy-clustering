package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public  enum EDataFolder {
	
	
	DATA_ROOT("dataRoot","data"),
	CLEAN("clean","clean"), 
	DOWNLOAD("download","download"),
	INVERTED_INDEX("inverted index","invertedindex"),
	MATRIX("matrix","matrix"),
	MACHINE_LEARNING("machine learning","machinelearning"),
	BACKUP("BACKUP","backup"),
	LEXICON("LEXICON","lexicon"),
	TRAIN("TRAIN","train");
	
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
	
	/**
	 * Retorna la enumeracion de acuerdo al valor del path
	 * @return
	 */
	public EDataFolder getFromPath(String path){
		EDataFolder folder = null;
		for (EDataFolder dataFolder : EDataFolder.toList()) {
			if(dataFolder.getPath().equals(path)){
				folder = dataFolder;
				break;
			}
		}
		return folder;
	}
}
