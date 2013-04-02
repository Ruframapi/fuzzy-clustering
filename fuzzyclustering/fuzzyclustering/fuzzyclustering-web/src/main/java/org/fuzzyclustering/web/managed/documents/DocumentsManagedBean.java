package org.fuzzyclustering.web.managed.documents;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Managed bean que gestiona la vista de las carpetas de contienen 
 * los archivos del procesamiento del sistema
 * @author Ricardo Carvajal Salamanca
 *
 */
@ManagedBean(name = "documents")
@ViewScoped
public class DocumentsManagedBean implements Serializable {
	
	private Collection<DocumentDTO> documentsColl;
	private DocumentDTO document;

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
	
	public void onRowSelect(SelectEvent event){
		StringBuilder stringBuilder = new StringBuilder();
		String documentData = PersistenceFacade.getInstance().readFile(document.getCompletePath());
		stringBuilder.append(documentData);
		int position = 0;
		while((position+=100)<documentData.length()){
			stringBuilder.insert(position+10, " ");
		}
		this.document.setLazyData(stringBuilder.toString());
	}

	private DocumentsLazyDataModel documentsModel = null;
	
	public DocumentsLazyDataModel getDocuments(){
		if(documentsModel == null){
			documentsModel = new DocumentsLazyDataModel();
		}
		return documentsModel;
	}

	
	
}
