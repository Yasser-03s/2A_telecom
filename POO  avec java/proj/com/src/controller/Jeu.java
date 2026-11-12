package com.src.controller;

import java.util.ArrayList;
import java.util.List;

import com.src.model.Borne;
import com.src.model.Joueur;
import com.src.view.InterfaceConsole;

/** Partie de Schotten-Totten : état commun à toutes les variantes. */
public abstract class Jeu {

    public static final int NB_BORNES = 9;

    protected final List<Borne> bornes = new ArrayList<>();
    protected final Joueur[] joueurs = new Joueur[2];
    protected final InterfaceConsole console;
    protected final GestionTours gestionTours = new GestionTours();
    protected Joueur gagnant;

    protected Jeu(Joueur j1, Joueur j2, InterfaceConsole console) {
        this.joueurs[0] = j1;
        this.joueurs[1] = j2;
        this.console = console;
        for (int i = 1; i <= NB_BORNES; i++) {
            bornes.add(new Borne(i));
        }
    }

    public Joueur getGagnant() { return gagnant; }

    /** Lance la partie jusqu'à sa fin. */
    public abstract void lancer();

    /** Condition de victoire : 5 Bornes au total ou 3 Bornes adjacentes. */
    protected boolean conditionVictoire(int cote) {
        int total = 0;
        int suite = 0;
        for (Borne b : bornes) {
            if (b.getGagnant() == cote) {
                total++;
                suite++;
                if (suite >= 3) return true;
            } else {
                suite = 0;
            }
        }
        return total >= 5;
    }

    protected int compterBornes(int cote) {
        int n = 0;
        for (Borne b : bornes) {
            if (b.getGagnant() == cote) n++;
        }
        return n;
    }
}
