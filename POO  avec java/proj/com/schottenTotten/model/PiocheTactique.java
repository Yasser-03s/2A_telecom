package com.schottenTotten.model;

public class PiocheTactique {
    private final List<CarteTactique> cartes;

    public PiocheTactique(List<CarteTactique> cartes) { this.cartes = cartes; }

    public int getNombreCartes(){ 
        return cartes.size(); 
        }
    public boolean estVide(){ 
        return cartes.isEmpty(); 
    }
    public CarteTactique piocher(){ 
        return cartes.remove(0); 
        }
}