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
	private boolean global;
	private String description;
	
	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public PropertyDTO(){
		
	}
	
	public PropertyDTO(String name, String value){
		this.name = name;
		this.value = value;
	}
	
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		boolean isEquals = false;
		PropertyDTO property = (PropertyDTO) obj;
		if(this.name.equals(property.name)){
			isEquals =  true;
		}
		return isEquals;
	}
	
	
}
