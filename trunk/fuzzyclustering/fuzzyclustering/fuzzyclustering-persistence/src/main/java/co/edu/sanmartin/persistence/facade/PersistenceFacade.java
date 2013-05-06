package co.edu.sanmartin.persistence.facade;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dao.DocumentDAO;
import co.edu.sanmartin.persistence.dao.PropertyDAO;
import co.edu.sanmartin.persistence.dao.QueryDocumentDAO;
import co.edu.sanmartin.persistence.dao.SourceDAO;
import co.edu.sanmartin.persistence.dao.StopwordDAO;
import co.edu.sanmartin.persistence.dao.WorkspaceDAO;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueryDocumentDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.file.FileManager;

/**
 * Fachada que expone los metodos utilizados para la persistencia del sistema
 * 
 * @author Ricardo Carvajal Salamanca
 * @version 1.0
 * 
 */
public class PersistenceFacade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4134771351415236980L;
	private static Logger logger = Logger.getRootLogger();
	private SourceDAO sourceDAO;
	private PropertyDAO propertyDAO;
	private StopwordDAO stopwordDAO;
	private DocumentDAO documentDAO;
	private QueryDocumentDAO queryDocumentDAO;
	private FileManager fileManager;
	private WorkspaceDTO workspace;
	

	public PersistenceFacade(WorkspaceDTO workspace) {
		this.sourceDAO = new SourceDAO(workspace);
		this.stopwordDAO = new StopwordDAO(workspace);
		this.propertyDAO = new PropertyDAO(workspace);
		
		this.documentDAO = new DocumentDAO(workspace);
		this.queryDocumentDAO = new QueryDocumentDAO(workspace);
		this.workspace = workspace;
		this.fileManager = new FileManager(this.workspace);
		logger.debug("Facade Persistence Initialized");
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
		return propertyDAO.getProperty(property,this.workspace);
	}	
	
	public int getDownloadDocumentAmount(){
		return this.documentDAO.getDownloadDocumentAmount();
	}	
	
	public void insertDocument(DocumentDTO document) throws SQLException {
		this.documentDAO.insert(document);
	}
	
	public void updateDocument(DocumentDTO document) throws SQLException{
		this.documentDAO.update(document);
	}
	
	public void truncateDocument() throws SQLException {
		this.documentDAO.truncate();
	}
	
	public DocumentDTO selectDocumentById(int idDocument){
		return this.documentDAO.selectDocumentById(idDocument);
	}
	
	public Collection<DocumentDTO> getPaginateDocumentsColl(long startId, int limit){
		return this.documentDAO.getPaginateDocumentsColl(startId, limit);
	}
	
	public Collection<DocumentDTO> getDocumentsForClean(){
		return this.documentDAO.getDocumentsForClean();
	}
	public void insertQueryDocument(QueryDocumentDTO queryDocument) throws SQLException {
		this.queryDocumentDAO.insert(queryDocument);
	}
	
	
	public void truncateQueryDocument() throws SQLException {
		this.queryDocumentDAO.truncate();
	}
	

	public void createFolder(String folderPath) {
		fileManager.createFolder(folderPath);
	}
	
	public void deleteFolder(EDataFolder dataFolder ){
		fileManager.deteleFolder(dataFolder);
	}
	
	public void deleteFile(EDataFolder dataFolder, String fileName){
		fileManager.deleteFile(dataFolder, fileName);
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
	
	/**
	 * Lee los archivos ubicados en la carpeta de proceso de aplicacion
	 * @param dataFolder subcarpeta de destino
	 * @param fileName nombre del archivo
	 * @return
	 */
	public String readFile(EDataFolder dataFolder, String fileName){
		return fileManager.readFile(dataFolder, fileName);
	}
	
	/**
	 * Lee los archivos ubicados en el main root de la aplicacion
	 * @param dataFolder carpeta contentedora del archivo
	 * @param fileName nombre del archivo a leer
	 * @return
	 */
	public String readRootFile(EDataFolder dataFolder, String fileName){
		return fileManager.readRootFile(dataFolder, fileName);
	}
	
	public void saveDoubleMatrixNio(double[][] matrix,String fileName) throws Exception{
		this.fileManager.saveDoubleMatrixNio(matrix, fileName);
	}
	
	public void saveMatrixDouble(double[][] matrix, EDataFolder dataFolder, 
			String fileName, int matrixLimit, int roundScale){
		this.fileManager.saveMatrixDouble(matrix, dataFolder, fileName, matrixLimit, roundScale);
	}
	
	public <T> List<T> readFileMatrix( EDataFolder dataFolder, String fileName){
		return this.fileManager.readFileMatrix(dataFolder, fileName);
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
		sourceDAO = new SourceDAO(this.workspace);
		stopwordDAO = new StopwordDAO(this.workspace);
		propertyDAO = new PropertyDAO(this.workspace);
		fileManager = new FileManager(this.workspace);
		logger.debug("Facade Persistence Re-Initialized");
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
		documentDAO.createTable(true);
		this.refreshMemoryData();
	}
	
	/**
	 * Realiza el backup del sistema
	 */
	public void backupSystem(){
		stopwordDAO.backupTable();
		sourceDAO.backupTable();
		propertyDAO.backupTable();
		documentDAO.backupTable();
	}
	
	/**
	 * Restaura un backup del sistema
	 */
	public void restoreSystem(){
		propertyDAO.truncateTable();
		propertyDAO.restoreTable();
		stopwordDAO.restoreTable();
		sourceDAO.restoreTable();
		this.refreshMemoryData();
	}
}
