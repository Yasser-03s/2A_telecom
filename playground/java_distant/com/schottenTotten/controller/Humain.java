package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.List;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
//import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.view.InterfaceConsole;

// joueur qui joue au clavier, via la console
public class Humain implements Joueur {

    private String nom;
    private int numero;
    private ArrayList<Carte> main;
    private InterfaceConsole console;

    // la console existe avant le joueur et peut être partagée (agrégation)
    public Humain(String nom, int numero, InterfaceConsole console) {
        this.nom = nom;
        this.numero = numero;
        this.console = console;
        main = new ArrayList<Carte>();
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public List<Carte> getMain() {
        return main;
    }

    // le jeu verifie le coup : s'il est refusé, on nous le redemande
    @Override
    public int[] choisirCoup(List<Borne> bornes, boolean tactiquePermise) {
        console.afficherMain(main);
        int carte = console.lireEntier("Carte à jouer (0 = passer)", 0, main.size());
        if (carte == 0) {
            return null;
        }
        Carte choisie = main.get(carte - 1);

        int borne = console.lireEntier("Borne où la poser", 1, bornes.size());
        return new int[] { carte - 1, borne - 1 };
    }

    @Override
    public int choisirRevendication(List<Borne> bornes) {
        return console.lireEntier("Revendiquer la Borne (0 = terminer)", 0, bornes.size());
    }

    @Override
    public int choisir(String invite, int min, int max) {
        return console.lireEntier(invite, min, max);
    }
}
