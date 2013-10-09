/*
 * Emetteur.java
 * Permet d'envoyer les donnees de la carte RASPBERRY-PI vers l'access point en bord de piste
 */

package telemetrieMoto.acquisition.comm.udp;

import java.io.*;
import java.net.*;

import telemetrieMoto.Proprietes;



/**
 * <b>Emetteur permet d'envoyer les informations de la moto vers un {@link Recepteur} en bord de piste.</b>
 * <p>Le protocole de communication UDP a été choisi pour envoyer les informations sans perdre de temps 
 * avec les acquittements. L'adresse IP de l'Emetteur et du {@link Recepteur}, ainsi que les ports 
 * utilisés sont stockés dans le fichier de configuration du projet.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see Recepteur
 */
public class Emetteur {


	/** La socket de communication pour l'envoie des informations de la moto. */
	private DatagramSocket sockEnvoie_ = null;
	
	
	/** La socket de communication pour la réception d'informations. */
	private DatagramSocket sockEcoute_ = null;


	/** L'adresse IP à laquelle on doit envoyer les informations. */
	private InetAddress adresseIP_ = null;
	
	
	/** L'instance représentant le fichier de configuration du projet à interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();


	/** Le port sur lequel on doit envoyer les informations de la moto au {@link Recepteur}. */
	private int port_ = Integer.valueOf( prop_s.getPropriete( "UDPPort"));
	
	
	/** Le port sur lequel on écoutera la réception d'informations provenant du {@link Recepteur}. */
	private int portACK_ = Integer.valueOf( prop_s.getPropriete( "UDPACK"));


	/** 
	 * Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stockée dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugUDP"));




	/**
	 * Le constructeur d'Emetteur embarqué sur la moto.
	 */
	public Emetteur() {

		// Creation de la socket
		try {
			sockEnvoie_ = new DatagramSocket();
			sockEcoute_ = new DatagramSocket( portACK_);
		} catch( SocketException e) {
			e.printStackTrace();
			System.exit( 1);
		}

		if( DEBUG_S) {
			System.out.println( "Socket cree.");
		}

		// Creation de l'adresse IP
		try {
			adresseIP_ = InetAddress.getByName( prop_s.getPropriete( "IPPCBordDePiste"));
		} catch( UnknownHostException e) {
			e.printStackTrace();
			System.exit( 1);
		}

		if( DEBUG_S) {
			System.out.println( "Creation de l'adresse IP OK.");
		}
	}




	/**
	 * Permet de convertir un objet en tableau d'octets.
	 * @param o 
	 * 			L'objet à convertir en tableau d'octets.
	 * @return L'objet converti en tableau d'octets.
	 */
	public static byte[] toByteArray( Object o) {
		byte[] resultat = null;

		// Ecriture de l'objet dans un flux d'octets
		ByteArrayOutputStream fluxTabOctetsEcriture = new ByteArrayOutputStream();
		ObjectOutputStream fluxSortieObjet = null;
		try {
			fluxSortieObjet = new ObjectOutputStream( fluxTabOctetsEcriture);
			fluxSortieObjet.writeObject( o);
			if( DEBUG_S){
				System.out.println( "Ecriture de la trame dans le flux de sortie OK.");
			}

			resultat = fluxTabOctetsEcriture.toByteArray();

			if( DEBUG_S) {
				System.out.println( "Conversion de la trame en un tableau d'octets OK.");
			}

			// Fermeture des flux intermediaires
			fluxSortieObjet.close();
			fluxTabOctetsEcriture.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit( 1);
		}
		
		return resultat;
	}





	/**
	 * Permet d'envoyer un message au {@link Recepteur}.
	 * @param trame 
	 * 				La trame à envoyer.
	 */
	public void envoyerMessage( Object trame) {
		
		// Le tableau d'octets a envoyer
		byte[] aEnvoye = Emetteur.toByteArray( trame);

		// Creation du paquet a envoyer au serveur
		DatagramPacket paquetAEnvoyer = new DatagramPacket( aEnvoye, aEnvoye.length, adresseIP_, port_);

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
			System.out.println( "Paquet envoye.");
		}
	}
	
	
	
	
	/**
	 * Permet d'attendre la réception d'un acquittement du {@link Recepteur}.
	 */
	public void ecouterACK() {
		if( DEBUG_S) {
			System.out.println( "On attend de recevoir un ACK...");
		}
		try {
			// Creation du paquet de reception
			DatagramPacket paquetReception = new DatagramPacket( new byte[ 512], 512);
			// Attente de l'ACK
			sockEcoute_.receive( paquetReception);
			
			if( DEBUG_S) {
				System.out.println( "Paquet recu : " + new String( paquetReception.getData()));
			}
		} catch (IOException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		}
	}
}