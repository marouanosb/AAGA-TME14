package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {

    // --- UTILS ---
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

    private double totalDistance(ArrayList<Point> cluster) {
        Point center = barycentre(cluster);
        double sum = 0;
        for (Point p : cluster) sum += distance(p, center);
        return sum;
    }

    // --- K-MEANS STANDARD ---
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {
        int k = 5;
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();
        ArrayList<Point> centroids = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            centroids.add(points.get(i));
            clusters.add(new ArrayList<Point>());
        }

        boolean changed = true;
        int maxIters = 50; 
        while (changed && maxIters-- > 0) {
            for (ArrayList<Point> cluster : clusters) cluster.clear();
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

    // --- K-MEANS BUDGET ---
    public ArrayList<ArrayList<Point>> calculKMeansBudget(ArrayList<Point> points) {
        int k = 5;
        double budget = 10101.0;
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();

        // Initialisation avec les membres fondateurs (s1...s5)
        for (int i = 0; i < k; i++) {
            ArrayList<Point> cluster = new ArrayList<Point>();
            cluster.add(points.get(i));
            clusters.add(cluster);
        }

        ArrayList<Point> remaining = new ArrayList<>(points.subList(k, points.size()));

        boolean added = true;
        while (added) {
            added = false;
            Point bestPoint = null;
            int bestClusterIdx = -1;
            double minImpact = Double.MAX_VALUE;

            for (Point p : remaining) {
                for (int i = 0; i < k; i++) {
                    ArrayList<Point> current = clusters.get(i);
                    current.add(p);
                    double currentScore = totalDistance(current);
                    
                    if (currentScore <= budget) {
                        if (currentScore < minImpact) {
                            minImpact = currentScore;
                            bestPoint = p;
                            bestClusterIdx = i;
                        }
                    }
                    current.remove(current.size() - 1); // Backtrack
                }
            }

            if (bestPoint != null) {
                clusters.get(bestClusterIdx).add(bestPoint);
                remaining.remove(bestPoint);
                added = true;
            }
        }
        return clusters;
    }
}