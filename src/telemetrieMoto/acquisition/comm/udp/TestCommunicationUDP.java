/*
 * TestCommunicationUDP.java
 * Permet de tester la communication en UDP entre le Recepteur et l'Emetteur
 */

package telemetrieMoto.acquisition.comm.udp;

import static org.junit.Assert.*;

import org.junit.Test;

import telemetrieMoto.acquisition.comm.gps.TrameGPGGA;
import telemetrieMoto.acquisition.comm.gps.TrameGPRMC;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;




/**
 * <b>TestCommunicationUDP permet de tester la communication entre le {@link Recepteur} et l'{@link Emetteur}.</b>
 * <p>Les tests consistent en l'envoie de {@link TrameGPS} avec échange d'acquittements pour vérifier que
 * la communication se passe bien dans les deux sens. De plus un test d'intégrité des messages envoyés est effectué.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see Emetteur
 * @see Recepteur
 */
public class TestCommunicationUDP {




	/**
	 * Permet de tester la communication UDP entre le {@link Recepteur} et l'{@link Emetteur}.
	 */
	@Test
	public void test() {
		// Creation du Recepteur et de l'emetteur
		Recepteur pcBordDePiste = new Recepteur();
		Emetteur carteRPi = new Emetteur();

		// Creation des trames GPS a envoyer
		TrameGPS trameGPGGA = new TrameGPGGA( "$GPGGA,064036.289,4836.5375,N,00740.9373,E,1,04,3.2,200.2,M,,,,,0000*0E");
		TrameGPRMC trameGPRMC = new TrameGPRMC( "3648.5375,N", "74000.9373,E", ( float) 053740.000, 'A', 100106);

		// Envoie de la trame GPGGA
		carteRPi.envoyerMessage( trameGPGGA);

		// On veriifie que le trame recue correspond bien
		assertTrue( pcBordDePiste.ecouterCarte().equals( trameGPGGA));

		// Attente de l'ACK du recepteur
		carteRPi.ecouterACK();
		
		// Memes operations pour la trame GPRMC
		carteRPi.envoyerMessage( trameGPRMC);
		assertTrue( pcBordDePiste.ecouterCarte().equals( trameGPRMC));
		carteRPi.ecouterACK();
		
		// On test que l'on soit bien sortit de l'attente
		assertTrue( true);
	}

}
