package com.schottenTotten.model;

public class CarteClan extends Carte {
    private final Couleur couleur;
    private final int valeur;            // 1 à 9

    public CarteClan(Couleur couleur, int valeur) {
        if (couleur == null) {
            throw new IllegalArgumentException("La couleur ne peut pas être nulle");
        }
        if (valeur < 1 || valeur > 9) {
            throw new IllegalArgumentException("La valeur doit être entre 1 et 9");
        }
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Couleur getCouleur(){ 
        return couleur; 
        }
    public int getValeur(){ 
        return valeur; 
        }
    @Override
    public String toString(){
        return valeur + " " + couleur;
    }
    
}