package com.schottenTotten.model;

// une Borne : une Combinaison par côté (côté 1 = joueur 1, côté 2 = joueur 2)
public class Borne {

    private int numero;
    private Combinaison[] cotes;
    private int gagnant = 0;          // 0 = non revendiquée, sinon 1 ou 2
    private int premierComplet = 0;   // côté qui a complété sa Combinaison en premier

    // les deux Combinaisons sont créées à l'extérieur et passées au constructeur 
    public Borne(int numero, Combinaison combinaison1, Combinaison combinaison2) {
        this.numero = numero;
        cotes = new Combinaison[2];
        cotes[0] = combinaison1;
        cotes[1] = combinaison2;
    }

    public int getNumero() {
        return numero;
    }

    public int getGagnant() {
        return gagnant;
    }

    public void setGagnant(int cote) {
        gagnant = cote;
    }

    public int getPremierComplet() {
        return premierComplet;
    }

    public Combinaison getCombinaison(int cote) {
        return cotes[cote - 1];
    }

    public boolean estComplete(int cote) {
        return cotes[cote - 1].estComplete();
    }

    public boolean peutRecevoir(int cote) {
        return gagnant == 0 && !cotes[cote - 1].estComplete();
    }

    public void ajouter(int cote, CarteClan carte) {
        if (!peutRecevoir(cote)) {
            throw new IllegalStateException("Borne " + numero + " : pose impossible du côté " + cote);
        }
        cotes[cote - 1].ajouter(carte);
        if (estComplete(cote) && premierComplet == 0) {
            premierComplet = cote;
        }
    }

    // utilisé par les ruses (Stratège, Banshee, Traître)
    public CarteClan retirer(int cote, int index) {
        if (gagnant != 0) {
            throw new IllegalStateException("Borne " + numero + " : déjà revendiquée");
        }
        CarteClan carte = cotes[cote - 1].retirer(index);
        if (premierComplet == cote) {
            premierComplet = 0;
            if (estComplete(3 - cote)) {
                premierComplet = 3 - cote;
            }
        }
        return carte;
    }
}
