package org.fuzzyclustering.web.managed.documents;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedProperty;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.fuzzyclustering.web.managed.WorkspaceManagedBean;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class DocumentsLazyDataModel extends LazyDataModel<DocumentDTO> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6975474989059428682L;
	private static final int PAGE_SIZE = 10;

	private List<DocumentDTO> datasource;  
	
	
	private WorkspaceDTO workspace;

	public DocumentsLazyDataModel(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	
	@Override  
    public DocumentDTO getRowData(String rowKey) {  
        for(DocumentDTO document : datasource) {  
            if(document.getId()==Integer.parseInt(rowKey))  
                return document;  
        }  
  
        return null;  
    }  
	    
    @Override  
    public Integer getRowKey(DocumentDTO document) {  
        return document.getId();  
    }  
    
    
	@Override
	public List<DocumentDTO> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		// TODO Corregir Paginacion
		
		this.datasource = 
				(List<DocumentDTO>) this.workspace.getPersistence().getPaginateDocumentsColl(first, pageSize);
		return this.datasource;
	}
	
	@Override
	public int getRowCount() {
		
		return this.workspace.getPersistence().getDownloadDocumentAmount();
	}
	
	@Override
	public int getPageSize() {
		return PAGE_SIZE;
	};

}
