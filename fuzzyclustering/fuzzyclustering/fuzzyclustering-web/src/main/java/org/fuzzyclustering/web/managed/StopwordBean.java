package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

@ManagedBean( name = "stopword")
@ViewScoped
public class StopwordBean implements Serializable{
	private static Logger logger  = Logger.getRootLogger();
	private static final long serialVersionUID = -8166935663965015152L;
	
	private StopwordDTO stopwordDTO;
	private StopwordDTO stopwordSelected;
	private Collection<StopwordDTO> stopwordCol;
	private boolean isSelected;
	
	public StopwordBean(){
		this.newStopword();
		this.loadStopwordCol();
	}
	
	
	public StopwordDTO getStopwordDTO() {
		return stopwordDTO;
	}


	public void setStopwordDTO(StopwordDTO stopwordDTO) {
		this.stopwordDTO = stopwordDTO;
	}


	public StopwordDTO getStopwordSelected() {
		return stopwordSelected;
	}


	public void setStopwordSelected(StopwordDTO stopwordSelected) {
		this.stopwordSelected = stopwordSelected;
	}


	public Collection<StopwordDTO> getStopwordCol() {
		return stopwordCol;
	}


	public void setStopwordCol(Collection<StopwordDTO> stopwordCol) {
		this.stopwordCol = stopwordCol;
	}
	
	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}


	/**
	 * Almacena el objeto source en la base de datos
	 */
	public void createStopword(){
		logger.debug("Init Create Stop Word Method");
		try {
			stopwordDTO.setStopwordOrder(this.stopwordCol.size());
			PersistenceFacade.getInstance().createStopword(stopwordDTO);
			PersistenceFacade.getInstance().refreshMemoryData();
			this.loadStopwordCol();
			this.stopwordDTO = new StopwordDTO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	/**
	 * Elimina el objeto source en la base de datos
	 */
	public void deleteStopword(){
		try {
			PersistenceFacade.getInstance().deleteStopword(stopwordDTO);
			PersistenceFacade.getInstance().refreshMemoryData();
			this.loadStopwordCol();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Crea un nuevo objeto source
	 */
	public void newStopword(){
		this.stopwordDTO = new StopwordDTO();
	}
	
	/**
	 * Carga la lista de los origenes de datos
	 */
	public void loadStopwordCol(){
		try {
			this.stopwordCol = PersistenceFacade.getInstance().getAllStopword();
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
	   FacesMessage msg = new FacesMessage("Stopword Selected", ((StopwordDTO) event.getObject()).getName());  
       FacesContext.getCurrentInstance().addMessage(null, msg);  
    }
   
   public void onRowEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Propiedad Modificada",
				((StopwordDTO) event.getObject()).getName());

		FacesContext.getCurrentInstance().addMessage(null, msg);
		try {
			PersistenceFacade.getInstance().updateStopword((StopwordDTO) event.getObject());
			PersistenceFacade.getInstance().refreshMemoryData();
			this.loadStopwordCol();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   public void upRule(){
	   
	   try {
		   if(this.stopwordDTO.getStopwordOrder()>0){
			  ArrayList<StopwordDTO> stopwordList = (ArrayList<StopwordDTO>) this.stopwordCol;
			  int position = stopwordList.indexOf(this.stopwordDTO);
		      this.stopwordDTO.setStopwordOrder(position-1);
		      PersistenceFacade.getInstance().updateStopword(this.stopwordDTO);
		      StopwordDTO stopwordBefore = stopwordList.get(position-1);
		      stopwordBefore.setStopwordOrder(position);
		      PersistenceFacade.getInstance().updateStopword(stopwordBefore);
		      PersistenceFacade.getInstance().refreshMemoryData();
			  this.loadStopwordCol();  
		   }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
   
	
}
