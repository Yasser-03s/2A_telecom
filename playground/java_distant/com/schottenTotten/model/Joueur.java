package com.schottenTotten.model;

import java.util.List;

// Un joueur (humain ou IA). Le joueur décide, le jeu vérifie que les règles sont respectées.
public interface Joueur {

    String getNom();

    // 1 ou 2
    int getNumero();

    List<Carte> getMain();

    // Renvoie {indice de la carte dans la main, indice de la Borne (0 à 8)}, ou null pour passer.
    // tactiquePermise : le joueur a le droit de jouer une carte tactique ce tour-ci.
    int[] choisirCoup(List<Borne> bornes, boolean tactiquePermise);

    // Renvoie le numéro (1 à 9) de la Borne à revendiquer, ou 0 pour terminer la revendication
    int choisirRevendication(List<Borne> bornes);

    // Question posée par le jeu (valeur d'un Joker, cible d'une ruse...) : réponse entre min et max
    int choisir(String invite, int min, int max);
}
