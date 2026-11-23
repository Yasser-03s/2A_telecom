# Schotten-Totten — mode réseau (variante de base)

## Prérequis
- Java JDK (Java 8 ou supérieur) sur la machine hôte.
- Un serveur SSH actif sur la machine hôte.
- `nc` (netcat) installé sur la machine hôte.
- Depuis la machine du joueur 2 : un client SSH.

Le joueur distant n'a pas besoin d'installer Java ni de copier le projet.

## Compilation (sur la machine hôte)
Depuis le dossier qui contient `com/` :

```sh
mkdir -p build
find com -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d build
```

## Lancer une partie réseau

### 1. Sur la machine hôte (joueur 1)
```sh
java -cp build com.schottenTotten.Main --serve 5000
```
Le serveur attend la connexion du joueur 2. Le port `5000` est configurable.

### 2. Sur la machine du joueur 2
Dans un terminal, remplacer `utilisateur` et `IP_HOTE` par les identifiants de la machine hôte :

```sh
ssh -t utilisateur@IP_HOTE "nc 127.0.0.1 5000"
```

La connexion SSH transporte l'entrée et la sortie du terminal distant vers le jeu. Le serveur Java écoute uniquement sur l'interface locale de la machine hôte : le port du jeu n'est donc pas exposé directement au réseau.

### 3. Jouer
Le joueur 1 saisit son nom dans le terminal hôte ; le joueur 2 saisit le sien dans son terminal distant. Le plateau et les événements publics sont affichés sur les deux terminaux. Chaque main et chaque demande de coup sont envoyées uniquement au terminal du joueur concerné.

## Mode local inchangé
```sh
java -cp build com.schottenTotten.Main
```
Une graine aléatoire peut toujours être donnée en premier argument dans ce mode.

## Limites du projet fourni
- Le mode réseau ajouté fonctionne avec la **variante de base**.
- `VarianteTactique.java`, `CarteTactique.java` et `PiocheTactique.java` étaient incomplètes dans le ZIP d'origine ; la variante tactique n'est donc pas activée ni prétendue fonctionnelle.
- Le serveur accepte un seul joueur distant pour une partie. Pour relancer une partie, relancer la commande `--serve`.
- Si le joueur distant ferme sa session en cours de partie, la partie s'interrompt.

