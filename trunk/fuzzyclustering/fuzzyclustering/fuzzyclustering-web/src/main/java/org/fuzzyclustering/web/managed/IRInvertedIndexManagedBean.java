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
import co.edu.sanmartin.webscraping.facade.WebscrapingFacade;


@ManagedBean(name = "irInvertedIndex")
@ViewScoped
public class IRInvertedIndexManagedBean implements Serializable {
	private static Logger logger = Logger.getRootLogger();
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	
	/*@PostConstruct
	public void load(){
		documents.loadDocuments(EDataFolder.ORIGINAL_RSS);
	}*/
	
	public void load(){
		documents.loadDocuments(EDataFolder.INVERTED_INDEX);
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	public void buildInvertedIndex(){
		logger.info("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Creación de Indices", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			IRFacade.getInstance().createInvertedIndex();
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso Finalizado", "Se realizo correctamente la generación de indices.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(PatternSyntaxException e){
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error al realizar el proceso", e.getDescription());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
}
