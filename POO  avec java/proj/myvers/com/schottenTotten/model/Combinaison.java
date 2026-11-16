package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Arrays;

// Les cartes posées par un joueur devant une Borne, comparables selon les règles du jeu
public class Combinaison implements Comparable<Combinaison> {

    public static final int TAILLE = 3;

    // Du plus faible au plus fort
    public static final int SOMME = 0;
    public static final int SUITE = 1;
    public static final int COULEUR = 2;
    public static final int BRELAN = 3;
    public static final int SUITE_COULEUR = 4;

    public static final String[] NOMS = {"Somme", "Suite", "Couleur", "Brelan", "Suite couleur"};

    private ArrayList<CarteClan> cartes;

    public Combinaison() {
        cartes = new ArrayList<CarteClan>();
    }

    public void ajouter(CarteClan carte) {
        if (cartes.size() >= TAILLE) {
            throw new IllegalStateException("Une combinaison contient au plus " + TAILLE + " cartes");
        }
        cartes.add(carte);
    }

    public CarteClan retirer(int index) {
        if (index < 0 || index >= cartes.size()) {
            throw new IllegalArgumentException("Aucune carte à l'indice " + index);
        }
        return cartes.remove(index);
    }

    // On renvoie une copie pour que l'extérieur ne modifie pas la liste directement
    public ArrayList<CarteClan> getCartes() {
        return new ArrayList<CarteClan>(cartes);
    }

    public int getNombre() {
        return cartes.size();
    }

    public boolean estComplete() {
        return cartes.size() == TAILLE;
    }

    public int getSomme() {
        int somme = 0;
        for (CarteClan carte : cartes) {
            somme = somme + carte.getValeur();
        }
        return somme;
    }

    public int getType() {
        if (!estComplete()) {
            throw new IllegalStateException("La combinaison n'est pas complète");
        }
        int[] v = new int[TAILLE];
        for (int i = 0; i < TAILLE; i++) {
            v[i] = cartes.get(i).getValeur();
        }
        Arrays.sort(v);
        boolean memeCouleur = cartes.get(0).getCouleur() == cartes.get(1).getCouleur()
                && cartes.get(1).getCouleur() == cartes.get(2).getCouleur();
        boolean suite = v[1] == v[0] + 1 && v[2] == v[1] + 1;
        boolean brelan = v[0] == v[2];

        if (suite && memeCouleur) {
            return SUITE_COULEUR;
        }
        if (brelan) {
            return BRELAN;
        }
        if (memeCouleur) {
            return COULEUR;
        }
        if (suite) {
            return SUITE;
        }
        return SOMME;
    }

    // Positif si this bat o, 0 si les deux sont à égalité (même type et même somme)
    @Override
    public int compareTo(Combinaison o) {
        int c = Integer.compare(getType(), o.getType());
        if (c != 0) {
            return c;
        }
        return Integer.compare(getSomme(), o.getSomme());
    }

    @Override
    public String toString() {
        if (!estComplete()) {
            return cartes.toString();
        }
        return NOMS[getType()] + ", somme " + getSomme();
    }
}
