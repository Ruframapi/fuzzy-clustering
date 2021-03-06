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
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que gestiona la descarga de tweets
 * @author Ricardo Carvajal Salamanca
 * 
 * Glosario B�sico de Twitter
___________________________________________________________________________________________________

<h4>CC (Carbon Copy):</h4>al igual que en los email, significa que le pones una copia a 
otro usuario diferente al destino.
<h4>DM (Direct Message) o Mensaje Directo:</h4> es un mensaje que se env�a de forma directa
a un usuario espec�fico de Twitter. Solo el usuario origen y el destino lo pueden leer.
<h4>Follow:</h4> Seguir a otro usuario de Twitter.
<h4>Follower � Seguidor:</h4> es un usuario que sigue el estatus y conversaciones de otro, 
suscritos ambos dentro de Twitter.
<h4>FollowFriday</h4> es una etiqueta utilizada para decir a tus Followers (seguidores) 
acerca de otros usuarios que vale la pena seguir. Esto se hace los d�as viernes y 
se publica #FollowFriday y uno o varios usuarios para hacer saber a otros 
que se est� recomendando personas para seguir. Ejemplo: #FF @xbaez  @csolares2 
porque comparten contenido muy interesante (podr�as �nicamente poner una lista de usuarios).
<h4>Following � Siguiendo:</h4> estatus de que un usuario est� siguiendo a otro. 
En la aplicaci�n de Twitter es el directorio de personas a la que un usuario est� siguiendo.
<h4>Hashtag � Etiqueta:</h4> son palabras a las cu�les se les agrega el s�mbolo # y se utilizan 
para generar interacci�n y conversaci�n entre los usuarios y comunidades sobre temas espec�ficos. 
Ejemplos: #RedesSociales, #Ecolog�a, etc.
<h4>Listas:</h4> se utilizan para ordenar o catalogar usuarios y cuentas de Twitter, 
agrup�ndolas de acuerdo a los criterios y preferencias del usuario. 
Por ejemplo: Amigos, Mercadotecnia, Baseball, etc.
<h4>Microblogging:</h4> es una aplicaci�n Web que permite al usuario insertar 
mensajes breves similar a los post de un blog y limitado a un cierto n�mero de caracteres.
Las opciones de env�o pueden ser sitios Web, SMS, mensajer�a instant�nea o aplicaciones particulares.
<h4>MT (Modified Tweet):</h4> significa que un Tweet que ha sido publicado, 
ha sido parafraseado del escrito originalmente por otro usuario. 
Cuando se hace esta acci�n se acostumbra poner MT.
<h4>Reply:</h4> responder a un mensaje que ha sido publicado en Twitter. 
El mensaje es p�blico y cualquiera en la comunidad lo puede leer.
RT (ReTweet):</h4> acci�n de reenv�ar un mensaje de otro usuario hacia la comunidad de Twitter.
Cuando se realiza esta acci�n aparecen las letras RT en el mensaje.
<h4>Timeline:</h4> Tambi�n conocido como la l�nea en el tiempo, es la cronolog�a de los tweets, 
o de manera m�s sencilla el listado de los tweets conformen se van generando.
<h4>TT (Trending Topics) o Temas del Momento:</h4> son temas de moda o palabras m�s usadas 
en un momento dado dentro de la comunidad de Twitter,  estos consolidan las 
hashtags o etiquetas con mayor cantidad de publicaciones.
<h4>Tweet (Tuit):</h4> mensajes p�blicos de hasta 140 caracteres que se escriben, 
env�an y publican en la aplicaci�n de Twitter de preferencia del usuario y 
que pueden ser le�dos por los seguidores y todo aquel que tenga acceso a la cuenta.

twitter.consumer.key=KajYug9Z0gnjRz0BrFhQSQ
twitter.consumer.secret=eqf3HZBwPPaKBzt9D4aoOdNhWH2391R85utRENLNEE
twitter.access.token=836549780-xZ5AoEG3061UbvWqHee8DhuONJmvRIJjIvnMwsS0
twitter.access.token.secret=XZ8LRynlddi7vvcIFsjuNsLtDsk6jqcoXoOo9LX687A
 *
 */
public class TwitterScraping {
	
	private static Logger logger = Logger.getRootLogger();
	private AtomicInteger atomicSequence;
	private String dataRoot;
	private WorkspaceDTO workspace;
	
	private Twitter twitter;
	
	public TwitterScraping(WorkspaceDTO workspace,AtomicInteger atomicSequence) throws TwitterException{
		try {
			this.workspace = workspace;
			this.atomicSequence = atomicSequence;
			this.init();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init() throws PropertyValueNotFoundException, TwitterException{
		this.twitter = TwitterConnection.getInstance(this.workspace).getTwitter();
		//this.twitter = TwitterConnection.
	}

	/**
	 * Retorna el Timeline del usuario
	 * @return
	 */
	public Collection<String> getTwitterDocuments(){
		Collection<String> documentCol = new ArrayList<String>();
		try {
			int timeLinePages = this.workspace.getPersistence().getProperty(EProperty.TWITTER_HOME_TIMELINE_PAGES).intValue();
			List<Status> statuses =  this.twitter.getHomeTimeline();			
			for (Status status : statuses) {
				System.out.println(status.getId() + "-" +status.getUser().getName() + ":" + status.getText());
			}

			ResponseList<Status> responseList = twitter.getUserTimeline(new Paging(1, timeLinePages));
			for (Status b : responseList) {
				documentCol.add(b.getText());
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
	public void saveTwitterDocument(String dataRoot, SourceDTO source) throws SQLException, IllegalStateException, TwitterException{
		logger.debug("Init saveTwitterDocument Source:" + source.getUrl());
    	if(this.twitter !=null && this.twitter.getId()!=0){
	    	Collection<DocumentDTO> documentContent = this.getTwitterDocuments(source);
	    	logger.info("Documents to save Twitter Source:" + source.getUrl() + 
					" Amount:" + documentContent.size());
	    	for (DocumentDTO documentDTO : documentContent) {	
	    		logger.debug("Creating new twitter file:"+ documentDTO.getName());
	    		this.workspace.getPersistence().writeFile(EDataFolder.DOWNLOAD, documentDTO.getName(), documentDTO.getLazyData());
	    		this.workspace.getPersistence().insertDocument(documentDTO);
			}
    	}
    }
	
	/**
	 * Retorna el Timeline del usuario especificado
	 * @param screenName identificacion del usuario
	 * @return
	 * @throws SQLException 
	 */
	public Collection<DocumentDTO> getTwitterDocuments(SourceDTO source) throws SQLException{
		Collection<DocumentDTO> documentCol = new ArrayList<DocumentDTO>();
		try {
			int timeLinePages = this.workspace.getPersistence().getProperty(EProperty.TWITTER_HOME_TIMELINE_PAGES).intValue();
			Paging paging = new Paging();
			
			if(source.getSinceId()>0){
				paging.setSinceId(source.getSinceId());
			}
			else{
				paging.setPage(1);
				paging.setCount(timeLinePages);
			}
			
			List<Status> statuses =  this.twitter.getUserTimeline(source.getUrl());			
			/*for (Status status : statuses) {
				System.out.println(status.getId() + "-" +status.getUser().getName() + ":" + status.getText());
			}*/

			ResponseList<Status> responseList = twitter.getUserTimeline(source.getUrl(),paging);

			for (Status b : responseList) {
				DocumentDTO document = new DocumentDTO(String.valueOf(this.atomicSequence)+".txt");
				this.atomicSequence.incrementAndGet();
				document.setLazyData(b.getText());
				document.setPublishedDate(b.getCreatedAt());
				document.setDownloadDate(new Date());
				document.setSource(source.getUrl());
				document.setSourceType(ESourceType.TWITTER);
				documentCol.add(document);
			}
			if(responseList.size()>0){
				source.setSinceId(responseList.get(0).getId());
				source.setLastQuery(responseList.get(0).getCreatedAt());
				this.workspace.getPersistence().updateSource(source);
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
