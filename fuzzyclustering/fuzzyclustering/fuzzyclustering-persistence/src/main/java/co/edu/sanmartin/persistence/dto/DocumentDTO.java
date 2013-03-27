package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;
import java.util.Date;

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
	private String path;
    private String name;
    private String nameWithoutExtension;
    private String lazyData;
    private String source;
    private Date downloadDate;
    
    
	public DocumentDTO(String path, String name) {
		this.path = path;
		this.name = name;
		this.nameWithoutExtension = this.setFileNameWithOutExtension();
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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

	public String getNameWithoutExtension() {
		return nameWithoutExtension;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	

	public Date getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(Date downloadDate) {
		this.downloadDate = downloadDate;
	}

	/**
	 * Retorna la ruta completa del archivo
	 * @return
	 */
	public String getCompletePath(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.path);
		stringBuilder.append(System.getProperty("file.separator"));
		stringBuilder.append(this.name);
		return stringBuilder.toString();
	}
	
	/**
	 * Retorna el nombre del archivo
	 * @return
	 */
	private String setFileNameWithOutExtension(){
		String fileName = name.substring(name.lastIndexOf(System.getProperty("file.separator")) + 1,
				name.length());
		String[] fileNameColl = fileName.split(System.getProperty("file.separator")+".");
		return fileNameColl[0];
	}

    
    
}
