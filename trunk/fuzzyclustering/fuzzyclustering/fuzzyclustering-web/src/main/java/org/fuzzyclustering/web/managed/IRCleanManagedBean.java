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
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;


@ManagedBean(name = "irClean")
@ViewScoped
public class IRCleanManagedBean implements Serializable {
	private static Logger logger = Logger.getRootLogger();
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	public void clean(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Limpieza de textos", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			//IRFacade.getInstance().cleanText();
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
