package com.src.model;

/** Carte Clan : une couleur et une valeur de 1 à 9. */
public class CarteClan extends Carte {

    private final Couleur couleur;
    private final int valeur;

    public CarteClan(Couleur couleur, int valeur) {
        if (valeur < 1 || valeur > 9) {
            throw new IllegalArgumentException("La valeur doit être entre 1 et 9");
        }
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Couleur getCouleur() { return couleur; }
    public int getValeur()      { return valeur; }

    @Override
    public String toString() {
        return "" + valeur + couleur.getInitiale();
    }
}
