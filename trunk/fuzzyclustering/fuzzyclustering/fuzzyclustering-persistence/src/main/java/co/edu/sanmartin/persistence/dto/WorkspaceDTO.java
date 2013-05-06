package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;

import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class WorkspaceDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6928603781576747107L;
	private String name;
	private String databaseHost;
	private String dataRoot;
	private PersistenceFacade persistence;


	public WorkspaceDTO(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public void setDatabaseHost(String databaseHost) {
		this.databaseHost = databaseHost;
	}

	public String getDataRoot() {
		return dataRoot;
	}

	public void setDataRoot(String dataRoot) {
		this.dataRoot = dataRoot;
	}
	
	public  PersistenceFacade getPersistence() {
		if(persistence==null){
			this.persistence = new PersistenceFacade(this);
		}
		return persistence;
	}
	
	@Override
	public boolean equals(Object other) {
	    return (other instanceof WorkspaceDTO) && (name != null)
	        ? name.equals(((WorkspaceDTO) other).name)
	        : (other == this);
	}
	
}
