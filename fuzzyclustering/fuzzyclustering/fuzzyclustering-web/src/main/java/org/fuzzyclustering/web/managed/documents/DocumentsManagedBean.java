package org.fuzzyclustering.web.managed.documents;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Managed bean que gestiona la vista de las carpetas de contienen 
 * los archivos del procesamiento del sistema
 * @author Ricardo Carvajal Salamanca
 *
 */
@ManagedBean(name = "documents")
@SessionScoped
public class DocumentsManagedBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4934178771044717618L;
	private static Logger logger = Logger.getLogger("DocumentsManagedBean");
	private Collection<DocumentDTO> documentsColl;
	private DocumentDTO document;
	private String originalDataDocument;
	private String cleanDataDocument;
	
	public Collection<DocumentDTO> getDocumentsColl() {
		return documentsColl;
	}

	public void setDocumentsColl(Collection<DocumentDTO> documentsColl) {
		this.documentsColl = documentsColl;
	}

	public DocumentDTO getDocument() {
		return document;
	}

	public void setDocument(DocumentDTO document) {
		this.document = document;
	}

	public void loadDocuments(EDataFolder dataFolder){
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		Collection<DocumentDTO> fileList = persistenceFacade.getFileList(dataFolder);
		this.documentsColl = fileList;
	}
	
	/**
	 * Crea una cola de consula al seleccionar un documento
	 * @param event
	 */
	public void onRowSelect(SelectEvent event){
		DocumentDTO documentSelect = (DocumentDTO) event.getObject();
		FacesMessage msg = new FacesMessage("Consultando Documento ", 
											"ID:"+documentSelect.getId() + 
											" Name:" +documentSelect.getName() );  
	    FacesContext.getCurrentInstance().addMessage(null, msg); 
		sendQueryDocument(documentSelect.getId());
	}
	
	/**
	 * Envia una consulta del texto del documento al servidor
	 */
	private void sendQueryDocument(int documentId){
		logger.info("Sending Query document document Id:" + documentId );
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		QueueDTO queue = new QueueDTO();
		queue.setEvent(EQueueEvent.QUERY_DOCUMENT);
		queue.setModule(EModule.QUERYASYNCH);
		queue.setInitDate(persistenceFacade.getServerDate().getTime());
		queue.setParams(String.valueOf(documentId));
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			persistenceFacade.insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	
	public String getOriginalDataDocument() {
		return originalDataDocument;
	}

	public void setOriginalDataDocument(String originalDataDocument) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(originalDataDocument);
		int position = 0;
		while((position+=100)<originalDataDocument.length()){
			stringBuilder.insert(position+10, " ");
		}
		this.originalDataDocument = stringBuilder.toString();
	}

	public String getCleanDataDocument() {
		return cleanDataDocument;
	}

	public void setCleanDataDocument(String cleanDataDocument) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(originalDataDocument);
		int position = 0;
		while((position+=100)<originalDataDocument.length()){
			stringBuilder.insert(position+10, " ");
		}
		this.cleanDataDocument = stringBuilder.toString();
	}


	private DocumentsLazyDataModel documentsModel = null;
	
	public DocumentsLazyDataModel getDocuments(){
		if(documentsModel == null){
			documentsModel = new DocumentsLazyDataModel();
		}
		return documentsModel;
	}
	
	public void normalize(){
		FacesMessage msg = new FacesMessage("Normalizando Documento ", 
				"ID:"+this.document.getId() + 
				" Name:" +this.document.getName() ); 
		FacesContext.getCurrentInstance().addMessage(null, msg); 
		this.document.setCleanDate(null);
		try {
			PersistenceFacade.getInstance().updateDocument(this.document);
			try {
				Thread.sleep (1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendQueryDocument(document.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Error in normalize",e);
		}
		
	}

	
	
}
