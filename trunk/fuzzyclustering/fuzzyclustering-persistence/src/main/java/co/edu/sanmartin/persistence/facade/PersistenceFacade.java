package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dao.PropertyDAO;
import co.edu.sanmartin.persistence.dao.SourceDAO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.file.FileManager;
/**
 * Fachada que expone los metodos utilizados para la persistencia del sistema
 * @author Ricardo Carvajal Salamanca
 * @version 1.0
 *
 */
public class PersistenceFacade {
	
	private static PersistenceFacade instance;
	private SourceDAO sourceDAO;
	private PropertyDAO propertyDAO;
	private FileManager fileManager;
	private PersistenceFacade(){
		sourceDAO = new SourceDAO();
		propertyDAO = new PropertyDAO();
		fileManager = new FileManager();
	}
	
	public static PersistenceFacade getInstance(){
		if ( instance == null){
			instance = new PersistenceFacade();
		}
		return instance;
		
	}
	public void createSource(SourceDTO sourceDTO) throws SQLException{
	   sourceDAO.insert(sourceDTO);
	}
	public void updateSource(SourceDTO sourceDTO) throws SQLException{
	   sourceDAO.insert(sourceDTO);
	}
	
	public void deleteSource(SourceDTO sourceDTO) throws SQLException{
		   sourceDAO.delete(sourceDTO);
		}
	
	public Collection<SourceDTO> getAllSources() throws SQLException{
		return sourceDAO.selectAll();
	}
	
	public Collection<PropertyDTO> getAllProperties() throws SQLException{
		return propertyDAO.selectAll();
	}
	
	public void updateProperty(PropertyDTO propertyDTO) throws SQLException{
	   propertyDAO.update(propertyDTO);
	}
	
	public PropertyDTO getProperty(EProperty property) throws SQLException, 
															PropertyValueNotFoundException{
		return propertyDAO.getProperty(property);
	}
	
	public void createFolder(String folderPath) {
		fileManager.createFolder(folderPath);
	}
	
	public void writeFile(String folderPath, String fileName, String data) {
		fileManager.writeFile(folderPath, fileName, data);
	}
	
	public void writeFile(EProperty destinationProperty, String fileName, String data){
		fileManager.writeFile(destinationProperty, fileName, data);
	}
	
	public String readFile(String fileName) {
		return fileManager.readFile(fileName);
	}
	
	public Collection<String> getFileList(String folderPath) {
		return fileManager.getFileList(folderPath);
	}
	
	public String getFileName(String path) {
		return fileManager.getFileName(path);
	}
	
	public String getFileNameWithOutExtension(String path) {
		return fileManager.getFileNameWithOutExtension(path);
	}
	
	
	/**
	 * Inicializa las tablas del sistema
	 * @throws SQLException
	 */
	public void initialize() throws SQLException{
		sourceDAO.createTable(true);
		propertyDAO.createTable(true);
	}
}
