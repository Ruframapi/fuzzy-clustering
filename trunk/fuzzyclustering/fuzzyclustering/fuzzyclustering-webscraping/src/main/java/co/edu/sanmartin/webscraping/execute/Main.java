package co.edu.sanmartin.webscraping.execute;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */
public class Main{

	public static void main ( String args[]){
		Dequeue dequeue = new Dequeue();
		dequeue.run();
	}
}
