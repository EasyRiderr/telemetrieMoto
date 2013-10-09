/*
 * TrameGPRMC.java
 * Permet de representer une trame GPS de type GPRMC
 */

package telemetrieMoto.acquisition.comm.gps;

import telemetrieMoto.Proprietes;


/**
 * <b>TrameGPRMC permet de repr�senter une trame GPS de type GPRMC.</b>
 * <p>Une trame GPS GPRMC est caract�ris�e par les informations suivantes :
 * <ul>
 * 	<li>Une latitude</li>
 * 	<li>Une longitude</li>
 * 	<li>Une heure</li>
 * 	<li>La validit� de ses donn�es</li>
 * 	<li>Une date</li>
 * </ul>
 * Si le <i>GPS</i> ne nous envoie pas une de ces informations la trame sera nulle.
 * </p>
 * 
 * @author Yoan DUMAS
 * @version 1.1
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPS
 * @see telemetrieMoto.acquisition.comm.gps.TrameGPGGA
 * @see telemetrieMoto.acquisition.peripheriques.GPS
 */
public class TrameGPRMC extends TrameGPS {


	/** L'identifiant de s�rialisation. */
	private static final long serialVersionUID = 1L;


	/** Permet de savoir si les donn�es re�ues du GPS sont valides.
	 * <br>Par convention : 'A' = donn�es valides, 'V' = donn�es invalides. */
	private char donneesValides_;


	/** La date exprim�e au format : ddmmyy. */
	private int date_;
	
	/** Permet d'effectuer les affichages si vrai <i>(true)</i>, sinon on n'affiche rien <i>(false)</i>.
	 * Sa valeur est stock�e dans le fichier de configuration du projet. */
	private static final boolean DEBUG_S = Boolean.valueOf( Proprietes.getInstance().getPropriete( "DebugGPS"));




	/**
	 * Le constructeur de trame GPRMC.
	 * @param latitude
	 * 					La latitude de la coordonn�e GPRMC.
	 * @param longitude
	 * 					La longitude de la coordonn�e GPRMC.
	 * @param heure
	 * 					L'heure UTC de l'acquisition de la trame GPRMC.
	 * @param donneesValides
	 * 					Permet de savoir si la trame est correcte.
	 * @param date
	 * 					La date d'acquisition de la trame.
	 */
	public TrameGPRMC( String latitude, String longitude, float heure, char donneesValides, int date) {
		setLatitude( latitude);
		setLongitude( longitude);
		setHeure( heure);
		donneesValides_ = donneesValides;
		date_ = date;
	}




	/**
	 * Le constructeur de trame GPRMC � partir d'une chaine de caract�res.
	 * @param trameGPSLue
	 * 					La trame brute recue du GPS.
	 */
	public TrameGPRMC( String trameGPSLue) {
		if( trameGPSLue != null) {

			// On se debarasse de l'entete de la trame
			String chaineAParser = trameGPSLue.substring( trameGPSLue.indexOf( ',') + 1);

			String chaineParsee[] = chaineAParser.split( ",");

			if( chaineParsee.length >= 8) {
				try {
					
					// Extraction des informations utiles
					setHeure( Float.valueOf( chaineParsee[ 0]));
					donneesValides_ = chaineParsee[ 1].charAt( 0);
					setLatitude( chaineParsee[ 2] + "," + chaineParsee[ 3]);
					setLongitude( chaineParsee[ 4] + "," + chaineParsee[ 5]);
					date_ = Integer.valueOf( chaineParsee[ 8]);
					
				} catch( NumberFormatException e) {
					if( DEBUG_S) {
						System.err.println( "Erreur : trame incomplete.");
						setHeure( 0);
						donneesValides_ = '\0';
						setLatitude( "");
						setLongitude( "");
						date_ = 0;
					}
				}
			}
		}
	}




	/**
	 * Retourne la trame GPRMC en chaine de caract�res.
	 * <p>La chaine de caract�res aura le format : "Latitude = <i>...</i>, longitude = <i>...</i>, 
	 * heure = <i>...</i>, etat = <i>...</i>, date = <i>...</i></p>
	 * 
	 * @return La trame GPRMC convertie en chaine de caract�res.
	 */
	public String toString() {
		return "Latitude = " + getLatitude() + ", longitude = " + getLongitude()
				+ ", heure = " + getHeure() + ", etat = " + donneesValides_ 
				+ ", date = " + date_;
	}
	
	
	
	
	/**
	 * Retourne la trame GPRMC convertie en chaine de caract�res.
	 * <p>La chaine de caract�res aura le format : "<i>classe de la trame (GPRMC);latitude;longitude
	 * ;heure;donn�es valides;date</i></p>
	 * 
	 * @deprecated Cette m�thode �tait utilis�e pour la s�rialisation dans des fichiers texte. 
	 * Utiliser {@link #toString()} � la place. 
	 */
	public String afficher() {
		return TrameGPRMC.class + ";" + getLatitude() + ";" + getLongitude()
				+ ";" + getHeure() + ";" + donneesValides_ 
				+ ";" + date_;
	}




	/**
	 * Permet de savoir si la trame GPRMC re�ue est valide.
	 * @return true, si la trame est valide
	 * 		   false, si la trame n'est pas valide.
	 */
	public boolean estValide() {
		return donneesValides_ == 'A';
	}




	/**
	 * Retourne l'indicateur de validit� de la trame GPRMC.
	 * @return L'indicateur de validit� de la trame GPRMC.
	 */
	public char getDonneesValides() {
		return donneesValides_;
	}




	/**
	 * Permet de modifier l'indicateur de validit� de la trame GPRMC.
	 * @param donneesValides 
	 *					Le nouvel indicateur de validit� de la trame GPRMC.
	 */
	public void setDonneesValides( char donneesValides) {
		donneesValides_ = donneesValides;
	}




	/**
	 * Retourne la date de l'acquisition de la trame GPRMC.
	 * @return La date de l'acquisition de la trame GPRMC.
	 */
	public int getDate() {
		return date_;
	}




	/**
	 * Permet de modifier la date de l'acquisition de la trame GPRMC.
	 * @param date
	 * 				La nouvelle date de l'acquisition de la trame GPRMC.
	 */
	public void setDate( int date) {
		date_ = date;
	}




	@Override
	public boolean equals( TrameGPS trame) {
		if( trame.getClass().equals( TrameGPRMC.class)) {
			return getLatitude().equals( trame.getLatitude()) && getLongitude().equals( trame.getLongitude())
					&& getHeure() == trame.getHeure() && donneesValides_ == ( ( TrameGPRMC) trame).getDonneesValides() && date_ == ( ( TrameGPRMC) trame).getDate();
		}
		return false;
	}
}
