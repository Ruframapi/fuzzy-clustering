package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.constant.properties.PropertiesLoader;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;

public class PropertyDAO extends AbstractDAO<PropertyDTO> {
	
	
	public PropertyDAO (){
	}

	ArrayList<PropertyDTO> propertyCol = new ArrayList<PropertyDTO>();
	@Override
	public void insert(PropertyDTO object) {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "insert into property (name,value) VALUES (?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.setString(2, object.getValue());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
	}

	@Override
	public void update(PropertyDTO object){
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "UPDATE property SET value = ? WHERE name =?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getValue());
			statement.setString(2, object.getName());
			statement.executeUpdate();
			this.selectAll(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
	}

	@Override
	public void delete(PropertyDTO object) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Retorna el valor de la propiedad de acuerdo a su nombre
	 * @param <T>
	 * @param name
	 * @param refresh indica si muestra los datos en memoria o realiza la consulta en la base de datos
	 * @return
	 * @throws SQLException
	 * @throws PropertyValueNotFoundException 
	 */
	public <T> PropertyDTO  getProperty(Enum<?> property) 
			throws PropertyValueNotFoundException {
		
		PropertyDTO searchProperty = new PropertyDTO();
		searchProperty.setName(property.name());
		int propertyIndex = this.propertyCol.indexOf(searchProperty);
		if(propertyIndex==-1){
			throw new PropertyValueNotFoundException();
		}
		searchProperty = this.propertyCol.get(propertyIndex);
		if(searchProperty == null){
			throw new PropertyValueNotFoundException();
		}
		return searchProperty;
		
	}
	
	/**
	 * Elimina la tabla
	 */
	public void dropTable(){
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "DROP TABLE IF EXISTS property";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina los datos de la tabla
	 */
	public void truncateTable(){
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "TRUNCATE TABLE property";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Crea la tabla del DTO
	 * @throws SQLException
	 */
	public void createTable( boolean dropTable ){
		try {
			if(dropTable == true){
				this.dropTable();
			}
			sQLQuery = 	"CREATE TABLE property (" +
							"name varchar(40) NOT NULL," +
							"value varchar(254))";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
			this.insertDefautValues();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
	}
	
	/**
	 * Inserta las propiedades por defecto en la tabla de propiedades
	 * @throws SQLException 
	 */
	public void insertDefautValues() throws SQLException{
		Collection<EProperty> properties = EProperty.toList();
		for (EProperty eProperty : properties) {
			PropertyDTO propertyDTO = new PropertyDTO();
			propertyDTO.setName(eProperty.name());
			this.insert(propertyDTO);
		}
		
	}

	public Collection<PropertyDTO> selectAll(boolean refresh) {

		if(refresh){
			this.propertyCol = new ArrayList<PropertyDTO>();
			try {
				connection = getConnectionPool().getConnection();
				sQLQuery = "select * from property order by name";
				statement = connection.prepareStatement(sQLQuery);
				rs = statement.executeQuery();

				while(rs.next()){
					PropertyDTO objectDTO = new PropertyDTO();
					objectDTO.setName(rs.getString("name"));
					objectDTO.setValue(rs.getString("value"));
					objectDTO.setGlobal(false);
					this.propertyCol.add(objectDTO);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally{
				getConnectionPool().freeConnection(connection);
			}
		}
		return propertyCol;
	}

	@Override
	public Collection<PropertyDTO> selectAll() throws SQLException {
		// TODO Auto-generated method stub
		if(this.propertyCol==null || this.propertyCol.isEmpty()){
			this.selectAll(true);
		}
		return this.propertyCol;
	}
	
	public void backupTable(){
		super.backupTable("property");
	}
	
	public void restoreTable(){
		super.restoreTable("property", "name,value");
	}

	
}
