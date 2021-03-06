package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.QueueFacade;


@ManagedBean(name = "cmean")
@SessionScoped
public class CMeansManagedBean implements Serializable {
	
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4059587653963594498L;
	private static Logger logger = Logger.getRootLogger();
	private boolean reducedMatrix = true;
	private int centroidsAmount;
	private int iterationAmount=10000;
	private double fuzzyValue=2.0;
	private double stopValue = 0.0000001;
	
	
	
	
	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}

	

	public boolean isReducedMatrix() {
		return reducedMatrix;
	}



	public void setReducedMatrix(boolean reducedMatrix) {
		this.reducedMatrix = reducedMatrix;
	}



	public int getCentroidsAmount() {
		return centroidsAmount;
	}



	public void setCentroidsAmount(int centroidsAmount) {
		this.centroidsAmount = centroidsAmount;
	}



	public int getIterationAmount() {
		return iterationAmount;
	}



	public void setIterationAmount(int iterationAmount) {
		this.iterationAmount = iterationAmount;
	}

	public double getFuzzyValue() {
		return fuzzyValue;
	}

	public void setFuzzyValue(double fuzzyValue) {
		this.fuzzyValue = fuzzyValue;
	}

	public double getStopValue() {
		return stopValue;
	}

	public void setStopValue(double stopValue) {
		this.stopValue = stopValue;
	}

	public void generate(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Clusterizacion", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueCmeans(EQueueEvent.GENERATE_FUZZY_CMEANS, QueueFacade.getInstance().getServerDate().getTime());
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
	 * Solicita la generacion de la matrix de pertenencia de terminos
	 */
	public void generateMembershipIndex(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Generando Indice de Pertenencia", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			QueueDTO queue = new QueueDTO();
			queue.setModule(EModule.MACHINE_LEARNING);
			queue.setEvent(EQueueEvent.GENERATE_MEMBERSHIP_INDEX);
			queue.setInitDate(QueueFacade.getInstance().getServerDate().getTime());
			queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
			queue.setStatus(EQueueStatus.ENQUEUE);
			try {
				QueueFacade.getInstance().insertQueue(queue);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	 * Solicita la generacion de la matrix de pertenencia de terminos
	 */
	public void classifyDocumentsInFile(){
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Realizando proceso de clasificacion", "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			QueueDTO queue = new QueueDTO();
			queue.setModule(EModule.MACHINE_LEARNING);
			queue.setEvent(EQueueEvent.CLASSIFYFORFILE);
			queue.setInitDate(QueueFacade.getInstance().getServerDate().getTime());
			queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
			queue.setStatus(EQueueStatus.ENQUEUE);
			try {
				QueueFacade.getInstance().insertQueue(queue);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	private void addQueueCmeans(EQueueEvent queueEvent, Date date){
		QueueDTO queue = new QueueDTO();
		queue.setModule(EModule.MACHINE_LEARNING);
		queue.setEvent(queueEvent);
		StringBuilder params = new StringBuilder();
		params.append(String.valueOf(this.reducedMatrix));
		params.append(",");
		params.append(this.centroidsAmount);
		params.append(",");
		params.append(this.iterationAmount);
		params.append(",");
		params.append(this.fuzzyValue);
		params.append(",");
		params.append(this.stopValue);
		params.append(",");
		params.append("true");
		queue.setParams(params.toString());
		queue.setInitDate(date);
		queue.setWorkspace(this.workspaceBean.getWorkspace().getName());
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			QueueFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
