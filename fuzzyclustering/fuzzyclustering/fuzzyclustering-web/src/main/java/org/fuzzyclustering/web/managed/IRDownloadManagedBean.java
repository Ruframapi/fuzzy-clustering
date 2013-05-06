package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.QueueFacade;


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
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;
	
	
	@PostConstruct
	public void init(){
		if(workspaceBean!= null && workspaceBean.getWorkspace()!=null){
		this.downloadDocumentAmount = 
				this.workspaceBean.getWorkspace().getPersistence().getDownloadDocumentAmount();
		}
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	
	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}

	public void download(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "Consultando las fuentes de información...");
		Calendar calendar = QueueFacade.getInstance().getServerDate();
		this.addQueueDownload(EQueueEvent.DOWNLOAD_RSS, calendar.getTime());
		calendar.add(Calendar.SECOND, 30);
		this.addQueueDownload(EQueueEvent.DOWNLOAD_TWITTER, calendar.getTime());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		//WebscrapingFacade.getInstance().downloadSources();
	}
	
	/**
	 * Realiza la Descarga de la fuente Reuters
	 */
	public void downloadReuters(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		Calendar calendar = QueueFacade.getInstance().getServerDate();
		this.addQueueDownload(EQueueEvent.DOWNLOAD_TRAIN, calendar.getTime());
		msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Proceso Finalizado", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
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
		queue.setParams("data");
		queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			QueueFacade.getInstance().insertQueue(queue);
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
		queue.setInitDate(QueueFacade.getInstance().getServerDate().getTime());
		queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
		queue.setStatus(EQueueStatus.ENQUEUE);
		queue.setParams("data");
		try {
			QueueFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Actualiza la información de las descargas
	 */
	public void poolListener(){
		Collection<QueueDTO> enqueueColl = new ArrayList<QueueDTO>();
		this.downloadEnqueue = QueueFacade.getInstance().getQueueByStatusDate(EModule.WEBSCRAPPING, 
																			EQueueStatus.ENQUEUE, new Date());
		this.downloadActive = QueueFacade.getInstance().getQueueByStatusDate(EModule.WEBSCRAPPING, 
																			EQueueStatus.ACTIVE, new Date());
		this.downloadDocumentAmount = this.workspaceBean.getWorkspace().getPersistence().getDownloadDocumentAmount();
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
	

	public void load(){
		
	}
	

}
