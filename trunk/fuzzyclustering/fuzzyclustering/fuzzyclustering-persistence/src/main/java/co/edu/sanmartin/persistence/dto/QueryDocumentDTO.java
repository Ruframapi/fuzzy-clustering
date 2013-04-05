package co.edu.sanmartin.persistence.dto;

/**
 * DTO con la informacion de la consulta de los archivos de noticias de forma asincrona
 * @author Ricardo Carvajal Salamanca
 *
 */
public class QueryDocumentDTO {
	private String originalData;
	private String cleanData;
	
	
	public String getOriginalData() {
		return originalData;
	}
	public void setOriginalData(String originalData) {
		this.originalData = originalData;
	}
	public String getCleanData() {
		return cleanData;
	}
	public void setCleanData(String cleanData) {
		this.cleanData = cleanData;
	}
	
	
}
