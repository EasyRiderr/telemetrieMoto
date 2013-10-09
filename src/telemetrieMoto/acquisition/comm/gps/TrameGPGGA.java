/*
 * TrameGPGGA.java
 * Permet de representer une trame GPS de type GPGGA
 */

package telemetrieMoto.acquisition.comm.gps;

import telemetrieMoto.Proprietes;


/**
 * <b>TrameGPGGA permet de repr�senter une trame GPS de type GPGGA.</b>
 * <p>Une trame GPS GPGGA est caract�ris�e par les informations suivantes :
 * <ul>
 * 	<li>Une latitude</li>
 * 	<li>Une longitude</li>
 * 	<li>Une heure</li>
 * 	<li>Un nombre de satellite</li>
 * 	<li>Une dilution horizontale</li>
 * 	<li>Une altitude</li>
 * </ul>
 * Si le <i>GPS</i> ne nous envoie pas une de ces informations la trame sera nulle.
 * </p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPRMC
 * @see telemetrieMoto.acquisition.peripheriques.GPS
 */
public class TrameGPGGA extends TrameGPS {


	/** L'identifiant de s�rialisation. */
	private static final long serialVersionUID = 1L;


	/** Le nombre de satellite que l'on capte lors de l'acquisition. */
	private int nbSat_;


	/** La dilution horizontale subie par la trame GPS. */
	private float HDOP_;


	/** L'altitude associ�e � la coordonn�e GPS. */
	private float altitude_;


	/** L'instance repr�sentant le fichier de configuration du projet � interroger. */
	private static Proprietes prop_s = Proprietes.getInstance();


	/** La dilution horizontale max que l'on accepte. 
	 * Sa valeur est stock�e dans le fichier de configuration du projet. */
	private static final float HDOPMAX_S = Float.valueOf( prop_s.getPropriete( "HdopMax"));


	/** Le nombre de satellites min a partir duquel on decide que la trame GPS est valide.
	 * Sa valeur est stock�e dans le fichier de configuration du projet. */
	private static final int NBSATMIN_S = Integer.valueOf( prop_s.getPropriete( "NbSatellitesMin"));

	
	/** Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * Sa valeur est stock�e dans le fichier de configuration du projet. 
	 */
	private static final boolean DEBUG_S = Boolean.valueOf( prop_s.getPropriete( "DebugGPS"));




	/**
	 * Le constructeur de trame GPGGA.
	 * @param latitude
	 * 					La latitude de la coordonn�e GPGGA.
	 * @param longitude
	 * 					La longitude de la coordonn�e GPGGA.
	 * @param heure
	 * 					L'heure UTC de l'acquisition de la trame GPGGA.
	 * @param nbSat
	 * 					Le nombre de satellite que le GPS a capt� lors de l'acquisition.
	 * @param HDOP
	 * 					La dilution horizontale subie par la trame GPGGA.
	 * @param altitude
	 * 					L'altitude associ�e a la coordonn�e GPGGA.
	 */
	public TrameGPGGA( String latitude, String longitude, float heure, int nbSat, float HDOP, float altitude) {
		setLatitude( latitude);
		setLongitude( longitude);
		setHeure( heure);
		nbSat_ = nbSat;
		HDOP_ = HDOP;
		altitude_ = altitude;
	}




	/**
	 * Le constructeur de trame GPGGA � partir d'une chaine de caract�res.
	 * @param trameGPSLue
	 * 						La trame brute re�ue du GPS
	 */
	public TrameGPGGA( String trameGPSLue) {
		if( trameGPSLue != null) {

			// On se debarasse de l'entete de la trame
			String chaineAParser = trameGPSLue.substring( trameGPSLue.indexOf( ',') + 1);

			String chaineParsee[] = chaineAParser.split( ",");

			if( chaineParsee.length >= 8) {
				try {

					// Extraction des informations utiles
					setHeure( Float.valueOf( chaineParsee[ 0]));
					setLatitude( chaineParsee[ 1] + "," + chaineParsee[ 2]);
					setLongitude( chaineParsee[ 3] + "," + chaineParsee[ 4]);
					nbSat_ = Integer.valueOf( chaineParsee[ 6]);
					HDOP_ = Float.valueOf( chaineParsee[ 7]);
					altitude_ = Float.valueOf( chaineParsee[ 8]);

				} catch( NumberFormatException e) {
					if( DEBUG_S) {
						System.err.println( "Erreur : trame incomplete!");
					}
					setHeure( 0);
					setLatitude( "");
					setLongitude( "");
					nbSat_ = 0;
					HDOP_ = 0;
					altitude_ = 0;
				}
			}
		}
	}




	/**
	 * Retourne la trame GPGGA en chaine de caract�res.
	 * <p>La chaine de caract�res aura le format : "Latitude = <i>...</i>, longitude = <i>...</i>, 
	 * heure = <i>...</i>, nombre de satellites = <i>...</i>, dilution horizontale = <i>...</i>,
	 * altitude = <i>...</i></p>
	 * 
	 * @return La trame GPS convertie en chaine de caract�res.
	 */
	public String toString() {
		return "Latitude = " + getLatitude() + ", longitude = " + getLongitude()
				+ ", heure = " + getHeure() + ", nombre de satellites = " + nbSat_
				+ ", dilution horizontale = " + HDOP_ + ", altitude = " + altitude_;
	}
	
	
	
	
	/**
	 * Retourne la trame GPGGA convertie en chaine de caract�res.
	 * <p>La chaine de caract�res aura le format : "<i>classe de la trame (GPGGA);latitude;longitude
	 * ;heure;nombre de satellites;dilution horizontale;altitude</i></p>
	 * 
	 * @deprecated Cette m�thode �tait utilis�e pour la s�rialisation dans des fichiers texte. 
	 * Utiliser {@link #toString()} � la place. 
	 */
	public String afficher() {
		return TrameGPGGA.class + ";" + getLatitude() + ";" + getLongitude()
				+ ";" + getHeure() + ";" + nbSat_
				+ ";" + HDOP_ + ";" + altitude_;
	}




	/**
	 * Permet de savoir si la trame GPGGA re�ue est valide.
	 * @return true, si la trame est valide
	 * 		   false, si la trame n'est pas valide
	 */
	public boolean estValide() {
		return nbSat_ >= NBSATMIN_S && HDOP_ <= HDOPMAX_S;
	}




	/**
	 * Retourne le nombre de satellites capt�s.
	 * @return Le nombre de satellites capt�s.
	 */
	public int getNbSat() {
		return nbSat_;
	}




	/**
	 * Permet de modifier le nombre de satellites capt�s.
	 * @param nbSat 
	 * 				Le nouveau nombre de satellites capt�s
	 */
	public void setNbSat( int nbSat) {
		nbSat_ = nbSat;
	}




	/**
	 * Retourne la dilution horizontale subie par la trame GPS.
	 * @return La dilution horizontale subie par la trame GPS.
	 */
	public float getHDOP() {
		return HDOP_;
	}




	/**
	 * Permet de modifier la dilution horiizontale subie par la trame GPS.
	 * @param HDOP
	 * 				La nouvelle dilution horiizontale subie par la trame GPS.
	 */
	public void setHDOP( float HDOP) {
		HDOP_ = HDOP;
	}




	/**
	 * Retourne l'altitude de la coordonn�e GPS.
	 * @return L'altitude de la coordonn�e GPS.
	 */
	public float getAltitude() {
		return altitude_;
	}




	/**
	 * Permet de modifier l'altitude de la coordonn�e GPS.
	 * @param altitude
	 * 					La nouvelle altitude de la coordonn�e GPS.
	 */
	public void setAltitude( float altitude) {
		altitude_ = altitude;
	}




	@Override
	public boolean equals(TrameGPS trame) {
		if( trame.getClass().equals( TrameGPGGA.class)) {
			return getLatitude().equals( trame.getLatitude()) && getLongitude().equals( trame.getLongitude())
					&& getHeure() == trame.getHeure() && nbSat_ == ( ( TrameGPGGA ) trame).getNbSat()
					&& HDOP_ == ( ( TrameGPGGA) trame).getHDOP() && altitude_ == ( ( TrameGPGGA) trame).getAltitude();
		}
		return false;
	}
}
