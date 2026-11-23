package com.schottenTotten.controller;

import com.schottenTotten.model.Joueur;
import com.schottenTotten.view.InterfaceConsole;

// ce que toutes les variantes ont en commun : deux joueurs, une console, des tours, un gagnant
public abstract class Jeu {

    protected Joueur[] joueurs;
    protected InterfaceConsole console;
    protected GestionTours gestionTours;
    protected Joueur gagnant;

    public Jeu(Joueur j1, Joueur j2, InterfaceConsole console, GestionTours gestionTours) {
        joueurs = new Joueur[2];
        joueurs[0] = j1;
        joueurs[1] = j2;
        this.console = console;
        this.gestionTours = gestionTours;
    }

    public Joueur getGagnant() {
        return gagnant;
    }

    // joue la partie jusqu'à la fin
    public abstract void lancer();
}
