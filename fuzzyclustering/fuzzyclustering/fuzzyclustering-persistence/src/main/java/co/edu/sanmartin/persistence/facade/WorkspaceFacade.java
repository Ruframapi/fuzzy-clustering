package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.dao.WorkspaceDAO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

/*
 * Gestion los espacios de trabajo
 */

public class WorkspaceFacade {
	private static Logger logger = Logger.getRootLogger();
	private static WorkspaceFacade instance;
	private  HashMap<String,WorkspaceDTO> workspacePool;
	private static WorkspaceDAO workspaceDAO;
	
	private WorkspaceFacade(){
		WorkspaceFacade.workspaceDAO = new WorkspaceDAO();
		this.workspacePool = new HashMap<String,WorkspaceDTO>();
	}
	
	public static WorkspaceFacade getInstance(){
		if(instance== null){
			instance = new WorkspaceFacade();
		}
		return instance;
	}
	
	
	
	public HashMap<String, WorkspaceDTO> getWorkspacePool() {
		return workspacePool;
	}

	public void setWorkspacePool(HashMap<String, WorkspaceDTO> workspacePool) {
		this.workspacePool = workspacePool;
	}

	/**
	 * Crea un espacio de trabajo
	 * @param workspace
	 */
	public void createWorkspace(WorkspaceDTO workspace) throws Exception{
		WorkspaceDAO workspaceDAO = new WorkspaceDAO();
		workspaceDAO.createDatabase(workspace);
		workspaceDAO.insert(workspace);
		this.initializeWorkspace(workspace.getName());
	}
	
	
	public void initializeWorkspace(String workspaceName) throws SQLException{
		WorkspaceDTO workspace = getWorkspace(workspaceName);
		workspace.getPersistence().initialize();
	}
	/**
	 * Elimina un espacio de trabajo
	 * @param workspace
	 */
	public void deleteWorkspace(WorkspaceDTO workspace) throws Exception{
		WorkspaceDAO workspaceDAO = new WorkspaceDAO();
		workspaceDAO.delete(workspace);
		workspaceDAO.dropDatabase(workspace);
	}
	
	/**
	 * Retorna el listado de todos los espacios de trabajo
	 * @return
	 */
	public Collection<WorkspaceDTO> getAllWorkspace(){
		WorkspaceDAO workspaceDAO = new WorkspaceDAO();
		return workspaceDAO.selectAllWorkpace();
	}
	/**
	 * Retorna el workspace
	 * @param workspaceName
	 * @return
	 * @throws Exception 
	 */
	public static WorkspaceDTO getWorkspace(String workspaceName){
		if(workspaceName == null){
			throw new RuntimeException("El espacio de trabajo no puede ser null");
		}
		WorkspaceFacade worksapceFacade = WorkspaceFacade.getInstance();
		if(worksapceFacade.getWorkspacePool() == null){
			worksapceFacade.setWorkspacePool(new HashMap<String,WorkspaceDTO>());
		}
		WorkspaceDTO workspace = worksapceFacade.getWorkspacePool().get(workspaceName);
		if(workspace==null){
			try {
				workspace = workspaceDAO.selectWorkspace(workspaceName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
			worksapceFacade.getWorkspacePool().put(workspaceName, workspace);
		}
		return workspace;
	}
	
	/**
	 * Retorna el workspace
	 * @param workspaceName
	 * @param refresh indica si recarga el espacio de trabajo
	 * @return
	 * @throws Exception 
	 */
	public  WorkspaceDTO getWorkspace(String workspaceName, boolean refresh) throws Exception{
		WorkspaceDTO workspace = workspacePool.get(workspaceName);
		workspace = workspaceDAO.selectWorkspace(workspaceName);
		workspacePool.put(workspaceName, workspace);
		return workspace;
	}

	
	
}
