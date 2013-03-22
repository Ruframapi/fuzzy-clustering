package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sun.swing.internal.plaf.synth.resources.synth;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dao.PropertyDAO;
import co.edu.sanmartin.persistence.dao.QueueDAO;
import co.edu.sanmartin.persistence.dao.SourceDAO;
import co.edu.sanmartin.persistence.dao.StopwordDAO;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.file.FileManager;

/**
 * Fachada que expone los metodos utilizados para la persistencia del sistema
 * 
 * @author Ricardo Carvajal Salamanca
 * @version 1.0
 * 
 */
public class PersistenceFacade {

	private static PersistenceFacade instance;
	private static Logger logger = Logger.getRootLogger();
	private SourceDAO sourceDAO;
	private PropertyDAO propertyDAO;
	private StopwordDAO stopwordDAO;
	private QueueDAO queueDAO;
	private FileManager fileManager;
	

	private PersistenceFacade() {
		this.sourceDAO = new SourceDAO();
		this.stopwordDAO = new StopwordDAO();
		this.propertyDAO = new PropertyDAO();
		this.queueDAO = new QueueDAO();
		this.fileManager = new FileManager();
		logger.info("Facade Persistence Initialized");
	}

	public static PersistenceFacade getInstance() {
		if (instance == null) {
			instance = new PersistenceFacade();
		}
		return instance;

	}

	public void createSource(SourceDTO sourceDTO) throws SQLException {
		sourceDAO.insert(sourceDTO);
	}

	public void updateSource(SourceDTO sourceDTO) throws SQLException {
		sourceDAO.update(sourceDTO);
	}

	public void deleteSource(SourceDTO sourceDTO) throws SQLException {
		sourceDAO.delete(sourceDTO);
	}
	
	public Collection<SourceDTO> getAllSources() throws SQLException {
		return sourceDAO.selectAll();
	}

	public Collection<SourceDTO> getAllSources(boolean refresh) throws SQLException {
		return sourceDAO.selectAll(refresh);
	}
	

	public void createStopword(StopwordDTO stopwordDTO) throws SQLException {
		stopwordDAO.insert(stopwordDTO);
	}

	public void updateStopword(StopwordDTO stopwordDTO) throws SQLException {
		stopwordDAO.update(stopwordDTO);
	}

	public void deleteStopword(StopwordDTO stopwordDTO) throws SQLException {
		stopwordDAO.delete(stopwordDTO);
	}
	

	public Collection<StopwordDTO> getAllStopword() throws SQLException {
		return stopwordDAO.selectAll();
	}

	public Collection<PropertyDTO> getAllProperties(boolean refresh) throws SQLException {
		return propertyDAO.selectAll(refresh);
	}

	public void updateProperty(PropertyDTO propertyDTO) throws SQLException {
		propertyDAO.update(propertyDTO);
	}

	public PropertyDTO getProperty(Enum<?> property) throws PropertyValueNotFoundException {
		return propertyDAO.getProperty(property);
	}
	
	public Collection<QueueDTO> getQueueByStatusDate(EModule module, EQueueStatus status, Date nowDate){
		return this.queueDAO.getQueueByStatusDate(module, status, nowDate);
	}
	
	public Collection<QueueDTO> getQueueByStatus(EQueueStatus status, Date nowDate){
		return this.queueDAO.getQueueByStatus(status, nowDate);
	}
	
	public Collection<QueueDTO> getQueueByStatus(EQueueEvent event, EQueueStatus status){
		return this.queueDAO.getQueueByStatus(event, status);
	}
	
	public synchronized void updateQueue(QueueDTO queue) throws SQLException {
		this.queueDAO.update(queue);
	}
	
	public synchronized void insertQueue(QueueDTO queue) throws SQLException {
		this.queueDAO.insert(queue);
	}
	
	public void truncateQueue() throws SQLException {
		this.queueDAO.truncate();
	}
	
	public void createQueueTable(boolean dropTable) throws SQLException {
		this.queueDAO.createTable(dropTable);
	}

	public void createFolder(String folderPath) {
		fileManager.createFolder(folderPath);
	}
	
	public void deleteFolder( EDataFolder dataFolder ){
		fileManager.deteleFolder(dataFolder);
	}

	public void writeFile(String folderPath, String fileName, String data) {
		fileManager.writeFile(folderPath, fileName, data);
	}

	public void writeFile(EDataFolder dataFolder, String fileName,
			String data) {
		fileManager.writeFile(dataFolder, fileName, data);
	}

	public String readFile(String fileName) {
		return fileManager.readFile(fileName);
	}
	
	public String readFile(EDataFolder dataFolder, String fileName){
		return fileManager.readFile(dataFolder, fileName);
	}

	public Collection<DocumentDTO> getFileList(EDataFolder dataFolder) {
		return fileManager.getFileList(dataFolder);
	}

	public String getFileName(String path) {
		return fileManager.getFileName(path);
	}

	public String getFileNameWithOutExtension(String path) {
		return fileManager.getFileNameWithOutExtension(path);
	}
	
	public String getFolderPath(EDataFolder dataFolder){
		return fileManager.getFolderPath(dataFolder);
	}
	

	
	/**
	 * Refresca los datos en memoria
	 */
	public void refreshMemoryData(){
		sourceDAO = new SourceDAO();
		stopwordDAO = new StopwordDAO();
		propertyDAO = new PropertyDAO();
		fileManager = new FileManager();
		logger.info("Facade Persistence Re-Initialized");
	}

	/**
	 * Inicializa las tablas del sistema
	 * 
	 * @throws SQLException
	 */
	public void initialize() throws SQLException {
		sourceDAO.createTable(true);
		stopwordDAO.createTable(true);
		propertyDAO.createTable(true);
		queueDAO.createTable(true);
		this.refreshMemoryData();
	}
	
	/**
	 * Realiza el backup del sistema
	 */
	public void backupSystem(){
		stopwordDAO.backupTable();
		sourceDAO.backupTable();
		propertyDAO.backupTable();
		queueDAO.backupTable();
	}
	
	/**
	 * Restaura un backup del sistema
	 */
	public void restoreSystem(){
		propertyDAO.truncateTable();
		propertyDAO.restoreTable();
		stopwordDAO.restoreTable();
		sourceDAO.restoreTable();
		queueDAO.restoreTable();
		this.refreshMemoryData();
	}
}
