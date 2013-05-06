package co.edu.sanmartin.persistence.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de gestion el mapeo de archivos en memoria tuilizando las
 * librerias java.nio de java.
 * 
 * @author Ricardo Carvajal Salamanca
 *
 */
public class BigDoubleMatrixFileManager implements Closeable{
	private static Logger logger = Logger.getLogger("BigDoubleMatrixFileManager");
	private static final String METADATA_NAME = "META_DATA";
	private static final String METADATA_ITEM1 = "height";
	private static final String METADATA_ITEM2 = "width";
    private RandomAccessFile memoryMappedFile;
    private int width;
    private int height;
    private String fileName;
    private MappedByteBuffer out;
    private WorkspaceDTO workspace;

    public BigDoubleMatrixFileManager(WorkspaceDTO workspace){
    	this.workspace = workspace;
    }
    
    public void loadReadWrite(EDataFolder dataFolder, String fileName, 
    							 int height, int width) throws IOException{
    	this.width = width;
        this.height = height;
    	this.fileName = fileName;
    	this.workspace = workspace;
    	String folderPath = workspace.getPersistence().getFolderPath(dataFolder);
    	workspace.getPersistence().createFolder(folderPath);
    	memoryMappedFile = new RandomAccessFile(folderPath + System.getProperty("file.separator") + fileName, "rw");
    	out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8*height*width);
    	
    }
    
    public void loadReadOnly(EDataFolder dataFolder, String fileName ) throws IOException{
    	this.fileName = fileName;
    	this.workspace = workspace;
    	String folderPath = this.workspace.getPersistence().getFolderPath(dataFolder);
    	this.memoryMappedFile = new RandomAccessFile(folderPath + System.getProperty("file.separator") + fileName, "r");
    	this.loadMetadata();
    	this.fileName = fileName;
    	out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, 8*height*width);
    }
    
    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double get(int x, int y) {
    	int index = (x*8*width)+(8*y);
        return out.getDouble(index);
        
    }

    public void set(int x, int y, double d) {
      int index = (x*8*width)+(8*y);
      out.putDouble(index, d);
        
    }

    public void close() throws IOException {
    	memoryMappedFile.close();
    	this.saveMetadata();
    }
    
    /**
     * Guarda los metadatos para reconstruir la matrix
     */
    private void saveMetadata(){
    	StringBuilder data = new StringBuilder();
    	logger.info("Savin Metadata: Height:" +this.height + "Width:" + this.width );
    	data.append(METADATA_ITEM1);
    	data.append(",");
    	data.append(this.height);
    	data.append(";");
    	data.append(METADATA_ITEM2);
    	data.append(",");
    	data.append(this.width);
    	
    	this.workspace.getPersistence().writeFile(EDataFolder.MATRIX, 
    							getMetadataName(), data.toString());
    }
    
    /**
     * Carga los metadatos para la construccion de la matrix
     */
    private void loadMetadata(){
    	String metadata = this.workspace.getPersistence().readFile(EDataFolder.MATRIX, 
    																getMetadataName());
    	metadata = metadata.replaceAll(System.getProperty("line.separator"), "");
    	String[] metadataColl = metadata.split(";");
    	
    	HashMap<String,Integer> metaData = new HashMap<String, Integer>();
    	
    	for (int i = 0; i < metadataColl.length; i++) {
    		String[] data = metadataColl[i].split(",");
    		metaData.put(data[0], Integer.parseInt(data[1]));
		}
    	
    	this.height = metaData.get(METADATA_ITEM1);
    	this.width = metaData.get(METADATA_ITEM2);
    	
    }
    
    /**
     * Retorna el nombre del archivo de metadatos
     * @param filePath nombre del archivo original
     * @return
     */
    private String getMetadataName(){
    	String fileName = this.workspace.getPersistence().getFileNameWithOutExtension(this.fileName);
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append(fileName);
    	stringBuilder.append("_");
    	stringBuilder.append(this.METADATA_NAME);
    	stringBuilder.append(".dat");
    	return stringBuilder.toString();
    }

    private void clean(MappedByteBuffer mapping) {
        if (mapping == null) return;
        Cleaner cleaner = ((DirectBuffer) mapping).cleaner();
        if (cleaner != null) cleaner.clean();
    }
}


