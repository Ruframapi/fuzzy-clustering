package org.fuzzyclustering.web.managed;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

@ManagedBean(name = "status")
@ApplicationScoped
public class StatusManagedBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4274203108208588123L;
	private String downloadStatus = "test";

	@PostConstruct
	public void init(){
		//FacesContext ctx = FacesContext.getCurrentInstance();
		//HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
		//this.downloadStatus = (String) session.getAttribute("status");
	}
	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();  
        pushContext.push("/status", downloadStatus); 
	}
		
	
	
}
