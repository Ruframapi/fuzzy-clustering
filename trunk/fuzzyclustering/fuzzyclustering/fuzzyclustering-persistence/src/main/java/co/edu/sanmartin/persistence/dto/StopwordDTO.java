package co.edu.sanmartin.persistence.dto;

import java.io.Serializable;

public class StopwordDTO implements Serializable{
	private String name;
	private String regex;
	private String regexReplace;
	private int stopwordOrder;
	private boolean enabled;
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getRegexReplace() {
		return regexReplace;
	}
	public void setRegexReplace(String regexReplace) {
		this.regexReplace = regexReplace;
	}
	public int getStopwordOrder() {
		return stopwordOrder;
	}
	public void setStopwordOrder(int stopwordOrder) {
		this.stopwordOrder = stopwordOrder;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		boolean isEquals = false;
		StopwordDTO stopword1 = (StopwordDTO) obj;
		if(stopword1.getName().equals(this.getName())){
			isEquals = true;
		}
		return isEquals;
	}
	
	
	

}
