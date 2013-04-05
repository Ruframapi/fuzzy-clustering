package co.edu.sanmartin.persistence.file;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de gestion el mapeo de archivos en memoria tuilizando las
 * librerias java.nio de java.
 * 
 * @author Ricardo Carvajal Salamanca
 *
 */
public class BigMatrixFileManager implements Closeable {
	private static final String METADATA_NAME = "META_DATA";
	private static final String METADATA_ITEM1 = "height";
	private static final String METADATA_ITEM2 = "width";
    private static final int MAPPING_SIZE = 1 << 30;
    private RandomAccessFile raf;
    private int width;
    private int height;
    private String fileName;
    private final List<MappedByteBuffer> mappings = new ArrayList<MappedByteBuffer>();

    
    public void loadReadWrite(EDataFolder dataFolder, String fileName, 
    							 int height, int width) throws IOException{
    	this.fileName = fileName;
    	String folderPath = PersistenceFacade.getInstance().getFolderPath(dataFolder);
    	this.raf = new RandomAccessFile(folderPath + System.getProperty("file.separator") + fileName, "rw");
    	this.loadFile(MapMode.READ_WRITE,height,width);
    }
    
    public void loadReadOnly(EDataFolder dataFolder, String fileName ) throws IOException{
    	this.fileName = fileName;
    	String folderPath = PersistenceFacade.getInstance().getFolderPath(dataFolder);
    	this.raf = new RandomAccessFile(folderPath + System.getProperty("file.separator") + fileName, "r");
    	this.loadMetadata();
    	this.loadFile(MapMode.READ_ONLY,height,width);
    }
    
    private void loadFile(MapMode mapMode, int height,int width)  throws IOException{	
        try {
            this.width = width;
            this.height = height;
            long size = 8L * width * height;
            for (long offset = 0; offset < size; offset += MAPPING_SIZE) {
                long size2 = Math.min(size - offset, MAPPING_SIZE);
                mappings.add(raf.getChannel().map(mapMode, offset, size2));
            }
        } catch (IOException e) {
            raf.close();
            throw e;
        }
    }
    

    protected long position(int x, int y) {
        return (long) y * width + x;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double get(int x, int y) {
        assert x >= 0 && x < width;
        assert y >= 0 && y < height;
        long p = position(x, y) * 8;
        int mapN = (int) (p / MAPPING_SIZE);
        int offN = (int) (p % MAPPING_SIZE);
        return mappings.get(mapN).getDouble(offN);
        //return mappings.get(x).getDouble(y);
        
    }

    public void set(int x, int y, double d) {
        assert x >= 0 && x < width;
        assert y >= 0 && y < height;
        long p = position(x, y) * 8;
        int mapN = (int) (p / MAPPING_SIZE);
        int offN = (int) (p % MAPPING_SIZE);
        mappings.get(mapN).putDouble(offN, d);
       //mappings.get(x).putDouble(y, d);
        
    }

    public void close() throws IOException {
        for (MappedByteBuffer mapping : mappings)
            clean(mapping);
        	raf.close();
        	this.saveMetadata();
    }
    
    /**
     * Guarda los metadatos para reconstruir la matrix
     */
    private void saveMetadata(){
    	StringBuilder data = new StringBuilder();
    	
    	data.append(METADATA_ITEM1);
    	data.append(",");
    	data.append(this.height);
    	data.append(";");
    	data.append(METADATA_ITEM2);
    	data.append(",");
    	data.append(this.width);
    	
    	PersistenceFacade.getInstance().writeFile(EDataFolder.MATRIX, 
    							getMetadataName(), data.toString());
    }
    
    /**
     * Carga los metadatos para la construccion de la matrix
     */
    private void loadMetadata(){
    	String metadata = PersistenceFacade.getInstance().readFile(EDataFolder.MATRIX, getMetadataName());
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
    	PersistenceFacade persistence = PersistenceFacade.getInstance();
    	String fileName = persistence.getFileNameWithOutExtension(this.fileName);
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append(fileName);
    	stringBuilder.append("_");
    	stringBuilder.append(this.METADATA_NAME);
    	stringBuilder.append(".txt");
    	return stringBuilder.toString();
    }

    private void clean(MappedByteBuffer mapping) {
        if (mapping == null) return;
        Cleaner cleaner = ((DirectBuffer) mapping).cleaner();
        if (cleaner != null) cleaner.clean();
    }
}


