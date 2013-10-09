Développement d’une carte de télémétrie moto temps réel

Le projet débute par le choix d'un GPS et d'un inclinomètre ainsi qu'une clé USB permettant la communication 
en Wifi, afin que la carte puisse faire parvenir en temps réel les informations des capteurs placés sur la 
moto à un récepteur de bord de piste, dans notre cas un PC. Le projet s'est poursuivi par la modélisation du 
circuit imprimé grâce au logiciel ALTIUM tout en tenant compte des contraintes de réalisation du laboratoire 
de l'ISIMA et des composants choisis. Une fois la carte assemblée, la partie logicielle du projet a été 
implémentée. Elle permet d'envoyer au PC bord de pistes les trames GPS de types GPGGA et GPRMC à une fréquence
de 10 Hz en Wifi. Les trames reçues sont ensuite stockées dans un fichier pour que l'on puisse effectuer le 
post traitement consistant à analyser les trames reçues dans le but d'évaluer la précision du système GPS, 
et de tester la fiabilité du réseau Wifi en fonction de la vitesse de déplacement de la moto.