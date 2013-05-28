package org.fuzzyclustering.web.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
/**
 * Clase que inicializa la configuración de log4j
 * @author Ricardo
 *
 */
public class InitServlet extends HttpServlet {
	
	private Logger logger = org.apache.log4j.Logger.getRootLogger();
	@Override
	public void init(ServletConfig config) throws ServletException {
		//PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		try {
			StringBuilder logPath = new StringBuilder();
			//logPath.append(persistenceFacade.getProperty(ESystemProperty.MAIN_PATH).getValue());
			//logPath.append("\\log");
			//persistenceFacade.createFolder(logPath.toString());
			//logPath.append("\\fuzzyclustering.log");
			logPath.append("fuzzyclustering.log");
			System.setProperty("logfile.name",logPath.toString());
			System.out.println("Initialising log4j");
			String log4jLocation = config
					.getInitParameter("log4j-properties-location");

			ServletContext sc = config.getServletContext();

			if (log4jLocation == null) {
				System.out.println("No log4j properites...");
				BasicConfigurator.configure();
			} else {
				String webAppPath = sc.getRealPath("/");
				String log4jProp = webAppPath + log4jLocation;
				File output = new File(log4jProp);

				if (output.exists()) {
					System.out.println("Initialising log4j with: " + log4jProp);
					PropertyConfigurator.configure(log4jProp);
					
				} else {
					System.out.println("Find not found (" + log4jProp + ").");
					BasicConfigurator.configure();
				}
			}
			
			super.init(config);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		logger.debug("Fuzzy Logic Log Started");
		
	}
}
