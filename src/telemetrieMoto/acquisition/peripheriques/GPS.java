/*
 * GPS.java
 * Permet de communiquer avec le GPS UP501 de Fastrax
 */

package telemetrieMoto.acquisition.peripheriques;

import java.io.IOException;

import telemetrieMoto.Proprietes;
import telemetrieMoto.acquisition.comm.gps.TrameGPGGA;
import telemetrieMoto.acquisition.comm.gps.TrameGPRMC;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;
import telemetrieMoto.acquisition.comm.rs232.PortSerie;


/**
 * <b>GPS représente un périphérique matériel de type GPS (Global Positioning System).</b>
 * <p>La classe permet de communiquer avec tous types de GPS utilisant le protocole <b>NMEA</b>.
 * Elle permet d'écouter le GPS et de le configurer. Les configurations possibles actuellement sont 
 * les suivantes :
 * <ul>
 * <li>Configuration du nombre de bauds de la liaison.</li>
 * <li>Configuration des trames NMEA que l'on souhaite recevoir.</li>
 * <li>Configuration de la fréquence d'acquisition du GPS.</li>
 * </ul>
 * <i>Les paramètres de configuration du GPS sont lus dans le fichier de configuration du projet.</i></p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see PortSerie
 * @see TrameGPS
 * @see TrameGPGGA
 * @see TrameGPRMC
 */
public class GPS {


	/** Le port série sur lequel est connecté le GPS. */
	private PortSerie uartGPS_;


	/** Le buffer des caractères lus sur le port série en attente de traitement. */
	private StringBuffer charLus_ = new StringBuffer();


	/** Le mot clé permettant de spécifier que l'on envoie une commande au GPS. */
	private static final String PMTK_S = "PMTK";


	/** L'instance représentant le fichier de configuration du projet à interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();


	/** La taille maximale d'une trame GPS.
	 * Définit dans le protocole NMEA. 
	 * <br><i>Sa valeur est stockée dans le fichier de configuration.</i> */
	private static final int TAILLE_TRAME_MAX_S = Integer.valueOf( prop_s.getPropriete( "GPSTailleTrameMax"));


	/** Le nombre de trames maximum à lire en attendant un acquittement d'une commande NMEA du GPS.
	 * <br><i>Sa valeur est stockée dans le fichier de configuration.</i> */
	private static final int NB_TRAME_ACK_S = Integer.valueOf( prop_s.getPropriete( "GPSNbTrameAttenteACK"));


	/** Le nombre de bauds par défaut de la liaison GPS. 
	 * <br><i>Sa valeur est stockée dans le fichier de configuration.</i>*/
	private static final int NBBAUDSGSDEFAUT_S = Integer.valueOf( prop_s.getPropriete( "GPSNbBaudsDefaut"));


	/** Le nombre de bauds de la liaison GPS souhaité. 
	 * <br><i>Sa valeur est stockée dans le fichier de configuration.</i>*/
	private static final int NBBAUDSGPSSOUHAITES_S = Integer.valueOf( prop_s.getPropriete( "GPSNbBaudsSouhaite"));


	/************************************************* Les différentes commandes à envoyer au GPS *************************************************/

	/** La commande qui permet d'envoyer au GPS le nombre de baud de la liaison souhaité. */
	private static final String SET_BAUD_RATE_S = prop_s.getPropriete( "GPSCmdSetNbBauds");
	/** La commande qui permet de tester la liaison série avec le GPS. */
	private static final String TEST_UART_S = prop_s.getPropriete( "GPSCmdTestUart");
	/** La commande qui permet de fixer la fréquence d'acquisition des {@link TrameGPS}. */
	private static final String SET_FREQ_S = prop_s.getPropriete( "GPSCmdSetFreq");
	/** La commande permettant de préciser le type des {@link TrameGPS} que l'on souhaite recevoir. */
	private static final String SET_TRAMES_S = prop_s.getPropriete( "GPSCmdSetTrames");
	/** Le paramètre de la commande {@link #SET_TRAMES_S} ==> on ne veut que les {@link TrameGPGGA} et {@link TrameGPRMC}. */
	private static final String GPGGA_GPRMC_UART_S = prop_s.getPropriete( "GPSParamSetTrames");

	/**********************************************************************************************************************************************/
	
	
	/** Permet de savoir si la liaison du GPS est en 9600 bauds. */
	private volatile boolean gpsEn9600Bauds_;
	
	/** Permet de savoir si la liaison du GPS est en 115200 bauds. */
	private volatile boolean gpsEn115200Bauds_;


	/** 
	 * Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stockée dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugGPS"));




	/**
	 * Le constructeur de GPS.
	 * Permet d'initialiser le port série utilisé pour la communication avec le GPS matériel.
	 */
	public GPS() {

		if( DEBUG_S) {
			System.out.println( "Initialisation de l'UART du GPS sur " + prop_s.getPropriete( "NomPortSerie") + " a " + NBBAUDSGSDEFAUT_S + " bauds.");
		}

		// Initialisation du port serie
		uartGPS_ = new PortSerie( prop_s.getPropriete( "NomPortSerie"), NBBAUDSGSDEFAUT_S);
		try {
			uartGPS_.connect();
		} catch( IOException e) {
			System.err.println( "Erreur lors de la connection sur le port serie : " + e.getStackTrace());
		}
	}




	/**
	 * Permet d'initialiser le GPS.
	 * <p>Configuration de :
	 * <ul>
	 * 	<li>La fréquence d'acquisition du GPS.</li>
	 * 	<li>Le débit de la liaison série.</li>
	 * 	<li>La sélection des trames que l'on souhaite recevoir.</li>
	 * </ul>
	 * Tous les paramètres de la configuration sont lus dans le fichier de configuration du projet.
	 * Si une configuration échoue, le programme s'arrête.</p>
	 */
	public void initGPS() {

		// On modifie le debit de la liaison
		setBaudRate( NBBAUDSGPSSOUHAITES_S);

		// On fixe la frequence d'envoie des trames
		setSampleFrequency( Integer.valueOf( prop_s.getPropriete( "GPSParamFreq")));
		if( DEBUG_S){
			System.out.println( "Fin de la selection de la frequence d'emission des trames du GPS.");
		}

		// On demande au GPS de nous envoyer que les trames NMEA GPGGA et GPRMC
		selectionTrame( GPGGA_GPRMC_UART_S);
	}




	/**
	 * Permet de fixer la fréquence d'envoie des trames du GPS.
	 * @param freqHz 
	 * 					La fréquence d'envoie des trames du GPS en Hertz.
	 */
	private void setSampleFrequency( int freqHz) {
		int freqMs = 1000 / freqHz;										// La frequence d'envoie du GPS en millisecondes
		String paramFreq = "" + String.valueOf( freqMs) + ",0,0,0,0";		// Le parametre de la commande de reglage de la frequence

		if( DEBUG_S) {
			System.out.println( "On fixe la frequence du GPS a " + freqMs + "ms.");
		}

		// Envoie de la commande au GPS
		envoyerCommande( SET_FREQ_S, paramFreq);

		if( DEBUG_S) {
			System.out.println( "Commande setSampleFrequency envoyee. Ecoute de la reponse ...\n\n\n\n");
		}
		// Analyse de la reponse du GPS
		ecouterReponse( SET_FREQ_S);
	}




	/**
	 * Permet de sélectionner le type de {@link TrameGPS} que l'on souhaite recevoir.
	 * @param listeTrames 
	 * 						La liste des trames <b>NMEA</b> que l'on souhaite recevoir.
	 */
	private void selectionTrame( String listeTrames) {

		if( DEBUG_S) {
			System.out.println( "On ne selectionne que les trames GPGGA et GPRMC.");
		}

		envoyerCommande( SET_TRAMES_S, GPGGA_GPRMC_UART_S);

		// Analyse de la reponse du GPS
		ecouterReponse( SET_TRAMES_S);
	}




	/**
	 * Permet d'envoyer une commande <b>NMEA</b> au GPS.
	 * @param numCmd 
	 * 					Le numéro de la commande à envoyer.
	 * @param paramCmd 
	 * 					Les paramètres de la commande à envoyer.
	 */
	private void envoyerCommande( String numCmd, String paramCmd) {
		String trameAEnvoyer;	// La trame contenant la commande a envoyer au GPS
		char checksum = 0;		// Le checksum calcule par rapport a la trame que l'on veut envoyer


		// Une trame commence par un $
		trameAEnvoyer = "$";


		// On souhaite envoyer une commande
		for( int i = 0 ; i < PMTK_S.length() ; ++i) {
			checksum ^= PMTK_S.charAt( i); 
		}
		trameAEnvoyer += PMTK_S;


		// On ajoute le numero de la commande a executer
		for( int i = 0 ; i < numCmd.length() ; ++i) {
			checksum ^= numCmd.charAt( i);
		}
		trameAEnvoyer += numCmd;


		// On ajoute eventuellement les parametres de la commande
		if( paramCmd != null) {

			// Ajout de la virgule de separation
			trameAEnvoyer += ",";
			checksum ^= ',';

			// Ajout du parametre
			for( int i = 0 ; i < paramCmd.length() ; ++i) {
				checksum ^= paramCmd.charAt( i);
			}
			trameAEnvoyer += paramCmd;
		}


		if( DEBUG_S) {
			System.out.println( "trameAEnvoyer = " + trameAEnvoyer + ", on va ajouter le checksum.");
		}


		// Ajout du marqueur de fin de trame
		trameAEnvoyer += "*";

		// Ajout du checksum a la trame
		String checksumASCII = Integer.toHexString( ( int) checksum).toUpperCase();	// Le checksum convertit en ASCII a envoyer au GPS
		trameAEnvoyer += checksumASCII;

		// Ajout des caracteres de terminaison de la trame
		trameAEnvoyer += "\r\n";


		if( DEBUG_S) {
			System.out.println( "On envoie au GPS : " + trameAEnvoyer);
		}

		// Envoie de la trame GPS
		uartGPS_.ecrirePortSerie( trameAEnvoyer);

		if( DEBUG_S) {
			System.out.println( "La trame a ete envoye.\n\n\n\n\n");
		}
	}




	/**
	 * Permet de lire le premier caractère reçu par le GPS.
	 * Attention fonction bloquante ! Elle attend de recevoir un caractère du GPS.
	 * @return c
	 * 				Le premier caractère reçu par le GPS.
	 */
	private char lireCaractere() {
		char c = '\0';	// Le caractere que l'on va lire

		while( c == '\0') {
			if( charLus_.length() != 0) {
				// Lecture du caractere
				c = charLus_.charAt( 0);
				// On efface le caractere lu
				charLus_.deleteCharAt( 0);
			} else {
				// On actualise le buffer des caracteres lus
				charLus_.append( uartGPS_.lirePortSerie());
			}
		}

		return c;
	}




	/**
	 * Permet de récuprer une {@link TrameGPRMC} ou une {@link TrameGPGGA} à partir de la trame reçue 
	 * du GPS.
	 * @return La trame {@link TrameGPRMC} ou {@link TrameGPGGA} que l'on vient de recevoir.
	 */
	public TrameGPS lireTrameGPS() {
		TrameGPS trameResultat = null;	// La trame GPS correspondante a la trame lue
		String trameLue = null;			// La trame lue sur le GPS

		if( DEBUG_S) {
			System.out.println( "On attend de recevoir une trame GPS non nulle.");
		}
		while( trameLue == null || ( trameLue.charAt( 3) != 'R' && trameLue.charAt( 4) != 'G')) {
			trameLue = lireTrame();
		}
		if( DEBUG_S) {
			System.out.println( "On a recu une trame GPGGA ou GPRMC.");
		}

		if( trameLue.charAt( 3)== 'R') {
			// On a une trame de type GPRMC, on la parse pour recuperer les informations dont on a besoin
			trameResultat = new TrameGPRMC( trameLue);
		} else if( trameLue.charAt( 4) == 'G') {
			// On a une trame du type GPGGA, on la parse pour recuperer les informations dont on a besoin
			trameResultat = new TrameGPGGA( trameLue);
		} else {
			// On a une trame differente, non traite pour le moment
		}

		return trameResultat;
	}




	/**
	 * Retourne la trame GPS reçue en chaine de caractères.
	 * Tous types de trames NMEA confondues.
	 * @return La trame GPS que l'on vient de recevoir ou null si celle-ci n'est pas valide ==> 
	 * erreur de checksum par exemple.
	 */
	private String lireTrame() {

		String trameLue = "";	// La trame GPS que l'on a lu
		char c;					// Le caractere lu sur l'UART du GPS
		char checksum = 0;		// Le checksum calcule par rapport a la trame que l'on veut envoyer
		char checksumGPS;		// Le checksum envoye par le GPS pour la trame courante

		if( DEBUG_S){
			System.out.println( "Attente de la reception d'un '$'.");
		}

		// On attend le derbut de la trame
		while( ( c = lireCaractere()) != '$');

		for( int i = 0 ; i < TAILLE_TRAME_MAX_S && c != '*' ; ++i) {
			// On ajoute le caractere a la trame
			trameLue += c;
			checksum ^= c;
			c = lireCaractere();
		}

		// On ajoute le symbole de fin et les deux caracteres de checksum a notre trame
		for( int i = 0 ; i < 3 ; ++i) {
			trameLue += c;
			c = lireCaractere();
		}

		if( DEBUG_S) {
			System.out.println( "On a recu la trame :\n" + trameLue);
		}

		// Calcul du checksum pour verifier la validite de la trame
		checksumGPS = (char) (( trameLue.charAt( trameLue.length() - 2) << 8) | trameLue.charAt( trameLue.length() - 1));

		if( DEBUG_S) {
			System.out.println( "Checksum envoye par le GPS : " + trameLue.charAt( trameLue.length() - 2) + trameLue.charAt( trameLue.length() - 1));
		}

		if( ( checksumGPS & checksum) == 0) {
			trameLue = null;
			if( DEBUG_S) {
				System.out.println( "La trame recue n'etait pas correcte ! On renvoi null.");
			}
		}

		return trameLue;
	}




	/**
	 * Permet d'écouter l'acquittement de la commande que l'on vient d'envoyer.
	 * Afin de vérifier qu'elle ait bien été executée ou comprendre pourquoi elle n'a pas fonctionné.
	 * @param numCmd 
	 * 					Le numéro de la commande dont on veut écouter le retour.
	 * @return Plusieurs retours sont possibles :
	 * <ul>
	 * <li>-1 pour pas d'acquitement reçu.</li>
	 * <li>0 pour commande non valide.</li> 
	 * <li>1 pour commande non suppotée.</li> 
	 * <li>2 pour ordre reçu mais non accompli.</li>
	 * <li>3 pour ordre reçu et accompli.</li>
	 * </ul>
	 */
	private int ecouterACK( int numCmd) {
		int nbTrameLues = 0;		// Le nombre de trames que l'on a lu avant de tomber sur une trame d'ACK
		String trameNMEA = "";		// Une trame NMEA que le GPS nous a envoye
		String numCmdLu = "";		// Le numero de commande de lu dans la trame d'ACK
		int resultat = -1;			// Le resultat de la lecture de l'ACK pour la commande numCmd

		if( DEBUG_S) {
			System.out.println( "On attend de recevoir une trame d'ACK.");
		}

		// On attend de recevoir une trame d'acquitement
		trameNMEA = lireTrame();
		boolean boucle = true;	// Permet la recherche de la trame d'ACK
		while( ++nbTrameLues < NB_TRAME_ACK_S && boucle) {
			if( trameNMEA != null && trameNMEA.contains( PMTK_S)) {
				boucle = false;
			} else {
				trameNMEA = lireTrame();
			}
		}

		if( DEBUG_S) {
			System.out.println( "Derniere trame lue :\n" + trameNMEA);
		}

		if( nbTrameLues < NB_TRAME_ACK_S) {
			// On a recu une trame d'ACK, on recupere le numero de commande
			String[] tabParse = trameNMEA.split( ",");
			numCmdLu = tabParse[ 1];

			if( DEBUG_S) {
				System.out.println( "Numero de la commande lu = " + numCmdLu);
			}

			// Controle du numero de commande
			if( numCmd == Integer.valueOf( numCmdLu)) {
				// L'ACK recue correspond bien a la commande, on recupere le code de retour
				// Trame du type : $PMTK001,cmdInt,resultat
				String resCmd = tabParse[ 2];
				resultat = Integer.valueOf( "" + resCmd.charAt( 0));

				if( DEBUG_S) {
					System.out.println( "Le resultat de la commande " + numCmdLu + " est " + resultat);
				}
			} else if( DEBUG_S){
				System.out.println( "L'ACK recu ne correspond pas au numero de parametre passe en parametre.\nACK recu = "
						+ numCmdLu + ", ACK attendu = " + numCmd);
			}
		} else if( DEBUG_S) {
			System.out.println( "On a lu " + NB_TRAME_ACK_S + " trames et aucun ACK n'a ete recu ==> resultat = " + resultat + ".");
		}

		return resultat;
	}




	/**
	 * Permet d'analyser la réponse envoyée par le GPS.
	 * Si elle ne correspond pas au résultat attendu on stop le programme.
	 * @param cmd 
	 * 				La chaine représentant le numéro de la commande dont on attend un acquittement.
	 */
	private void ecouterReponse( String cmd) {

		int resCmd = ecouterACK( Integer.valueOf( cmd));
		boolean erreur = false;
		
		switch( resCmd) {
		case 0:
			// Invalid command / packet
		case 1: 
			// Unsupported command / packet type
		case 2:
			// Valid command / packet, but action failed
			erreur = true;
			break;
		case 3:
			// Valid command / packet, and action succeeded
			if( DEBUG_S) {
				System.out.println( "La commande s'est correctement effectuee.");
			}
			break;
		default:
			if( DEBUG_S) {
				System.err.println( "ERREUR : L'ACK nous renvoie un code de retour inconnu!");
			}
			erreur = true;
			break;
		}
		
		if( erreur){
			if( DEBUG_S) {
				System.out.println( "Erreur : code de retour = " + resCmd);
			}
			// On stop le programme
			System.exit( 1);
		}
	}




	/**
	 * Permet de changer le débit de la liaison série et de le signaler au GPS pour qu'il s'adapte.
	 * @param br
	 * 				Le nouveau débit de la liaison série.
	 */
	private void setBaudRate( int br) {
		// On ne sait pas quel est le nombre de bauds du GPS
		gpsEn9600Bauds_ = false;
		gpsEn115200Bauds_ = false;

		// On test la liaison serie pour le verifier
		if( DEBUG_S) {
			System.out.println( "On lance un test de la liaison serie en 9600 bauds et son timeout.");
		}
		// On test la liaison serie
		envoyerCommande( TEST_UART_S, null);
		new Thread( new ThreadTimeoutTestSerie()).start();

		if( DEBUG_S) {
			System.out.println( "On attend l'ACK de la liaison serie.");
		}
		// Analyse de la reponse du GPS
		ecouterReponse( TEST_UART_S);
		// On a recu un ACK, on est donc en 9600 bauds
		gpsEn9600Bauds_ = true;
		
		if( !gpsEn115200Bauds_) {
			// On doit faire passer le GPS en 115200 bauds
			envoyerCommande( SET_BAUD_RATE_S, String.valueOf( br));

			if( DEBUG_S) {
				System.out.println( "Dodo 1s apres avoir demande au GPS de passer en 115200 bauds");
			}
			// Attente un seconde avant de faire le test de liaison serie
			try {
				Thread.sleep( 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if( DEBUG_S) {
				System.out.println( "On passe l'uart du GPS en 115200 bauds.");
			}
			// On adapte l'UART
			uartGPS_.setBaudRate( br);

			if( DEBUG_S) {
				System.out.println( "On fait dodo 1s en attendant que le port serie de la carte passe en 115200 bauds.");
			}
			// Attente un seconde avant de faire le test de liaison serie
			try {
				Thread.sleep( 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if( DEBUG_S) {
				System.out.println( "On lance un test de la liaison serie.");
			}
			// On test la liaison serie
			envoyerCommande( TEST_UART_S, null);

			if( DEBUG_S) {
				System.out.println( "On attend l'ACK de la liaison serie.");
			}
			// Analyse de la reponse du GPS
			ecouterReponse( TEST_UART_S);
		}
	}
	
	
	
	
	/**
	 * <b>ThreadTimeoutTestSerie permet de s'assurer que le débit de liaison série et du GPS sont 
	 * compatibles.</b>
	 * <p>Le thread change le débit de la liaison après un certain timeout si on n'a toujours rien reçu du GPS.</p>
	 * 
	 * @author Yoan DUMAS
	 * @version 1.1
	 */
	private class ThreadTimeoutTestSerie implements Runnable {

		@Override
		public void run() {
			try {
				// Initialisation du timeout permettant de deduire le nombre de bauds de la liaison serie
				Thread.sleep( 1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if( !gpsEn9600Bauds_) {
				if( DEBUG_S) {
					System.out.println( "ThreadTimeoutTestSerie : On passe l'uart en 115200 bauds.");
				}
				// On passe l'UART du GPS en 115200 bauds
				uartGPS_.setBaudRate( NBBAUDSGPSSOUHAITES_S);
				
				if( DEBUG_S) {
					System.out.println( "ThreadTimeoutTestSerie : On lance un test de la liaison serie.");
				}
				// On renvoi un test de liaison serie
				envoyerCommande( TEST_UART_S, null);
				
				if( DEBUG_S) {
					System.out.println( "ThreadTimeoutTestSerie : On ecoute la reponse du test de la liaison serie.");
				}
				// Analyse de la reponse du GPS dans la fonction appelante
				gpsEn115200Bauds_ = true;
			}
		}
		
	}
}
