/*
 * CoordonneesGPSDegresDecimaux.java
 * Permet de representer des coordonnees GPS avec la latitude et la longitude en Degres Decimaux
 */

package telemetrieMoto.postTraitement.coordonnees;

import telemetrieMoto.acquisition.comm.gps.TrameGPGGA;
import telemetrieMoto.acquisition.comm.gps.TrameGPS;




/**
 * <b>CoordonneesGPSDegresDecimaux permet de repr�senter une coordonn�e GPS en utilisant 
 * les degr�s d�cimaux.</b>
 * <p>La classe propose un syst�me de conversion des trames au format WGS84 au format degr�s d�cimaux.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 */
public class CoordonneesGPSDegresDecimaux {


	/** La latitude exprim�e en degr�s d�cimaux. */
	private double latitude_;


	/** La longitude exprim�e en degr�s d�cimaux. */
	private double longitude_;
	
	
	/** L'altitude exprim�e en m�tres. */
	private double altitude_;




	/**
	 * Le constructeur de coordonn�es GPS en repr�sentation degr�s d�cimaux.
	 * @param latitude
	 * 					La latitude de la coordonn�e en degr�s d�cimaux.
	 * @param longitude
	 * 					La longitude de la coordonn�e en degr�s d�cimaux.
	 * @param altitude
	 * 					L'altitude de la coordonn�e en m�tres.
	 */
	public CoordonneesGPSDegresDecimaux( double latitude, double longitude, double altitude) {
		latitude_ = latitude;
		longitude_ = longitude;
		altitude_ = altitude;
	}




	/**
	 * Construit une coordonn�e GPS en repr�sentation degr�s d�cimaux � partir d'une {@link TrameGPS} 
	 * au format WGS84.
	 * @param trame
	 * 				La {@link TrameGPS} � partir de laquelle on va extraire la coordonn�e.
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
	 * Retourne la coordonn�e GPS sous forme <i>latitude;longitude;altitude</i>.
	 * @return La coordonn�e GPS sous forme <i>latitude:longitude;altitude</i>.
	 */
	public String toString() {
		return latitude_ + ";" + longitude_ + ";" + altitude_;
	}




	/**
	 * Permet d'obtenir la valeur enti�re de v.
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
