/*
 * CoordonneesGPSLambert2.java
 * Permet de representer des coordonnees GPS avec la representation Lambert 2
 */

package telemetrieMoto.postTraitement.coordonnees;





/**
 * <b>CoordonneesGPSLambert2 permet de représenter des coordonnée GPS sous forme cartesienne.</b>
 * <p>La classe permet la conversion des trames GPS au format WGS84 sous forme cartésienne grâce à 
 * la projection de <b>Lambert II</b>.</p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 */
public class CoordonneesGPSLambert2 {

	
	/** La coordonnée cartésienne sur l'axe des x. */
	private double x_;
	
	
	/** La coordonnée cartésienne sur l'axe des y. */
	private double y_;
	
	
	/** La coordonnée cartésienne sur l'axe des z. */
	private double z_;
	
	
	
	
	/**
	 * Le constructeur de coordonnées GPS en représentation <b>Lambert II</b>.
	 * @param x 
	 * 			La coordonnée cartésienne sur l'axe des x.
	 * @param y
	 * 			La coordonnée cartésienne sur l'axe des y.
	 * @param z
	 * 			La coordonnée cartésienne sur l'axe des z.
	 */
	public CoordonneesGPSLambert2( double x, double y, double z) {
		x_ = x;
		y_ = y;
		z_ = z;
	}
	
	
	
	/* 50m d'erreur sur x, 5m d'err sur y
	Ecart type sur x et sur y plus ou moins 5m
	//Methode Peter*/
	/**
	 * Permet de construire une coordonée GPS sous forme cartésienne à partir d'une coordonnée exprimée
	 * en degrés décimaux.
	 * @param cgdd
	 * 				La coordonnée GPS exprimée en degrés décimaux.
	 */
	public CoordonneesGPSLambert2( CoordonneesGPSDegresDecimaux cgdd) {
		double a, e2, sinFi, cosFi, N;
		double longi, lati, alti;
		
		longi = Radians( cgdd.getLongitude());
		lati = Radians( cgdd.getLatitude());
		alti = cgdd.getAltitude();
		
		a = 6378137.0;
		e2 = Math.pow( 0.08181919112, 2);
		
		sinFi = Math.sin( lati);
		cosFi = Math.cos( lati);
		
		N = a / Math.sqrt( 1 - e2 * sinFi * sinFi );
		
		x_  = ( N + alti) * cosFi * Math.cos( longi) + 168.0;
        y_  = ( N + alti) * cosFi * Math.sin( longi) + 60.0;
        z_  = ( N * ( 1 - e2) + alti) * sinFi - 320.0;
		
		
        // Passage en plane ne fonctionne pas
        double L, tmp;

		L = Math.log( Math.tan( Math.PI / 4.0 + lati / 2.0) * Math.pow( ( 1 - 0.08248325676 * Math.sin( lati)) / ( 1 + 0.08248325676 * Math.sin( lati)), 
				( 0.08248325676 / 2.0)) );

		tmp = 11745793.393416170 * Math.exp( - 0.728968627421412 * L);

		// Passage en plane
		x_ = 600000.d + tmp * Math.sin( 0.728968627421412 * ( longi - 0.040792344331977));
		y_ = 6199695.76801151690 - tmp * Math.cos( 0.728968627421412 * ( longi - 0.040792344331977));
	}
	
	
	
	
	/**
	 * Permet de convertir un angle en degrés en radians.
	 * @param x
	 * 			L'angle en degrés.
	 * @return L'angle en radians.
	 */
	public static double Radians( double x) { 
		return ( ( x * Math.PI) / 180.0); 
	}
	
	
	
	
	/**
	 * Permet de convertir un angle en radians en degrés.
	 * @param x
	 * 			L'angle en radians.
	 * @return L'angle en degrés.
	 */
	public static double Degres( double x) { 
		return ( ( x * 180.0) / Math.PI); 
	}	
	
	
	
	
	/**
	 * Permet d'afficher la coordonnée GPS sous forme <i>x;y;z</i>.
	 * @return La coordonnée GPS sous forme <i>x;y;z</i>.
	 */
	public String toString() {
		return x_ + ";" + y_ + ";" + z_;
	}




	/**
	 * Retourne l'abscisse de la coordonnée cartésienne.
	 * @return L'abscisse de la coordonnée cartésienne.
	 */
	public double getX() {
		return x_;
	}




	/**
	 * Retourne l'ordonnée de la coordonnée cartésienne.
	 * @return L'ordonnée de la coordonnée cartésienne.
	 */
	public double getY() {
		return y_;
	}




	/**
	 * Retourne la hauteur de la coordonée cartésienne.
	 * @return La hauteur de la coordonée cartésienne.
	 */
	public double getZ() {
		return z_;
	}
}
