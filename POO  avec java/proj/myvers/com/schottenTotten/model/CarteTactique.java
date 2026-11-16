package com.schottenTotten.model;

// Carte tactique : une troupe d'élite ou une ruse
public class CarteTactique extends Carte {

    public static final String TROUPE = "Troupe";
    public static final String RUSE = "Ruse";

    public static final String JOKER = "Joker";
    public static final String ESPION = "Espion";
    public static final String PORTE_BOUCLIER = "Porte-bouclier";
    public static final String CHASSEUR = "Chasseur de tête";
    public static final String STRATEGE = "Stratège";
    public static final String BANSHEE = "Banshee";
    public static final String TRAITRE = "Traître";

    private String nom;
    private String type;

    public CarteTactique(String nom, String type) {
        this.nom = nom;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean estTroupe() {
        return type.equals(TROUPE);
    }

    public boolean estRuse() {
        return type.equals(RUSE);
    }

    @Override
    public String getNom() {
        return nom;
    }
}
