package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;


@ManagedBean(name = "irDownload")
@ViewScoped
public class IRDownloadManagedBean implements Serializable {

	private static final long serialVersionUID = 3153779723933087121L;
	private static Logger logger = Logger.getRootLogger();
	private Collection<QueueDTO> downloadEnqueue;
	private Collection<QueueDTO> downloadActive;
	private int downloadDocumentAmount;
	
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	
	
	public void load(){
		documents.loadDocuments(EDataFolder.DOWNLOAD);
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	public void download(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "Consultando las fuentes de información...");
		Calendar calendar = PersistenceFacade.getInstance().getServerDate();
		this.addQueueDownload(EQueueEvent.DOWNLOAD_RSS, calendar.getTime());
		calendar.add(Calendar.SECOND, 30);
		this.addQueueDownload(EQueueEvent.DOWNLOAD_TWITTER, calendar.getTime());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		//WebscrapingFacade.getInstance().downloadSources();
	}
	
	public void reloadSettings(){
		logger.debug("Reloading Settings");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Actualizacion Configuracion", "Cargando los cambios a las variables de configuracion");
		this.addQueueDownload(EQueueEvent.RELOAD_DATA_MEMORY, new Date());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	/**
	 * Adiciona una cola en el webscrapping
	 * @param queueEvent tipo de evento a encolar
	 */
	private void addQueueDownload(EQueueEvent queueEvent, Date date){
		QueueDTO queue = new QueueDTO();
		queue.setModule(EModule.WEBSCRAPPING);
		queue.setEvent(queueEvent);
		queue.setInitDate(date);
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			PersistenceFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina completamente la descarga de los archivos y los archivos procesados.
	 */
	public void deleteDownloadSources(){
		QueueDTO queue = new QueueDTO();
		queue.setModule(EModule.WEBSCRAPPING);
		queue.setEvent(EQueueEvent.CLEAN_DOWNLOAD);
		queue.setInitDate(PersistenceFacade.getInstance().getServerDate().getTime());
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			PersistenceFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Actualiza la información de las descargas
	 */
	public void poolListener(){
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		Collection<QueueDTO> enqueueColl = new ArrayList<QueueDTO>();
		this.downloadEnqueue = persistenceFacade.getQueueByStatusDate(EModule.WEBSCRAPPING, 
																			EQueueStatus.ENQUEUE, new Date());
		this.downloadActive = persistenceFacade.getQueueByStatusDate(EModule.WEBSCRAPPING, 
																			EQueueStatus.ACTIVE, new Date());
		this.downloadDocumentAmount = persistenceFacade.getDownloadDocumentAmount();
	}

	public Collection<QueueDTO> getDownloadEnqueue() {
		return downloadEnqueue;
	}

	public Collection<QueueDTO> getDownloadActive() {
		return downloadActive;
	}

	public int getDownloadDocumentAmount() {
		return downloadDocumentAmount;
	}
	

	
	
	
	
	
	

}