package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Random;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteClan;
import com.schottenTotten.model.Combinaison;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.PiocheClan;
import com.schottenTotten.model.PiocheVideException;
import com.schottenTotten.view.InterfaceConsole;

// Variante de base : 54 cartes Clan, mains de 6 cartes, une seule pioche
public class VarianteBase extends Jeu {

    public static final int NB_BORNES = 9;

    // Composition : ces objets sont créés ici et disparaissent avec la partie
    protected ArrayList<Borne> bornes;
    protected PiocheClan piocheClan;

    public VarianteBase(Joueur j1, Joueur j2, InterfaceConsole console, GestionTours gestionTours, Random alea) {
        super(j1, j2, console, gestionTours);
        bornes = new ArrayList<Borne>();
        for (int i = 1; i <= NB_BORNES; i++) {
            bornes.add(new Borne(i, new Combinaison(), new Combinaison()));
        }
        piocheClan = new PiocheClan(alea);
    }

    // Taille de la main (6 en variante de base)
    protected int tailleMain() {
        return 6;
    }

    @Override
    public void lancer() {
        distribuer();

        while (gagnant == null) {
            Joueur joueur = joueurs[gestionTours.getNumeroJoueurCourant() - 1];
            Joueur adversaire = joueurs[2 - gestionTours.getNumeroJoueurCourant()];

            if (!peutJouer(joueur) && !peutJouer(adversaire)) {
                finParBlocage();
                break;
            }

            console.afficherTour(gestionTours.getNumeroTour(), joueur.getNom(), joueur.getNumero());
            console.afficherBornes(bornes);

            phaseJouerCarte(joueur);
            phaseRevendication(joueur);
            if (conditionVictoire(joueur.getNumero())) {
                gagnant = joueur;
                break;
            }
            phasePioche(joueur);
            gestionTours.tourSuivant();
        }

        console.afficherMessage("=== Fin de partie ===");
        console.afficherBornes(bornes);
        if (gagnant == null) {
            console.afficherVainqueur(null);
        } else {
            console.afficherVainqueur(gagnant.getNom());
        }
    }

    protected void distribuer() {
        try {
            for (int i = 0; i < tailleMain(); i++) {
                for (Joueur j : joueurs) {
                    j.getMain().add(piocheClan.piocher());
                }
            }
        } catch (PiocheVideException e) {
            console.afficherMessage(e.getMessage());
        }
    }

    //  Phase 1 : jouer une carte

    // Le joueur a-t-il au moins une carte Clan et une Borne où la poser ?
    protected boolean peutJouer(Joueur joueur) {
        boolean borneLibre = false;
        for (Borne b : bornes) {
            if (b.peutRecevoir(joueur.getNumero())) {
                borneLibre = true;
            }
        }
        if (!borneLibre) {
            return false;
        }
        for (Carte carte : joueur.getMain()) {
            if (carte instanceof CarteClan) {
                return true;
            }
        }
        return false;
    }

    // Pas de carte tactique en variante de base
    protected boolean tactiquePermise(Joueur joueur) {
        return false;
    }

    protected void phaseJouerCarte(Joueur joueur) {
        if (!peutJouer(joueur)) {
            console.afficherMessage(joueur.getNom() + " ne peut pas jouer de carte et passe.");
            return;
        }
        boolean joue = false;
        while (!joue) {
            int[] coup = joueur.choisirCoup(bornes, tactiquePermise(joueur));
            joue = jouerCoup(joueur, coup);
        }
    }

    // Applique le coup choisi par le joueur. Renvoie false s'il est refusé.
    protected boolean jouerCoup(Joueur joueur, int[] coup) {
        if (coup == null) {
            console.afficherMessage("Tu dois jouer une carte.");
            return false;
        }
        if (coup[0] < 0 || coup[0] >= joueur.getMain().size()) {
            console.afficherMessage("Carte inconnue.");
            return false;
        }
        Carte choisie = joueur.getMain().get(coup[0]);
        if (!(choisie instanceof CarteClan)) {
            console.afficherMessage("Cette carte ne peut pas être jouée.");
            return false;
        }
        if (coup[1] < 0 || coup[1] >= bornes.size() || !bornes.get(coup[1]).peutRecevoir(joueur.getNumero())) {
            console.afficherMessage("Pose impossible : Borne déjà revendiquée ou complète de ton côté.");
            return false;
        }
        Borne borne = bornes.get(coup[1]);
        borne.ajouter(joueur.getNumero(), (CarteClan) choisie);
        joueur.getMain().remove(coup[0]);
        console.afficherMessage(joueur.getNom() + " pose " + choisie + " sur la Borne " + borne.getNumero() + ".");
        return true;
    }

    //  Phase 2 : revendiquer 

    protected void phaseRevendication(Joueur joueur) {
        boolean fin = false;
        while (!fin) {
            int n = joueur.choisirRevendication(bornes);
            if (n == 0) {
                fin = true;
            } else {
                Borne borne = bornes.get(n - 1);
                if (revendicationValide(borne, joueur.getNumero())) {
                    borne.setGagnant(joueur.getNumero());
                    console.afficherMessage(joueur.getNom() + " revendique la Borne " + n + " !");
                    if (conditionVictoire(joueur.getNumero())) {
                        fin = true;
                    }
                } else {
                    console.afficherMessage("Revendication refusée pour la Borne " + n + ".");
                }
            }
        }
    }

    // Une Borne se revendique avec 3 cartes de son côté et une combinaison plus forte que celle
    // de l'adversaire. Si l'adversaire n'a pas fini de poser, il faut prouver qu'il ne pourra pas
    // nous battre, quelles que soient les cartes qui ne sont pas encore posées sur le terrain.
    public boolean revendicationValide(Borne borne, int cote) {
        if (borne.getGagnant() != 0 || !borne.estComplete(cote)) {
            return false;
        }
        int autre = 3 - cote;
        Combinaison mienne = borne.getCombinaison(cote);

        if (borne.estComplete(autre)) {
            int c = mienne.compareTo(borne.getCombinaison(autre));
            if (c != 0) {
                return c > 0;
            }
            // égalité : le premier à avoir posé sa 3e carte gagne
            return borne.getPremierComplet() == cote;
        }

        ArrayList<CarteClan> deja = borne.getCombinaison(autre).getCartes();
        int manque = Combinaison.TAILLE - deja.size();
        return adversaireNePeutPasBattre(mienne, deja, cartesNonPosees(), manque, 0);
    }

    // Essaie toutes les façons de compléter le côté adverse.
    // En cas d'égalité on gagne, car on a fini avant lui.
    private boolean adversaireNePeutPasBattre(Combinaison mienne, ArrayList<CarteClan> deja,
            ArrayList<CarteClan> restantes, int manque, int debut) {
        if (manque == 0) {
            Combinaison essai = new Combinaison();
            for (CarteClan carte : deja) {
                essai.ajouter(carte);
            }
            return mienne.compareTo(essai) >= 0;
        }
        for (int i = debut; i < restantes.size(); i++) {
            deja.add(restantes.get(i));
            boolean ok = adversaireNePeutPasBattre(mienne, deja, restantes, manque - 1, i + 1);
            deja.remove(deja.size() - 1);
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    // Cartes Clan qui ne sont posées sur aucune Borne (donc encore possibles pour l'adversaire)
    private ArrayList<CarteClan> cartesNonPosees() {
        boolean[][] posee = new boolean[CarteClan.COULEURS.length][10];
        for (Borne b : bornes) {
            for (int cote = 1; cote <= 2; cote++) {
                for (CarteClan c : b.getCombinaison(cote).getCartes()) {
                    posee[c.getCouleur()][c.getValeur()] = true;
                }
            }
        }
        ArrayList<CarteClan> restantes = new ArrayList<CarteClan>();
        for (int couleur = 0; couleur < CarteClan.COULEURS.length; couleur++) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                if (!posee[couleur][valeur]) {
                    restantes.add(new CarteClan(couleur, valeur));
                }
            }
        }
        return restantes;
    }

    //  Victoire 

    // 5 Bornes au total ou 3 Bornes adjacentes
    protected boolean conditionVictoire(int cote) {
        int total = 0;
        int suite = 0;
        for (Borne b : bornes) {
            if (b.getGagnant() == cote) {
                total++;
                suite++;
                if (suite >= 3) {
                    return true;
                }
            } else {
                suite = 0;
            }
        }
        return total >= 5;
    }

    protected int compterBornes(int cote) {
        int n = 0;
        for (Borne b : bornes) {
            if (b.getGagnant() == cote) {
                n++;
            }
        }
        return n;
    }

    //  Phase 3 : piocher 

    protected void phasePioche(Joueur joueur) {
        if (joueur.getMain().size() >= tailleMain() || piocheClan.estVide()) {
            return;
        }
        try {
            joueur.getMain().add(piocheClan.piocher());
            console.afficherMessage(joueur.getNom() + " pioche une carte (reste " + piocheClan.getNombreCartes() + ").");
        } catch (PiocheVideException e) {
            console.afficherMessage(e.getMessage());
        }
    }

    //  Fin de partie par blocage 

    private void finParBlocage() {
        int a = compterBornes(1);
        int b = compterBornes(2);
        console.afficherMessage("Plus aucun coup possible : le gagnant est celui qui a le plus de Bornes (" + a + " contre " + b + ").");
        if (a > b) {
            gagnant = joueurs[0];
        } else if (b > a) {
            gagnant = joueurs[1];
        }
    }
}
