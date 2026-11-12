package com.src;

import java.util.Random;

import com.src.ai.IA;
import com.src.controller.Humain;
import com.src.controller.VarianteBase;
import com.src.model.Joueur;
import com.src.view.InterfaceConsole;

/** Petit programme de test de la variante de base. Usage : java Main [graine] */
public class Main {

    public static void main(String[] args) {
        InterfaceConsole console = new InterfaceConsole();
        long graine = (args.length > 0) ? Long.parseLong(args[0]) : System.nanoTime();
        Random alea = new Random(graine);

        console.afficherMessage("=== Schotten-Totten - variante de base ===");
        console.afficherMessage("Cartes : numéro + initiale de la couleur (R, B, V, M, J, O). * = Borne du joueur 1, + = Borne du joueur 2.");
        int mode = console.lireEntier("Mode : 1. Humain vs Humain  2. Humain vs IA  3. IA vs IA", 1, 3);

        Joueur j1;
        Joueur j2;
        if (mode == 1) {
            j1 = new Humain(nom(console, "Nom du joueur 1", "Joueur 1"), 1, console);
            j2 = new Humain(nom(console, "Nom du joueur 2", "Joueur 2"), 2, console);
        } else if (mode == 2) {
            j1 = new Humain(nom(console, "Ton nom", "Joueur 1"), 1, console);
            j2 = new IA("IA", 2, alea);
        } else {
            j1 = new IA("IA 1", 1, alea);
            j2 = new IA("IA 2", 2, alea);
        }

        new VarianteBase(j1, j2, console, alea).lancer();
    }

    private static String nom(InterfaceConsole console, String invite, String parDefaut) {
        String s = console.lireTexte(invite);
        return s.isEmpty() ? parDefaut : s;
    }
}
