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
 * <b>PostTraitement permet d'extraire les coordonn�es GPS d'un fichier s�rialis� et de les traiter.</b>
 * <p>Les posts traitements consistent � d�s�rialiser les {@link TrameGPS} du fichier, ceci fait
 * les coordonn�es GPS sont converties en degr�s d�cimaux et en coordonn�es cart�sienne et stock�es dans 
 * deux fichiers csv distincts.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSDegresDecimaux
 * @see telemetrieMoto.postTraitement.coordonnees.CoordonneesGPSLambert2
 */
public class PostTraitement {


	/** L'instance repr�sentant le fichier de configuration du projet � interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();

	
	/** 
	 * Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * <br><i>Sa valeur est stock�e dans le fichier de configuration du projet.</i> 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugPT"));

	
	/** Le descripteur du fichier sur lequel on doit �crire les trames re�ues. */
	private FileOutputStream fos_ = null;

	
	/** Le flux d'�criture d'objets associ�s au descripteur de fichier. */
	private ObjectOutputStream oos_ = null;




	/**
	 * Permet de s�rialiser la trame pass�e en param�tre en la concat�nant � la fin du fichier. 
	 * @param trame
	 * 				La trame � �crire.
	 * @param nomFic
	 * 				Le nom du fichier dans lequel on va s�rialiser la trame.
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
	 * Permet de d�s�rialiser les trames GPS stock�es dans le fichier.
	 * @param nomFic
	 * 					Le chemin du fichier contenant les trames s�rialis�es.
	 * @return La liste des trames GPS d�s�rialis�es.
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
	 * Permet de fermer le fichier dans lequel on a �crit les trames s�rialis�es.
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
	 * Le programme � ex�cuter pour traiter les trames s�rialis�es.
	 * Permet de cr�er un fichier csv contenant les trames acquises converties en degr�s minutes et en 
	 * coordonn�es cartesiennes.
	 * @param args
	 * 				Non utilis�.
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
	 * Permet d'�crire dans un fichier texte.
	 * @param aEcrire
	 * 					La chaine � �crire dans le fichier.
	 * @param nomFic
	 * 					Le nom du fichier dans lequel on veut �crire.
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
