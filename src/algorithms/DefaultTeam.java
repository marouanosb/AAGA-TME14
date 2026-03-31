package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {

    // --- CLASSE INTERNE POUR LA MODULARITÉ ---
    class Cluster {
        private ArrayList<Point> points = new ArrayList<>();
        private Point barycentre = new Point(0, 0);

        public void addPoint(Point p) { points.add(p); updateBarycentre(); }
        public ArrayList<Point> getPoints() { return points; }
        public Point getBarycentre() { return barycentre; }

        public void updateBarycentre() {
            if (points.isEmpty()) return;
            long x = 0, y = 0;
            for (Point p : points) { x += p.x; y += p.y; }
            this.barycentre = new Point((int)(x/points.size()), (int)(y/points.size()));
        }

        public double getScore() {
            double sum = 0;
            for (Point p : points) sum += p.distance(barycentre);
            return sum;
        }
    }

    // --- K-MEANS STANDARD (Optimisé) ---
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {
        int k = 5;
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            Cluster c = new Cluster();
            c.addPoint(points.get(i));
            clusters.add(c);
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            ArrayList<ArrayList<Point>> nextGroups = new ArrayList<>();
            for (int i=0; i<k; i++) nextGroups.add(new ArrayList<>());

            for (Point p : points) {
                int best = 0;
                double minDist = p.distance(clusters.get(0).getBarycentre());
                for (int i=1; i<k; i++) {
                    double d = p.distance(clusters.get(i).getBarycentre());
                    if (d < minDist) { minDist = d; best = i; }
                }
                nextGroups.get(best).add(p);
            }

            for (int i=0; i<k; i++) {
                Point oldBary = clusters.get(i).getBarycentre();
                clusters.get(i).getPoints().clear();
                clusters.get(i).getPoints().addAll(nextGroups.get(i));
                clusters.get(i).updateBarycentre();
                if (!oldBary.equals(clusters.get(i).getBarycentre())) changed = true;
            }
        }

        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        for (Cluster c : clusters) result.add(c.getPoints());
        return result;
    }

    // --- K-MEANS BUDGET ---
    public ArrayList<ArrayList<Point>> calculKMeansBudget(ArrayList<Point> points) {
        int k = 5;
        double budget = 10101.0;
        ArrayList<Cluster> clusters = new ArrayList<>();
        
        for (int i = 0; i < k; i++) {
            Cluster c = new Cluster();
            c.addPoint(points.get(i));
            clusters.add(c);
        }

        ArrayList<Point> rest = new ArrayList<>(points.subList(k, points.size()));
        boolean added = true;
        while (added) {
            added = false;
            Point bestP = null;
            int bestC = -1;
            double minCost = Double.MAX_VALUE;

            for (Point p : rest) {
                for (int i = 0; i < k; i++) {
                    Cluster c = clusters.get(i);
                    // Simulation
                    c.getPoints().add(p);
                    Point oldBary = c.getBarycentre();
                    c.updateBarycentre();
                    double score = c.getScore();

                    if (score <= budget && score < minCost) {
                        minCost = score;
                        bestP = p;
                        bestC = i;
                    }
                    // Backtrack
                    c.getPoints().remove(c.getPoints().size()-1);
                    c.barycentre = oldBary; 
                }
            }
            if (bestP != null) {
                clusters.get(bestC).addPoint(bestP);
                rest.remove(bestP);
                added = true;
            }
        }
        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        for (Cluster c : clusters) result.add(c.getPoints());
        return result;
    }
}