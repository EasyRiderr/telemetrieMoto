/*
 * Recepteur.java
 * Permet de recevoir les donnees de la carte RASPBERRY-PI
 */

package telemetrieMoto.acquisition.comm.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

import telemetrieMoto.Proprietes;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;
import telemetrieMoto.postTraitement.PostTraitement;



/**
 * <b>Recepteur permet de recevoir les informations provenant de la moto ({@link Emetteur}).</b>
 * <p>Le protocole de communication <b>UDP</b> a été choisi pour échanger les informations sans perdre de temps 
 * avec les acquittements. L'adresse IP de l'{@link Emetteur} et du Recepteur, ainsi que les ports 
 * utilisés sont stockés dans le fichier de configuration du projet.
 * <br> Recepteur est aussi le programme à exécuter sur le PC en bord de piste pour recevoir et 
 * sérialiser grâce aux fonctions de {@link PostTraitement} les informations de la moto dans un 
 * fichier binaire.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see Emetteur
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.postTraitement.PostTraitement
 */
public class Recepteur {


	/** La socket de communication pour la réception d'informations provenant de la moto. */
	private DatagramSocket sockEcoute_ = null;
	
	
	/** La socket de communication pour l'envoie des informations vers la moto. */
	private DatagramSocket sockEnvoie_ = null;


	/** L'adresse IP à laquelle on doit envoyer les informations. */
	private InetAddress adresseIP_ = null;


	/** L'instance représentant le fichier de configuration du projet à interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();


	/** Le port sur lequel on doit écouter la réception d'informations provenant de la moto. */
	private int port_ = Integer.valueOf( prop_s.getPropriete( "UDPPort"));

	
	/** Le port sur lequel on doit envoyer les informations à la moto. */
	private int portACK_ = Integer.valueOf( prop_s.getPropriete( "UDPACK"));


	/** Permet d'arrêter l'attente de réception d'informations quand on n'a pas reçu de données depuis un certain temps. */
	private boolean stop_ = false;


	/** Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stockée dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugUDP"));




	/**
	 * Le constructeur de Recepteur.
	 */
	public Recepteur() {

		// Creation de la socket
		try {
			sockEcoute_ = new DatagramSocket( port_);
			sockEnvoie_ = new DatagramSocket();
		} catch( SocketException e) {
			e.printStackTrace();
			System.exit( 1);
		}

		if( DEBUG_S) {
			System.out.println( "Socket cree.");
		}

		// Creation de l'adresse IP
		try {
			adresseIP_ = InetAddress.getByName( prop_s.getPropriete( "IPCarte"));
		} catch( UnknownHostException e) {
			e.printStackTrace();
			System.exit( 1);
		}

		if( DEBUG_S) {
			System.out.println( "Creation de l'adresse IP OK.");
		}
	}





	/**
	 * Permet d'envoyer un acquittement à la carte sur la moto.
	 */
	private void envoyerACK() {

		// Le tableau d'octets a envoyer
		byte[] aEnvoye = "ACK".getBytes();

		// Creation du paquet a envoyer au serveur
		DatagramPacket paquetAEnvoyer = new DatagramPacket( aEnvoye, aEnvoye.length, adresseIP_, portACK_);

		if( DEBUG_S) {
			System.out.println( "Paquet de donnees cree.");
		}

		try {
			sockEnvoie_.send( paquetAEnvoyer);
		} catch( IOException e) {
			e.printStackTrace();
			System.exit( 1);
		}

		if( DEBUG_S) {
			System.out.println( "ACK envoye.");
		}
	}




	/**
	 * Permet de convertir un tableau d'octets en un objet.
	 * @param b 
	 * 				Le tableau d'octets à désérialiser.
	 * @return L'objet correspondant à <b>b</b> désérialisé.
	 */
	public static Object toObject( byte[] b) {

		Object o = null;	// L'objet lu a retourner

		// Creation du flux d'entree de byte[]
		ByteArrayInputStream fluxTabOctetsLecture = new ByteArrayInputStream( b);
		try {
			// Creation du flux de lecture d'objet
			ObjectInputStream fluxEntreeObjet = new ObjectInputStream( fluxTabOctetsLecture);
			o = fluxEntreeObjet.readObject();

			if( DEBUG_S) {
				System.out.println( "Lecture de la trame dans le flux d'octets OK.");
			}

			// Fermeture des flux de lecture
			fluxEntreeObjet.close();
			fluxTabOctetsLecture.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println( "Erreur dans la fonction Recepteur.toObject().");
			System.exit( 1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println( "Erreur la classe de l'objet deserialisee n'est pas connu.");
			System.exit( 1);
		}
		return o;
	}




	/**
	 * Permet d'attendre la réception d'un message de la carte.
	 * Un timeout est positionné sur l'attente d'un message, au bout de deux secondes, si l'on n'a rien
	 * reçu on renvoie null.
	 * @return La {@link TrameGPS} que la moto nous a envoyé.
	 */
	public TrameGPS ecouterCarte() {

		// La trame GPS que l'on a recu
		TrameGPS trame = null;

		if( DEBUG_S) {
			System.out.println( "On ecoute la carte...");
		}
		try {
			DatagramPacket paquetEcoute = new DatagramPacket( new byte[ 512], 512);

			// On met un timeout d'une seconde sur la socket
			sockEcoute_.setSoTimeout( 2000);
			try {
				sockEcoute_.receive( paquetEcoute);
			} catch( SocketTimeoutException e) {
				if( DEBUG_S) {
					System.err.println( "Le timeout d'attente de la reception d'une trame est depasse !");
				}
				stop_ = true;
			}

			if( DEBUG_S && !stop_) {
				System.out.println( "On a recu un paquet de donnees !");
			}

			Object donneesRecues = null;
			if( !stop_ && paquetEcoute.getData()[ 0] != 0) {
				donneesRecues = toObject( paquetEcoute.getData());
			}

			if( DEBUG_S && donneesRecues != null) {
				System.out.println( "Donnees recues : " + donneesRecues.toString());
			}

			if( !stop_) {
				envoyerACK();
			}

			// Si on recoit une trame gps on l'enregistre dans le fichier
			if( donneesRecues instanceof TrameGPS) {
				trame = ( TrameGPS) donneesRecues;
			}
		} catch( IOException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		}

		return trame;
	}




	/**
	 * Permet de savoir si l'on doit arrêter d'écouter la carte.
	 * @return <i>true</i> si on doit stoper l'écoute,
	 * 		   <i>false</i> sinon.
	 */
	public boolean arreterEcoute() {
		return stop_;
	}




	/**
	 * La fonction à exécuter sur le PC en bord de piste.
	 * Permet d'écouter la carte et d'enregistrer les trames reçues dans un fichier binaire pour le Posttraitement.
	 * @param args
	 * 				Non utilisé.
	 */
	public static void main( String args[]) {

		if( DEBUG_S) {
			System.out.println( "Debut du programme recepteur.");
		}
		Recepteur pcBordDePiste = new Recepteur();
		PostTraitement p = new PostTraitement();
		TrameGPS trame = null;

		if( DEBUG_S) {
			System.out.println( "Ecoute de la carte");
		}
		// On ecoute la carte
		while( !pcBordDePiste.arreterEcoute()) {
			trame = pcBordDePiste.ecouterCarte();
			if( trame != null) {
				p.ecrireTrameBin( trame, prop_s.getPropriete( "nomFichTramesBin"));
			}
		}

		// On ferme le fichier de serialisation
		p.fermerFichier();

		if( DEBUG_S) {
			System.out.println( "Fin du programme recepteur.");
		}
	}
}