package com.src.model;

import java.util.List;

/** Un joueur (humain ou IA). Les décisions sont demandées au joueur, les règles sont vérifiées par le jeu. */
public interface Joueur {

    String getNom();

    /** 1 ou 2. */
    int getNumero();

    List<CarteClan> getMain();

    /** Retourne {indice de la carte dans la main, indice de la Borne (0 à 8)}, ou null si aucun coup possible. */
    int[] choisirCoup(List<Borne> bornes);

    /** Retourne le numéro (1 à 9) de la Borne à revendiquer, ou 0 pour terminer la phase de revendication. */
    int choisirRevendication(List<Borne> bornes);
}
