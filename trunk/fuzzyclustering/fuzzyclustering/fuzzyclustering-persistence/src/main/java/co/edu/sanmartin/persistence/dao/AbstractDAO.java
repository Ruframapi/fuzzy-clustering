package co.edu.sanmartin.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EDatabase;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.factory.ConnectionDataSourcePoolMySQL;
import co.edu.sanmartin.persistence.factory.DAOFactory;
import co.edu.sanmartin.persistence.file.FileManager;

/**
 * Clase abstract para la definición de los DAO´s
 * @author Ricardo Carvajal Salamanca
 *
 */
public abstract class AbstractDAO<T> {
	
	protected Connection connection = null;
	protected PreparedStatement statement;
	protected ResultSet rs;
	protected String sQLQuery;
	protected WorkspaceDTO workspace;
	
	public AbstractDAO (WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	
	protected ConnectionDataSourcePoolMySQL getConnectionPool(){
		ConnectionDataSourcePoolMySQL connectionPool = DAOFactory.getInstance().getConnectionPool(EDatabase.MYSQL);
		return connectionPool;
	}

	public abstract void insert(T object) throws SQLException;
	public abstract void update(T object) throws SQLException;
	public abstract void delete(T object) throws SQLException;
	public abstract <T> void select() throws SQLException;
	public abstract Collection<T> selectAll() throws SQLException;
	
	/**
	 * Metodo encargado de copiar la configuracion de la tabla
	 * @param tableName nombre de la table
	 */
	public void backupTable(String tableName){
		try {
			//Crea la carpeta de backups si no existe
			FileManager fileManager = new FileManager(workspace);
			String folderPath = this.workspace.getPersistence().getFolderPath(EDataFolder.BACKUP);
			fileManager.createFolder(folderPath);
			
			StringBuilder destinationPath = new StringBuilder();
			destinationPath.append("'");
			destinationPath.append(folderPath);
			destinationPath.append(System.getProperty("file.separator"));
			destinationPath.append(tableName);
			//destinationPath.append("-");
			//SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy-HH_mm");
			//destinationPath.append(date_format.format(Calendar.getInstance().getTime()));
			destinationPath.append(".sql'");
			connection = getConnectionPool().getConnection(this.workspace);
			
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT * INTO OUTFILE ");
			queryBuilder.append(destinationPath);
			queryBuilder.append(" FROM ");
			queryBuilder.append(tableName);
			String os = System.getProperty("os.name").toLowerCase();
			
			if(os.indexOf("win") >= 0){
				
				String escapeString = queryBuilder.toString().replace(System.getProperty("file.separator"), 
												System.getProperty("file.separator")+
												System.getProperty("file.separator")+
												System.getProperty("file.separator")+
												System.getProperty("file.separator"));
				queryBuilder = new StringBuilder();
				queryBuilder.append(escapeString);
			}
			System.out.println(queryBuilder.toString());
			statement = connection.prepareStatement(queryBuilder.toString());
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	
	/**
	 * Metodo encargado de cargar la configuracion de la tabla
	 * @param tableName nombre de la table
	 */
	public void restoreTable(String tableName, String columns){
		try {
			//Crea la carpeta de backups si no existe
			FileManager fileManager = new FileManager(workspace);
			String folderPath = this.workspace.getPersistence().getFolderPath(EDataFolder.BACKUP);
			fileManager.createFolder(folderPath);
			
			StringBuilder destinationPath = new StringBuilder();
			destinationPath.append("'");
			destinationPath.append(folderPath);
			destinationPath.append(System.getProperty("file.separator"));
			destinationPath.append(tableName);
			//destinationPath.append("-");
			//SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy-HH_mm");
			//destinationPath.append(date_format.format(Calendar.getInstance().getTime()));
			destinationPath.append(".sql'");
			connection = getConnectionPool().getConnection(this.workspace);
			
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("LOAD DATA LOCAL INFILE ");
			queryBuilder.append(destinationPath);
			queryBuilder.append(" INTO TABLE ");
			queryBuilder.append(tableName);
			queryBuilder.append("(");
			queryBuilder.append(columns);
			queryBuilder.append(")");

			String os = System.getProperty("os.name").toLowerCase();
			
			if(os.indexOf("win") >= 0){
				
				String escapeString = queryBuilder.toString().replace(System.getProperty("file.separator"), 
												System.getProperty("file.separator")+
												System.getProperty("file.separator")+
												System.getProperty("file.separator")+
												System.getProperty("file.separator"));
				queryBuilder = new StringBuilder();
				queryBuilder.append(escapeString);
			}
			System.out.println(queryBuilder.toString());
			statement = connection.prepareStatement(queryBuilder.toString());
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	
}
