package org.fuzzyclustering.web.managed;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

@ManagedBean(name = "workspace")
@SessionScoped
public class WorkspaceManagedBean implements Serializable{

	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger("WorkspaceManagedBean");
	private static final long serialVersionUID = 2868240981111357381L;
	private WorkspaceDTO workspace = new WorkspaceDTO();
	private Collection<WorkspaceDTO> workspaceColl;

	@PostConstruct
	private void init(){
		this.workspaceColl = WorkspaceFacade.getInstance().getAllWorkspace();
		workspace = new WorkspaceDTO();
	}

	/**
	 * Crea un nuevo espacio de trabajo
	 */
	public void createWorkspace(){
		//TODO Validar que los espacios de trabajo no tengan caracteres especiales ni espacios
		logger.trace("Init createWorkspace method");
		try{
			WorkspaceFacade.getInstance().createWorkspace(this.workspace);
			this.init();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Espacio de trabajo creado", this.workspace.getName());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		catch(Exception e){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Error al crear el espacio de trabajo", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * Elimina un espacio de trabajo
	 */
	public void deleteWorkspace(){
		logger.trace("Init deleteWorkspace method");
		try{
			WorkspaceFacade.getInstance().deleteWorkspace(this.workspace);
			this.init();
		}
		catch(Exception e){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Error al eliminar el espacio de trabajo", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}


	public String loadWorkspace(){
		String returnValue = null;
		if(workspace!=null){
			this.workspace = WorkspaceFacade.getWorkspace(workspace.getName());
			returnValue = "source.xhtml";
		}
		else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No se puede cargar el espacio de trabajo","Verifique y vuelva a intentar");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		return returnValue;

	}


	public WorkspaceDTO getWorkspace() {
		return workspace;
	}

	public void setWorkspace(WorkspaceDTO workspace) {
		this.workspace = workspace;
	}

	public Collection<WorkspaceDTO> getWorkspaceColl() {
		return workspaceColl;
	}

	public void setWorkspaceColl(Collection<WorkspaceDTO> workspaceColl) {
		this.workspaceColl = workspaceColl;
	}

	public boolean isIntroAvailable(){
		boolean introAvailable = false;
		if(this.workspace!=null && workspace.getName()!=null && !workspace.getName().isEmpty()){
			introAvailable = true;
		}
		return introAvailable;
	}
	
	public String systemOut(){
		this.workspace = new WorkspaceDTO();
		return "index.xhtml";
	}
	
	/**
	 * Invalida la session del usuario
	 */
	public void invalidateSession(){
		this.workspace = new WorkspaceDTO();
	}
}
