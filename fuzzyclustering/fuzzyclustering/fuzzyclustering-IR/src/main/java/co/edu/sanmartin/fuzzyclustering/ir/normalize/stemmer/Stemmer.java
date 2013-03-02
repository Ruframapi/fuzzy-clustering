package co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.snowball.SnowballStemmer;

public class Stemmer {
	private static Logger logger = Logger.getRootLogger();
	public String stem(String data, String fileName, boolean persist) {
		Class stemClass;
		StringBuilder stemString = new StringBuilder();
		try {
			stemClass = Class
					.forName("co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.language.spanishStemmer");

			//SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
			SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

			String[] dataSplit = data.split(",");

			for (int i = 0; i < dataSplit.length; i++) {
				String string = dataSplit[i];
				stemmer.setCurrent(string);
				stemmer.stem();
				stemString.append(stemmer.getCurrent());
				stemString.append(",");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return stemString.toString();
	}

}
