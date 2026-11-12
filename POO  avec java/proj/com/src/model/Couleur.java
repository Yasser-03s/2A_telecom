package com.src.model;

/** Les six couleurs des cartes Clan. */
public enum Couleur {
    ROUGE, BLEU, VERT, MAUVE, JAUNE, ORANGE;

    /** Première lettre en majuscule (R, B, V, M, J, O). */
    public char getInitiale() {
        return name().charAt(0);
    }
}
