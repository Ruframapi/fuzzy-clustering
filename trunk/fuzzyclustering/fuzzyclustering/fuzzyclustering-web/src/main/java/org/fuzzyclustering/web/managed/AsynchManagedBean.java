package org.fuzzyclustering.web.managed;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.context.RequestContext;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import co.edu.sanmartin.persistence.dto.DocumentDTO;

@ManagedBean(name = "asynch")
@ApplicationScoped
public class AsynchManagedBean implements Serializable{

	private static Logger logger = Logger.getLogger("AsynchManagedBean");
	private static final long serialVersionUID = -4274203108208588123L;
	private String downloadStatus = "test";
	private String documentData;
	private String cleanData;
	private String documentName;
	private OutputLabel originalDataLabel;
	private OutputLabel cleanDataLabel;
	private final PushContext pushContext = PushContextFactory.getDefault().getPushContext();
	
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
		try{
			RequestContext requestContext = RequestContext.getCurrentInstance();  
			this.documentData = document.getLazyData();
			this.cleanData = document.getLazyCleanData();
			this.documentName= document.getName();
			
			this.originalDataLabel.setValue(document.getLazyData());
			//getCleanDataLabel().setValue(this.cleanData);
			//isValid = calculate isValid
			
			//RequestContext requestContext = RequestContext.getCurrentInstance();
			//requestContext.update("originalDataLabel");
			//requestContext.update("cleanDataLabel");
			requestContext.update(":form2:documents");   
	        pushContext.push("/document", document); 
	        pushContext.notifyAll();
	        
		}catch(Throwable e){
			logger.error("Error in AsynchManagedBean setDocument",e);
		}
        
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
	
	public OutputLabel getOriginalDataLabel() {
		return originalDataLabel;
	}
	public void setOriginalDataLabel(OutputLabel originalDataLabel) {
		this.originalDataLabel = originalDataLabel;
	}
	public OutputLabel getCleanDataLabel() {
		return cleanDataLabel;
	}
	public void setCleanDataLabel(OutputLabel cleanDataLabel) {
		this.cleanDataLabel = cleanDataLabel;
	}
	public void sendMessageAsynch(String message){
		logger.info("Sending Message Asynch" + message);
		PushContext pushContext = PushContextFactory.getDefault().getPushContext(); 
		pushContext.push("/notifications", new FacesMessage("Mensaje del Servidor Remoto", message));
		
	}
	
	

}
