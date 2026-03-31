package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cluster {
    private ArrayList<Point> points;
    private Point barycentre;
    private double currentScore;

    public Cluster() {
        this.points = new ArrayList<>();
        this.barycentre = new Point(0, 0);
        this.currentScore = 0.0;
    }

    public void addPoint(Point p) {
        points.add(p);
        updateBarycentre();
    }

    public void removePoint(Point p) {
        points.remove(p);
        updateBarycentre();
    }

    private void updateBarycentre() {
        if (points.isEmpty()) return;
        long sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        this.barycentre = new Point((int) (sumX / points.size()), (int) (sumY / points.size()));
        updateScore();
    }

    private void updateScore() {
        double sum = 0;
        for (Point p : points) {
            sum += p.distance(this.barycentre);
        }
        this.currentScore = sum;
    }

    public ArrayList<Point> getPoints() { return points; }
    public Point getBarycentre() { return barycentre; }
    public double getScore() { return currentScore; }
}