package org.fuzzyclustering.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fuzzyclustering.web.managed.AsynchManagedBean;

import co.edu.sanmartin.persistence.dto.DocumentDTO;

/**
 * Servlet implementation class AsynchServlet
 */
@WebServlet("/AsynchServlet")

public class AsynchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("AsynchServlet");
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

		this.doPost(request, response);
		//status.setDownloadStatus("Downloading: " + new Date().toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Init AsynchServlet doPost Servlet");
		try{

		if (request.getParameter("status")!=null){
			AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
			bean1.setDownloadStatus((String) request.getParameter("status"));
		}
		if(request.getParameter("originalDocument")!=null){
			logger.debug("Original Document received:" + request.getParameter("originalDocument"));
			AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
			DocumentDTO document = new DocumentDTO();
			document.setLazyData(this.getWrappedData(request.getParameter("originalDocument")));
			document.setLazyCleanData(this.getWrappedData(request.getParameter("cleanDocument")));
			document.setName(request.getParameter("documentName"));
			bean1.setDocument(document);
			logger.debug("Document sended to AsynchManagedBean");
			/*DocumentsManagedBean documents = (DocumentsManagedBean) FacesContext.getCurrentInstance().
					getExternalContext().getSessionMap().get("documents");
			documents.setOriginalDataDocument(this.getWrappedData(request.getParameter("originalDocument")));
			 */
		}
			String message = request.getParameter("message");
			if(message!=null){
				AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
				if(bean1!=null){
					bean1.sendMessageAsynch(message);
				}
			}
		}
		catch(Exception e){
			logger.error("Error indoPost", e);
		}
	}

	/**
	 * Retorna la informacion de los archivos normalizada
	 * @param data
	 * @return
	 */
	public String getWrappedData(String data){
		StringBuilder stringBuilder = new StringBuilder();
		if(data!=null && !data.isEmpty()){
			stringBuilder.append(data);
			int position = 0;

			while((position+=110)<data.length()){
				stringBuilder.insert(position-10, " ");
			}
		}
		return stringBuilder.toString();
	}

}
