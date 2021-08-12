package ar.edu.itba.ss.cellindexmethod;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {
    private final int m;
    private final double l;
    private final double r;
    private final boolean isPeriodic;

    private final List<List<Particle>> cells = new ArrayList<>();

    // Whoever calls this should check that l/m > r
    public CellIndexMethod(int m, double l, double r, boolean isPeriodic) {
        this.m = m;
        this.l = l;
        this.r = r;
        this.isPeriodic = isPeriodic;
    }

    public void run(List<Particle> particles) {
        // We clear variables
        cells.clear();
        for(Particle p: particles) {
            p.clearNeighbors();
        }

        // We initialize the cell matrix
        for (int i = 0; i < m * m; i++) {
            cells.add(new ArrayList<>());
        }

        // We add each particle to its cell
        for (Particle p: particles) {
            double cellX = Math.floor(p.getCoordX() / (l/m));
            double cellY = Math.floor(p.getCoordY() / (l/m));
            List<Particle> cell = cells.get((int) (cellY * m + cellX));
            p.setCellX(cellX);
            p.setCellY(cellY);
            cell.add(p);
        }

        // For each cell we calculate neighbors using adjacent cells
        for (List<Particle> cell : cells) {
            for (Particle p: cell) {
                double cellX = p.getCellX();
                double cellY = p.getCellY();

                // Im assuming bottom left is (0,0) and x goes to the right
                addNeighbors(p, cellX, cellY);                        // Same cell
                addNeighbors(p, cellX, cellY + 1);               // On top
                addNeighbors(p, cellX + 1, cellY + 1);      // Top right
                addNeighbors(p, cellX + 1, cellY);               // Directly right
                addNeighbors(p, cellX + 1, cellY - 1);      // Bottom right
            }
        }
    }

    private void addNeighbors(Particle p, double cellX, double cellY) {
        if (isPeriodic) {
            // Normalize cellX and cellY
            if (cellX >= m)
                cellX = 0;
            if (cellY >= m)
                cellY = 0;
            if (cellX == -1)
                cellX = m - 1;
            if (cellY == -1)
                cellY = m - 1;
        } else {
            // Ignore out of bounds
            if (cellX >= m || cellX < 0 || cellY >= m || cellY < 0)
                return;
        }

        List<Particle> cell = cells.get((int) (cellY * m + cellX));

        for (Particle adjacent: cell) {
            // We skip if same particle
            if (adjacent.getParticleId() == p.getParticleId())
                continue;

            double distance;

            if (isPeriodic)
                distance = p.getPeriodicDistanceTo(adjacent, l);
            else
                distance = p.getDistanceTo(adjacent);

            // Not sure if < or <=
            if (distance < r) {
                p.addNeighbor(adjacent);
                adjacent.addNeighbor(p);
            }
        }
    }
}
