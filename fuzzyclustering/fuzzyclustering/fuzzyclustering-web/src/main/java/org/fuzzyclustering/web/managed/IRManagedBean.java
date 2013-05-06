package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.QueueFacade;



@ManagedBean(name = "irBean")
@ViewScoped
public class IRManagedBean implements Serializable {
	private static final long serialVersionUID = 2591152720434384133L;
	private static Logger logger = Logger.getRootLogger();
	@ManagedProperty(value = "#{documents}") 
	private DocumentsManagedBean documents;
	
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;
	
	private Integer minTermsOcurrences;
	private Integer newDimension = 2;
	private boolean saveReadable = false;
	private Integer readableRowsAmount = 0;
	
	
	public void load(){
		System.out.print("Load");
	//	documents.loadDocuments(EDataFolder.INVERTED_INDEX);
	}

	public void setDocuments(DocumentsManagedBean documents) {
		this.documents = documents;
	}
	
	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}
	
	public void buildInvertedIndex(){
		logger.debug("Start buildInvertedIndex");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Creación de Indices", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_INVERTED_INDEX, 
									QueueFacade.getInstance().getServerDate().getTime(),
									this.minTermsOcurrences.toString());
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso En Curso", ".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		catch(PatternSyntaxException e){
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error al realizar el proceso", e.getDescription());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	
	/**
	 * Envia la solicitud de creación de la matrix Termino Termino
	 */
	public void buildTermTermMatrix(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Creación de Matrix Termino Termino", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_TERM_TERM_MATRIX, 
									QueueFacade.getInstance().getServerDate().getTime(),
									null);
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso En Curso", ".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(PatternSyntaxException e){
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error al realizar el proceso", e.getDescription());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	/**
	 * Envia la solicitud de la creación de la matrix PPMI
	 */
	public void buildPPMIMatrix(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Creación de Matrix PPMI", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_PPMI_MATRIX, 
									QueueFacade.getInstance().getServerDate().getTime(),
									null);
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso En Curso", ".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(PatternSyntaxException e){
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error al realizar el proceso", e.getDescription());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	/**
	 * Envia la solicitud de la creación de la matrix PPMI
	 */
	public void buildReducedMatrix(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Reduccion de Dimensionalidad", "");
		StringBuilder params = new StringBuilder();
		params.append(this.newDimension);
		params.append(",");
		params.append(this.saveReadable);
		params.append(",");
		params.append(this.readableRowsAmount);
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_REDUCED_PPMI_MATRIX, 
									QueueFacade.getInstance().getServerDate().getTime(),
									params.toString());
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso En Curso", ".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(Exception e){
			logger.error("Error in IRManagedBean buildReducedMatrix",e);
		}
	}
	
	/**
	 * Envia la solicitud de las matrices necesarias para generar los CMeans
	 */
	public void buildCmeansAllMatrix(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Creacion de Matrices", "Iniciando el proceso de creacion de las matrices necesarias para " +
						"la ejecucion de los algoritmos C-Mmeans");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_ALL_MATRIX, 
									QueueFacade.getInstance().getServerDate().getTime(),
									this.minTermsOcurrences.toString());
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proceso En Curso", ".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(PatternSyntaxException e){
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error al realizar el proceso", e.getDescription());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	
	/**
	 * Adiciona una cola en el webscrapping
	 * @param queueEvent tipo de evento a encolar
	 */
	private void addQueueDownload(EQueueEvent queueEvent, Date date, String params){
		QueueDTO queue = new QueueDTO();
		queue.setModule(EModule.QUERYASYNCH);
		queue.setEvent(queueEvent);
		queue.setInitDate(date);
		queue.setParams(params);
		queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			QueueFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Integer getMinTermsOcurrences() {
		return minTermsOcurrences;
	}

	public void setMinTermsOcurrences(Integer minTermsOcurrences) {
		this.minTermsOcurrences = minTermsOcurrences;
	}

	public Integer getNewDimension() {
		return newDimension;
	}

	public void setNewDimension(Integer newDimension) {
		this.newDimension = newDimension;
	}

	public boolean isSaveReadable() {
		return saveReadable;
	}

	public void setSaveReadable(boolean saveReadable) {
		this.saveReadable = saveReadable;
	}

	public Integer getReadableRowsAmount() {
		return readableRowsAmount;
	}

	public void setReadableRowsAmount(Integer readableRowsAmount) {
		this.readableRowsAmount = readableRowsAmount;
	}

	
	
	
	
}
