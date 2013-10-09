/*
 * TestConversionCoordonneesGPS.java
 * Permet d'effectuer les tests de conversion de coordonnees GPS
 */

package telemetrieMoto.postTraitement.coordonnees;

import static org.junit.Assert.*;

import org.junit.Test;

import telemetrieMoto.acquisition.comm.gps.TrameGPRMC;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;




/**
 * <b>TestConversionCoordonneesGPS permet de tester la validité des conversions de {@link TrameGPS} aux
 * différents formats.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 */
public class TestConversionCoordonneesGPS {

	
	
	
	/**
	 * Permet de vérifier que la conversion des coordonnées GPS WGS84 vers des coordonnées en degrés 
	 * décimaux fonctionne.
	 */
	@Test
	public void testConversionWGS84VersDegresDecimaux() {
		// Creation d'une trame GPS valide de test
		final TrameGPS trame = new TrameGPRMC( "4545.5635,N", "00306.6789,E", 164715.f, 'A', 280213);
		
		// Les resultats attendus
		final double latDDAttendue = 45.7594;
		final double lonDDAttendue = 3.1113;
		final double erreur = 0.0001;
		
		
		// Conversion en degres decimaux
		CoordonneesGPSDegresDecimaux cdd = new CoordonneesGPSDegresDecimaux( trame);
		
		// Verification du resultat
		assertTrue( "Erreur de conversion sur la latitude du WGS84 vers le DD.", Math.abs( cdd.getLatitude() - latDDAttendue) < erreur);
		assertTrue( "Erreur de conversion sur la longitude du WGS84 vers le DD.", Math.abs( cdd.getLongitude() - lonDDAttendue) < erreur);
	}
	
	
	
	
	/**
	 * Permet de vérifier que la conversion des coordonnées GPS en degrés décimaux vers des coordonnées 
	 * cartésiennes fonctionne.
	 */
	@Test
	public void testConversionDDVersCartesien() {
		// Creation d'une trame GPS valide de test
		final TrameGPS trame = new TrameGPRMC( "4545.5635,N", "00306.6789,E", 164715.f, 'A', 280213);
		
		// Conversion de la trame en degres decimaux
		CoordonneesGPSDegresDecimaux cdd = new CoordonneesGPSDegresDecimaux( trame);
		
		// Puis conversion en coordonnees cartesiennes
		CoordonneesGPSLambert2 cl2 = new CoordonneesGPSLambert2( cdd);
		
		// Les resultats attendus ==> http://www.telegonos.fr/convertir-avec-carte
		final double x = 660276.854;
		final double y = 84639.891;
		final double erreur = 5;
		
		//System.out.println( "x = " + cl2.getX() + " / diff = " + Math.abs( cl2.getX() - x));
		//System.out.println( "y = " + cl2.getY() + " / diff = " + Math.abs( cl2.getY() - y));
		
		// Verification du resultat
		assertTrue( "Erreur de conversion sur l'abcisse du DD vers le cartesien.", Math.abs( cl2.getX() - x) < erreur);
		assertTrue( "Erreur de conversion sur l'ordonnee du DD vers le cartesien.", Math.abs( cl2.getY() - y) < erreur);
	}
}