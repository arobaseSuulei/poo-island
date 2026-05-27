# The Island

Projet POO – IATIC3.

Notre version informatique du jeu de société *The Island* 
L'idée : sauver un maximum de ses explorateurs en les amenant sur les plages refuges avant que le volcan ne fasse tout sauter.

## L'équipe

- DIAGOU Maelle
- DIALLO Souleymane
- EHOUN Bercine
- MAMWA Winnie
- NKOUAKAM Franck

## Pour lancer le jeu

Il faut Java 17 minimum. Il n'est pas nécessaire d'avoir Maven installé, le
wrapper est inclus dans le projet.

### Sous Windows

.\mvnw.cmd clean package
java -jar TheIsland.jar


### Sous Linux / Mac


./mvnw clean package
java -jar TheIsland.jar


Le premier lancement va télécharger Maven dans `~/.m2`.
Les lancements suivants sont instantanés.

## Comment c'est organisé

Le code est dans `src/main/java/ile`, découpé en plusieurs packages :

- `geometrie` – tout ce qui touche aux coordonnées hexagonales (q, r en axial)
- `plateau` – le plateau et ses cases
- `pions` – joueurs, explorateurs, barques
- `pions.monstres` – les trois créatures (serpent, requin, baleine), en
  polymorphisme (chacune a son `attaquer()`)
- `tuiles` – tuiles de terrain et leurs pouvoirs cachés
- `moteur` – la classe `Partie` qui orchestre tout, plus `Etape`, `De`,
  `ArbitreDefense`, etc.
- `ihm` – l'interface graphique (Swing)

Les images de fond sont dans `src/main/resources/images/`.

## Quelques choix techniques

- Nous avons opté pour les **coordonnées axiales** pour le plateau plutôt que
  les classiques (col, ligne). Cela simplifie le calcul des voisins (pas de
  cas pair/impair à gérer) et des distances (une seule formule).
- Les créatures sont organisées en une **hiérarchie polymorphe**, ce qui évite tout `switch` pour décider qui
  attaque quoi.
- Le plateau est stocké dans une `Map<Hexagone, Cellule>` au lieu d'un
  `Case[][]`, ce qui est plus adapté pour itérer uniquement sur les cases
  existantes.

## Comment jouer

Au lancement : écran d'accueil, on choisit le nombre de joueurs (2 à 4) puis
les pseudos.

À chaque tour, dans l'ordre :

1. **Début de tour** : il est possible de jouer une tuile pouvoir gardée en
   main (dauphin, vent, appel de créature)
2. **Déplacement** : 3 points à dépenser (déplacer un explorateur ou une
   barque coûte 1 point, embarquer aussi)
3. **Retirer une tuile** : retirer une tuile en bordure de mer, dans l'ordre
   plages → forêts → montagnes
4. **Lancer le dé** : déplace une créature de l'espèce tirée

La partie s'arrête dès que l'éruption volcanique est révélée. Le joueur avec
le plus de points de trésor gagne.

Le bouton **Aide** dans la fenêtre de jeu donne accès aux règles détaillées
(onglets).

## Générer la Javadoc


.\mvnw.cmd javadoc:javadoc


Puis ouvrir `target/reports/apidocs/index.html`.

## Pistes d'amélioration

Plusieurs extensions seraient envisageables pour enrichir le jeu :

- **Sauvegarde de partie** : permettre de sauvegarder et reprendre une partie
  interrompue (sérialisation de l'état du jeu)
- **Mode réseau** : rendre le jeu jouable en multijoueur sur plusieurs machines
  via des sockets Java, chaque joueur depuis son propre poste
- **Minuteur de tour** : ajouter un timer par joueur pour limiter le temps de
  réflexion et pimenter les parties
- **Intelligence artificielle** : implémenter un joueur IA pour permettre de
  jouer en solo, avec différents niveaux de difficulté
- **Visuels améliorés** : remplacer les cercles colorés des pions par de
  vraies images, et animer les déplacements sur le plateau
- **Sons et musique** : ajouter des effets sonores (éruption, attaque de
  créature) et une musique d'ambiance
- **Replay** : enregistrer le déroulement d'une partie pour pouvoir la revoir
  coup par coup après la fin
