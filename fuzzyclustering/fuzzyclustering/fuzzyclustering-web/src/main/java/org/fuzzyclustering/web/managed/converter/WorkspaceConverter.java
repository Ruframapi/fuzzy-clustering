package org.fuzzyclustering.web.managed.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

public class WorkspaceConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		WorkspaceDTO workspace = new WorkspaceDTO();
		if(arg2!=null && !arg2.isEmpty()){
			workspace = WorkspaceFacade.getWorkspace(arg2);
		}
		return workspace;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		String value = null;
		if(arg2!=null && !arg2.toString().isEmpty()){
			WorkspaceDTO workspace = (WorkspaceDTO) arg2;
			value = workspace.getName();
		}
		return value;
	}

}
