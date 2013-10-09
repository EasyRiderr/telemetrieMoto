/*
 * TelemetrieMoto.java
 * Permet d'executer le programme de telemetrie moto
 * Gere le GPS et envoie ses donnees en UDP
 */

package telemetrieMoto.acquisition;

import telemetrieMoto.Proprietes;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;
import telemetrieMoto.acquisition.comm.udp.Emetteur;
import telemetrieMoto.acquisition.peripheriques.GPS;


/**
 * <b>TelemetrieMoto est la classe à exécuter pour lancer l'acquisition des données de télémétrie moto.</b>
 * <p>L'acquisition des données de télémétrie moto consiste en :
 * <ul>
 * 	<li>L'acquisition des donées GPS</li>
 * 	<li>L'envoi des données <i>(Emetteur)</i> au PC en bord de piste <i>(Recepteur)</i></li>
 * </ul>
 * Suivant ce qui est définit dans le fichier de configuration, elle permet d'afficher une trace de l'exécution du programme.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.Proprietes
 * @see telemetrieMoto.acquisition.comm.udp.Emetteur
 * @see telemetrieMoto.acquisition.comm.udp.Recepteur
 * @see telemetrieMoto.acquisition.peripheriques.GPS
 */
public class TelemetrieMoto {

	/** La variable du fichier de configuration permettant de savoir si on doit afficher une trace de l'exécution du progamme. */
	private static final boolean DEBUG_S = Boolean.valueOf( Proprietes.getInstance().getPropriete( "DebugTM"));


	/**
	 * Permet d'executer le programme de telemetrie moto
	 */
	public static void main( String[] args) {
		// Instanciation du GPS de la carte
		GPS gps = new GPS();
		gps.initGPS();

		// Creation de l'Emetteur des donnees
		Emetteur em = new Emetteur();

		// La trame GPS lue a envoyer au recepteur si elle valide
		TrameGPS trame;

		// On envoie les trames GPS au recepteur
		while( true){
			// Lecture et analyse de la trame
			trame = gps.lireTrameGPS();
			if( trame != null && trame.estValide()) {
				if( DEBUG_S) {
					System.out.println( "On envoie une trame valide !");
				}
				em.envoyerMessage( trame);
			} else {
				em.envoyerMessage( "/!\\Trame recue non valide :s !");
			}
		}
	}

}
