/*
 * TrameGPS.java
 * Permet de representer une trame GPS
 */

package telemetrieMoto.acquisition.comm.gps;

import java.io.Serializable;

/**
 * <b>TrameGPS permet de représenter tous les types de trames GPS en ne conservant que les informations essentielles.</b>
 * <p>Une TrameGPS est donc caractérisée par :
 * <ul>
 * 	<li>Une latitude</li>
 * 	<li>Une longitude</li>
 * 	<li>Une heure</li>
 * </ul>
 * C'est une classe abstraite qui ne doit pas être instanciée directement.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPRMC
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPGGA
 * @see telemetrieMoto.acquisition.peripheriques.GPS
 */
public abstract class TrameGPS implements Serializable {


	/** L'identifiant de sérialisation. */
	private static final long serialVersionUID = 1L;


	/** La latitude que le GPS nous envoie. */
	private String latitude_;
	
	
	/** La longitude que le GPS nous envoie. */
	private String longitude_;
	
	
	/** L'heure UTC exprimée au format : hhmmss.sss. */
	private float heure_;
	
	
	
	
	/**
	 * Permet de convertir la trame GPS en chaine de caractères.
	 * @return La trame GPS convertie en chaine de caractères.
	 */
	public abstract String toString();
	
	
	
	
	/**
	 * Permet de savoir si la trame GPS reçue est valide.
	 * @return true, si la trame est valide
	 * 		   false, si la trame n'est pas valide.
	 */
	public abstract boolean estValide();
	
	
	
	
	/**
	 * Permet de comparer deux trames GPS.
	 * <p>Deux trames GPS sont considérées comme égales si :
	 * <ul>
	 * 	<li>Elles sont de même type <i>(GPGGA / GPRMC)</i></li>
	 * 	<li>Tous leurs attributs sont égaux</li>
	 * </ul>
	 * </p>
	 * @param trame
	 * 				La trame à comparer avec la trame courante.
	 * @return true si les deux trames sont identiques,
	 * 		   false sinon.
	 */
	public abstract boolean equals( TrameGPS trame);
	
	
	
	
	/**
	 * Retourne la latitude de la trame GPS.
	 * @return La latitude de la trame GPS.
	 */
	public String getLatitude() {
		return latitude_;
	}




	/**
	 * Retourne la longitude de la trame GPS.
	 * @return La longitude de la trame GPS.
	 */
	public String getLongitude() {
		return longitude_;
	}




	/**
	 * Retourne l'heure où la trame GPS a été acquise.
	 * @return L'heure où la trame GPS a été acquise.
	 */
	public float getHeure() {
		return heure_;
	}




	/**
	 * Permet de modifier la latitude de la coordonnée GPS.
	 * @param latitude
	 * 					La nouvelle latitude de la coordonnée GPS.
	 */
	public void setLatitude( String latitude) {
		latitude_ = latitude;
	}




	/**
	 * Permet de modifier la longitude de la coordonnée GPS.
	 * @param longitude
	 * 					La nouvelle longitude de la coordonnée GPS.
	 */
	public void setLongitude( String longitude) {
		longitude_ = longitude;
	}




	/**
	 * Permet de modifier l'heure de l'acquisition de la trame GPS.
	 * @param heure
	 * 					La nouvelle heure de l'acquisition de la trame GPS.
	 */
	public void setHeure( float heure) {
		heure_ = heure;
	}
	
	
}
