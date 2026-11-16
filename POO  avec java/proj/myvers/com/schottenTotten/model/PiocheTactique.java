package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Pioche des 8 cartes tactiques jouées dans la variante tactique, mélangée
public class PiocheTactique {

    private ArrayList<CarteTactique> cartes;

    public PiocheTactique(Random alea) {
        cartes = new ArrayList<CarteTactique>();
        cartes.add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));
        cartes.add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));
        cartes.add(new CarteTactique(CarteTactique.ESPION, CarteTactique.TROUPE));
        cartes.add(new CarteTactique(CarteTactique.PORTE_BOUCLIER, CarteTactique.TROUPE));
        cartes.add(new CarteTactique(CarteTactique.CHASSEUR, CarteTactique.RUSE));
        cartes.add(new CarteTactique(CarteTactique.STRATEGE, CarteTactique.RUSE));
        cartes.add(new CarteTactique(CarteTactique.BANSHEE, CarteTactique.RUSE));
        cartes.add(new CarteTactique(CarteTactique.TRAITRE, CarteTactique.RUSE));
        Collections.shuffle(cartes, alea);
    }

    public int getNombreCartes() {
        return cartes.size();
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public CarteTactique piocher() throws PiocheVideException {
        if (cartes.isEmpty()) {
            throw new PiocheVideException("La pioche tactique est vide");
        }
        return cartes.remove(cartes.size() - 1);
    }

    public void remettreDessous(CarteTactique carte) {
        cartes.add(0, carte);
    }
}
