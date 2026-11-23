package com.schottenTotten;

import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;

import com.schottenTotten.ai.IA;
import com.schottenTotten.controller.GestionTours;
import com.schottenTotten.controller.Humain;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.controller.VarianteBase;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.view.InterfaceConsole;

// lancement : java com.schottenTotten.Main [graine]
// seule la variante de base est jouable pour l'instant
// avec une graine on rejoue exactement la même partie
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--serve".equals(args[0])) {
            lancerServeur(args);
            return;
        }

        InterfaceConsole console = new InterfaceConsole();

        Random alea = new Random();
        if (args.length > 0) {
            try {
                alea = new Random(Long.parseLong(args[0]));
            } catch (NumberFormatException e) {
                console.afficherMessage("graine ignorée : " + args[0] + " n'est pas un nombre");
            }
        }

        console.afficherMessage("=== Schotten-Totten (variante de base) ===");
        console.afficherMessage("cartes : valeur + couleur (R B V M J O)  * borne du joueur 1  + borne du joueur 2");
        int mode = console.lireEntier("1 humain vs humain  2 humain vs IA  3 IA vs IA", 1, 3);

        Joueur j1;
        Joueur j2;
        if (mode == 1) {
            j1 = new Humain(demanderNom(console, "nom du joueur 1", "Joueur 1"), 1, console);
            j2 = new Humain(demanderNom(console, "nom du joueur 2", "Joueur 2"), 2, console);
        } else if (mode == 2) {
            j1 = new Humain(demanderNom(console, "ton nom", "Joueur 1"), 1, console);
            j2 = new IA("IA", 2, alea);
        } else {
            j1 = new IA("IA 1", 1, alea);
            j2 = new IA("IA 2", 2, alea);
        }

        Jeu jeu = new VarianteBase(j1, j2, console, new GestionTours(), alea);
        jeu.lancer();
    }


    // Mode réseau : la machine hôte exécute le jeu et attend le joueur 2.
    // Le joueur distant ouvre un tunnel SSH avec nc vers le port local.
    private static void lancerServeur(String[] args) {
        int port = 5000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1]);
                return;
            }
        }
        if (port < 1 || port > 65535) {
            System.err.println("Le port doit être compris entre 1 et 65535.");
            return;
        }

        InterfaceConsole locale = new InterfaceConsole();
        locale.afficherMessage("=== Schotten-Totten : hôte réseau ===");
        locale.afficherMessage("Port SSH à utiliser depuis l'autre machine : " + port);
        locale.afficherMessage("En attente du joueur distant...");

        try (ServerSocket serveur = new ServerSocket(port, 1, InetAddress.getLoopbackAddress());
             Socket socket = serveur.accept()) {
            InterfaceConsole distante = new InterfaceConsole(socket.getInputStream(), socket.getOutputStream());
            InterfaceConsole diffusion = new InterfaceConsole(locale, distante);

            diffusion.afficherMessage("Joueur distant connecté.");
            String nom1 = demanderNom(locale, "nom du joueur 1 (toi)", "Joueur 1");
            String nom2 = demanderNom(distante, "nom du joueur 2", "Joueur 2");

            Random alea = new Random();
            Joueur j1 = new Humain(nom1, 1, locale);
            Joueur j2 = new Humain(nom2, 2, distante);
            Jeu jeu = new VarianteBase(j1, j2, diffusion, new GestionTours(), alea);
            jeu.lancer();
        } catch (IOException e) {
            System.err.println("Erreur réseau : " + e.getMessage());
        }
    }

    // si on ne tape rien on garde le nom par défaut
    private static String demanderNom(InterfaceConsole console, String invite, String parDefaut) {
        String nom = console.lireTexte(invite);
        if (nom.isEmpty()) {
            return parDefaut;
        }
        return nom;
    }
}