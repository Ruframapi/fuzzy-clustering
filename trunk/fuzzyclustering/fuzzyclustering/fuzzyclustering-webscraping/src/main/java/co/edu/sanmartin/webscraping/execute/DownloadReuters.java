package co.edu.sanmartin.webscraping.execute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de realiza la simulacion de la descarga de archivos a partir de un archivo de texto
 * de prueba de la forma TEMA -> TEXTO para validar el modelo
 * @author Ricardo Carvajal Salamanca
 *
 */
public class DownloadReuters {

	private static Logger logger = Logger.getLogger("DownloadReuters");
	private WorkspaceDTO workspace;
	
	public DownloadReuters(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	/**
	 * Realiza la simulacion de archivos de la fuente Reuters
	 */
	public void simulateDownload() {
		logger.debug("Init saveRSSDocument Source:" + "reuters");
		Collection<DocumentDTO> documentContent = this.getReutersDocuments("r8-train-all-terms.txt");
		logger.info("Documents to save Reuters:" + " Amount:" + documentContent.size());
		
		for (DocumentDTO documentDTO : documentContent) {
			logger.debug("Creating new file:"+ documentDTO.getName());
			this.workspace.getPersistence().writeFile(EDataFolder.DOWNLOAD, documentDTO.getName(), 
											documentDTO.getLazyData());
			try {
				this.workspace.getPersistence().insertDocument(documentDTO);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	/**
	 * Retorna los documentos de Reuters a partir de un archivo de prueba
	 * @param fileName
	 * @return
	 */
	private ArrayList<DocumentDTO>  getReutersDocuments(String fileName){
		String fileReuters = this.workspace.getPersistence().readFile(EDataFolder.TRAIN, fileName);
		String[] row = fileReuters.split(System.getProperty("line.separator"));
		
		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setName(i+".txt");
			document.setLazyData(row[i].split("\t")[1]);
			document.setSourceType(ESourceType.RSS);
			document.setSource("Reuters");
			document.setPublishedDate(new Date());
			document.setDownloadDate(new Date());
			fileCol.add(document);
		}
		
		return fileCol;
	}
}
