package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DefaultTeam {

    // --- HELPER METHODS ---
    private double distance(Point p1, Point p2) {
        return p1.distance(p2);
    }

    private Point barycentre(ArrayList<Point> cluster) {
        if (cluster.isEmpty()) return new Point(0, 0);
        long sumX = 0, sumY = 0;
        for (Point p : cluster) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Point((int) (sumX / cluster.size()), (int) (sumY / cluster.size()));
    }

    private double totalDistance(ArrayList<Point> cluster, Point center) {
        double sum = 0;
        for (Point p : cluster) sum += distance(p, center);
        return sum;
    }

    // --- K-MEANS CLUSTERING (Standard) ---
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {
        int k = 5;
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();
        ArrayList<Point> centroids = new ArrayList<>();

        // Initialisation : on prend les k premiers points comme centres
        for (int i = 0; i < k; i++) {
            centroids.add(points.get(i));
            clusters.add(new ArrayList<>());
        }

        boolean changed = true;
        int maxIters = 100; // Sécurité pour éviter les boucles infinies

        while (changed && maxIters-- > 0) {
            // 1. Vider les clusters
            for (ArrayList<Point> cluster : clusters) cluster.clear();

            // 2. Assigner chaque point au centre le plus proche
            for (Point p : points) {
                int bestK = 0;
                double minDist = distance(p, centroids.get(0));
                for (int i = 1; i < k; i++) {
                    double d = distance(p, centroids.get(i));
                    if (d < minDist) {
                        minDist = d;
                        bestK = i;
                    }
                }
                clusters.get(bestK).add(p);
            }

            // 3. Recalculer les centres
            changed = false;
            for (int i = 0; i < k; i++) {
                if (clusters.get(i).isEmpty()) continue;
                Point newCentroid = barycentre(clusters.get(i));
                if (!newCentroid.equals(centroids.get(i))) {
                    centroids.set(i, newCentroid);
                    changed = true;
                }
            }
        }
        return clusters;
    }

    // --- K-MEANS CLUSTERING (Avec Budget) ---
    public ArrayList<ArrayList<Point>> calculKMeansBudget(ArrayList<Point> points) {
        int k = 5;
        double budget = 10101.0;
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();
        
        // Initialisation avec les membres fondateurs
        for (int i = 0; i < k; i++) {
            ArrayList<Point> cluster = new ArrayList<>();
            cluster.add(points.get(i));
            clusters.add(cluster);
        }

        // Liste des points restants à assigner
        ArrayList<Point> remainingPoints = new ArrayList<>(points.subList(k, points.size()));

        // Heuristique gloutonne : on essaie d'ajouter les points les plus proches des fondateurs
        // On trie les points par distance au fondateur le plus proche pour maximiser le remplissage
        boolean added = true;
        while (added) {
            added = false;
            Point bestPoint = null;
            int bestClusterIdx = -1;
            double minImpact = Double.MAX_VALUE;

            for (Point p : remainingPoints) {
                for (int i = 0; i < k; i++) {
                    ArrayList<Point> currentCluster = clusters.get(i);
                    
                    // Simulation de l'ajout
                    currentCluster.add(p);
                    Point newBary = barycentre(currentCluster);
                    double newCost = totalDistance(currentCluster, newBary);
                    
                    if (newCost <= budget) {
                        // On cherche le point qui coûte le moins cher en budget
                        if (newCost < minImpact) {
                            minImpact = newCost;
                            bestPoint = p;
                            bestClusterIdx = i;
                        }
                    }
                    // Retirer pour la simulation suivante
                    currentCluster.remove(currentCluster.size() - 1);
                }
            }

            if (bestPoint != null) {
                clusters.get(bestClusterIdx).add(bestPoint);
                remainingPoints.remove(bestPoint);
                added = true;
            }
        }

        return clusters;
    }
}