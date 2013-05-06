package org.fuzzyclustering.web.managed;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionBeanManager<T> {

	public static <T> T getSessionBean(String sessionName) {
        FacesContext fx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fx.getExternalContext().getSession(false);
        @SuppressWarnings("unchecked")
		T sessionBean = (T) session.getAttribute("validarUsuarioBean");
        return sessionBean;
    }
}
