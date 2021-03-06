package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;

/**
 * DTO con la informacion de la cola de eventos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class QueueDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5106959435991648786L;
	private int id;
	private EModule module;
	private Date initDate;
	private Date processDate;
	private EQueueEvent event;
	private EQueueStatus status;
	private String params;
	private String workspace;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public EModule getModule() {
		return module;
	}
	public void setModule(EModule module) {
		this.module = module;
	}
	public Date getInitDate() {
		return initDate;
	}
	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public EQueueEvent getEvent() {
		return event;
	}
	public void setEvent(EQueueEvent event) {
		this.event = event;
	}
	public EQueueStatus getStatus() {
		return status;
	}
	public void setStatus(EQueueStatus status) {
		this.status = status;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getWorkspace() {
		return workspace;
	}
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	
}
