package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Pioche des 54 cartes Clan (6 couleurs x valeurs de 1 à 9), mélangée
public class PiocheClan {

    private ArrayList<CarteClan> cartes;

    public PiocheClan(Random alea) {
        cartes = new ArrayList<CarteClan>();
        for (int couleur = 0; couleur < CarteClan.COULEURS.length; couleur++) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                cartes.add(new CarteClan(couleur, valeur));
            }
        }
        Collections.shuffle(cartes, alea);
    }

    public int getNombreCartes() {
        return cartes.size();
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public CarteClan piocher() throws PiocheVideException {
        if (cartes.isEmpty()) {
            throw new PiocheVideException("La pioche Clan est vide");
        }
        return cartes.remove(cartes.size() - 1);
    }

    // On pioche à la fin de la liste, donc "dessous" = début de la liste
    public void remettreDessous(CarteClan carte) {
        cartes.add(0, carte);
    }
}
