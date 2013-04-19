package co.edu.sanmartin.persistence.test;

import java.io.IOException;

import org.junit.Test;

import co.edu.sanmartin.persistence.file.FileManager;

public class FileManagerTest {
	
	@Test
	public void TestReadFileFileChannel1(){
		FileManager fileManager = new FileManager();
		try {
			fileManager.readFileNio("D:\\RicardoC\\fuzzyclustering\\data\\download\\0.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFileWriteNio(){
		String text = "Arrancó cambio en la tributación de los empleados. Si a comienzos de año, a los empleados colombianos los sorprendió una retención";
		FileManager fileManager = new FileManager();
		try {
			fileManager.writeFileNio("D:\\RicardoC\\fuzzyclustering\\data\\download\\", "0.txt", text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
