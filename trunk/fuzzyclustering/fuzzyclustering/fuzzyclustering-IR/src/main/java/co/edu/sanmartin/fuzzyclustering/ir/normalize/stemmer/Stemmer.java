package co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.snowball.SnowballStemmer;
import co.edu.sanmartin.persistence.constant.ELanguage;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class Stemmer {
	private static Logger logger = Logger.getRootLogger();
	private WorkspaceDTO workspace;
	
	public Stemmer(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	
	public String stem(String data) {
		Class stemClass = null;
		StringBuilder stemString = new StringBuilder();
		
    	
    	ELanguage language = null;
    	try {
			PropertyDTO property = workspace.getPersistence().getProperty(EProperty.LANGUAGE);
			language = ELanguage.valueOf(property.getValue());
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		
		try {
			switch(language){
				case SPANISH:
				stemClass = Class
					.forName("co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.language.spanishStemmer");
				break;
				case ENGLISH:
				stemClass = Class
					.forName("co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.language.englishStemmer");
				break;
			}

			SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
			String splitSeparator = null;
			try {
				splitSeparator = this.workspace.getPersistence().getProperty(EProperty.TEXT_SPLIT_TOKEN).getValue();
			} catch (PropertyValueNotFoundException e) {
				logger.error("Error in InvertedIndexWorkerThread", e);
			}

			String[] dataSplit = data.split(splitSeparator);

			for (int i = 0; i < dataSplit.length; i++) {
				String string = dataSplit[i];
				stemmer.setCurrent(string);
				stemmer.stem();
				stemString.append(stemmer.getCurrent());
				stemString.append(splitSeparator);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return stemString.toString();
	}

}
