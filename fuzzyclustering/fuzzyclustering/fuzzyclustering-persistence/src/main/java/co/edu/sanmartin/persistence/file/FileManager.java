package co.edu.sanmartin.persistence.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de la gestion de almacenamiento en archivos y directorios en disco
 * @author Ricardo Carvajal Salamanca
 */
public class FileManager {

	private static Logger log = Logger.getRootLogger();

	/**
	 * Escribe un archivo en disco en caso de existir lo elimina y crea uno nuevo
	 * @param folderPath ubicacion del archivo
	 * @param fileName nombre del archivo
	 * @param data informacion del archivo
	 */
	public void writeFile(String folderPath, String fileName, String data) {
		try {

			this.createFolder(folderPath);
			File file = new File(folderPath + System.getProperty("file.separator") + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			else{
				file.delete();
			}
			FileWriter fileWritter = new FileWriter(file);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(data);
			bufferWritter.flush();
			bufferWritter.close();
			log.info("Write file Done");

		} catch (IOException e) {
			log.error("Error in write file", e);
		}
	}
	
	/**
	 * Escribe un archivo en disco
	 * @param destinationProperty propiedad con el path de destino
	 * @param fileName nombre del archivo
	 * @param data información del archivo
	 */
	public void writeFile(EDataFolder dataFolder, String fileName, String data){
		
    	this.writeFile(this.getFolderPath(dataFolder), fileName, data);
	}
	
	/**
	 * Elimina la carpeta especificada
	 * @param dataFolder
	 */
	public void deteleFolder(EDataFolder dataFolder){
		String dataPath = PersistenceFacade.getInstance().getFolderPath(dataFolder);
		File file = new File(dataPath);
		file.delete();
	}

	/**
	 * Retorna el archivo de la carpeta especificada
	 * @param dataFolder
	 * @param fileName
	 * @return
	 */
	public String readFile(EDataFolder dataFolder, String fileName){
		StringBuilder stringBuider = new StringBuilder();
		stringBuider.append(PersistenceFacade.getInstance().getFolderPath(dataFolder));
		stringBuider.append(System.getProperty("file.separator"));
		stringBuider.append(fileName);
    	return this.readFile(stringBuider.toString());
	}

	/**
	 * Retorna la información del archivo
	 * @param fileName nombre del archivo
	 * @return el contenido del archivo
	 */
	public String readFile(String fileName) {
		log.info("Reading from file.");
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(fileName), "ISO-8859-1");
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (Exception e) {
			log.error("Error in readFile", e);
		} finally {
			scanner.close();
		}
		//log.info("Text read in: " + text);
		return text.toString();

	}

	/**
	 * Verifica si existe el directorio, si no existe lo crea en disco
	 * @param folderPath
	 */
	public void createFolder(String folderPath) {
		File folder = new File(folderPath);
		boolean result = false;
		if (!folder.exists()) {
			log.info("creating directory:" + folderPath);
			result = folder.mkdirs();
		}
		if (result) {
			log.info("directory created:" + folderPath);
		}
	}
	
	/**
	 * Retorna la lista de archivos de un directorio
	 * @param folderPath
	 * @return la coleccion con el nombre de los archivos de la carpeta
	 */
	public Collection<DocumentDTO> getFileList(EDataFolder dataFolder) {
		String folderPath = this.getFolderPath(dataFolder);
		Collection<DocumentDTO> fileListColl = new ArrayList<DocumentDTO>();
		File folder = new File(folderPath);
		String[] fileList = folder.list();
		if (fileList == null) {
			System.out.println("There is not file Path:" + folderPath);
		} else {
			for (int i = 0; i < fileList.length; i++) {
				DocumentDTO document = new DocumentDTO(folderPath,fileList[i]);
				fileListColl.add(document);
			}
		}
		return fileListColl;
	}


	/**
	 * Extrae el nombre del archivo de una ruta completa
	 * @param path ruta en disco del archivo
	 * @return en nombre del archivo
	 */
	public String getFileName(String path) {
		String fileName = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1,
				path.length());
		return fileName;
	}
	
	/**
	 * Extrae el nombre del archivo sin extension a partir del path
	 * @param path ruta en disco del archivo
	 * @return en nombre del archivo
	 */
	public String getFileNameWithOutExtension(String path) {
		String fileName = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1,
				path.length());
		String[] fileNameColl = fileName.split(System.getProperty("file.separator")+".");
		return fileNameColl[0];
	}
	
	/**
	 * Retorna el path de las carpetas que gestiona el sistema
	 * @param dataFolder enumeracion con las diferentes carpetas del sistema
	 * @return
	 */
	public String getFolderPath(EDataFolder dataFolder){
		StringBuilder folderPath = new StringBuilder();
    	try {
    		folderPath.append(PersistenceFacade.getInstance().getProperty(ESystemProperty.MAIN_PATH).getValue());
    		folderPath.append(dataFolder.getPath());
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return folderPath.toString();
	}

}