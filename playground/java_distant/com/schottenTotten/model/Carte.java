package com.schottenTotten.model;

// Carte du jeu. Classe abstraite : on ne crée jamais une "Carte" seule,
// seulement une CarteClan ou une CarteTactique.
public abstract class Carte {

    public abstract String getNom();

    @Override
    public String toString() {
        return getNom();
    }
}
