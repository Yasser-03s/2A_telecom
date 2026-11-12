package com.src.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Pioche des 54 cartes Clan (6 couleurs x valeurs 1 à 9), mélangée. */
public class PiocheClan {

    private final List<CarteClan> cartes = new ArrayList<>();

    public PiocheClan(Random alea) {
        for (Couleur c : Couleur.values()) {
            for (int v = 1; v <= 9; v++) {
                cartes.add(new CarteClan(c, v));
            }
        }
        Collections.shuffle(cartes, alea);
    }

    public int getNombreCartes() { return cartes.size(); }
    public boolean estVide()     { return cartes.isEmpty(); }

    public CarteClan piocher() {
        return cartes.remove(cartes.size() - 1);
    }
}
