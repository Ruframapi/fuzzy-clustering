package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;

import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;

/**
 * DTO de las propiedades
 * @author Ricardo Carvajal Salamanca
 *
 */
public class PropertyDTO implements Serializable{
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * Retorna el valor entero de la propiedad
	 * @return
	 * @throws PropertyValueNotFoundException 
	 */
	public int intValue() throws PropertyValueNotFoundException{
		if(this.value==null){
			throw new PropertyValueNotFoundException();
		}
		int value = Integer.parseInt(this.value);
		return value;
	}
	
	
}
