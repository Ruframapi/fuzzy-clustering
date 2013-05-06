package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

@ManagedBean(name = "setup")
@ViewScoped
public class SetupManagedBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspace;
	
	@PostConstruct
	public void init(){
		this.loadPropertyColl();
	}

	public void setWorkspace(WorkspaceManagedBean workspace) {
		this.workspace = workspace;
	}

	private Collection<PropertyDTO> propertyCol = new ArrayList<PropertyDTO>();

	public Collection<PropertyDTO> getPropertyCol() {
		return propertyCol;
	}

	public void setPropertyCol(Collection<PropertyDTO> propertyCol) {
		this.propertyCol = propertyCol;
	}
	
	public void loadPropertyColl() {
		try {
			this.propertyCol = this.workspace.getWorkspace().getPersistence().getAllProperties(true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onRowEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Propiedad Modificada",
				((PropertyDTO) event.getObject()).getName()+((PropertyDTO)event.getObject()).getValue());

		FacesContext.getCurrentInstance().addMessage(null, msg);
		try {
			this.workspace.getWorkspace().getPersistence().updateProperty((PropertyDTO) event.getObject());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshMemoryData(){
		this.workspace.getWorkspace().getPersistence().refreshMemoryData();
	}
	
	public void backupSystem(){
		this.workspace.getWorkspace().getPersistence().backupSystem();
	}
	
	public void restoreSystem(){
		this.workspace.getWorkspace().getPersistence().restoreSystem();
	}

}
