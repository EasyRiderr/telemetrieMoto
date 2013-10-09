/*
 * PortSerie.java
 * Permet d'ecouter et de communiquer avec un port serie RS 232
 */

package telemetrieMoto.acquisition.comm.rs232;


import gnu.io.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import telemetrieMoto.Proprietes;

 
/**
 * <b>PortSerie permet la communication s�rie gr�ce au port RS 232.</b>
 * <p>Pour cela elle utilise la librairie RXTX. Toutes les configurations 
 * du port s�rie sont lues dans le fichier de configuration du projet.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 */
public class PortSerie {


	/** L'objet repr�sentant le port s�rie mat�riel sur lequel on va communiquer. */
	private SerialPort portSerie_;
	

	/** Le flux d'�criture sur le port s�rie. */
	private OutputStream fluxSortie_;
	
	
	/** Le flux de lecture sur le port s�rie. */
	private InputStream fluxEntree_;


	/** Le nombre de bauds de la liaison s�rie.
	 * <br><i>Cette information est lue dans le fichier de configuration du projet.</i> */
	private int nbBauds_;


	/** Le nom du port s�rie auquel on souhaite se connecter.
	 * <br><i>Cette information est lue dans le fichier de configuration du projet.</i> */
	private String nomPort_;


	/** L'identifiant du port s�rie auquel on souhaite se connecter. */
	private CommPortIdentifier portId_;


	/** Permet de savoir si l'on est connect� au port s�rie. */
	private boolean estConnecte_;
	
	
	/** Le buffer contenant les messages lus sur le port s�rie en attente d'une lecture de l'utilisateur.
	 * A chaque lecture de l'utilisateur le buffer est vid�. 
	 */
	private volatile StringBuffer msgLus_;
	
	
	/** Le buffer contenant les messages � �crire en attente d'�criture sur le port s�rie.
	 * D�s que le message a �t� envoy� sur le port s�rie, le buffer est vid�. 
	 */
	private volatile StringBuffer msgAEcrire_;
	
	
	/** Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stock�e dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( Proprietes.getInstance().getPropriete( "DebugPS"));




	/**
	 * Le constructeur de port s�rie.
	 * @param nomPort
 	 *					Le nom du port s�rie � ouvrir.
	 * @param nbBauds
	 * 					Le nombre de bauds de la liaison s�rie.
	 */
	public PortSerie( String nomPort, int nbBauds) {
		nomPort_ = nomPort;
		nbBauds_ = nbBauds;
		// La connexion n'est pas encore etablie
		estConnecte_ = false;
		// Initialisation des buffers
		msgLus_ = new StringBuffer( 256);
		msgAEcrire_ = new StringBuffer( 256);
	}




	/**
	 * Permet d'�tablir la connexion au port s�rie.
	 * Lance un thread d'�coute sur le port s�rie permettant la lecture de celui-ci et un autre d'�criture.
	 * @throws IOException si l'on a pas pu obtenir les flux de communication sur le port s�rie.
	 */
	public void connect() throws IOException {

		if( nomPort_ == null) {
			if( DEBUG_S) {
				System.err.println( "Erreur : vous devez saisir un nom de port valide avant de se connecter.");
			}
			return;
		}

		// On declare au systeme quel port nous allons utiliser pour communiquer
		System.setProperty( "gnu.io.rxtx.SerialPorts", nomPort_);

		if( DEBUG_S) {
			System.out.println( "Connexion au port : " + nomPort_);
		}

		try {
			// On recupere l'identifiant du port de communication pour le port que l'on souhaite ouvrir
			portId_ = CommPortIdentifier.getPortIdentifier( nomPort_);

			// On prend la main sur le port avec un timeout de 5 secondes
			portSerie_ = (SerialPort) portId_.open( "SerialMonitor", 5000);

			// On definit les parametres de connexion pour le port
			setParametreConnexion();
			
			// On ouvre les flux de communication
			fluxSortie_ = portSerie_.getOutputStream();
			fluxEntree_ = portSerie_.getInputStream();

		} catch( NoSuchPortException e) {
			throw new IOException( "Le port " + nomPort_ + " n'existe pas !");

		} catch( PortInUseException e) {
			throw new IOException("Le port " + nomPort_ + " est deja utilise !");

		} catch( IOException e) {
			// Erreur lors de l'ouverture des flux de communication, on ferme le port serie
			portSerie_.close();
			// Puis on fait suivre l'exception
			throw e;
		}

		new Thread( new ThreadEcriture()).start();
		new Thread( new ThreadLecture()).start();

		if( DEBUG_S) {
			System.out.println( "Connecte au port " + nomPort_);
		}
		estConnecte_ = true;
	}




	/**
	 * Permet de param�trer la communication au port s�rie.
	 * @throws IOException Si le port s�rie est mal param�tr�.
	 */
	private void setParametreConnexion() throws IOException {

		try {
			// Definition des parametres d'utilisation du port
			portSerie_.setSerialPortParams( nbBauds_, 
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// Definition du mode de controle
			portSerie_.setFlowControlMode( SerialPort.FLOWCONTROL_NONE);

		} catch ( UnsupportedCommOperationException ex) {

			if( DEBUG_S) {
				System.err.println( "Les parametres pour le port " + nomPort_ + " : "
						+ nbBauds_ + " bauds "
						+ SerialPort.DATABITS_8 + " " 
						+ SerialPort.STOPBITS_1 + " "
						+ SerialPort.PARITY_NONE + " "
						+ SerialPort.FLOWCONTROL_NONE
						+ ", ne sont pas supportes!");
			}
			throw new IOException( "Au moins un parametre n'est pas supporte.");
		}

	}




	/*
	 * Permet d'afficher la liste des ports series disponibles sur la sortie standard
	 */
	/*public static void listeDesPortsDisponibles() {
		System.out.println( "Les ports disponibles sont :");

		// On recupere la liste des ports disponibles
		String[] portsSeries = listePorts();

		if( portsSeries != null) {
			// On affiche la liste des ports disponibles
			if( portsSeries.length == 0) {
				System.out.println( "Aucun port serie disponible n'a etait detecte.");
			} else {
				for( int i = 0 ; i < portsSeries.length ; ++i) {
					System.out.println( "Nom du port : " + portsSeries[ i]);
				}
			}
		}
		System.out.println();
	}*/




	/*
	 * Retourne un tableau de string representant les identifiants des ports series disponibles
	 * @return tabPorts, le tableau des ports series disponibles
	 */
	/*private static String[] listePorts() {

		// On recupere la liste des identifiants de ports series
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();

		// La liste des ports series
		ArrayList< String> listePortsSeries = new ArrayList< String>();

		// Le tableau d'identifiants des ports series
		String tabPorts[] = null;

		// Parcours des elements de la liste des identifiants des ports disponibles sur la machine
		while( ports.hasMoreElements()) {
			// On recupere le port
			CommPortIdentifier port = ( CommPortIdentifier) ports.nextElement();

			if( port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				// Le port courant est un port serie
				listePortsSeries.add( port.getName());
			}
		}
		// Conversion de la liste des ports series en tableau et retour de celui-ci
		tabPorts = listePortsSeries.toArray( new String[0]);

		return tabPorts;
	}*/




	/**
	 * Permet de se d�connecter du port s�rie.
	 * Ferme les flux de communication, mettant fin aux threads de lecture et �criture sur le port s�rie.
	 */
	public void disconnect() {
		if( portSerie_ != null) {

			try {
				// Fermeture des flux de communication
				fluxSortie_.close();
				fluxEntree_.close();

			} catch (IOException e) {
				// Erreur lors de la fermeture des ports ==> sans importance, on affiche sur la console l'erreur
				if( DEBUG_S) {
					e.printStackTrace();
				}
			}

			// Fermeture du port
			portSerie_.close();
		}
		
		// On arrete les threads de lecture et ecriture du port serie
		fluxSortie_ = null;
		fluxEntree_ = null;
		
		estConnecte_ = false;
	}




	/**
	 * Permet de savoir si l'on est bien connect� au port s�rie.
	 * @return <i>true</i> si l'on est connect� au port s�rie, <i>false</i> sinon.
	 */
	public boolean estConnecte() {
		return estConnecte_;
	}




	/**
	 * Permet d'�crire sur le port s�rie mat�riel.
	 * @param msg
	 * 				Le message � �crire sur le port s�rie.
	 * @throws IOException
	 * 				Si l'envoi du message a echou�. 
	 */
	private void ecrire( String msg) throws IOException {
		if( estConnecte_) {
			// On envoie le message sur le port
			fluxSortie_.write( msg.getBytes());
			// On force l'ecriture du message sur le flux
			fluxSortie_.flush();
		}
	}




	/**
	 * Le thread permettant d'�crire sur le port s�rie mat�riel.
	 * Il n'est actif que sur une interruption lanc�e par la fonction {@link PortSerie#ecrirePortSerie(String)}. Il �crit le message
	 * � �crire sur le port s�rie puis vide le buffer d'�criture. Le thread se rendort jusqu'� ce qu'il soit
	 * r�veill� par une autre interruption. Il est actif tant que le flux d'�criture n'est pas nul.
	 * 
	 * @author Yoan DUMAS
	 * @version 1.1
	 */
	private class ThreadEcriture implements Runnable {
		
		@Override
		public void run() {
			
			while( fluxSortie_ != null) {
				try {
					synchronized( msgAEcrire_) {
						msgAEcrire_.wait();
						// On envoie le message sur le port serie
						ecrire( msgAEcrire_.toString());
						msgAEcrire_.delete( 0, msgAEcrire_.length());
					}

				} catch (IOException e) {
					if( DEBUG_S) {
						System.err.println( "On a intercepte une IOException");
						e.printStackTrace();
					}

				} catch( InterruptedException e) {
					if( DEBUG_S) {
						System.err.println( "On a intercepte une InterruptedException.");
						e.printStackTrace();
					}
				}
			}
		}

	}




	/**
	 * Permet de lire sur le port s�rie mat�riel.
	 * @return Le message lu sur le port s�rie.
	 * @throws IOException Si la lecture s'est mal d�roul�e.
	 */
	private String lire() throws IOException {
		String res = null;	// La chaine a retourner
		if( estConnecte_) {
			// On lit le message sur le posrt serie
			byte[] aLire = new byte[ 256];
			fluxEntree_.read( aLire);
			res = new String( aLire);
		}
		return res;
	}
	
	
	
	
	/**
	 * Le thread permettant de lire les informations re�ues sur le port s�rie.
	 * Il effectue une lecture toutes les 100ms tant que le flux de lecture n'est pas nul.
	 * A chaque lecture, il concat�ne le message lu au buffer de lecture.
	 * 
	 * @author Yoan DUMAS
	 * @version 1.1
	 */
	private class ThreadLecture implements Runnable {

		@Override
		public void run() {
			
			while( fluxEntree_ != null) {
				if( estConnecte_) {
					// On lit le port serie
					try {
						String msgAAjouter = lire();
						if( msgAAjouter != null && msgAAjouter.length() != 0) {
							synchronized( PortSerie.class) {
								msgLus_.append( msgAAjouter);
							}
						}
						
						Thread.sleep( 100);

					} catch (IOException e) {
						if( DEBUG_S) {
							System.err.println( "Erreur lors de la lecture sur le port serie.");
							e.printStackTrace();
						}

					} catch (InterruptedException e) {
						if( DEBUG_S) {
							System.err.println( "Erreur le sleep a ete interrompu.");
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	
	
	
	
	/**
	 * Retourne la chaine lue sur le port s�rie.
	 * Vide le buffer de lecture, une fois la lecture termin�e.
	 * @return La chaine lue sur le port s�rie.
	 */
	public String lirePortSerie() {
		String res = null;
		synchronized( PortSerie.class) {
			res = msgLus_.toString();
			if( res != null && res.length() != 0) {
				msgLus_.delete( 0, msgLus_.length());
			}
		}
		return res;
	}
	
	
	
	
	/**
	 * Permet de pr�ciser quel message on doit envoyer sur le port s�rie.
	 * Il sera concat�ner au buffer des messages � �crire sur le port s�rie mat�riel.
	 * @param msg 
	 * 				Le message � envoyer sur le port s�rie.
	 */
	public void ecrirePortSerie( String msg) {
		synchronized( msgAEcrire_) {
			msgAEcrire_.append( msg);
			msgAEcrire_.notify();
		}
	}




	/**
	 * Permet de modifier le d�bit de la liaison s�rie (nombre de bauds).
	 * @param br 
	 * 				Le nouveau nombre de bauds de la liaison s�rie.
	 */
	public void setBaudRate( int br) {

		// Definition des parametres d'utilisation du port
		try {
			portSerie_.setSerialPortParams( br,
											SerialPort.DATABITS_8,
											SerialPort.STOPBITS_1,
											SerialPort.PARITY_NONE);
		} catch( UnsupportedCommOperationException e) {
			if( DEBUG_S) {
				System.err.println( "Les parametres pour le port " + nomPort_ + " : "
						+ nbBauds_ + " bauds "
						+ SerialPort.DATABITS_8 + " " 
						+ SerialPort.STOPBITS_1 + " "
						+ SerialPort.PARITY_NONE + " "
						+ SerialPort.FLOWCONTROL_NONE
						+ ", ne sont pas supportes!");
				e.printStackTrace();
			}
		}
	}
}
