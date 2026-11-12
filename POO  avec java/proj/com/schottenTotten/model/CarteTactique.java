package com.schottenTotten.model;

public class CarteTactique extends Carte {
    private final TypeTactique type;
    private final String effet;          // description de la capacité

    public CarteTactique(TypeTactique type, String effet) {
        this.type = type;
        this.effet = effet;
    }

    public TypeTactique getType() { 
        return type; 
        }
    public String getEffet()      { 
        return effet; 
        }

    @Override
    public String toString() {
        return type + " " + effet;
    }
}