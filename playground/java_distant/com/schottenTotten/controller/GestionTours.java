package com.schottenTotten.controller;

// Numéro du tour et joueur courant (le joueur 1 commence, puis on alterne)
public class GestionTours {

    private int numeroTour = 1;

    public int getNumeroTour() {
        return numeroTour;
    }

    // 1 ou 2
    public int getNumeroJoueurCourant() {
        return (numeroTour - 1) % 2 + 1;
    }

    public void tourSuivant() {
        numeroTour++;
    }
}
