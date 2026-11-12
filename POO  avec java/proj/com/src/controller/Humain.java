package com.src.controller;

import java.util.ArrayList;
import java.util.List;

import com.src.model.Borne;
import com.src.model.CarteClan;
import com.src.model.Joueur;
import com.src.view.InterfaceConsole;

/** Joueur qui joue au clavier via la console. */
public class Humain implements Joueur {

    private final String nom;
    private final int numero;
    private final List<CarteClan> main = new ArrayList<>();
    private final InterfaceConsole console;

    public Humain(String nom, int numero, InterfaceConsole console) {
        this.nom = nom;
        this.numero = numero;
        this.console = console;
    }

    @Override public String getNom()          { return nom; }
    @Override public int getNumero()          { return numero; }
    @Override public List<CarteClan> getMain() { return main; }

    @Override
    public int[] choisirCoup(List<Borne> bornes) {
        boolean possible = false;
        for (Borne b : bornes) {
            if (b.peutRecevoir(numero)) possible = true;
        }
        if (!possible || main.isEmpty()) {
            return null;
        }
        console.afficherMain(main);
        int carte = console.lireEntier("Carte à jouer", 1, main.size());
        while (true) {
            int n = console.lireEntier("Borne où la poser", 1, bornes.size());
            if (bornes.get(n - 1).peutRecevoir(numero)) {
                return new int[] { carte - 1, n - 1 };
            }
            console.afficherMessage("Pose impossible : Borne déjà revendiquée ou complète de ton côté.");
        }
    }

    @Override
    public int choisirRevendication(List<Borne> bornes) {
        return console.lireEntier("Revendiquer la Borne (0 = terminer)", 0, bornes.size());
    }
}
