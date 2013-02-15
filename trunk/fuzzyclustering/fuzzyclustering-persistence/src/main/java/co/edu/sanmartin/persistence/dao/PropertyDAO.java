package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;

public class PropertyDAO extends AbstractDAO<PropertyDTO> {

	@Override
	public void insert(PropertyDTO object) throws SQLException {
		connection = getConnection();
		sQLQuery = "insert into property (name,value) VALUES (?,?)";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, object.getName());
		statement.setString(2, object.getValue());
		statement.executeUpdate();
	}

	@Override
	public void update(PropertyDTO object) throws SQLException {
		connection = getConnection();
		sQLQuery = "update property set value = ? where name =?";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, object.getValue());
		statement.setString(2, object.getName());
		statement.executeUpdate();
		
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
	 * @param name
	 * @return
	 * @throws SQLException
	 * @throws PropertyValueNotFoundException 
	 */
	public PropertyDTO  getProperty(EProperty property) throws SQLException, 
															PropertyValueNotFoundException {
		connection = getConnection();
		sQLQuery = "select * from property where name = ?";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, property.name());
		rs = statement.executeQuery();
		PropertyDTO objectDTO = null;
		Collection<PropertyDTO> objectColl = new ArrayList<PropertyDTO>();
		if(rs.next()){
			objectDTO = new PropertyDTO();
			objectDTO.setName(rs.getString("name"));
			objectDTO.setValue(rs.getString("value"));
		}
		else{
			throw new PropertyValueNotFoundException();
		}
		return objectDTO;
		
	}
	
	/**
	 * Crea la tabla del DTO
	 * @throws SQLException
	 */
	public void createTable( boolean dropTable ) throws SQLException{
		connection = getConnection();
		sQLQuery = null;
		if(dropTable == true){
			sQLQuery = "DROP TABLE IF EXISTS property";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		}
		sQLQuery = 	"CREATE TABLE property (" +
						"name varchar(40) NOT NULL," +
						"value varchar(254))";
		statement = connection.prepareStatement(sQLQuery);
		statement.executeUpdate();
		this.insertDefautValues();
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

	@Override
	public Collection<PropertyDTO> selectAll() throws SQLException {
		connection = getConnection();
		sQLQuery = "select * from property order by name";
		statement = connection.prepareStatement(sQLQuery);
		rs = statement.executeQuery();
		Collection<PropertyDTO> objectColl = new ArrayList<PropertyDTO>();
		while(rs.next()){
			PropertyDTO objectDTO = new PropertyDTO();
			objectDTO.setName(rs.getString("name"));
			objectDTO.setValue(rs.getString("value"));
			objectColl.add(objectDTO);
		}
		return objectColl;
	}

	
}
