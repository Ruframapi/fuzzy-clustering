package org.fuzzyclustering.web.managed;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import co.edu.sanmartin.persistence.dto.DocumentDTO;

@ManagedBean(name = "asynch")
@ApplicationScoped
public class AsynchManagedBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4274203108208588123L;
	private String downloadStatus = "test";
	private String documentData;
	private String cleanData;
	private String documentName;
	

	@PostConstruct
	public void init(){

	}
	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();  
        pushContext.push("/status", downloadStatus); 
	}
	public void setDocument(DocumentDTO document) {
		this.documentData = document.getLazyData();
		this.cleanData = document.getLazyCleanData();
		this.documentName= document.getName();
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();  
        pushContext.push("/document", document); 
	}
	
	public String getDocumentData() {
		return documentData;
	}
	public String getCleanData() {
		return cleanData;
	}
	public String getDocumentName() {
		return documentName;
	}
	
	public void sendMessageAsynch(String message){
		
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Mensaje Recibido", message);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	

}
