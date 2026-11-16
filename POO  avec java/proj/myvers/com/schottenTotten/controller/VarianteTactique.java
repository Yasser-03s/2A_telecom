package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Random;

import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteClan;
import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.PiocheTactique;
import com.schottenTotten.model.PiocheVideException;
import com.schottenTotten.view.InterfaceConsole;

// Variante tactique : mains de 7 cartes, deux pioches (Clan et tactique).
// Cartes tactiques gérées : Joker, Espion, Porte-bouclier, Chasseur de tête, Stratège, Banshee, Traître.
// (Colin-Maillard et Combat de boue ne sont pas implémentés.)
public class VarianteTactique extends VarianteBase {

    protected PiocheTactique piocheTactique;
    private int[] nbTactiquesJouees;

    public VarianteTactique(Joueur j1, Joueur j2, InterfaceConsole console, GestionTours gestionTours, Random alea) {
        super(j1, j2, console, gestionTours, alea);
        piocheTactique = new PiocheTactique(alea);
        nbTactiquesJouees = new int[2];
    }

    @Override
    protected int tailleMain() {
        return 7;
    }

    // On ne peut pas jouer de carte tactique si on en a déjà joué plus que l'adversaire
    @Override
    protected boolean tactiquePermise(Joueur joueur) {
        return nbTactiquesJouees[joueur.getNumero() - 1] <= nbTactiquesJouees[2 - joueur.getNumero()];
    }

    @Override
    protected boolean peutJouer(Joueur joueur) {
        if (super.peutJouer(joueur)) {
            return true;
        }
        if (!tactiquePermise(joueur)) {
            return false;
        }
        for (Carte carte : joueur.getMain()) {
            if (carte instanceof CarteTactique && tactiqueJouable(joueur, (CarteTactique) carte)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Jouer une carte tactique ----------

    @Override
    protected boolean jouerCoup(Joueur joueur, int[] coup) {
        boolean coupValide = coup != null && coup[0] >= 0 && coup[0] < joueur.getMain().size();
        if (!coupValide || !(joueur.getMain().get(coup[0]) instanceof CarteTactique)) {
            return super.jouerCoup(joueur, coup);
        }

        CarteTactique tactique = (CarteTactique) joueur.getMain().get(coup[0]);
        if (!tactiquePermise(joueur)) {
            console.afficherMessage("Carte tactique refusée : tu en as déjà joué plus que ton adversaire.");
            return false;
        }
        if (!tactiqueJouable(joueur, tactique)) {
            console.afficherMessage(tactique.getNom() + " ne peut pas être jouée maintenant.");
            return false;
        }
        if (tactique.estTroupe() && (coup[1] < 0 || coup[1] >= bornes.size() || !bornes.get(coup[1]).peutRecevoir(joueur.getNumero()))) {
            console.afficherMessage("Pose impossible : Borne déjà revendiquée ou complète de ton côté.");
            return false;
        }

        // La carte quitte la main avant d'être jouée
        joueur.getMain().remove(coup[0]);
        nbTactiquesJouees[joueur.getNumero() - 1]++;

        if (tactique.estTroupe()) {
            jouerTroupe(joueur, tactique, coup[1]);
        } else if (tactique.getNom().equals(CarteTactique.CHASSEUR)) {
            jouerChasseur(joueur);
        } else if (tactique.getNom().equals(CarteTactique.STRATEGE)) {
            jouerStratege(joueur);
        } else if (tactique.getNom().equals(CarteTactique.BANSHEE)) {
            jouerBanshee(joueur);
        } else {
            jouerTraitre(joueur);
        }
        return true;
    }

    // Vrai si la carte a au moins une façon d'être jouée (sans regarder le nombre de tactiques jouées)
    private boolean tactiqueJouable(Joueur joueur, CarteTactique carte) {
        int numero = joueur.getNumero();
        if (carte.estTroupe()) {
            for (Borne b : bornes) {
                if (b.peutRecevoir(numero)) {
                    return true;
                }
            }
            return false;
        }
        if (carte.getNom().equals(CarteTactique.CHASSEUR)) {
            return !piocheClan.estVide() || !piocheTactique.estVide();
        }
        if (carte.getNom().equals(CarteTactique.STRATEGE)) {
            return !sourcesStratege(numero).isEmpty();
        }
        if (carte.getNom().equals(CarteTactique.BANSHEE)) {
            return !cartesPosees(3 - numero).isEmpty();
        }
        // Traître
        return !cartesPosees(3 - numero).isEmpty() && !bornesDestination(numero, -1).isEmpty();
    }

    // Troupes d'élite : elles se posent comme une carte Clan dont le joueur choisit valeur et couleur
    private void jouerTroupe(Joueur joueur, CarteTactique tactique, int indiceBorne) {
        int valeur = 7;
        if (tactique.getNom().equals(CarteTactique.JOKER)) {
            valeur = joueur.choisir("Valeur du Joker", 1, 9);
        } else if (tactique.getNom().equals(CarteTactique.PORTE_BOUCLIER)) {
            valeur = joueur.choisir("Valeur du Porte-bouclier", 1, 3);
        }
        String couleurs = "";
        for (int i = 0; i < CarteClan.COULEURS.length; i++) {
            couleurs = couleurs + (i + 1) + ")" + CarteClan.COULEURS[i] + " ";
        }
        console.afficherMessage("Couleurs : " + couleurs);
        int couleur = joueur.choisir("Couleur", 1, CarteClan.COULEURS.length) - 1;

        CarteClan carte = new CarteClan(couleur, valeur);
        bornes.get(indiceBorne).ajouter(joueur.getNumero(), carte);
        console.afficherMessage(joueur.getNom() + " joue " + tactique + " : " + carte + " sur la Borne " + (indiceBorne + 1) + ".");
    }

    // Chasseur de tête : on pioche 3 cartes (dans les pioches de son choix), puis on en remet 2 sous les pioches
    private void jouerChasseur(Joueur joueur) {
        int total = piocheClan.getNombreCartes() + piocheTactique.getNombreCartes();
        int nb = 3;
        if (total < 3) {
            nb = total;
        }
        console.afficherMessage(joueur.getNom() + " joue Chasseur de tête et pioche " + nb + " carte(s).");
        for (int i = 0; i < nb; i++) {
            piocherAvecChoix(joueur);
        }
        for (int i = 0; i < nb - 1; i++) {
            console.afficherMain(joueur.getMain());
            int n = joueur.choisir("Carte à remettre sous sa pioche", 1, joueur.getMain().size());
            Carte carte = joueur.getMain().remove(n - 1);
            if (carte instanceof CarteClan) {
                piocheClan.remettreDessous((CarteClan) carte);
            } else {
                piocheTactique.remettreDessous((CarteTactique) carte);
            }
        }
    }

    // Stratège : déplacer une de ses cartes vers une autre Borne non revendiquée
    private void jouerStratege(Joueur joueur) {
        int numero = joueur.getNumero();
        ArrayList<int[]> sources = sourcesStratege(numero);
        console.afficherMessage(joueur.getNom() + " joue Stratège. Cartes déplaçables :");
        int[] source = choisirCible(joueur, sources, numero, "Carte à déplacer");

        ArrayList<Integer> destinations = bornesDestination(numero, source[0]);
        String texte = "Destinations :";
        for (int i = 0; i < destinations.size(); i++) {
            texte = texte + "  " + (i + 1) + ") Borne " + (destinations.get(i) + 1);
        }
        console.afficherMessage(texte);
        int d = joueur.choisir("Borne de destination", 1, destinations.size());

        CarteClan carte = bornes.get(source[0]).retirer(numero, source[1]);
        bornes.get(destinations.get(d - 1)).ajouter(numero, carte);
        console.afficherMessage(carte + " passe de la Borne " + (source[0] + 1) + " à la Borne " + (destinations.get(d - 1) + 1) + ".");
    }

    // Banshee : défausser une carte de l'adversaire posée sur une Borne non revendiquée
    private void jouerBanshee(Joueur joueur) {
        int autre = 3 - joueur.getNumero();
        console.afficherMessage(joueur.getNom() + " joue Banshee. Cartes de l'adversaire :");
        int[] cible = choisirCible(joueur, cartesPosees(autre), autre, "Carte à défausser");
        CarteClan carte = bornes.get(cible[0]).retirer(autre, cible[1]);
        console.afficherMessage(carte + " est défaussée de la Borne " + (cible[0] + 1) + ".");
    }

    // Traître : prendre une carte de l'adversaire et la poser de son côté sur une Borne non revendiquée
    private void jouerTraitre(Joueur joueur) {
        int numero = joueur.getNumero();
        int autre = 3 - numero;
        console.afficherMessage(joueur.getNom() + " joue Traître. Cartes de l'adversaire :");
        int[] cible = choisirCible(joueur, cartesPosees(autre), autre, "Carte à prendre");

        ArrayList<Integer> destinations = bornesDestination(numero, -1);
        String texte = "Destinations :";
        for (int i = 0; i < destinations.size(); i++) {
            texte = texte + "  " + (i + 1) + ") Borne " + (destinations.get(i) + 1);
        }
        console.afficherMessage(texte);
        int d = joueur.choisir("Borne de destination", 1, destinations.size());

        CarteClan carte = bornes.get(cible[0]).retirer(autre, cible[1]);
        bornes.get(destinations.get(d - 1)).ajouter(numero, carte);
        console.afficherMessage(carte + " passe du côté de " + joueur.getNom() + " sur la Borne " + (destinations.get(d - 1) + 1) + ".");
    }

    // ---------- Outils pour les ruses ----------

    // Cartes posées par "cote" sur des Bornes non revendiquées : {indice de la Borne, indice de la carte}
    private ArrayList<int[]> cartesPosees(int cote) {
        ArrayList<int[]> liste = new ArrayList<int[]>();
        for (int i = 0; i < bornes.size(); i++) {
            if (bornes.get(i).getGagnant() == 0) {
                int nb = bornes.get(i).getCombinaison(cote).getNombre();
                for (int k = 0; k < nb; k++) {
                    liste.add(new int[] { i, k });
                }
            }
        }
        return liste;
    }

    // Indices des Bornes où "cote" peut encore poser une carte, sauf la Borne "exclue"
    private ArrayList<Integer> bornesDestination(int cote, int exclue) {
        ArrayList<Integer> liste = new ArrayList<Integer>();
        for (int i = 0; i < bornes.size(); i++) {
            if (i != exclue && bornes.get(i).peutRecevoir(cote)) {
                liste.add(i);
            }
        }
        return liste;
    }

    // Cartes qu'on peut déplacer avec le Stratège : il faut une autre Borne où les poser
    private ArrayList<int[]> sourcesStratege(int numero) {
        ArrayList<int[]> sources = new ArrayList<int[]>();
        for (int[] cible : cartesPosees(numero)) {
            if (!bornesDestination(numero, cible[0]).isEmpty()) {
                sources.add(cible);
            }
        }
        return sources;
    }

    // Affiche la liste des cartes ciblables et demande au joueur d'en choisir une
    private int[] choisirCible(Joueur joueur, ArrayList<int[]> cibles, int cote, String invite) {
        for (int i = 0; i < cibles.size(); i++) {
            CarteClan carte = bornes.get(cibles.get(i)[0]).getCombinaison(cote).getCartes().get(cibles.get(i)[1]);
            console.afficherMessage("  " + (i + 1) + ") " + carte + " (Borne " + (cibles.get(i)[0] + 1) + ")");
        }
        int choix = joueur.choisir(invite, 1, cibles.size());
        return cibles.get(choix - 1);
    }

    // ---------- Pioche : on choisit entre les deux pioches ----------

    @Override
    protected void phasePioche(Joueur joueur) {
        if (joueur.getMain().size() < tailleMain()) {
            piocherAvecChoix(joueur);
        }
    }

    private void piocherAvecChoix(Joueur joueur) {
        boolean clan = !piocheClan.estVide();
        boolean tactique = !piocheTactique.estVide();
        if (!clan && !tactique) {
            return;
        }
        int choix = 1;
        if (clan && tactique) {
            choix = joueur.choisir("Piocher : 1 = Clan (" + piocheClan.getNombreCartes() + "), 2 = Tactique ("
                    + piocheTactique.getNombreCartes() + ")", 1, 2);
        } else if (tactique) {
            choix = 2;
        }
        try {
            if (choix == 1) {
                joueur.getMain().add(piocheClan.piocher());
            } else {
                joueur.getMain().add(piocheTactique.piocher());
            }
            console.afficherMessage(joueur.getNom() + " pioche une carte (reste " + piocheClan.getNombreCartes()
                    + " Clan, " + piocheTactique.getNombreCartes() + " tactique).");
        } catch (PiocheVideException e) {
            console.afficherMessage(e.getMessage());
        }
    }
}
