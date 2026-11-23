package com.schottenTotten.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
//import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;

// IA tres simple : tous ses choix sont aleatoires, et elle tente de revendiquer chaque Borne dont son cote est complet
public class IA implements Joueur {

    private String nom;
    private int numero;
    private ArrayList<Carte> main;
    private Random alea;
    private ArrayList<Integer> dejaTentees;

    public IA(String nom, int numero, Random alea) {
        this.nom = nom;
        this.numero = numero;
        this.alea = alea;
        main = new ArrayList<Carte>();
        dejaTentees = new ArrayList<Integer>();
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

    @Override
    public int[] choisirCoup(List<Borne> bornes, boolean tactiquePermise) {
        List<Integer> bornesLibres = new ArrayList<Integer>();
        for (int i = 0; i < bornes.size(); i++) {
            if (bornes.get(i).peutRecevoir(numero)) {
                bornesLibres.add(i);
            }
        }

        // on ne garde que les cartes qu'on a une chance de pouvoir jouer
        List<Integer> cartes = new ArrayList<Integer>();
        for (int i = 0; i < main.size(); i++) {
            Carte carte = main.get(i);
            if (!bornesLibres.isEmpty()) {
                cartes.add(i);
            }
        }
        if (cartes.isEmpty()) {
            return null;
        }

        int indiceCarte = cartes.get(alea.nextInt(cartes.size()));
        int indiceBorne = 0;
        if (!bornesLibres.isEmpty()) {
            indiceBorne = bornesLibres.get(alea.nextInt(bornesLibres.size()));
        }
        return new int[] { indiceCarte, indiceBorne };
    }

    @Override
    public int choisirRevendication(List<Borne> bornes) {
        for (Borne b : bornes) {
            if (b.getGagnant() == 0 && b.estComplete(numero) && !dejaTentees.contains(b.getNumero())) {
                dejaTentees.add(b.getNumero());
                return b.getNumero();
            }
        }
        dejaTentees.clear();
        return 0;
    }

    @Override
    public int choisir(String invite, int min, int max) {
        return min + alea.nextInt(max - min + 1);
    }
}
