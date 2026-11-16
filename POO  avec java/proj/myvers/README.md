# Schotten-Totten en Java (PG220)

Les numéros de page (p.) sont ceux du PDF `PG220.pdf` du Pr. Lakhlef (page N du PDF = diapo N).

## 1. Compiler, lancer, tester

Depuis le dossier qui contient `com/` :

```
javac com/schottenTotten/*.java com/schottenTotten/*/*.java
java com.schottenTotten.Main
java com.schottenTotten.Main 42      (optionnel : graine du hasard, pour rejouer la même partie)
```

`com/schottenTotten/*/*.java` seul ne compile pas `Main`, qui est un niveau au-dessus (dans `com/schottenTotten/`), d'où le premier motif en plus. `Jeu` est abstraite : on ne peut pas la lancer, le point d'entrée est `Main`.

Tests JUnit 5 (dans `test/`, séparés pour que la commande de compilation ci-dessus n'ait pas besoin de JUnit) :

```
javac -cp junit-platform-console-standalone.jar -d out com/schottenTotten/*.java com/schottenTotten/*/*.java test/com/schottenTotten/controller/*.java
java -jar junit-platform-console-standalone.jar --class-path out --scan-class-path
```

(Le `.jar` se télécharge sur Maven Central ; avec VS Code ou Eclipse, il suffit d'ajouter JUnit 5 au projet et de lancer la classe.) Résultat attendu : 32 tests réussis.

## 2. Ton UML, relation par relation

| Relation de ton UML | Où dans le code | Cours |
|---|---|---|
| VarianteBase hérite de Jeu | `class VarianteBase extends Jeu` | p.55-57 |
| VarianteTactique hérite de VarianteBase | `class VarianteTactique extends VarianteBase` | p.55-57 |
| Humain et IA implémentent Joueur | `implements Joueur` | p.69-70, 72, 76 |
| Composition : VarianteBase contient une PiocheClan | `piocheClan = new PiocheClan(alea)` dans le constructeur | p.48 |
| Composition : VarianteBase contient des Bornes | `bornes.add(new Borne(...))` dans le constructeur | p.48 |
| Composition : PiocheClan contient des CarteClan | `cartes.add(new CarteClan(...))` dans son constructeur | p.48 |
| Composition : VarianteTactique contient une PiocheTactique | `piocheTactique = new PiocheTactique(alea)` | p.48 |
| Composition : PiocheTactique contient des CarteTactique | `cartes.add(new CarteTactique(...))` | p.48 |
| Agrégation : Jeu agrège InterfaceConsole | la console est créée dans `Main` et reçue par le constructeur | p.49-50 |
| Agrégation : Humain agrège InterfaceConsole | idem, reçue par le constructeur d'`Humain` | p.49-50 |
| Agrégation : Borne agrège des Combinaisons | `Borne(int numero, Combinaison c1, Combinaison c2)` : les Combinaisons sont créées à l'extérieur (par VarianteBase) | p.49-50 (même schéma que `Person(name, Date birthDate)`) |
| Agrégation : Combinaison agrège des Cartes | les cartes existent avant (dans la main) et arrivent par `ajouter(CarteClan)` | p.49, 52 (`enrollStudent`) |
| Association : Jeu - Joueur | attribut `Joueur[] joueurs` | p.39, 50 |
| Association : Jeu - GestionTours | attribut `gestionTours`, reçu par le constructeur | p.50 |

Rappel de la différence utilisée (p.48-49) : en composition, le conteneur crée l'objet et en est responsable ; en agrégation, l'objet contenu existe indépendamment et est simplement relié.

Écarts avec ton UML, à connaître :
- Ce n'est pas `Jeu` mais `VarianteBase` qui possède les Bornes (comme dans ton résumé). `Jeu` ne garde que ce qui est commun à toutes les variantes : joueurs, console, tours, gagnant.
- `Combinaison` stocke des `CarteClan` (sous-classe de `Carte`), parce que seules les cartes Clan comptent dans une formation. Une troupe d'élite (Joker...) est posée sous la forme d'une CarteClan dont le joueur choisit la valeur et la couleur.
- Il n'y a plus de classe `Couleur` : un `enum` n'est pas dans le cours. La couleur est un indice dans le tableau `CarteClan.COULEURS`.
- Ajout de `PiocheVideException` (exception, pas une relation).
- Le sujet (§6) suggère une `JeuFactory`. Elle n'est pas dans ton UML, donc je ne l'ai pas mise : `Main` choisit la variante avec un `if`. Si le prof la demande, c'est une classe de 10 lignes avec une méthode statique qui renvoie un `Jeu`.

## 3. Classe par classe

### Package `model`

**Carte** (abstraite)
- Classe abstraite avec une méthode abstraite `getNom()` : p.77-79 (exemple `Shape.draw()` p.78).
- `toString()` redéfini avec `@Override` : p.59.
- C'est elle qui permet une main contenant des CarteClan et des CarteTactique mélangées (tableau hétérogène : p.64).

**CarteClan** (hérite de Carte)
- `extends` : p.55-56. Constructeur avec `this.x = x` : p.22, 36.
- Constantes `public static final` : p.14, 47. Tableau `String[] COULEURS` : p.39.
- `throw new IllegalArgumentException(...)` : p.101 (exception non vérifiée), p.105 (clause `throw`).
- Attributs privés + getters : p.16-18 (encapsulation).
- `@Override getNom()` : p.59.

**CarteTactique** (hérite de Carte)
- Mêmes éléments que CarteClan (p.55-56, 14, 47, 59).
- `type.equals(TROUPE)` : p.87 (`equals` compare le contenu, `==` compare les références).

**PiocheVideException**
- Exception définie par l'utilisateur, copiée sur `StackException` : `extends Exception` + `super(message)`, p.108-110 (et `super(...)` p.57).
- Exception vérifiée (p.101) : le compilateur oblige à la gérer, ce qui répond au §7 du sujet (cartes insuffisantes dans le deck).

**PiocheClan** et **PiocheTactique**
- Composition de leurs cartes : p.48.
- `ArrayList<CarteClan>` : p.51-52 (relation one-to-many, version `ArrayList` de `Course`).
- `piocher() throws PiocheVideException` avec `throw new ...` : p.105-107, 110 (même schéma que `Stack.push`).
- Hors cours : `java.util.Random` et `Collections.shuffle` (voir §4).

**Combinaison** (implémente `Comparable<Combinaison>`)
- `implements Comparable<Combinaison>` et `compareTo` : p.84-86 ; `Integer.compare` : p.86 ; positif / 0 / négatif : p.84.
- `equals` n'est volontairement pas redéfini : deux combinaisons à `compareTo == 0` n'ont pas les mêmes cartes, et elles ne vont jamais dans un `TreeSet` (cohérence equals / compareTo : p.88).
- Constantes `static final` pour les types de formation : p.14. Tableau `int[]` + `Arrays.sort` : p.39-40, p.83 (« Tableaux.sort() »).
- `getCartes()` renvoie une copie (`new ArrayList<CarteClan>(cartes)`) pour que l'extérieur ne modifie pas l'attribut : p.16-18.
- `IllegalStateException` / `IllegalArgumentException` : même famille que les exemples p.101.

**Borne**
- Agrégation : constructeur qui reçoit ses deux Combinaisons : p.50.
- Tableau d'objets `Combinaison[]` : p.39-41.
- Encapsulation (attributs privés, `peutRecevoir`, `ajouter`, `retirer`) : p.16-18. Exceptions : p.105.

**Joueur** (interface)
- Définition d'une interface : p.69-70. Méthodes publiques sans implémentation : p.70.
- Permet de manipuler Humain et IA de la même façon (argument polymorphe) : p.74-75.
- `List<Carte>` et `List<Borne>` en paramètres : p.75 (même idée que `printIt(List<String>)`).

### Package `controller`

**Jeu** (abstraite)
- Classe abstraite avec `abstract void lancer()` : p.77-79. Attributs `protected` : p.58.
- `Joueur[] joueurs` : p.39 ; le tableau contient des Humain ou des IA : p.64.
- Reçoit la console et GestionTours par le constructeur : p.50.

**GestionTours**
- Classe simple : attributs, constructeur par défaut (p.24), méthodes (p.14-15).

**VarianteBase** (hérite de Jeu)
- `extends`, `super(...)` dans le constructeur : p.55-57. `@Override lancer()` : p.59.
- `ArrayList<Borne>` : p.51-52. `boolean[][]` (cartes déjà posées) : p.41. Constante `NB_BORNES` : p.14.
- Méthodes `protected` que la variante tactique redéfinit (`tailleMain`, `peutJouer`, `jouerCoup`, `phasePioche`, `tactiquePermise`) : p.58 (protected), p.68 (redéfinition = polymorphisme dynamique).
- `instanceof` et cast `(CarteClan)` : p.65-66.
- `try { ... } catch (PiocheVideException e)` : p.96-97, 110.
- `for (Borne b : bornes)` : p.40, 64, 71.

**VarianteTactique** (hérite de VarianteBase)
- `extends` et `super(...)` : p.55-57. Appels `super.peutJouer(...)` et `super.jouerCoup(...)` : p.60 (`super.toString()`).
- Redéfinition de méthodes en gardant la même visibilité : p.59-60.
- `int[] nbTactiquesJouees` : p.39-40. `ArrayList<int[]>` : p.39, 52.
- `instanceof`, cast `(CarteTactique)`, comparaison de noms avec `equals` : p.65-66, 87.
- Composition de la PiocheTactique : p.48.

**Humain** (implémente Joueur)
- `implements Joueur` : p.72 (g), 76. Agrégation de la console : p.49-50.
- `instanceof` + cast pour savoir si une carte est une ruse : p.65-66.

### Package `view`

**InterfaceConsole**
- `Integer.parseInt` dans un `try` / `catch (NumberFormatException e)` : p.95-97 (c'est le code de `AddArguments2`).
- `BufferedReader` + `catch (IOException e)` : p.100-101 (exception vérifiée).
- Aucune règle du jeu dedans : elle ne fait qu'afficher et lire.
- Hors cours : `InputStreamReader`, `System.exit`, `String.length()` / `trim()`.

### Package `ai`

**IA** (implémente Joueur)
- `implements Joueur` : p.72 (g), 76. `List<Integer> l = new ArrayList<Integer>()` : p.71, 75.
- `instanceof` + cast : p.65-66.
- Hors cours : `java.util.Random` (`nextInt`), obligatoire pour des choix aléatoires.

### `Main`
- `main(String[] args)` et lecture d'un argument avec `parse...` dans un `try` / `catch (NumberFormatException e)` : p.95-96 (`AddArguments`).
- `Jeu jeu = new VarianteBase(...)` ou `new VarianteTactique(...)` : type statique `Jeu`, type dynamique choisi à l'exécution : p.65 ; appel de `jeu.lancer()` = polymorphisme : p.68.

### Test JUnit
- JUnit n'est pas dans le cours (il est exigé par le sujet, §8).
- `JoueurScripte` est une classe imbriquée statique implémentant `Joueur` : p.111, 113. Elle sert à tester les ruses avec des réponses écrites à l'avance, sans clavier.

## 4. Ce qui n'est pas dans le cours (à savoir justifier)

| Élément | Où | Pourquoi |
|---|---|---|
| `java.util.Random`, `nextInt` | PiocheClan, PiocheTactique, IA, Main | il faut du hasard pour mélanger et pour l'IA |
| `Collections.shuffle` | les deux pioches | `Collections` est cité p.83 (pour `sort`) ; `shuffle` est son équivalent pour mélanger |
| `InputStreamReader` | InterfaceConsole | pour brancher `BufferedReader` (p.100) sur le clavier |
| `System.exit(0)` | InterfaceConsole | quitter proprement si l'entrée est fermée |
| Méthodes de `ArrayList` : `get`, `size`, `remove(int)`, `isEmpty`, `contains`, `clear`, `add(index, e)` | partout | le cours montre `ArrayList` (p.52, 71) mais seulement `add` |
| `Long.parseLong` | Main | même principe que `Integer.parseInt` (p.95) |
| `IllegalStateException` | Borne, Combinaison | même famille que `IllegalArgumentException` (p.101) |
| `int... reponses` (varargs) | test seulement | pratique pour écrire les réponses d'un joueur scripté |

## 5. Simplifications et limites

- Cartes tactiques : 8 sur 10. Joker (x2), Espion, Porte-bouclier, Chasseur de tête, Stratège, Banshee, Traître sont jouables. Colin-Maillard et Combat de boue ne sont pas implémentés (ils changent les règles d'une Borne et compliquent la revendication).
- La règle « on ne peut pas jouer plus de cartes tactiques que l'adversaire » est appliquée (`tactiquePermise`).
- Revendication avec un adversaire incomplet : le code teste toutes les cartes qui ne sont pas posées sur une Borne, y compris celles de ta propre main (plus prudent que la règle officielle). Il ne tient pas compte des cartes tactiques que l'adversaire pourrait encore jouer.
- Un Joker posé en « 9 rouge » marque ce 9 rouge comme déjà posé, alors que le vrai 9 rouge peut encore être dans la pioche.
- Si plus personne ne peut jouer, la partie s'arrête et gagne celui qui a le plus de Bornes (égalité possible).
- L'IA est aléatoire : elle tente de revendiquer toute Borne complète de son côté, d'où des messages « Revendication refusée » fréquents.

## 6. Extensibilité (sujet §3)

- Nouvelle IA : une classe qui `implements Joueur` (quatre méthodes), rien d'autre à modifier. Le jeu ne manipule que l'interface.
- Nouvelle variante : une classe qui hérite de `VarianteBase` et redéfinit `tailleMain()`, `peutJouer`, `jouerCoup`, `phasePioche`... C'est ce que fait `VarianteTactique`, sans toucher à `VarianteBase`.
- Nouvelle carte tactique : une constante dans `CarteTactique`, une ligne dans `PiocheTactique`, une méthode `jouerXxx` et une ligne dans `jouerCoup` / `tactiqueJouable` de `VarianteTactique`.
