/*
 * TestTrameGPS.java
 * Permet de tester les operations sur les trames GPS
 */

package telemetrieMoto.acquisition.comm.gps;

import static org.junit.Assert.*;

import org.junit.Test;



/**
 * <b>TestTrameGPS permet de tester les trames GPS.</b>
 * <p>TestTrameGPS teste la validité et la comparaison des trames GPS : GPRMC et GPGGA.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPGGA
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPRMC
 */
public class TestTrameGPS {

	
	
	/**
	 * Permet de tester la validité des trames GPGGA.
	 * <br>Une trame GPGGA est considerée comme valide si :
	 * <ul>
	 * 	<li>la dilution horizontale est inférieure à une valeur définie dans le fichier de configuration</li>
	 * 	<li>le nombre de satellite est supérieur à une valeur définie dans le fichier de configuration</li>
	 * </ul>
	 */
	@Test
	public void testValiditeTrameGPGGA() {
		// Creation des trames de test
		TrameGPS trameGPGGAValide = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 6, ( float) 3.2, ( float) 200.2);
		TrameGPS trameGPGGANonValideNbSat = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 2, ( float) 3.2, ( float) 200.2);
		TrameGPS trameGPGGANonValideHdop = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 6, ( float) 13.2, ( float) 200.2);
		TrameGPS trameGPGGANonValideNbSatHdop = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 2, ( float) 13.2, ( float) 200.2);
		
		// On verifie que la premiere trame soit valide
		assertTrue( trameGPGGAValide.estValide());
		// Et que toutes les autres soient non valides
		assertFalse( trameGPGGANonValideNbSat.estValide());
		assertFalse( trameGPGGANonValideHdop.estValide());
		assertFalse( trameGPGGANonValideNbSatHdop.estValide());
	}
	
	
	
	
	/**
	 * Permet de tester la validite des trames GPRMC.
	 * <br>Une trame GPRMC est valide si son indicateur de validité est égale à 'A'
	 */
	@Test
	public void testValiditeTrameGPRMC() {
		// Creation des trames a tester
		TrameGPS trameGPRMCValide = new TrameGPRMC( "4836.5375,N", "00740.9373,E", ( float) 053740.000, 'A', 100106);
		TrameGPS trameGPRMCNonValide = new TrameGPRMC( "4836.5375,N", "00740.9373,E", ( float) 053740.000, 'V', 100106);
		
		// La premiere trame doit etre valide
		assertTrue( trameGPRMCValide.estValide());
		// La seconde ne doit pas etre valide
		assertFalse( trameGPRMCNonValide.estValide());
	}
	
	
	
	
	/**
	 * Permet de tester l'égalite de deux trames GPS.
	 * <p>Deux trames GPS sont considérées comme égales si :
	 * <ul>
	 * 	<li>Elles sont de même type <i>(GPGGA / GPRMC)</i></li>
	 * 	<li>Tous leurs attributs sont égaux</li>
	 * </ul>
	 * </p>
	 */
	@Test
	public void testEgaliteTrames() {
		// Creation des trames a tester
		TrameGPS trameGPGGA1 = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 6, ( float) 3.2, ( float) 200.2);
		TrameGPS trameGPGGA2 = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 6, ( float) 3.2, ( float) 200.2);
		TrameGPS trameGPGGA3 = new TrameGPGGA( "4836.5375,S", "70400.9373,E", ( float) 64036.289, 7, ( float) 5.2, ( float) 200.4);
		TrameGPS trameGPRMC1 = new TrameGPRMC( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 'A', 100106);
		TrameGPS trameGPRMC2 = new TrameGPRMC( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 'A', 100106);
		TrameGPS trameGPRMC3 = new TrameGPRMC( "4836.5375,S", "70400.9373,E", ( float) 64036.289, 'A', 100106);
		
		// Les trames GPGGA 1 et 2 sont identiques
		assertTrue( trameGPGGA1.equals( trameGPGGA2));
		// Idem pour les trames GPRMC
		assertTrue( trameGPRMC1.equals( trameGPRMC2));
		
		// Les trames GPGGA 1 et 3 sont differentes
		assertFalse( trameGPGGA1.equals( trameGPGGA3));
		// Idem pour les trames GPRMC
		assertFalse( trameGPRMC1.equals( trameGPRMC3));
		
		// Une trame GPGGA et une trame GPRMC ne doivent pas etre consideree comme equals
		assertFalse( trameGPGGA1.equals( trameGPRMC1));
	}

}
