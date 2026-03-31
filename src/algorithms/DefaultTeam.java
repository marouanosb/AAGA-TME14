package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {

    // ---------------------------------------------------------
    // CLASSE INTERNE : Gestion intelligente d'un groupe de points
    // ---------------------------------------------------------
    class Cluster {
        private ArrayList<Point> points = new ArrayList<>();
        private Point barycentre = new Point(0, 0);

        public void addPoint(Point p) {
            points.add(p);
            updateBarycentre();
        }

        public void removeLastPoint() {
            if (!points.isEmpty()) {
                points.remove(points.size() - 1);
                updateBarycentre();
            }
        }

        public void updateBarycentre() {
            if (points.isEmpty()) return;
            long x = 0, y = 0;
            for (Point p : points) {
                x += p.x;
                y += p.y;
            }
            this.barycentre = new Point((int) (x / points.size()), (int) (y / points.size()));
        }

        public double getScore() {
            double sum = 0;
            for (Point p : points) {
                sum += p.distance(this.barycentre);
            }
            return sum;
        }

        public ArrayList<Point> getPoints() { return points; }
        public Point getBarycentre() { return barycentre; }
    }

    // ---------------------------------------------------------
    // ALGORITHME 1 : K-MEANS STANDARD (Objectif: Score minimal)
    // ---------------------------------------------------------
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {
        int k = 5;
        if (points.size() < k) return new ArrayList<>();

        ArrayList<Cluster> clusters = new ArrayList<>();
        // Initialisation : K premiers points
        for (int i = 0; i < k; i++) {
            Cluster c = new Cluster();
            c.addPoint(points.get(i));
            clusters.add(c);
        }

        boolean changed = true;
        int maxIters = 100;

        while (changed && maxIters-- > 0) {
            changed = false;
            // 1. Assigner chaque point au cluster dont le barycentre est le plus proche
            ArrayList<ArrayList<Point>> nextAssignments = new ArrayList<>();
            for (int i = 0; i < k; i++) nextAssignments.add(new ArrayList<Point>());

            for (Point p : points) {
                int bestK = 0;
                double minDist = p.distance(clusters.get(0).getBarycentre());
                for (int i = 1; i < k; i++) {
                    double d = p.distance(clusters.get(i).getBarycentre());
                    if (d < minDist) {
                        minDist = d;
                        bestK = i;
                    }
                }
                nextAssignments.get(bestK).add(p);
            }

            // 2. Mettre à jour les clusters et vérifier la convergence
            for (int i = 0; i < k; i++) {
                Point oldBary = clusters.get(i).getBarycentre();
                clusters.get(i).getPoints().clear();
                clusters.get(i).getPoints().addAll(nextAssignments.get(i));
                clusters.get(i).updateBarycentre();

                if (!oldBary.equals(clusters.get(i).getBarycentre())) {
                    changed = true;
                }
            }
        }

        return convertResult(clusters);
    }

    // ---------------------------------------------------------
    // ALGORITHME 2 : K-MEANS BUDGET (Objectif: Max de points)
    // ---------------------------------------------------------
    public ArrayList<ArrayList<Point>> calculKMeansBudget(ArrayList<Point> points) {
        int k = 5;
        double budgetMax = 10101.0;
        ArrayList<Cluster> clusters = new ArrayList<>();

        // Initialisation avec les membres fondateurs (5 premiers points)
        for (int i = 0; i < k; i++) {
            Cluster c = new Cluster();
            c.addPoint(points.get(i));
            clusters.add(c);
        }

        ArrayList<Point> remaining = new ArrayList<>(points.subList(k, points.size()));
        boolean added = true;

        while (added) {
            added = false;
            Point bestPoint = null;
            int targetClusterIdx = -1;
            double minImpact = Double.MAX_VALUE;

            // Heuristique : chercher le point dont l'ajout coûte le moins de budget
            for (Point p : remaining) {
                for (int i = 0; i < k; i++) {
                    Cluster c = clusters.get(i);
                    
                    // Simulation
                    c.addPoint(p);
                    double score = c.getScore();

                    if (score <= budgetMax) {
                        if (score < minImpact) {
                            minImpact = score;
                            bestPoint = p;
                            targetClusterIdx = i;
                        }
                    }
                    // Annulation de la simulation
                    c.removeLastPoint();
                }
            }

            if (bestPoint != null) {
                clusters.get(targetClusterIdx).addPoint(bestPoint);
                remaining.remove(bestPoint);
                added = true;
            }
        }

        return convertResult(clusters);
    }

    // Fct pour transformer Cluster -> ArrayList<Point>
    private ArrayList<ArrayList<Point>> convertResult(ArrayList<Cluster> clusters) {
        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        for (Cluster c : clusters) {
            result.add(c.getPoints());
        }
        return result;
    }
}