package com.schottenTotten.model;

// Exception vérifiée : on essaie de piocher dans une pioche vide
public class PiocheVideException extends Exception {

    public PiocheVideException(String message) {
        super(message);
    }
}
