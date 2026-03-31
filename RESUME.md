# Projet de Partitionnement Spatial : K-Means & Budget

Ce projet implémente deux variantes d'algorithmes de clustering pour organiser un ensemble de points 2D ($n$ points) en $k=5$ partitions distinctes, en utilisant une approche modulaire en Java.

## 1. K-Means Clustering Standard
L'objectif est de trouver une partition de $S$ en $k$ parties qui minimise la somme des distances de chaque point par rapport au barycentre (centre de gravité) de son groupe.

### Fonctionnement (Algorithme de Lloyd)
L'heuristique procède par itérations successives :
* **Assignation** : Chaque point est rattaché au barycentre le plus proche.
* **Mise à jour** : Les barycentres sont recalculés à partir de la position moyenne des points de chaque cluster.
* **Convergence** : Le processus s'arrête lorsque les barycentres ne se déplacent plus ou que le nombre maximum d'itérations est atteint.

---

## 2. K-Means avec Restriction Budgétaire
Le problème consiste à maximiser le nombre de points intégrés dans les clusters tout en respectant un budget de distance fixe $B = 10101$.

### Contraintes spécifiques
* **Membres fondateurs** : Les 5 premiers points du fichier de test sont obligatoirement les points de départ de chaque cluster ($s_1, s_2, \dots, s_k$).
* **Budget de distance** : Pour chaque cluster $S_i$, la somme des distances des points par rapport au barycentre de $S_i$ doit être inférieure ou égale à $B$.
* **Objectif de score** : Obtenir le plus grand nombre d'éléments possible dans l'union $S_1 \cup S_2 \cup \dots \cup S_k$.

### Heuristique Gloutonne (Greedy)
1. On initialise les 5 clusters avec leurs membres fondateurs.
2. Pour chaque point non assigné, on **simule** son ajout dans chaque cluster possible.
3. On calcule le nouvel impact sur le budget (nouveau barycentre $\rightarrow$ nouveau score).
4. Si le budget est respecté, on valide l'ajout dans le cluster qui minimise l'augmentation du coût.
5. On itère jusqu'à saturation du budget pour tous les groupes.

---

## Architecture Technique
Le code repose sur une structure **orientée objet** pour garantir performance et clarté :

* **Classe `Cluster`** : Une classe interne qui encapsule la liste des points et gère la mise à jour dynamique du barycentre. Elle permet de "simuler" des ajouts et de les annuler facilement (Backtracking) pour tester le budget.
* **Optimisation** : Utilisation de calculs de distances Euclidiennes et de barycentres pondérés.

## Installation et Utilisation
Le projet est compilé et exécuté via **Ant**. 

### Nettoyage et Compilation
Pour éviter les erreurs de lien (`NoSuchMethodError`) dues à d'anciennes versions compilées :
```bash
ant clean
ant compile
ant run