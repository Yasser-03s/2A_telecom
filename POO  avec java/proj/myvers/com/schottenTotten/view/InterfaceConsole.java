package com.schottenTotten.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteClan;

// Affichage et saisie dans le terminal. Ne connaît aucune règle du jeu.
public class InterfaceConsole {

    // Un seul lecteur pour toute l'application
    private BufferedReader lecteur = new BufferedReader(new InputStreamReader(System.in));

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
            System.out.println(formaterCote(b, 1) + " [ " + b.getNumero() + " ]" + marqueGagnant(b) + " " + formaterCote(b, 2));
        }
        System.out.println();
    }

    private String formaterCote(Borne borne, int cote) {
        List<CarteClan> cartes = borne.getCombinaison(cote).getCartes();
        String texte = "";
        for (int i = 0; i < 3; i++) {
            String carte = "...";
            if (i < cartes.size()) {
                carte = cartes.get(i).toString();
            }
            texte = texte + cadrer(carte, 4);
        }
        return texte;
    }

    private String cadrer(String texte, int largeur) {
        while (texte.length() < largeur) {
            texte = texte + " ";
        }
        return texte;
    }

    // * = Borne du joueur 1, + = Borne du joueur 2
    private String marqueGagnant(Borne borne) {
        if (borne.getGagnant() == 1) {
            return "*";
        }
        if (borne.getGagnant() == 2) {
            return "+";
        }
        return " ";
    }

    public void afficherMain(List<Carte> main) {
        String texte = "Main : ";
        for (int i = 0; i < main.size(); i++) {
            texte = texte + (i + 1) + ")" + main.get(i) + "  ";
        }
        System.out.println(texte);
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
                if (n >= min && n <= max) {
                    return n;
                }
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
            String ligne = lecteur.readLine();
            if (ligne != null) {
                return ligne;
            }
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
        }
        System.out.println();
        System.out.println("Fin de l'entrée, arrêt du programme.");
        System.exit(0);
        return "";
    }
}
