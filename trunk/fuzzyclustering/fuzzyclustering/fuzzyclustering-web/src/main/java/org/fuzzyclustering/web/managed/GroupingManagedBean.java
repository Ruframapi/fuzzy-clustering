package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.webscraping.facade.WebscrapingFacade;

@ManagedBean(name = "grouping")
@ViewScoped
public class GroupingManagedBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7338478105752808632L;
	private static Logger logger = Logger.getRootLogger();
	private String status;
	private boolean isWebscrapingFinished;

	public boolean isWebscrapingFinished() {
		return isWebscrapingFinished;
	}

	public void setWebscrapingFinished(boolean isWebscrapingFinished) {
		this.isWebscrapingFinished = isWebscrapingFinished;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void start(){
		logger.info("Start grouping process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "Consultando las fuentes de información...");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		WebscrapingFacade.getInstance().downloadSources();
	}
	
	public void download(){
		logger.info("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Descarga", "Consultando las fuentes de información...");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		WebscrapingFacade.getInstance().downloadSources();
	}
	
	public void cleanText(){
		logger.info("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Limpieza de textos", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			IRFacade.getInstance().cleanText();
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
	
	public void invertedIndex(){
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
