package com.src.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.src.model.Borne;
import com.src.model.CarteClan;
import com.src.model.Joueur;

/** IA très simple : coup aléatoire, tente de revendiquer toutes les Bornes complètes. */
public class IA implements Joueur {

    private final String nom;
    private final int numero;
    private final List<CarteClan> main = new ArrayList<>();
    private final Random alea;
    private final Set<Integer> dejaTentees = new HashSet<>();

    public IA(String nom, int numero, Random alea) {
        this.nom = nom;
        this.numero = numero;
        this.alea = alea;
    }

    @Override public String getNom()          { return nom; }
    @Override public int getNumero()          { return numero; }
    @Override public List<CarteClan> getMain() { return main; }

    @Override
    public int[] choisirCoup(List<Borne> bornes) {
        List<Integer> possibles = new ArrayList<>();
        for (int i = 0; i < bornes.size(); i++) {
            if (bornes.get(i).peutRecevoir(numero)) possibles.add(i);
        }
        if (possibles.isEmpty() || main.isEmpty()) {
            return null;
        }
        int carte = alea.nextInt(main.size());
        int borne = possibles.get(alea.nextInt(possibles.size()));
        return new int[] { carte, borne };
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
}
