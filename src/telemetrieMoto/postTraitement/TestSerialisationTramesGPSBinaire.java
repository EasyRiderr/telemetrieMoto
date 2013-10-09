/*
 * TestSerialisationTramesGPSBinaire.java
 * Permet de tester la serialisation des trames GPS en binaire
 */

package telemetrieMoto.postTraitement;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import telemetrieMoto.acquisition.comm.gps.TrameGPGGA;
import telemetrieMoto.acquisition.comm.gps.TrameGPRMC;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;




/**
 * <b>TestSerialisationTramesGPSBinaire permet de tester la sérialisation des {@link TrameGPS}.</b>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.postTraitement.PostTraitement
 */
public class TestSerialisationTramesGPSBinaire {

	
	
	
	/**
	 * Test le mécanisme de sérialisation des {@link TrameGPS}. Vérifie que la sérialisation des trames s'exécute 
	 * correctement. Désérialise les trames et vérifie qu'elle n'ai pas été altérées. Une fois les 
	 * tests terminés, le fichier de test est supprimé.
	 */
	@Test
	public void test() {
		// Les trames a serialiser
		TrameGPS t1 = new TrameGPGGA( "4836.5375,N", "00740.9373,E", ( float) 64036.289, 6, ( float) 3.2, ( float) 200.2);
		TrameGPS t2 = new TrameGPRMC( "3648.5375,N", "74000.9373,E", ( float) 053740.000, 'A', 100106);

		// La classe de post traitement qui va nous permettre de serialiser et deserialiser les trames
		PostTraitement p = new PostTraitement();

		// On serialise les trames
		p.ecrireTrameBin( t1, "fichierTest.serial");
		p.ecrireTrameBin( t2, "fichierTest.serial");

		// On ferme le fichier
		p.fermerFichier();

		// Lecture des trames
		ArrayList< TrameGPS> listeTramesLues = p.lireTramesBin( "fichierTest.serial");

		// On supprime le fichier
		File ficAsuppr = new File( "fichierTest.serial");
		ficAsuppr.delete();
		
		// La liste de trame lues ne doit pas etre nulle
		assertNotNull( "La liste de trames lues est nulle", listeTramesLues);
		// On verifie que l'on ai bien lu les deux trames serialisees
		assertEquals( "Taille de la liste des trames lues", 2, listeTramesLues.size());
		
		// On verifie que les trames lues sont egales aux trames enregistrees
		assertTrue( "La serialisation de la premiere trame est erronee", listeTramesLues.get( 0).equals( t1));
		assertTrue( "La serialisation de la seconde trame est erronee", listeTramesLues.get( 1).equals( t2));
	}

}
