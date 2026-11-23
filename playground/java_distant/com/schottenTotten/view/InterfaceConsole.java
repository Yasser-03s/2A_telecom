package com.schottenTotten.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteClan;

// affichage et saisie dans le terminal sans aucune règle du jeu
public class InterfaceConsole {

    // Chaque joueur possède sa propre entrée/sortie. Une console de diffusion
    // peut envoyer les informations publiques aux deux terminaux.
    private BufferedReader lecteur;
    private PrintStream[] sorties;

    public InterfaceConsole() {
        this(System.in, System.out);
    }

    public InterfaceConsole(InputStream entree, OutputStream sortie) {
        lecteur = new BufferedReader(new InputStreamReader(entree));
        sorties = new PrintStream[] { new PrintStream(sortie, true) };
    }

    public InterfaceConsole(InterfaceConsole... consoles) {
        lecteur = new BufferedReader(new InputStreamReader(System.in));
        sorties = new PrintStream[consoles.length];
        for (int i = 0; i < consoles.length; i++) {
            sorties[i] = consoles[i].sorties[0];
        }
    }

    private void afficher(String texte) {
        for (PrintStream sortie : sorties) {
            sortie.print(texte);
            sortie.flush();
        }
    }

    private void ligne(String texte) {
        afficher(texte + System.lineSeparator());
    }

    // ---------- affichage ----------

    public void afficherMessage(String message) {
        ligne(message);
    }

    public void afficherTour(int numeroTour, String nomJoueur, int numeroJoueur) {
        ligne("");
        ligne("--- tour " + numeroTour + " : " + nomJoueur + " (joueur " + numeroJoueur + ") ---");
    }

    // une ligne par borne  cartes du joueur 1 [ numero ] cartes du joueur 2
    // * borne gagnée par le joueur 1  + borne gagnée par le joueur 2
    public void afficherBornes(List<Borne> bornes) {
        for (Borne b : bornes) {
            String marque = " ";
            if (b.getGagnant() == 1) {
                marque = "*";
            }
            if (b.getGagnant() == 2) {
                marque = "+";
            }
            ligne(texteCote(b, 1) + "[ " + b.getNumero() + " ]" + marque + " " + texteCote(b, 2));
        }
        System.out.println();
    }

    // trois places par côté  une place vide s'affiche --
    private String texteCote(Borne borne, int cote) {
        List<CarteClan> cartes = borne.getCombinaison(cote).getCartes();
        String texte = "";
        for (int i = 0; i < 3; i++) {
            if (i < cartes.size()) {
                texte = texte + cartes.get(i) + " ";
            } else {
                texte = texte + "-- ";
            }
        }
        return texte;
    }

    public void afficherMain(List<Carte> main) {
        String texte = "main :";
        for (int i = 0; i < main.size(); i++) {
            texte = texte + "  " + (i + 1) + ")" + main.get(i);
        }
        ligne(texte);
    }

    public void afficherVainqueur(String nom) {
        ligne("");
        if (nom == null) {
            ligne("égalité");
        } else {
            ligne(nom + " gagne la partie");
        }
    }

    // ---------- saisie ----------

    // redemande tant que la saisie n'est pas un entier entre min et max
    public int lireEntier(String invite, int min, int max) {
        while (true) {
            afficher(invite + " [" + min + "-" + max + "] : ");
            try {
                int n = Integer.parseInt(lireLigne().trim());
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // pas un nombre  on redemande
            }
            ligne("saisie invalide");
        }
    }

    public String lireTexte(String invite) {
        afficher(invite + " : ");
        return lireLigne().trim();
    }

    // si l'entrée est fermée on arrête le programme
    private String lireLigne() {
        String ligne = null;
        try {
            ligne = lecteur.readLine();
        } catch (IOException e) {
            ligne = null;
        }
        if (ligne == null) {
            ligne("fin de la saisie");
            System.exit(0);
        }
        return ligne;
    }
}