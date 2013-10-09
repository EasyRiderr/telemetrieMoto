/*
 * CoordonneesGPSDegresDecimaux.java
 * Permet de representer des coordonnees GPS avec la latitude et la longitude en Degres Decimaux
 */

package telemetrieMoto.postTraitement.coordonnees;

import telemetrieMoto.acquisition.comm.gps.TrameGPGGA;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;




/**
 * <b>CoordonneesGPSDegresDecimaux permet de représenter une coordonnée GPS en utilisant 
 * les degrés décimaux.</b>
 * <p>La classe propose un système de conversion des trames au format WGS84 au format degrés décimaux.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 */
public class CoordonneesGPSDegresDecimaux {


	/** La latitude exprimée en degrés décimaux. */
	private double latitude_;


	/** La longitude exprimée en degrés décimaux. */
	private double longitude_;
	
	
	/** L'altitude exprimée en mètres. */
	private double altitude_;




	/**
	 * Le constructeur de coordonnées GPS en représentation degrés décimaux.
	 * @param latitude
	 * 					La latitude de la coordonnée en degrés décimaux.
	 * @param longitude
	 * 					La longitude de la coordonnée en degrés décimaux.
	 * @param altitude
	 * 					L'altitude de la coordonnée en mètres.
	 */
	public CoordonneesGPSDegresDecimaux( double latitude, double longitude, double altitude) {
		latitude_ = latitude;
		longitude_ = longitude;
		altitude_ = altitude;
	}




	/**
	 * Construit une coordonnée GPS en représentation degrés décimaux à partir d'une {@link TrameGPS} 
	 * au format WGS84.
	 * @param trame
	 * 				La {@link TrameGPS} à partir de laquelle on va extraire la coordonnée.
	 */
	public CoordonneesGPSDegresDecimaux( TrameGPS trame) {
		// La longitude et latitude a transformer
		latitude_ = Double.valueOf( trame.getLatitude().substring( 0, trame.getLatitude().length() - 2));
		longitude_ = Double.valueOf( trame.getLongitude().substring( 0, trame.getLongitude().length() - 2));

		if( trame.getLatitude().charAt( trame.getLatitude().length() - 1) == 'S') {
			latitude_ *= - 1;
		}
		if( trame.getLongitude().charAt( trame.getLongitude().length() - 1) == 'W') {
			longitude_ *= -1;
		}

		// Conversion en degres minutes
		double degres = partieEntiere( latitude_ / 100.0f);
		double minutes = latitude_ - ( degres * 100.0f);

		latitude_ = degres + minutes / 60.0f;
		degres = partieEntiere( longitude_ / 100.0f);
		minutes = longitude_ - ( degres * 100.0f);
		longitude_ = degres + minutes / 60.0f;
		
		// Recherche de l'altitude du point
		if( trame instanceof TrameGPGGA) {
			altitude_ = ( ( TrameGPGGA) trame).getAltitude();
		} else {
			altitude_ = 0.d;
		}
	}




	/**
	 * Retourne la coordonnée GPS sous forme <i>latitude;longitude;altitude</i>.
	 * @return La coordonnée GPS sous forme <i>latitude:longitude;altitude</i>.
	 */
	public String toString() {
		return latitude_ + ";" + longitude_ + ";" + altitude_;
	}




	/**
	 * Permet d'obtenir la valeur entière de v.
	 * @param v
	 * 			Le nombre dont on veut l'arrondie entier.
	 * @return L'arrondi entier de v.
	 */
	public static double partieEntiere( double v) {
		float res;
		if( v < 0.0) {
			v *= -1.0;
			res = (float) Math.floor( v);
			res *= -1.0;
		} else {
			res = (float) Math.floor( v);
		}
		return res;
	}




	/**
	 * Retourne la latitude.
	 * @return La latitude.
	 */
	public double getLatitude() {
		return latitude_;
	}




	/**
	 * Retourne la longitude.
	 * @return La longitude.
	 */
	public double getLongitude() {
		return longitude_;
	}




	/**
	 * Retourne l'altitude.
	 * @return L'altitude.
	 */
	public double getAltitude() {
		return altitude_;
	}
}
