package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.ESourceType;

/**
 * DTO de los documentos que se almacenan en archivos de texto en el sistema
 * @author Ricardo
 *
 */
public class DocumentDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 97645150680592259L;
	private static Logger logger = Logger.getLogger("DocumentDTO");
	private int id;
	private ESourceType sourceType;
    private String name;
    private String nameWithoutExtension;
    private String lazyData;
    private String lazyCleanData;
    private String lazyClusterTerm;
    private String source;
    private Date downloadDate;
    private Date publishedDate;
    private Date cleanDate;
    
    
    public DocumentDTO() {
    	
    }
    
	public DocumentDTO(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLazyData() {
		return lazyData;
	}

	public void setLazyData(String lazyData) {
		this.lazyData = lazyData;
	}

	public String getLazyCleanData() {
		return lazyCleanData;
	}

	public void setLazyCleanData(String lazyCleanData) {
		this.lazyCleanData = lazyCleanData;
	}

	
	public String getLazyClusterTerm() {
		return lazyClusterTerm;
	}

	public void setLazyClusterTerm(String lazyClusterTerm) {
		this.lazyClusterTerm = lazyClusterTerm;
	}

	public String getNameWithoutExtension() {
		String nombreArchivo = null;
		try{
			nombreArchivo=this.name.substring(0,this.name.lastIndexOf("."));
		}
		catch(Exception e){
			logger.error("Error in getNameWithoutExtension",e);
		}
		return nombreArchivo;	
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public ESourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(ESourceType sourceType) {
		this.sourceType = sourceType;
	}

	public Date getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(Date downloadDate) {
		this.downloadDate = downloadDate;
	}

	
	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}
	
	

	public Date getCleanDate() {
		return cleanDate;
	}

	public void setCleanDate(Date cleanDate) {
		this.cleanDate = cleanDate;
	}
	

    
    
}
