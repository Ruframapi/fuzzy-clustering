package co.edu.sanmartin.webscraping.twitter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que gestiona la descarga de tweets
 * @author Ricardo Carvajal Salamanca
 * 
 * Glosario Básico de Twitter
___________________________________________________________________________________________________

<h4>CC (Carbon Copy):</h4>al igual que en los email, significa que le pones una copia a 
otro usuario diferente al destino.
<h4>DM (Direct Message) o Mensaje Directo:</h4> es un mensaje que se envía de forma directa
a un usuario específico de Twitter. Solo el usuario origen y el destino lo pueden leer.
<h4>Follow:</h4> Seguir a otro usuario de Twitter.
<h4>Follower ó Seguidor:</h4> es un usuario que sigue el estatus y conversaciones de otro, 
suscritos ambos dentro de Twitter.
<h4>FollowFriday</h4> es una etiqueta utilizada para decir a tus Followers (seguidores) 
acerca de otros usuarios que vale la pena seguir. Esto se hace los días viernes y 
se publica #FollowFriday y uno o varios usuarios para hacer saber a otros 
que se está recomendando personas para seguir. Ejemplo: #FF @xbaez  @csolares2 
porque comparten contenido muy interesante (podrías únicamente poner una lista de usuarios).
<h4>Following ó Siguiendo:</h4> estatus de que un usuario está siguiendo a otro. 
En la aplicación de Twitter es el directorio de personas a la que un usuario está siguiendo.
<h4>Hashtag ó Etiqueta:</h4> son palabras a las cuáles se les agrega el símbolo # y se utilizan 
para generar interacción y conversación entre los usuarios y comunidades sobre temas específicos. 
Ejemplos: #RedesSociales, #Ecología, etc.
<h4>Listas:</h4> se utilizan para ordenar o catalogar usuarios y cuentas de Twitter, 
agrupándolas de acuerdo a los criterios y preferencias del usuario. 
Por ejemplo: Amigos, Mercadotecnia, Baseball, etc.
<h4>Microblogging:</h4> es una aplicación Web que permite al usuario insertar 
mensajes breves similar a los post de un blog y limitado a un cierto número de caracteres.
Las opciones de envío pueden ser sitios Web, SMS, mensajería instantánea o aplicaciones particulares.
<h4>MT (Modified Tweet):</h4> significa que un Tweet que ha sido publicado, 
ha sido parafraseado del escrito originalmente por otro usuario. 
Cuando se hace esta acción se acostumbra poner MT.
<h4>Reply:</h4> responder a un mensaje que ha sido publicado en Twitter. 
El mensaje es público y cualquiera en la comunidad lo puede leer.
RT (ReTweet):</h4> acción de reenvíar un mensaje de otro usuario hacia la comunidad de Twitter.
Cuando se realiza esta acción aparecen las letras RT en el mensaje.
<h4>Timeline:</h4> También conocido como la línea en el tiempo, es la cronología de los tweets, 
o de manera más sencilla el listado de los tweets conformen se van generando.
<h4>TT (Trending Topics) o Temas del Momento:</h4> son temas de moda o palabras más usadas 
en un momento dado dentro de la comunidad de Twitter,  estos consolidan las 
hashtags o etiquetas con mayor cantidad de publicaciones.
<h4>Tweet (Tuit):</h4> mensajes públicos de hasta 140 caracteres que se escriben, 
envían y publican en la aplicación de Twitter de preferencia del usuario y 
que pueden ser leídos por los seguidores y todo aquel que tenga acceso a la cuenta.

twitter.consumer.key=KajYug9Z0gnjRz0BrFhQSQ
twitter.consumer.secret=eqf3HZBwPPaKBzt9D4aoOdNhWH2391R85utRENLNEE
twitter.access.token=836549780-xZ5AoEG3061UbvWqHee8DhuONJmvRIJjIvnMwsS0
twitter.access.token.secret=XZ8LRynlddi7vvcIFsjuNsLtDsk6jqcoXoOo9LX687A
 *
 */
public class TwitterScraping {
	
	private static Logger logger = Logger.getRootLogger();
	 
	private PersistenceFacade persistence = PersistenceFacade.getInstance();
	private Twitter twitter;
	
	public TwitterScraping() throws TwitterException{
		try {
			this.init();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init() throws PropertyValueNotFoundException, TwitterException{
		this.twitter = TwitterConnection.getInstance().getTwitter();
		//this.twitter = TwitterConnection.
	}
	
	

	/**
	 * Retorna el Timeline del usuario
	 * @return
	 */
	public Collection<String> getTwitterDocuments(){
		Collection<String> documentCol = new ArrayList<String>();
		try {
			int timeLinePages = persistence.getProperty(EProperty.TWITTER_HOME_TIMELINE_PAGES).intValue();
			List<Status> statuses =  this.twitter.getHomeTimeline();			
			for (Status status : statuses) {
				System.out.println(status.getUser().getName() + ":" + status.getText());
			}

			ResponseList<Status> responseList = twitter.getUserTimeline(new Paging(1, timeLinePages));
			for (Status b : responseList) {
				documentCol.add(b.getText());
				System.out.println(b.getText());
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return documentCol;
	}
	
	
	
	
	/**
	 * Almacena los tweets descargados en disco
	 * @param screenName nombre del usuario de twitter a decargar
	 * @throws SQLException 
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	public void saveTwitterDocument(String screenName, AtomicInteger sequence) throws SQLException, IllegalStateException, TwitterException{
    	logger.debug("Init saveRSSDocument");
    	if(this.twitter ==null && this.twitter.getId()==0){
	    	Collection<String> documentContent = this.getTwitterDocuments(screenName);
	    	PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
	    	for (String content : documentContent) {		
	    		logger.debug("Creating new twitter file number:"+ sequence);
	    		String fileName = String.valueOf(sequence)+".txt";
	    		persistenceFacade.writeFile(EDataFolder.ORIGINAL_TWITTER, fileName, content);
				DocumentDTO document = new DocumentDTO(EDataFolder.ORIGINAL_TWITTER.getPath(), fileName);
	    		document.setSource(screenName);
	    		document.setDownloadDate(new Date());
	    		persistenceFacade.insertDocument(document);
	    		sequence.incrementAndGet();
			}
    	}
    }
	
	/**
	 * Retorna el Timeline del usuario especificado
	 * @param screenName identificacion del usuario
	 * @return
	 */
	public Collection<String> getTwitterDocuments(String screenName){
		Collection<String> documentCol = new ArrayList<String>();
		try {
			int timeLinePages = persistence.getProperty(EProperty.TWITTER_HOME_TIMELINE_PAGES).intValue();
			List<Status> statuses =  this.twitter.getUserTimeline(screenName);			
			for (Status status : statuses) {
				System.out.println(status.getUser().getName() + ":" + status.getText());
			}

			ResponseList<Status> responseList = twitter.getUserTimeline(screenName,new Paging(1, timeLinePages));
			for (Status b : responseList) {
				documentCol.add(b.getText());
				System.out.println(b.getText());
			}
		} catch (TwitterException e) {
			logger.error("Error in getTwitterDocuments:" + e.getErrorMessage()+
						"StatusCode:" + e.getStatusCode());
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return documentCol;
	}
	
	
	/**
	 * Retorna el timeline de la cuenta
	 */
	public void getTimeLine() {
		try {
			List<Status> statuses = twitter.getHomeTimeline();
			System.out.println("Showing home timeline.");
			for (Status status : statuses) {
				System.out.println(status.getUser().getName() + ":" + status.getText());
			}
			ResponseList<Status> a;

			a = twitter.getUserTimeline(new Paging(1, 5));
			for (Status b : a) {
				System.out.println(b.getText());
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Retorna la lista de amigos de la cuenta
	 * @return
	 */
	public ArrayList<SourceDTO> getFriends(){
		ArrayList<SourceDTO> sourceColl = new ArrayList<SourceDTO>();
		try {
			long twitterId =twitter.getId();
            long cursor = -1;
            IDs ids;
            System.out.println("Listing followers's ids.");
            do {
                if (0 < twitterId) {
                    ids = twitter.getFriendsIDs(twitterId, cursor);
                } else {
                    ids = twitter.getFriendsIDs(cursor);
                }
                for (long id : ids.getIDs()) {
                	User user = twitter.showUser(id);
                	SourceDTO sourceDTO = new SourceDTO();
                	sourceDTO.setName(user.getName());
                	sourceDTO.setUrl(user.getURL());
                	sourceColl.add(sourceDTO);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
            System.exit(-1);
        }
		return sourceColl;
	}

	/**
	 * Relaciona una Amigo
	 * @param screenName el idenficador del amigo a seguir @XXXXXX
	 */
	public void createFriendship(String screenName){
		try {
			twitter.createFriendship(screenName, true);
		//	twitter.createFriendship(screenName);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina un amigo que se esta siguiendo
	 * @param screenName El identificador del amigo que se esta siguiendo
	 */
	public void removeFriendship(String screenName){
		try {
			twitter.destroyFriendship(screenName);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
