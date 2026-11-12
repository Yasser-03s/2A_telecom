package com.schottenTotten.model;

public class PiocheClan {
    private final List<CarteClan> cartes;

    public PiocheClan(List<CarteClan> cartes) { this.cartes = cartes; }

    public int getNombreCartes(){ 
        return cartes.size(); 
        }
    public boolean estVide(){ 
        return cartes.isEmpty(); 
        }
    public CarteClan piocher(){ 
        return cartes.remove(0);
        }
}