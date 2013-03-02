package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * DAO que gestiona la persistencia de las reglas de Stopwords
 * 
 * @author Ricardo Carvajal Salamanca
 * 
 */
public class StopwordDAO extends AbstractDAO<StopwordDTO> {

	private Collection<StopwordDTO> stopwordColl;
	
	public StopwordDAO(){
		this.selectAll(true);
	}
	
	@Override
	public void insert(StopwordDTO object) {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "insert into stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES (?,?,?,?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.setString(2, object.getRegex());
			statement.setString(3, object.getRegexReplace());
			statement.setInt(4, object.getStopwordOrder());
			statement.setBoolean(5, object.isEnabled());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}

	}

	@Override
	public void update(StopwordDTO object) {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "update stopword set name = ?,regex =?, regexreplace = ?, stopwordorder = ?, enabled = ? where name =?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.setString(2, object.getRegex());
			statement.setString(3, object.getRegexReplace());
			statement.setInt(4, object.getStopwordOrder());
			statement.setBoolean(5, object.isEnabled());
			statement.setString(6, object.getName());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}

	}

	@Override
	public void delete(StopwordDTO object) {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "delete from stopword where name = ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.executeUpdate();
			this.selectAll(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}

	}
	
	@Override
	public Collection<StopwordDTO> selectAll() {
		if(this.stopwordColl==null || this.stopwordColl.isEmpty()){
			this.selectAll(true);
		}
		return this.stopwordColl;
	}

	public Collection<StopwordDTO> selectAll(boolean refresh) {
		if(refresh){
			try {
				this.stopwordColl = new ArrayList();
				connection = getConnectionPool().getConnection();
				sQLQuery = "select * from stopword order by stopwordorder";
				statement = connection.prepareStatement(sQLQuery);
				rs = statement.executeQuery();
	
				while (rs.next()) {
					StopwordDTO stopwordDTO = new StopwordDTO();
					stopwordDTO.setName(rs.getString("name"));
					stopwordDTO.setRegex(rs.getString("regex"));
					stopwordDTO.setRegexReplace(rs.getString("regexreplace"));
					stopwordDTO.setStopwordOrder(rs.getInt("stopwordorder"));
					stopwordDTO.setEnabled(rs.getBoolean("enabled"));
					this.stopwordColl.add(stopwordDTO);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				getConnectionPool().freeConnection(connection);
			}
		}
		return this.stopwordColl;
	}

	/**
	 * Crea la tabla del DTO
	 * 
	 * @throws SQLException
	 */
	public void createTable(boolean dropTable) {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = null;
			if (dropTable == true) {
				sQLQuery = "DROP TABLE IF EXISTS stopword";
				statement = connection.prepareStatement(sQLQuery);
				statement.executeUpdate();
			}
			sQLQuery = "CREATE TABLE stopword (name varchar(40) NOT NULL," +
						"regex text NOT NULL,regexreplace text,stopwordorder int(11) NOT NULL, " +
						"enabled BOOLEAN NOT NULL)";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

	@Override
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	public void backupTable(){
		super.backupTable("stopword");
	}
	
	public void restoreTable(){
		super.restoreTable("stopword", "name,regex,regexreplace,stopwordorder,enabled");
	}

}
