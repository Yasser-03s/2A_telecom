package com.src.view;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.src.model.Borne;
import com.src.model.CarteClan;

/** Affichage et saisie dans le terminal. Ne connaît aucune règle du jeu. */
public class InterfaceConsole {

    private final Scanner scanner = new Scanner(System.in);   // un seul Scanner pour toute l'appli

    // ---------- Affichage ----------

    public void afficherMessage(String message) {
        System.out.println(message);
    }

    public void afficherTour(int numeroTour, String nomJoueur, int numeroJoueur) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("Tour " + numeroTour + " - " + nomJoueur + " (Joueur " + numeroJoueur + ")");
        System.out.println("==============================================");
    }

    public void afficherBornes(List<Borne> bornes) {
        for (Borne b : bornes) {
            System.out.println(formaterCote(b, 1)
                    + " [ " + b.getNumero() + " ]" + marqueGagnant(b) + " "
                    + formaterCote(b, 2));
        }
        System.out.println();
    }

    private String formaterCote(Borne b, int cote) {
        List<CarteClan> cartes = b.getCartes(cote);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            String s = (i < cartes.size()) ? cartes.get(i).toString() : "...";
            sb.append(String.format("%-3s", s));
            if (i < 2) sb.append(' ');
        }
        return sb.toString();
    }

    private String marqueGagnant(Borne b) {
        if (b.getGagnant() == 1) return "*";   // gagnée par le joueur 1
        if (b.getGagnant() == 2) return "+";   // gagnée par le joueur 2
        return " ";
    }

    public void afficherMain(List<CarteClan> main) {
        StringBuilder sb = new StringBuilder("Main : ");
        for (int i = 0; i < main.size(); i++) {
            sb.append(i + 1).append(")").append(main.get(i)).append("  ");
        }
        System.out.println(sb.toString().trim());
    }

    public void afficherVainqueur(String nom) {
        System.out.println();
        if (nom == null) {
            System.out.println("*** Partie terminée : égalité ***");
        } else {
            System.out.println("*** " + nom + " remporte la partie ! ***");
        }
    }

    // ---------- Saisie ----------

    public int lireEntier(String invite, int min, int max) {
        while (true) {
            System.out.print(invite + " [" + min + "-" + max + "] : ");
            String ligne = lireLigne();
            try {
                int n = Integer.parseInt(ligne.trim());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException e) {
                // on redemande
            }
            System.out.println("Entrée invalide.");
        }
    }

    public String lireTexte(String invite) {
        System.out.print(invite + " : ");
        return lireLigne().trim();
    }

    private String lireLigne() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Fin de l'entrée, arrêt du programme.");
            System.exit(0);
            return "";
        }
    }
}
