package com.schottenTotten;

import java.util.Random;

import com.schottenTotten.ai.IA;
import com.schottenTotten.controller.GestionTours;
import com.schottenTotten.controller.Humain;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.controller.VarianteBase;
import com.schottenTotten.controller.VarianteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.view.InterfaceConsole;

// Lancement : java com.schottenTotten.Main [graine]
public class Main {

    public static void main(String[] args) {
        InterfaceConsole console = new InterfaceConsole();
        Random alea = new Random();
        if (args.length > 0) {
            try {
                alea = new Random(Long.parseLong(args[0]));
            } catch (NumberFormatException e) {
                console.afficherMessage("Graine ignorée : " + args[0] + " n'est pas un nombre.");
            }
        }

        console.afficherMessage("=== Schotten-Totten ===");
        console.afficherMessage("Cartes : valeur + initiale de la couleur (R, B, V, M, J, O). * = Borne du joueur 1, + = Borne du joueur 2.");
        int variante = console.lireEntier("Variante : 1. Base  2. Tactique", 1, 2);
        int mode = console.lireEntier("Mode : 1. Humain vs Humain  2. Humain vs IA  3. IA vs IA", 1, 3);

        Joueur j1;
        Joueur j2;
        if (mode == 1) {
            j1 = new Humain(demanderNom(console, "Nom du joueur 1", "Joueur 1"), 1, console);
            j2 = new Humain(demanderNom(console, "Nom du joueur 2", "Joueur 2"), 2, console);
        } else if (mode == 2) {
            j1 = new Humain(demanderNom(console, "Ton nom", "Joueur 1"), 1, console);
            j2 = new IA("IA", 2, alea);
        } else {
            j1 = new IA("IA 1", 1, alea);
            j2 = new IA("IA 2", 2, alea);
        }

        GestionTours gestionTours = new GestionTours();
        Jeu jeu;
        if (variante == 1) {
            jeu = new VarianteBase(j1, j2, console, gestionTours, alea);
        } else {
            jeu = new VarianteTactique(j1, j2, console, gestionTours, alea);
        }
        jeu.lancer();
    }

    private static String demanderNom(InterfaceConsole console, String invite, String parDefaut) {
        String nom = console.lireTexte(invite);
        if (nom.isEmpty()) {
            return parDefaut;
        }
        return nom;
    }
}
