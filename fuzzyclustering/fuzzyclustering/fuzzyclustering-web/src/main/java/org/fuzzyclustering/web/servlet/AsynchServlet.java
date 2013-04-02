package org.fuzzyclustering.web.servlet;

import java.io.IOException;
import java.util.Date;

import javax.faces.bean.ManagedProperty;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fuzzyclustering.web.managed.StatusManagedBean;

/**
 * Servlet implementation class AsynchServlet
 */
@WebServlet("/AsynchServlet")

public class AsynchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@ManagedProperty(value = "#{status}") 
	private StatusManagedBean status;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AsynchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		if (request.getParameter("status")!=null){
			HttpSession session = request.getSession(true);
			session.setAttribute("status", "Downloading: " + new Date().toString());
		}
		//status.setDownloadStatus("Downloading: " + new Date().toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (request.getParameter("status")!=null){
			StatusManagedBean bean1 = (StatusManagedBean) getServletContext().getAttribute("status");
			bean1.setDownloadStatus((String) request.getParameter("status"));
		}

	}

	public StatusManagedBean getStatus() {
		return status;
	}

	public void setStatus(StatusManagedBean status) {
		this.status = status;
	}
	
	
	

}
