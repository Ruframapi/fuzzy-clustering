package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;


@ManagedBean(name = "irMatrix")
@ViewScoped
public class IRMatrixManagedBean implements Serializable {
	private static Logger logger = Logger.getRootLogger();
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	
	/*@PostConstruct
	public void load(){
		documents.loadDocuments(EDataFolder.ORIGINAL_RSS);
	}*/
	
	public void load(){
		documents.loadDocuments(EDataFolder.MATRIX);
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	
}
