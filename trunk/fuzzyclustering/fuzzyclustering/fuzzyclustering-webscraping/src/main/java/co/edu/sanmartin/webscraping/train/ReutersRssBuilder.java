package co.edu.sanmartin.webscraping.train;

import java.util.ArrayList;
import java.util.Date;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.webscraping.rss.RssBuilder;

public class ReutersRssBuilder {
	

	private WorkspaceDTO workspace;
	
	public ReutersRssBuilder( WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	public void createRssReutersR8(){
		ArrayList<DocumentDTO> documentsList = this.getReutersDocuments("r8-train-all-terms.txt");
		RssBuilder rssBuilder = new RssBuilder(this.workspace);
		rssBuilder.createRss(documentsList, "rssr8.xml");
	}
	
	
	/**
	 * Retorna los documentos de Reuters a partir de un archivo de prueba
	 * @param fileName
	 * @return
	 */
	private ArrayList<DocumentDTO>  getReutersDocuments(String fileName){
		String fileReuters = this.workspace.getPersistence().readRootFile(EDataFolder.TRAIN, fileName);
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
