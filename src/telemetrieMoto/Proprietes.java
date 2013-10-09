/*
 * Proprietes.java
 * Singleton permettant de charger le fichier de configuration du projet une seule fois
 * afin qu'il soit accessible partout
 */

package telemetrieMoto;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * <b>Propriete est la classe représentant le fichier de configuration contenant toutes les proprétés du projet.</b>
 * <p>Elle permet d'accéder au fichier de configuration du projet une unique fois au démarrage de l'application (<i>singleton</i>).
 * Ceci pour éviter de ralentir l'exécution au démarrage du programme.<br>
 * On utilise la classe Properties pour lire le fichier de configuration.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see java.util.Properties
 */
public class Proprietes {


	/** L'unique instance de la classe Proprietes. */
	private static Proprietes instance_s = null;


	/** Le fichier de configuration du projet. */
	private static Properties fichierConf_s;




	/**
	 * Le constructeur de Proprietes. 
	 * <p> Permet de charger le fichier de configuration : "<i>conf.properties</i>". Ce fichier sera utilisé par la classe Propriete
	 * pour récupérer la configuration du projet.</p>
	 * @see java.util.Properties
	 */
	private Proprietes() {
		fichierConf_s = new Properties();
		// Chargement du fichier
		try {
			FileInputStream in = null;
			try {
				in = new FileInputStream( "conf.properties");
				fichierConf_s.load( in);
			} finally {
				in.close();
			}
		} catch( IOException e) {
			System.err.println( "ERREUR : Impossible de charger le fichier de configuration !");
		} 
	}





	/**
	 * Retourne une instance de Proprietes
	 * @return L'instance de Proprietes
	 */
	public static Proprietes getInstance() {
		if( instance_s == null) {
			synchronized( Proprietes.class) {
				if( instance_s == null) {
					instance_s = new Proprietes();
				}
			}
		}

		return instance_s;
	}




	/**
	 * Retourne la valeur de la clé passée en paramètre.
	 * @param cle
	 * 				La propriété dont on veut connaître la valeur.
	 * @return La valeur correspondant a la clé passée en paramètre.
	 */
	public String getPropriete( String cle) {
		return fichierConf_s.getProperty( cle);
	}
}