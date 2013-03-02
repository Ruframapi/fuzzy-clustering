package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

@ManagedBean(name = "setup")
@ViewScoped
public class SetupManagedBean implements Serializable {
	
	private Collection<PropertyDTO> propertyCol = new ArrayList<PropertyDTO>();
	
	public SetupManagedBean(){
		this.loadPropertyColl();
	}
	
	public Collection<PropertyDTO> getPropertyCol() {
		return propertyCol;
	}

	public void setPropertyCol(Collection<PropertyDTO> propertyCol) {
		this.propertyCol = propertyCol;
	}

	/**
	 * Inicializa el Sistema
	 */
	public void initializeTables() {
		try {
			PersistenceFacade.getInstance().initialize();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Eliminacion de Tablas", "El sistema se ha inicializado. "
							+ "Todas las configuraciones fueron eliminadas");
			FacesContext.getCurrentInstance().addMessage(null, message);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retorna las propiedades del sistema
	 * 
	 * @return
	 */
	public void loadPropertyColl() {
		try {
			this.propertyCol = PersistenceFacade.getInstance().getAllProperties(true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Evento que gestion la edicion de la celda de los parametros de
	 * configuracion
	 */
	public void onCellEdit(CellEditEvent event) {
		int idx = event.getRowIndex();
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if (newValue != null && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Cell Changed", "Old: " + oldValue + ", New:" + newValue);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public void onRowEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Propiedad Modificada",
				((PropertyDTO) event.getObject()).getName()+((PropertyDTO)event.getObject()).getValue());

		FacesContext.getCurrentInstance().addMessage(null, msg);
		try {
			PersistenceFacade.getInstance().updateProperty((PropertyDTO) event.getObject());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshMemoryData(){
		PersistenceFacade.getInstance().refreshMemoryData();
	}
	
	public void backupSystem(){
		PersistenceFacade.getInstance().backupSystem();
	}
	
	public void restoreSystem(){
		PersistenceFacade.getInstance().restoreSystem();
	}
}
