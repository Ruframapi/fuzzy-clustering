package org.fuzzyclustering.web.managed;


import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.webscraping.facade.WebscrapingFacade;


@ManagedBean(name = "irDownload")
@ViewScoped
public class IRDownloadManagedBean implements Serializable {
	private static Logger logger = Logger.getRootLogger();
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	
	
	public void load(){
		documents.loadDocuments(EDataFolder.ORIGINAL_RSS);
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	public void download(){
		logger.info("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "Consultando las fuentes de información...");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		WebscrapingFacade.getInstance().downloadSources();
	}
}
