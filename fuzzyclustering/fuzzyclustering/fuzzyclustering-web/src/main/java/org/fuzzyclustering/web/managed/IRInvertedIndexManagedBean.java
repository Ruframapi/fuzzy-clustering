package org.fuzzyclustering.web.managed;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
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
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;


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
		logger.debug("Start download process");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inicializa Proceso de Creación de Indices", "Procesando los documentos descargados.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		try{
			this.addQueueDownload(EQueueEvent.GENERATE_INVERTED_INDEX, PersistenceFacade.getInstance().getServerDate().getTime());
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
	private void addQueueDownload(EQueueEvent queueEvent, Date date){
		QueueDTO queue = new QueueDTO();
		queue.setModule(EModule.QUERYASYNCH);
		queue.setEvent(queueEvent);
		queue.setInitDate(date);
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			PersistenceFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
