package co.edu.sanmartin.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Collection;

import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.factory.PostgresDAOFactory;

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
	
	protected Connection getConnection() throws SQLException{
		Connection connection = PostgresDAOFactory.getInstance().getConnection();
		return connection;
	}

	public abstract void insert(T object) throws SQLException;
	public abstract void update(T object) throws SQLException;
	public abstract void delete(T object) throws SQLException;
	public abstract <T> void select() throws SQLException;
	public abstract Collection<T> selectAll() throws SQLException;
	
}
