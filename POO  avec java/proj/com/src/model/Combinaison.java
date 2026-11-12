package com.src.model;

import java.util.Arrays;
import java.util.List;

/** Combinaison de trois cartes Clan, comparable selon les règles de Schotten-Totten. */
public class Combinaison implements Comparable<Combinaison> {

    /** Du plus faible au plus fort. */
    public enum Type {
        SOMME("Somme"), SUITE("Suite"), COULEUR("Couleur"), BRELAN("Brelan"), SUITE_COULEUR("Suite couleur");

        private final String libelle;
        Type(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    private final Type type;
    private final int somme;

    private Combinaison(Type type, int somme) {
        this.type = type;
        this.somme = somme;
    }

    public Type getType() { return type; }
    public int getSomme() { return somme; }

    public static Combinaison evaluer(List<CarteClan> cartes) {
        if (cartes.size() != 3) {
            throw new IllegalArgumentException("Une combinaison contient 3 cartes");
        }
        int[] v = new int[3];
        for (int i = 0; i < 3; i++) {
            v[i] = cartes.get(i).getValeur();
        }
        Arrays.sort(v);
        boolean memeCouleur = cartes.get(0).getCouleur() == cartes.get(1).getCouleur()
                           && cartes.get(1).getCouleur() == cartes.get(2).getCouleur();
        boolean suite  = v[1] == v[0] + 1 && v[2] == v[1] + 1;
        boolean brelan = v[0] == v[2];
        int somme = v[0] + v[1] + v[2];

        Type t;
        if (suite && memeCouleur) t = Type.SUITE_COULEUR;
        else if (brelan)          t = Type.BRELAN;
        else if (memeCouleur)     t = Type.COULEUR;
        else if (suite)           t = Type.SUITE;
        else                      t = Type.SOMME;
        return new Combinaison(t, somme);
    }

    /** Positif si this bat o. 0 en cas d'égalité parfaite (type et somme). */
    @Override
    public int compareTo(Combinaison o) {
        int c = Integer.compare(type.ordinal(), o.type.ordinal());
        return (c != 0) ? c : Integer.compare(somme, o.somme);
    }

    @Override
    public String toString() {
        return type.getLibelle() + ", somme " + somme;
    }
}
