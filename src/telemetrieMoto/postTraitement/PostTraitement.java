/*
 * PostTraitement.java
 * Permet d'effectuer les posts traitements pour le projet de telemetrie moto
 */

package telemetrieMoto.postTraitement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import telemetrieMoto.Proprietes;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;
import telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSDegresDecimaux;
import telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSLambert2;


/**
 * <b>PostTraitement permet d'extraire les coordonnées GPS d'un fichier sérialisé et de les traiter.</b>
 * <p>Les posts traitements consistent à désérialiser les {@link TrameGPS} du fichier, ceci fait
 * les coordonnées GPS sont converties en degrés décimaux et en coordonnées cartésienne et stockées dans 
 * deux fichiers csv distincts.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSDegresDecimaux
 * @see telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSLambert2
 */
public class PostTraitement {


	/** L'instance représentant le fichier de configuration du projet à interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();

	
	/** 
	 * Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stockée dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugPT"));

	
	/** Le descripteur du fichier sur lequel on doit écrire les trames reçues. */
	private FileOutputStream fos_ = null;

	
	/** Le flux d'écriture d'objets associés au descripteur de fichier. */
	private ObjectOutputStream oos_ = null;




	/**
	 * Permet de sérialiser la trame passée en paramètre en la concaténant à la fin du fichier. 
	 * @param trame
	 * 				La trame à écrire.
	 * @param nomFic
	 * 				Le nom du fichier dans lequel on va sérialiser la trame.
	 */
	public void ecrireTrameBin( TrameGPS trame, String nomFic) {
		try {
			if( fos_ == null && oos_ == null) {
				if( DEBUG_S) {
					System.out.println( "Creation du fichier de serialisation.");
				}
				// Ouverture d'un flux de sortie vers le fichier passe en parametre
				fos_ = new FileOutputStream( nomFic);
				// Creation d'un flux objet avec le flux fichier
				oos_ = new ObjectOutputStream( fos_);
			}
			// Serialisation : ecriture de l'objet dans le flux de sortie
			oos_.writeObject( trame);
			// On vide le tampon
			oos_.flush();
		} catch( IOException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		}
	}




	/**
	 * Permet de désérialiser les trames GPS stockées dans le fichier.
	 * @param nomFic
	 * 					Le chemin du fichier contenant les trames sérialisées.
	 * @return La liste des trames GPS désérialisées.
	 */
	public ArrayList< TrameGPS> lireTramesBin( String nomFic) {
		// La liste des trames GPS deserialisee
		ArrayList< TrameGPS> listeTrames = new ArrayList< TrameGPS>();

		try {
			// Ouverture du fichier de serialisation
			FileInputStream fis = new FileInputStream( nomFic);
			// Ouvertur du flux d'objets sur le fichier
			ObjectInputStream ois = new ObjectInputStream( fis);

			Object objetLu = null;
			do {
				// Lecture de l'objet
				objetLu = ois.readObject();
				if( objetLu != null && objetLu instanceof TrameGPS) {
					listeTrames.add( ( TrameGPS) objetLu);
				}
			} while( fis.available() > 0);

			// fermeture du flux et du fichier
			try {
				ois.close();
			} finally {
				fis.close();
			}
		} catch( IOException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		} catch( ClassNotFoundException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		}

		return listeTrames;
	}




	/**
	 * Permet de fermer le fichier dans lequel on a écrit les trames sérialisées.
	 */
	public void fermerFichier() {
		try {
			try {
				if( oos_ != null) {
					oos_.close();
				}
			} finally {
				if( fos_ != null) {
					fos_.close();
				}
			}
		} catch ( IOException e) {
			if( DEBUG_S) {
				e.printStackTrace();
			}
		} finally {
			oos_ = null;
			fos_ = null;
		}
	}




	/**
	 * Le programme à exécuter pour traiter les trames sérialisées.
	 * Permet de créer un fichier csv contenant les trames acquises converties en degrés minutes et en 
	 * coordonnées cartesiennes.
	 * @param args
	 * 				Non utilisé.
	 */
	public static void main( String args[]) {
		PostTraitement p = new PostTraitement();

		// On recupere toutes les trames lues
		ArrayList< TrameGPS> listeTrames = p.lireTramesBin( prop_s.getPropriete( "nomFichTramesBin"));

		// On ecrit leur conversion dans un fichier csv
		TrameGPS trame = null;
		CoordonneesGPSDegresDecimaux cdd = null;
		CoordonneesGPSLambert2 cl2 = null;
		for( Iterator< TrameGPS> it = listeTrames.iterator() ; it.hasNext() ; ) {
			trame = ( TrameGPS) it.next();
			cdd = new CoordonneesGPSDegresDecimaux( trame);
			cl2 = new CoordonneesGPSLambert2( cdd);
			ecrireDansFichierTxt( cdd.toString(), prop_s.getPropriete( "nomFichTramesCsv"));
			ecrireDansFichierTxt( cl2.toString(), prop_s.getPropriete( "nomFichTramesCsv2"));
		}
	}




	/**
	 * Permet d'écrire dans un fichier texte.
	 * @param aEcrire
	 * 					La chaine à écrire dans le fichier.
	 * @param nomFic
	 * 					Le nom du fichier dans lequel on veut écrire.
	 */
	public static void ecrireDansFichierTxt( String aEcrire, String nomFic) {
		// Le descripteur de fichier en ecriture
		FileWriter writer = null;
		// Le texte a ajouter au fichier
		String texte = aEcrire + '\n';

		try{
			try {
				// Ouverture du fichier
				writer = new FileWriter( nomFic, true);
				// On ajoute le texte a fichier
				writer.append( texte);
			} finally {
				// Fermeture du fichier
				writer.close();
			}
		} catch( IOException e){
			if( DEBUG_S) {
				e.printStackTrace();
			}
		}
	}
}
