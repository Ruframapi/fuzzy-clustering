package org.fuzzyclustering.web.servlet;

import java.io.IOException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fuzzyclustering.web.managed.AsynchManagedBean;
import javax.faces.context.FacesContext;
import org.fuzzyclustering.web.managed.FacesContextBuilder;
import org.fuzzyclustering.web.managed.documents.DocumentsManagedBean;
import org.primefaces.component.outputlabel.OutputLabel;

import co.edu.sanmartin.persistence.dto.DocumentDTO;

/**
 * Servlet implementation class AsynchServlet
 */
@WebServlet("/AsynchServlet")

public class AsynchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		// TODO Auto-generated method stub

		if (request.getParameter("status")!=null){
			AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
			bean1.setDownloadStatus((String) request.getParameter("status"));
		}
		if(request.getParameter("originalDocument")!=null){
			AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
			DocumentDTO document = new DocumentDTO();
			document.setLazyData(this.getWrappedData(request.getParameter("originalDocument")));
			document.setLazyCleanData(this.getWrappedData(request.getParameter("cleanDocument")));
			document.setName(request.getParameter("documentName"));
			bean1.setDocument(document);

			/*DocumentsManagedBean documents = (DocumentsManagedBean) FacesContext.getCurrentInstance().
					getExternalContext().getSessionMap().get("documents");
			documents.setOriginalDataDocument(this.getWrappedData(request.getParameter("originalDocument")));
			 */
		}
		if(request.getParameter("message")!=null){
			AsynchManagedBean bean1 = (AsynchManagedBean) getServletContext().getAttribute("asynch");
			bean1.sendMessageAsynch(request.getParameter("message"));
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
