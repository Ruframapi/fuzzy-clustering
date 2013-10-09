package org.fuzzyclustering.web.managed.clustering;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.chart.datamodel.DataTable;
import org.fuzzyclustering.web.managed.WorkspaceManagedBean;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentClustering;
import co.edu.sanmartin.fuzzyclustering.machinelearning.facade.MachineLearningFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;

@ManagedBean(name = "cmeanstest")
@SessionScoped
public class CMeanTestManagedBean implements Serializable {

	private static Logger logger = Logger.getLogger("CMeanTestManagedBean");
	@ManagedProperty(value = "#{workspace}") 
	private WorkspaceManagedBean workspaceBean;
	
	private Collection<DocumentDTO> documentCol;
	private String documentText;
	private String term;
	private DocumentClustering documentClustering;
	public void setWorkspaceBean(WorkspaceManagedBean workspaceBean) {
		this.workspaceBean = workspaceBean;
	}
	
	@PostConstruct
	public void init(){
		
		this.documentCol = this.workspaceBean.getWorkspace().getPersistence().getFileList(EDataFolder.TRAIN);
		this.documentClustering = new DocumentClustering(this.workspaceBean.getWorkspace());
	}


	public Collection<DocumentDTO> getDocumentCol() {
		return documentCol;
	}



	public void setDocumentCol(Collection<DocumentDTO> documentCol) {
		this.documentCol = documentCol;
	}

	public String getDocumentText() {
		return documentText;
	}

	public void setDocumentText(String documentText) {
		this.documentText = documentText;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	
	
	public DocumentClustering getDocumentClustering() {
		return documentClustering;
	}

	public void buildDocumentMembership(){
		DocumentDTO document = new DocumentDTO();
		document.setLazyData(this.documentText);
		document.setLazyClusterTerm(this.term);
		MachineLearningFacade machineLearning = new MachineLearningFacade();
		this.documentClustering = machineLearning.getDocumentClustering(this.workspaceBean.getWorkspace(), document);
		this.membershipDataTable.loadData(this.documentClustering.getMembershipMatrix());

		logger.trace("get Document Clustering sucessfull");
	}
	
	
	public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg); 
        UploadedFile file = event.getFile();
        String dataFile = null;
		try {
			dataFile = this.readFile(file.getInputstream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.workspaceBean.getWorkspace().getPersistence().writeFile(EDataFolder.TRAIN, "test.txt", dataFile);
        this.init();
    }  
	
	
	public String readFile(InputStream inputStream) {
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;
		try {
			scanner = new Scanner(inputStream, "ISO-8859-1");
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (Exception e) {
			logger.error("Error in readFile", e);
		} finally {
			scanner.close();
		}
		//log.info("Text read in: " + text);
		return text.toString();

	}
	
	private DataTable membershipDataTable = new DataTable();
	
	public DataTable getMembershipDataTable() {
		return membershipDataTable;
	}

	
}
