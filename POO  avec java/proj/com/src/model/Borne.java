package com.src.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Une Borne : jusqu'à 3 cartes de chaque côté (côté 1 = joueur 1, côté 2 = joueur 2). */
public class Borne {

    private final int numero;
    private final List<List<CarteClan>> cotes = new ArrayList<>();
    private int gagnant = 0;          // 0 = non revendiquée, sinon 1 ou 2
    private int premierComplet = 0;   // côté qui a posé sa 3e carte en premier

    public Borne(int numero) {
        this.numero = numero;
        cotes.add(new ArrayList<CarteClan>());
        cotes.add(new ArrayList<CarteClan>());
    }

    public int getNumero()         { return numero; }
    public int getGagnant()        { return gagnant; }
    public int getPremierComplet() { return premierComplet; }

    public List<CarteClan> getCartes(int cote) {
        return Collections.unmodifiableList(cotes.get(cote - 1));
    }

    public boolean estComplete(int cote) {
        return cotes.get(cote - 1).size() == 3;
    }

    public boolean peutRecevoir(int cote) {
        return gagnant == 0 && cotes.get(cote - 1).size() < 3;
    }

    public void ajouter(int cote, CarteClan carte) {
        if (!peutRecevoir(cote)) {
            throw new IllegalStateException("Borne " + numero + " : pose impossible côté " + cote);
        }
        cotes.get(cote - 1).add(carte);
        if (estComplete(cote) && premierComplet == 0) {
            premierComplet = cote;
        }
    }

    public void setGagnant(int cote) {
        this.gagnant = cote;
    }
}
