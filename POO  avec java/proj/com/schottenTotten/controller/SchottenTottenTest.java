package com.schottenTotten.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.schottenTotten.ai.IA;
import com.schottenTotten.model.Borne;
import com.schottenTotten.model.CarteClan;
import com.schottenTotten.model.Combinaison;
import com.schottenTotten.model.Couleur;
import com.schottenTotten.model.PiocheClan;

/*
 * C'EST QUOI JUNIT ?
 * JUnit est une bibliothèque Java qui lance automatiquement des tests.
 * - Un test = une méthode marquée @Test.
 * - Dans un test, on prépare une situation, on appelle notre code,
 *   puis on vérifie le résultat avec des "assertions" :
 *       assertEquals(attendu, obtenu)   : les deux valeurs doivent être égales
 *       assertTrue(condition)           : la condition doit être vraie
 *       assertFalse(condition)          : la condition doit être fausse
 *       assertThrows(Exception, code)   : le code doit lever cette exception
 * - Si une assertion est fausse, le test est ROUGE et JUnit dit laquelle.
 * - @BeforeEach : méthode exécutée avant CHAQUE test (chaque test repart d'un jeu neuf).
 *
 * Ce fichier est dans le package com.src.controller pour pouvoir utiliser
 * revendicationValide() (à rendre non-private) et le champ bornes de Jeu.
 */
class SchottenTottenTest {

    private VarianteBase jeu;

    // Avant chaque test : on crée une partie neuve (deux IA, graine fixe, pas d'affichage).
    @BeforeEach
    void preparer() {
        Random alea = new Random(1);
        jeu = new VarianteBase(new IA("A", 1, alea), new IA("B", 2, alea), null, alea);
    }

    // Petites aides pour écrire les tests plus court.
    private static CarteClan carte(Couleur couleur, int valeur) {
        return new CarteClan(couleur, valeur);
    }

    private static Combinaison combinaison(CarteClan... cartes) {
        return Combinaison.evaluer(Arrays.asList(cartes));
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

    // =====================================================================
    // 1. CLASSES DU MODÈLE
    // =====================================================================

    // Vérifie qu'une carte Clan n'accepte que les valeurs de 1 à 9 :
    // 0 et 10 doivent lever une IllegalArgumentException.
    @Test
    void carteClanRefuseUneValeurHorsDe1a9() {
        assertThrows(IllegalArgumentException.class, () -> new CarteClan(Couleur.ROUGE, 0));
        assertThrows(IllegalArgumentException.class, () -> new CarteClan(Couleur.ROUGE, 10));
    }

    // Vérifie l'affichage d'une carte : valeur suivie de l'initiale de la couleur.
    // Un 7 vert doit s'afficher "7V".
    @Test
    void carteClanSAfficheValeurPuisInitialeDeLaCouleur() {
        assertEquals("7V", carte(Couleur.VERT, 7).toString());
    }

    // Vérifie qu'une pioche neuve contient bien 54 cartes (6 couleurs x 9 valeurs),
    // et que piocher en retire une.
    @Test
    void piocheContient54CartesEtPiocherEnRetireUne() {
        PiocheClan pioche = new PiocheClan(new Random(1));
        assertEquals(54, pioche.getNombreCartes());
        pioche.piocher();
        assertEquals(53, pioche.getNombreCartes());
    }

    // Vérifie qu'une Borne accepte 3 cartes par côté, pas 4.
    // La 4e carte du même côté doit lever une IllegalStateException,
    // et le côté adverse doit rester libre.
    @Test
    void borneRefuseUneQuatriemeCarteDuMemeCote() {
        Borne borne = new Borne(1);
        poser(borne, 1, carte(Couleur.ROUGE, 1), carte(Couleur.ROUGE, 2), carte(Couleur.ROUGE, 3));
        assertFalse(borne.peutRecevoir(1));
        assertTrue(borne.peutRecevoir(2));
        assertThrows(IllegalStateException.class, () -> borne.ajouter(1, carte(Couleur.ROUGE, 4)));
    }

    // Vérifie le classement des combinaisons : un brelan (3 cartes de même valeur)
    // bat une suite (3 valeurs qui se suivent).
    @Test
    void unBrelanBatUneSuite() {
        Combinaison brelan = combinaison(carte(Couleur.ROUGE, 2), carte(Couleur.BLEU, 2), carte(Couleur.VERT, 2));
        Combinaison suite = combinaison(carte(Couleur.ROUGE, 7), carte(Couleur.BLEU, 8), carte(Couleur.VERT, 9));
        assertTrue(brelan.compareTo(suite) > 0);
    }

    // Vérifie qu'à type de combinaison égal, la somme des valeurs départage :
    // la suite 5-6-7 (somme 18) bat la suite 2-3-4 (somme 9).
    @Test
    void aTypeEgalLaSommeLaPlusHauteGagne() {
        Combinaison forte = combinaison(carte(Couleur.ROUGE, 5), carte(Couleur.BLEU, 6), carte(Couleur.VERT, 7));
        Combinaison faible = combinaison(carte(Couleur.ROUGE, 2), carte(Couleur.BLEU, 3), carte(Couleur.VERT, 4));
        assertTrue(forte.compareTo(faible) > 0);
    }

    // =====================================================================
    // 2. REVENDICATION D'UNE BORNE
    // =====================================================================

    // Vérifie le cas normal : 3 cartes de chaque côté.
    // Le joueur 1 a un brelan de 9, le joueur 2 a une simple suite :
    // le joueur 1 peut revendiquer, le joueur 2 non.
    @Test
    void ilFautUneCombinaisonPlusFortePourRevendiquer() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(Couleur.ROUGE, 9), carte(Couleur.BLEU, 9), carte(Couleur.VERT, 9));
        poser(borne, 2, carte(Couleur.ROUGE, 1), carte(Couleur.BLEU, 2), carte(Couleur.MAUVE, 3));
        assertTrue(jeu.revendicationValide(borne, 1));
        assertFalse(jeu.revendicationValide(borne, 2));
    }

    // Vérifie qu'on ne peut pas revendiquer sans avoir posé ses 3 cartes.
    @Test
    void onNePeutPasRevendiquerAvecMoinsDeTroisCartes() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(Couleur.ROUGE, 9), carte(Couleur.ROUGE, 8));
        assertFalse(jeu.revendicationValide(borne, 1));
    }

    // Vérifie l'exception des règles : l'adversaire n'a rien posé, et ton brelan de 5
    // peut encore être battu (brelan de 6, suite couleur...). La revendication est refusée.
    @Test
    void revendicationRefuseeSiLAdversairePeutEncoreMieuxFaire() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(Couleur.ROUGE, 5), carte(Couleur.BLEU, 5), carte(Couleur.VERT, 5));
        assertFalse(jeu.revendicationValide(borne, 1));
    }

    // Vérifie l'exception des règles dans le cas favorable : 9-8-7 de la même couleur
    // est la meilleure combinaison possible, rien ne peut la battre.
    // La revendication est acceptée même si l'adversaire n'a rien posé.
    @Test
    void revendicationAccepteeSiRienNePeutBattreSaCombinaison() {
        Borne borne = jeu.bornes.get(0);
        poser(borne, 1, carte(Couleur.ROUGE, 9), carte(Couleur.ROUGE, 8), carte(Couleur.ROUGE, 7));
        assertTrue(jeu.revendicationValide(borne, 1));
    }

    // =====================================================================
    // 3. CONDITIONS DE VICTOIRE
    // =====================================================================

    // Vérifie qu'au début de la partie personne n'a gagné.
    @Test
    void personneNeGagneAuDebut() {
        assertFalse(jeu.conditionVictoire(1));
        assertFalse(jeu.conditionVictoire(2));
    }

    // Vérifie la victoire à 5 Bornes dispersées (ici 1, 3, 5, 7, 9).
    @Test
    void cinqBornesDisperseesDonnentLaVictoire() {
        gagner(1, 1, 3, 5, 7, 9);
        assertTrue(jeu.conditionVictoire(1));
    }

    // Vérifie que 4 Bornes dispersées (1, 3, 5, 7) ne suffisent pas.
    @Test
    void quatreBornesDisperseesNeSuffisentPas() {
        gagner(1, 1, 3, 5, 7);
        assertFalse(jeu.conditionVictoire(1));
    }

    // Vérifie la victoire avec 3 Bornes adjacentes (ici 4, 5, 6).
    @Test
    void troisBornesAdjacentesDonnentLaVictoire() {
        gagner(2, 4, 5, 6);
        assertTrue(jeu.conditionVictoire(2));
    }

    // Vérifie qu'une Borne de l'adversaire coupe la série : le joueur 1 a les Bornes 4 et 6,
    // le joueur 2 a la 5, donc le joueur 1 n'a pas 3 Bornes adjacentes.
    @Test
    void uneBorneAdverseInterrompLaSerie() {
        gagner(1, 4, 6);
        gagner(2, 5);
        assertFalse(jeu.conditionVictoire(1));
    }
}