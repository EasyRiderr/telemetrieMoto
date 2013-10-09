D�veloppement d�une carte de t�l�m�trie moto temps r�el

Le projet d�bute par le choix d'un GPS et d'un inclinom�tre ainsi qu'une cl� USB permettant la communication 
en Wifi, afin que la carte puisse faire parvenir en temps r�el les informations des capteurs plac�s sur la 
moto � un r�cepteur de bord de piste, dans notre cas un PC. Le projet s'est poursuivi par la mod�lisation du 
circuit imprim� gr�ce au logiciel ALTIUM tout en tenant compte des contraintes de r�alisation du laboratoire 
de l'ISIMA et des composants choisis. Une fois la carte assembl�e, la partie logicielle du projet a �t� 
impl�ment�e. Elle permet d'envoyer au PC bord de pistes les trames GPS de types GPGGA et GPRMC � une fr�quence
de 10 Hz en Wifi. Les trames re�ues sont ensuite stock�es dans un fichier pour que l'on puisse effectuer le 
post traitement consistant � analyser les trames re�ues dans le but d'�valuer la pr�cision du syst�me GPS, 
et de tester la fiabilit� du r�seau Wifi en fonction de la vitesse de d�placement de la moto.