package com.src.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.src.model.Borne;
import com.src.model.CarteClan;
import com.src.model.Combinaison;
import com.src.model.Couleur;
import com.src.model.Joueur;
import com.src.model.PiocheClan;
import com.src.view.InterfaceConsole;

/** Variante de base : 54 cartes Clan, mains de 6 cartes, une seule pioche. */
public class VarianteBase extends Jeu {

    protected final PiocheClan piocheClan;

    public VarianteBase(Joueur j1, Joueur j2, InterfaceConsole console, Random alea) {
        super(j1, j2, console);
        this.piocheClan = new PiocheClan(alea);
    }

    /** Taille de la main (6 en variante de base). */
    protected int tailleMain() {
        return 6;
    }

    @Override
    public void lancer() {
        for (int i = 0; i < tailleMain(); i++) {
            for (Joueur j : joueurs) {
                j.getMain().add(piocheClan.piocher());
            }
        }

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

        console.afficherTour(gestionTours.getNumeroTour(), "Fin de partie", 0);
        console.afficherBornes(bornes);
        console.afficherVainqueur(gagnant == null ? null : gagnant.getNom());
    }

    // ---------- Phase 1 : jouer une carte ----------

    private boolean peutJouer(Joueur j) {
        if (j.getMain().isEmpty()) return false;
        for (Borne b : bornes) {
            if (b.peutRecevoir(j.getNumero())) return true;
        }
        return false;
    }

    private void phaseJouerCarte(Joueur joueur) {
        int[] coup = joueur.choisirCoup(bornes);
        if (coup == null) {
            console.afficherMessage(joueur.getNom() + " ne peut pas jouer de carte et passe.");
            return;
        }
        CarteClan carte = joueur.getMain().remove(coup[0]);
        Borne borne = bornes.get(coup[1]);
        borne.ajouter(joueur.getNumero(), carte);
        console.afficherMessage(joueur.getNom() + " pose " + carte + " sur la Borne " + borne.getNumero() + ".");
    }

    // ---------- Phase 2 : revendiquer ----------

    private void phaseRevendication(Joueur joueur) {
        while (true) {
            int n = joueur.choisirRevendication(bornes);
            if (n == 0) return;
            Borne borne = bornes.get(n - 1);
            if (revendicationValide(borne, joueur.getNumero())) {
                borne.setGagnant(joueur.getNumero());
                console.afficherMessage(joueur.getNom() + " revendique la Borne " + n + " !");
                if (conditionVictoire(joueur.getNumero())) return;
            } else {
                console.afficherMessage("Revendication refusée pour la Borne " + n + ".");
            }
        }
    }

    /**
     * Une Borne se revendique avec 3 cartes de son côté, et une combinaison plus forte que celle de l'adversaire.
     * Si l'adversaire n'a pas posé ses 3 cartes, il faut prouver qu'il ne pourra pas nous battre,
     * quelles que soient les cartes qui ne sont pas encore posées sur le terrain.
     */
    private boolean revendicationValide(Borne borne, int cote) {
        if (borne.getGagnant() != 0 || !borne.estComplete(cote)) {
            return false;
        }
        int autre = 3 - cote;
        Combinaison mienne = Combinaison.evaluer(borne.getCartes(cote));

        if (borne.estComplete(autre)) {
            Combinaison adverse = Combinaison.evaluer(borne.getCartes(autre));
            int c = mienne.compareTo(adverse);
            if (c != 0) return c > 0;
            return borne.getPremierComplet() == cote;   // égalité : le premier à avoir posé sa 3e carte
        }

        List<CarteClan> deja = new ArrayList<>(borne.getCartes(autre));
        int manque = 3 - deja.size();
        return adversaireNePeutPasBattre(mienne, deja, cartesNonPosees(), manque, 0);
    }

    /** Teste toutes les façons de compléter le côté adverse. L'égalité profite à celui qui a fini en premier (nous). */
    private boolean adversaireNePeutPasBattre(Combinaison mienne, List<CarteClan> deja,
                                              List<CarteClan> restantes, int manque, int debut) {
        if (manque == 0) {
            return mienne.compareTo(Combinaison.evaluer(deja)) >= 0;
        }
        for (int i = debut; i < restantes.size(); i++) {
            deja.add(restantes.get(i));
            boolean ok = adversaireNePeutPasBattre(mienne, deja, restantes, manque - 1, i + 1);
            deja.remove(deja.size() - 1);
            if (!ok) return false;
        }
        return true;
    }

    /** Cartes qui ne sont posées sur aucune Borne (donc encore possibles pour l'adversaire). */
    private List<CarteClan> cartesNonPosees() {
        boolean[][] posee = new boolean[Couleur.values().length][10];
        for (Borne b : bornes) {
            for (int cote = 1; cote <= 2; cote++) {
                for (CarteClan c : b.getCartes(cote)) {
                    posee[c.getCouleur().ordinal()][c.getValeur()] = true;
                }
            }
        }
        List<CarteClan> restantes = new ArrayList<>();
        for (Couleur col : Couleur.values()) {
            for (int v = 1; v <= 9; v++) {
                if (!posee[col.ordinal()][v]) restantes.add(new CarteClan(col, v));
            }
        }
        return restantes;
    }

    // ---------- Phase 3 : piocher ----------

    private void phasePioche(Joueur joueur) {
        if (joueur.getMain().size() < tailleMain() && !piocheClan.estVide()) {
            joueur.getMain().add(piocheClan.piocher());
            console.afficherMessage(joueur.getNom() + " pioche une carte (reste " + piocheClan.getNombreCartes() + ").");
        }
    }

    // ---------- Fin de partie par blocage ----------

    private void finParBlocage() {
        int a = compterBornes(1);
        int b = compterBornes(2);
        console.afficherMessage("Plus aucun coup possible : le gagnant est celui qui a le plus de Bornes (" + a + " contre " + b + ").");
        if (a > b) gagnant = joueurs[0];
        else if (b > a) gagnant = joueurs[1];
    }
}
