package org.fuzzyclustering.web.managed.documents;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.WorkspaceManagedBean;
import org.primefaces.event.SelectEvent;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.QueueFacade;

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
	private DocumentDTO document = new DocumentDTO();
	private String originalDataDocument;
	private String cleanDataDocument;
	private int documentId;
	private DocumentDTO selectedDocument;

	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspace;


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


	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public void setWorkspace(WorkspaceManagedBean workspace) {
		this.workspace = workspace;
	}

	public void loadDocuments(String dataRoot, EDataFolder dataFolder){
		Collection<DocumentDTO> fileList = 
				this.workspace.getWorkspace().getPersistence().getFileList(dataFolder);
		this.documentsColl = fileList;
	}

	/**
	 * Crea una cola de consula al seleccionar un documento
	 * @param event
	 */
	public void onRowSelect(SelectEvent event){
		this.document = (DocumentDTO) event.getObject();
		FacesMessage msg = new FacesMessage("Consultando Documento ", 
				"ID:"+document.getId() + 
				" Name:" +document.getName() );  
		FacesContext.getCurrentInstance().addMessage(null, msg); 
		//sendQueryDocumentAsynch(documentSelect.getId());
		sendQueryDocument(document.getName());
		this.selectedDocument = this.document;
	}

	/**
	 * Envia una consulta del texto del documento al servidor
	 */
	private void sendQueryDocumentAsynch(int documentId){
		logger.info("Sending Query document document Id:" + documentId );
		QueueDTO queue = new QueueDTO();
		queue.setEvent(EQueueEvent.QUERY_DOCUMENT);
		queue.setModule(EModule.QUERYASYNCH);
		queue.setInitDate(QueueFacade.getInstance().getServerDate().getTime());
		queue.setParams(String.valueOf(documentId));
		queue.setStatus(EQueueStatus.ENQUEUE);
		queue.setWorkspace(this.workspace.getWorkspace().getName());
		try {
			QueueFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * Envia una consulta del texto del documento al servidor
	 */
	private void sendQueryDocument(String documentName){
		logger.info("Sending Query document document Id:" + documentId );
		String originalData = this.workspace.getWorkspace().getPersistence().readFile(EDataFolder.DOWNLOAD, documentName);
		try{
			String cleanData = this.workspace.getWorkspace().getPersistence().readFile(EDataFolder.CLEAN, documentName);
			this.cleanDataDocument = cleanData.replace(",", " ");
		}
		catch(Exception e){
			logger.info("No Existe el archivo normalizado");
		}
		this.originalDataDocument = originalData;
		
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
			documentsModel = new DocumentsLazyDataModel(this.workspace.getWorkspace());;
		}
		return documentsModel;
	}

	public void normalize(){
		if(this.selectedDocument!=null){
			FacesMessage msg = new FacesMessage("Normalizando Documento ", 
					"ID:"+this.selectedDocument.getId() + 
					" Name:" +this.selectedDocument.getName() ); 
			FacesContext.getCurrentInstance().addMessage(null, msg); 
			this.selectedDocument.setCleanDate(null);
			try {
				this.workspace.getWorkspace().getPersistence().updateDocument(this.selectedDocument);
				try {
					Thread.sleep (1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendQueryDocumentAsynch(this.selectedDocument.getId());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Error in normalize",e);
			}

		}
	}


}
