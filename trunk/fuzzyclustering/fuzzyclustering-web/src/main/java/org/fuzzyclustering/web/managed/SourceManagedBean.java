package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

@ManagedBean(name = "source")
@ViewScoped
public class SourceManagedBean implements Serializable {

	private SourceDTO sourceDTO;
	private SourceDTO sourceSelected;

	private Collection<SourceDTO> sourceCol;

	public SourceManagedBean(){
		this.newSource();
		this.loadSources();
	}
	public SourceDTO getSourceDTO() {
		return sourceDTO;
	}

	public void setSourceDTO(SourceDTO sourceDTO) {
		this.sourceDTO = sourceDTO;
	}

	public Collection<SourceDTO> getSourceCol() {
		return sourceCol;
	}
	public void setSourceCol(Collection<SourceDTO> sourceCol) {
		this.sourceCol = sourceCol;
	}
	
	public SourceDTO getSourceSelected() {
		return sourceSelected;
	}
	public void setSourceSelected(SourceDTO sourceSelected) {
		this.sourceSelected = sourceSelected;
	}
	
	/**
	 * Almacena el objeto source en la base de datos
	 */
	public void createSource(){
		try {
			PersistenceFacade.getInstance().createSource(sourceDTO);
			this.loadSources();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina el objeto source en la base de datos
	 */
	public void deleteSource(){
		try {
			PersistenceFacade.getInstance().deleteSource(sourceSelected);
			this.loadSources();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Crea un nuevo objeto source
	 */
	public void newSource(){
		this.sourceDTO = new SourceDTO();
	}
	
	/**
	 * Carga la lista de los origenes de datos
	 */
	public void loadSources(){
		try {
			this.sourceCol = PersistenceFacade.getInstance().getAllSources();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Evento generado al momento de seleccionar una fila
	 * @param event
	 */
   public void onRowSelect(SelectEvent event) {  
        FacesMessage msg = new FacesMessage("Registro Seleccionado", ((SourceDTO) event.getObject()).getName());  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }
}
