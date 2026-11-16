package com.schottenTotten.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.schottenTotten.ai.IA;
import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteClan;
import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Combinaison;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.PiocheClan;
import com.schottenTotten.model.PiocheTactique;
import com.schottenTotten.model.PiocheVideException;
import com.schottenTotten.view.InterfaceConsole;

/*
 * JUnit : chaque méthode marquée @Test est un test. On prépare une situation,
 * on appelle notre code, puis on vérifie avec des assertions :
 *   assertEquals(attendu, obtenu)    les deux valeurs doivent être égales
 *   assertTrue(c) / assertFalse(c)   la condition doit être vraie / fausse
 *   assertThrows(Exc.class, code)    le code doit lever cette exception
 * @BeforeEach : méthode exécutée avant chaque test (chaque test repart d'une partie neuve).
 *
 * Ce fichier est dans le package com.schottenTotten.controller pour accéder aux membres
 * protected de VarianteBase (bornes, conditionVictoire, jouerCoup...).
 */
class SchottenTottenTest {

    private static final int ROUGE = 0;
    private static final int BLEU = 1;
    private static final int VERT = 2;
    private static final int MAUVE = 3;

    private VarianteBase jeu;
    private VarianteTactique jeuTactique;
    private InterfaceConsole console;

    // Joueur dont les réponses sont écrites à l'avance : sert à tester les ruses sans clavier.
    // C'est une classe imbriquée statique (cours p.113).
    private static class JoueurScripte implements Joueur {

        private int numero;
        private ArrayList<Carte> main = new ArrayList<Carte>();
        private int[] reponses;
        private int position = 0;

        JoueurScripte(int numero, int... reponses) {
            this.numero = numero;
            this.reponses = reponses;
        }

        @Override
        public String getNom() {
            return "Joueur " + numero;
        }

        @Override
        public int getNumero() {
            return numero;
        }

        @Override
        public List<Carte> getMain() {
            return main;
        }

        @Override
        public int[] choisirCoup(List<Borne> bornes, boolean tactiquePermise) {
            return null;
        }

        @Override
        public int choisirRevendication(List<Borne> bornes) {
            return 0;
        }

        @Override
        public int choisir(String invite, int min, int max) {
            int reponse = reponses[position];
            position++;
            return reponse;
        }
    }

    @BeforeEach
    void preparer() {
        Random alea = new Random(1);
        console = new InterfaceConsole();
        jeu = new VarianteBase(new IA("A", 1, alea), new IA("B", 2, alea), console, new GestionTours(), alea);
    }

    // Petites aides pour écrire les tests plus court
    private static CarteClan carte(int couleur, int valeur) {
        return new CarteClan(couleur, valeur);
    }

    private static Combinaison combinaison(CarteClan... cartes) {
        Combinaison c = new Combinaison();
        for (CarteClan carte : cartes) {
            c.ajouter(carte);
        }
        return c;
    }

    private void poser(Borne borne, int cote, CarteClan... cartes) {
        for (CarteClan c : cartes) {
            borne.ajouter(cote, c);
        }
    }

    private void gagner(int joueur, int... numerosDeBornes) {
        for (int n : numerosDeBornes) {
            jeu.bornes.get(n - 1).setGagnant(joueur);
        }
    }

    // Prépare une partie tactique avec deux joueurs scriptés
    private void preparerTactique(JoueurScripte j1, JoueurScripte j2) {
        Random alea = new Random(1);
        jeuTactique = new VarianteTactique(j1, j2, console, new GestionTours(), alea);
    }

    // =====================================================================
    // 1. CLASSES DU MODÈLE
    // =====================================================================

    // Une carte Clan n'accepte que les valeurs de 1 à 9 et les couleurs de 0 à 5
    @Test
    void carteClanRefuseUneValeurOuUneCouleurInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new CarteClan(ROUGE, 0));
        assertThrows(IllegalArgumentException.class, () -> new CarteClan(ROUGE, 10));
        assertThrows(IllegalArgumentException.class, () -> new CarteClan(6, 5));
    }

    // Une carte s'affiche avec sa valeur puis l'initiale de sa couleur : un 7 vert donne "7V"
    @Test
    void carteClanSAfficheValeurPuisInitialeDeLaCouleur() {
        assertEquals("7V", carte(VERT, 7).toString());
    }

    // Une pioche neuve contient 54 cartes (6 couleurs x 9 valeurs) et piocher en retire une
    @Test
    void piocheContient54CartesEtPiocherEnRetireUne() throws PiocheVideException {
        PiocheClan pioche = new PiocheClan(new Random(1));
        assertEquals(54, pioche.getNombreCartes());
        pioche.piocher();
        assertEquals(53, pioche.getNombreCartes());
    }

    // Piocher dans une pioche vide lève PiocheVideException
    @Test
    void piocherDansUnePiocheVideLeveUneException() throws PiocheVideException {
        PiocheClan pioche = new PiocheClan(new Random(1));
        for (int i = 0; i < 54; i++) {
            pioche.piocher();
        }
        assertTrue(pioche.estVide());
        assertThrows(PiocheVideException.class, () -> pioche.piocher());
    }

    // La pioche tactique contient 8 cartes
    @Test
    void piocheTactiqueContient8Cartes() {
        assertEquals(8, new PiocheTactique(new Random(1)).getNombreCartes());
    }

    // Remettre une carte sous la pioche la fait réapparaître en dernier
    @Test
    void remettreDessousAjouteUneCarte() throws PiocheVideException {
        PiocheClan pioche = new PiocheClan(new Random(1));
        CarteClan piochee = pioche.piocher();
        pioche.remettreDessous(piochee);
        assertEquals(54, pioche.getNombreCartes());
    }

    // Une Combinaison refuse une 4e carte
    @Test
    void combinaisonRefuseUneQuatriemeCarte() {
        Combinaison c = combinaison(carte(ROUGE, 1), carte(ROUGE, 2), carte(ROUGE, 3));
        assertTrue(c.estComplete());
        assertThrows(IllegalStateException.class, () -> c.ajouter(carte(ROUGE, 4)));
    }

    // On ne peut pas évaluer le type d'une combinaison incomplète
    @Test
    void combinaisonIncompleteNaPasDeType() {
        Combinaison c = combinaison(carte(ROUGE, 1));
        assertThrows(IllegalStateException.class, () -> c.getType());
    }

    // Une Borne accepte 3 cartes par côté, pas 4 ; le côté adverse reste libre
    @Test
    void borneRefuseUneQuatriemeCarteDuMemeCote() {
        Borne borne = new Borne(1, new Combinaison(), new Combinaison());
        poser(borne, 1, carte(ROUGE, 1), carte(ROUGE, 2), carte(ROUGE, 3));
        assertFalse(borne.peutRecevoir(1));
        assertTrue(borne.peutRecevoir(2));
        assertThrows(IllegalStateException.class, () -> borne.ajouter(1, carte(ROUGE, 4)));
    }

    // Quand on retire une carte du premier côté complet, c'est l'autre côté qui devient "premier"
    @Test
    void retirerUneCarteMetAJourLePremierComplet() {
        Borne borne = new Borne(1, new Combinaison(), new Combinaison());
        poser(borne, 1, carte(ROUGE, 1), carte(ROUGE, 2), carte(ROUGE, 3));
        poser(borne, 2, carte(BLEU, 1), carte(BLEU, 2), carte(BLEU, 3));
        assertEquals(1, borne.getPremierComplet());
        borne.retirer(1, 0);
        assertEquals(2, borne.getPremierComplet());
        assertFalse(borne.estComplete(1));
    }

    // Un brelan (3 cartes de même valeur) bat une suite
    @Test
    void unBrelanBatUneSuite() {
        Combinaison brelan = combinaison(carte(ROUGE, 2), carte(BLEU, 2), carte(VERT, 2));
        Combinaison suite = combinaison(carte(ROUGE, 7), carte(BLEU, 8), carte(VERT, 9));
        assertTrue(brelan.compareTo(suite) > 0);
        assertTrue(suite.compareTo(brelan) < 0);
    }

    // Une suite couleur bat un brelan
    @Test
    void uneSuiteCouleurBatUnBrelan() {
        Combinaison suiteCouleur = combinaison(carte(ROUGE, 1), carte(ROUGE, 2), carte(ROUGE, 3));
        Combinaison brelan = combinaison(carte(ROUGE, 9), carte(BLEU, 9), carte(VERT, 9));
        assertEquals(Combinaison.SUITE_COULEUR, suiteCouleur.getType());
        assertTrue(suiteCouleur.compareTo(brelan) > 0);
    }

    // À type égal, la somme la plus haute gagne : 5-6-7 bat 2-3-4
    @Test
    void aTypeEgalLaSommeLaPlusHauteGagne() {
        Combinaison forte = combinaison(carte(ROUGE, 5), carte(BLEU, 6), carte(VERT, 7));
        Combinaison faible = combinaison(carte(ROUGE, 2), carte(BLEU, 3), carte(VERT, 4));
        assertTrue(forte.compareTo(faible) > 0);
    }

    // =====================================================================
    // 2. REVENDICATION D'UNE BORNE
    // =====================================================================

    // Cas normal : 3 cartes de chaque côté, le brelan de 9 bat la suite
    @Test
    void ilFautUneCombinaisonPlusFortePourRevendiquer() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 9), carte(BLEU, 9), carte(VERT, 9));
        poser(borne, 2, carte(ROUGE, 1), carte(BLEU, 2), carte(MAUVE, 3));
        assertTrue(jeu.revendicationValide(borne, 1));
        assertFalse(jeu.revendicationValide(borne, 2));
    }

    // En cas d'égalité parfaite, celui qui a posé sa 3e carte en premier gagne
    @Test
    void enCasDEgaliteLePremierCompletGagne() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 9), carte(ROUGE, 8), carte(ROUGE, 7));
        poser(borne, 2, carte(BLEU, 9), carte(BLEU, 8), carte(BLEU, 7));
        assertTrue(jeu.revendicationValide(borne, 1));
        assertFalse(jeu.revendicationValide(borne, 2));
    }

    // On ne peut pas revendiquer sans avoir posé ses 3 cartes
    @Test
    void onNePeutPasRevendiquerAvecMoinsDeTroisCartes() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 9), carte(ROUGE, 8));
        assertFalse(jeu.revendicationValide(borne, 1));
    }

    // L'adversaire n'a rien posé et un brelan de 5 peut encore être battu : refusé
    @Test
    void revendicationRefuseeSiLAdversairePeutEncoreMieuxFaire() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 5), carte(BLEU, 5), carte(VERT, 5));
        assertFalse(jeu.revendicationValide(borne, 1));
    }

    // 9-8-7 de la même couleur : rien ne peut le battre, la revendication est acceptée
    @Test
    void revendicationAccepteeSiRienNePeutBattreSaCombinaison() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 9), carte(ROUGE, 8), carte(ROUGE, 7));
        assertTrue(jeu.revendicationValide(borne, 1));
    }

    // Une Borne déjà revendiquée ne se revendique plus
    @Test
    void uneBorneDejaRevendiqueeNeSeRevendiquePlus() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(ROUGE, 9), carte(ROUGE, 8), carte(ROUGE, 7));
        borne.setGagnant(2);
        assertFalse(jeu.revendicationValide(borne, 1));
    }

    // =====================================================================
    // 3. CONDITIONS DE VICTOIRE
    // =====================================================================

    @Test
    void personneNeGagneAuDebut() {
        assertFalse(jeu.conditionVictoire(1));
        assertFalse(jeu.conditionVictoire(2));
    }

    // 5 Bornes dispersées (1, 3, 5, 7, 9) donnent la victoire
    @Test
    void cinqBornesDisperseesDonnentLaVictoire() {
        gagner(1, 1, 3, 5, 7, 9);
        assertTrue(jeu.conditionVictoire(1));
    }

    // 4 Bornes dispersées ne suffisent pas
    @Test
    void quatreBornesDisperseesNeSuffisentPas() {
        gagner(1, 1, 3, 5, 7);
        assertFalse(jeu.conditionVictoire(1));
    }

    // 3 Bornes adjacentes (4, 5, 6) donnent la victoire
    @Test
    void troisBornesAdjacentesDonnentLaVictoire() {
        gagner(2, 4, 5, 6);
        assertTrue(jeu.conditionVictoire(2));
    }

    // Une Borne de l'adversaire coupe la série
    @Test
    void uneBorneAdverseInterrompLaSerie() {
        gagner(1, 4, 6);
        gagner(2, 5);
        assertFalse(jeu.conditionVictoire(1));
    }

    // =====================================================================
    // 4. VARIANTE TACTIQUE
    // =====================================================================

    // La main passe de 6 (base) à 7 cartes (tactique)
    @Test
    void laTailleDeMainDependDeLaVariante() {
        preparerTactique(new JoueurScripte(1), new JoueurScripte(2));
        assertEquals(6, jeu.tailleMain());
        assertEquals(7, jeuTactique.tailleMain());
    }

    // Banshee : la carte adverse choisie est défaussée, la Banshee quitte la main
    @Test
    void bansheeDefausseUneCarteAdverse() {
        JoueurScripte j1 = new JoueurScripte(1, 1);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.BANSHEE, CarteTactique.RUSE));
        jeuTactique.bornes.get(0).ajouter(2, carte(ROUGE, 5));

        assertTrue(jeuTactique.jouerCoup(j1, new int[] { 0, 0 }));
        assertEquals(0, jeuTactique.bornes.get(0).getCombinaison(2).getNombre());
        assertTrue(j1.getMain().isEmpty());
    }

    // Traître : la carte adverse passe de notre côté
    @Test
    void traitreVoleUneCarteAdverse() {
        JoueurScripte j1 = new JoueurScripte(1, 1, 1);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.TRAITRE, CarteTactique.RUSE));
        jeuTactique.bornes.get(0).ajouter(2, carte(ROUGE, 5));

        assertTrue(jeuTactique.jouerCoup(j1, new int[] { 0, 0 }));
        assertEquals(0, jeuTactique.bornes.get(0).getCombinaison(2).getNombre());
        assertEquals(1, jeuTactique.bornes.get(0).getCombinaison(1).getNombre());
    }

    // Stratège : une de nos cartes change de Borne (réponses : carte 1, puis 1re destination = Borne 2)
    @Test
    void strategeDeplaceUneCarte() {
        JoueurScripte j1 = new JoueurScripte(1, 1, 1);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.STRATEGE, CarteTactique.RUSE));
        jeuTactique.bornes.get(0).ajouter(1, carte(ROUGE, 5));

        assertTrue(jeuTactique.jouerCoup(j1, new int[] { 0, 0 }));
        assertEquals(0, jeuTactique.bornes.get(0).getCombinaison(1).getNombre());
        assertEquals(1, jeuTactique.bornes.get(1).getCombinaison(1).getNombre());
    }

    // Joker : valeur 9 (réponse 9) et couleur n°3 (réponse 3 = Vert) sur la Borne 4
    @Test
    void jokerSePoseAvecLaValeurEtLaCouleurChoisies() {
        JoueurScripte j1 = new JoueurScripte(1, 9, 3);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));

        assertTrue(jeuTactique.jouerCoup(j1, new int[] { 0, 3 }));
        CarteClan posee = jeuTactique.bornes.get(3).getCombinaison(1).getCartes().get(0);
        assertEquals(9, posee.getValeur());
        assertEquals(VERT, posee.getCouleur());
    }

    // Une ruse sans cible (Banshee alors que l'adversaire n'a rien posé) est refusée
    // et la carte reste dans la main
    @Test
    void uneRuseSansCibleEstRefusee() {
        JoueurScripte j1 = new JoueurScripte(1);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.BANSHEE, CarteTactique.RUSE));

        assertFalse(jeuTactique.jouerCoup(j1, new int[] { 0, 0 }));
        assertEquals(1, j1.getMain().size());
    }

    // Après une carte tactique, on ne peut pas en rejouer une tant que l'adversaire n'a pas rattrapé
    @Test
    void onNePeutPasJouerPlusDeTactiquesQueLAdversaire() {
        JoueurScripte j1 = new JoueurScripte(1, 5, 1, 5, 1);
        preparerTactique(j1, new JoueurScripte(2));
        j1.getMain().add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));
        j1.getMain().add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));

        assertTrue(jeuTactique.tactiquePermise(j1));
        assertTrue(jeuTactique.jouerCoup(j1, new int[] { 0, 0 }));
        assertFalse(jeuTactique.tactiquePermise(j1));
        assertFalse(jeuTactique.jouerCoup(j1, new int[] { 0, 1 }));
        assertEquals(1, j1.getMain().size());
    }

    // Une carte tactique ne passe pas en variante de base
    @Test
    void uneCarteTactiqueEstRefuseeEnVarianteDeBase() {
        JoueurScripte j1 = new JoueurScripte(1);
        j1.getMain().add(new CarteTactique(CarteTactique.JOKER, CarteTactique.TROUPE));
        VarianteBase base = new VarianteBase(j1, new JoueurScripte(2), console, new GestionTours(), new Random(1));
        assertFalse(base.jouerCoup(j1, new int[] { 0, 0 }));
    }
}
