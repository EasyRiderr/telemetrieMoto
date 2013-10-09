/*
 * TrameGPS.java
 * Permet de representer une trame GPS
 */

package telemetrieMoto.acquisition.comm.gps;

import java.io.Serializable;

/**
 * <b>TrameGPS permet de repr�senter tous les types de trames GPS en ne conservant que les informations essentielles.</b>
 * <p>Une TrameGPS est donc caract�ris�e par :
 * <ul>
 * 	<li>Une latitude</li>
 * 	<li>Une longitude</li>
 * 	<li>Une heure</li>
 * </ul>
 * C'est une classe abstraite qui ne doit pas �tre instanci�e directement.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPRMC
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPGGA
 * @see telemetrieMoto.acquisition.peripheriques.GPS
 */
public abstract class TrameGPS implements Serializable {


	/** L'identifiant de s�rialisation. */
	private static final long serialVersionUID = 1L;


	/** La latitude que le GPS nous envoie. */
	private String latitude_;
	
	
	/** La longitude que le GPS nous envoie. */
	private String longitude_;
	
	
	/** L'heure UTC exprim�e au format : hhmmss.sss. */
	private float heure_;
	
	
	
	
	/**
	 * Permet de convertir la trame GPS en chaine de caract�res.
	 * @return La trame GPS convertie en chaine de caract�res.
	 */
	public abstract String toString();
	
	
	
	
	/**
	 * Permet de savoir si la trame GPS re�ue est valide.
	 * @return true, si la trame est valide
	 * 		   false, si la trame n'est pas valide.
	 */
	public abstract boolean estValide();
	
	
	
	
	/**
	 * Permet de comparer deux trames GPS.
	 * <p>Deux trames GPS sont consid�r�es comme �gales si :
	 * <ul>
	 * 	<li>Elles sont de m�me type <i>(GPGGA / GPRMC)</i></li>
	 * 	<li>Tous leurs attributs sont �gaux</li>
	 * </ul>
	 * </p>
	 * @param trame
	 * 				La trame � comparer avec la trame courante.
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
	 * Retourne l'heure o� la trame GPS a �t� acquise.
	 * @return L'heure o� la trame GPS a �t� acquise.
	 */
	public float getHeure() {
		return heure_;
	}




	/**
	 * Permet de modifier la latitude de la coordonn�e GPS.
	 * @param latitude
	 * 					La nouvelle latitude de la coordonn�e GPS.
	 */
	public void setLatitude( String latitude) {
		latitude_ = latitude;
	}




	/**
	 * Permet de modifier la longitude de la coordonn�e GPS.
	 * @param longitude
	 * 					La nouvelle longitude de la coordonn�e GPS.
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
