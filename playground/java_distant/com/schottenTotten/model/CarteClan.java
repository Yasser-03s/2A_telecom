package com.schottenTotten.model;

// Carte Clan : une couleur (indice de 0 à 5) et une valeur de 1 à 9
public class CarteClan extends Carte {

    public static final String[] COULEURS = {"R", "B", "V", "M", "J", "O"}; // Première lettre d'une couleur

    private int couleur;
    private int valeur;

    public CarteClan(int couleur, int valeur) {
        if (couleur < 0 || couleur >= COULEURS.length) {
            throw new IllegalArgumentException("La couleur doit être entre 0 et " + (COULEURS.length - 1));
        }
        if (valeur < 1 || valeur > 9) {
            throw new IllegalArgumentException("La valeur doit être entre 1 et 9");
        }
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public int getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    @Override
    public String getNom() {
        return valeur + COULEURS[couleur];
    }
}
