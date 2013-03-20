package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

@ManagedBean(name = "source")
@ViewScoped
public class SourceManagedBean implements Serializable {

	private static final long serialVersionUID = -2663754899417381620L;
	private SourceDTO sourceDTO;
	private SourceDTO sourceRSSSelected;
	private SourceDTO sourceTwitterSelected;

	private Collection<SourceDTO> sourceColl;
	private Collection<SourceDTO> rssSourceCol;
	private Collection<SourceDTO> twitterSourceCol;

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

	public Collection<SourceDTO> getRssSourceCol() {
		return rssSourceCol;
	}
	public void setRssSourceCol(Collection<SourceDTO> rssSourceCol) {
		this.rssSourceCol = rssSourceCol;
	}
	
	public Collection<SourceDTO> getTwitterSourceCol() {
		return twitterSourceCol;
	}
	public void setTwitterSourceCol(Collection<SourceDTO> twitterSourceCol) {
		this.twitterSourceCol = twitterSourceCol;
	}
	
	public SourceDTO getSourceRSSSelected() {
		return sourceRSSSelected;
	}
	public void setSourceRSSSelected(SourceDTO sourceRSSSelected) {
		this.sourceRSSSelected = sourceRSSSelected;
	}
	public SourceDTO getSourceTwitterSelected() {
		return sourceTwitterSelected;
	}
	public void setSourceTwitterSelected(SourceDTO sourceTwitterSelected) {
		this.sourceTwitterSelected = sourceTwitterSelected;
	}
	/**
	 * Almacena el objeto source en la base de datos
	 */
	public void createSource(){
		if(sourceDTO.getUrl().startsWith("http")){
			sourceDTO.setType(ESourceType.RSS);
		}
		else if(sourceDTO.getUrl().startsWith("@")){
			this.createTwitterSource();
		}
		if(sourceDTO.getType()!=null){
			try {
				PersistenceFacade.getInstance().createSource(sourceDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.loadSources();
		}
}
	
	/**
	 * Retorna los origenes de datos de acuerdo al tipo
	 * @param sourceType tipo de origen (rss, twitter)
	 * @return
	 */
	public Collection<SourceDTO> selectBySourceType(ESourceType sourceType) {
		
		Collection<SourceDTO> sourceTypeColl = new ArrayList<SourceDTO>();
		for (SourceDTO sourceDTO : sourceColl) {
			if(sourceDTO.getType().equals(sourceType)){
				sourceTypeColl.add(sourceDTO);
			}
		}
		return sourceTypeColl;
	}
	
	
	/**
	 * Elimina el objeto source en la base de datos
	 */
	public void deleteRssSource(){
		try {
			PersistenceFacade.getInstance().deleteSource(sourceRSSSelected);
			this.loadSources();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Crea el origen de datos de twitter y lo registra como Friend en la
	 * cuenta del usuario de twitter
	 */
	private void createTwitterSource(){
		this.sourceDTO.setType(ESourceType.TWITTER);
		//WebscrapingFacade webscrapingFacade = WebscrapingFacade.getInstance();
		//webscrapingFacade.createFriendship(this.sourceDTO.getUrl());
	}
	/**
	 * Elimina el objeto source en la base de datos y de los amigos en la
	 * cuenta de twitter
	 */
	public void deleteTwitterSource(){
		try {
			//WebscrapingFacade webscrapingFacade = WebscrapingFacade.getInstance();
			//webscrapingFacade.removeFriendship(this.sourceDTO.getUrl());
			PersistenceFacade.getInstance().deleteSource(sourceTwitterSelected);
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
				this.sourceColl = PersistenceFacade.getInstance().getAllSources(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.rssSourceCol = selectBySourceType(ESourceType.RSS);
			this.twitterSourceCol = selectBySourceType(ESourceType.TWITTER);
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
