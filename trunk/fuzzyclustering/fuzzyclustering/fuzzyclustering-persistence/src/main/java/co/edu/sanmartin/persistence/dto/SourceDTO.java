package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;

import co.edu.sanmartin.persistence.constant.ESourceType;

public class SourceDTO implements Serializable{
	private String name;
	private String url;
	private ESourceType type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ESourceType getType() {
		return type;
	}
	public void setType(ESourceType type) {
		this.type = type;
	}
	
}
