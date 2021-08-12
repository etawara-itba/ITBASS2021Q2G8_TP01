package ar.edu.itba.ss.cellindexmethod;

import java.util.Set;
import java.util.TreeSet;

public class Particle implements Comparable<Particle> {
	private double coordX;
	private double coordY;
	private int particleId;
	private double radius;

	private Set<Particle> neighbors;

	private double cellX;
	private double cellY;

	public Particle(double coordX, double coordY, int particleId, double radius) {
		this.particleId = particleId;
		this.coordX = coordX;
		this.coordY = coordY;
		this.radius = radius;
		this.neighbors = new TreeSet<>();
	}

	public double getCoordX() {
		return coordX;
	}

	public double getCoordY() {
		return coordY;
	}

	public int getParticleId() {
		return particleId;
	}

	public double getRadius() {
		return radius;
	}

	public Set<Particle> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(Particle neighbor) {
		this.neighbors.add(neighbor);
	}

	public double getCellX() {
		return cellX;
	}

	public void setCellX(double cellX) {
		this.cellX = cellX;
	}

	public double getCellY() {
		return cellY;
	}

	public void setCellY(double cellY) {
		this.cellY = cellY;
	}

	public double getDistanceTo(Particle particle) {
		return Math.sqrt(Math.pow(coordX - particle.getCoordX(), 2) + Math.pow(coordY - particle.getCoordY(), 2)) - radius - particle.getRadius();
	}

	public Boolean hasNeighbor(){
		return !this.neighbors.isEmpty();
	}

	public double getPeriodicDistanceTo(Particle particle, double l) {
		double dx = Math.abs(this.coordX - particle.getCoordX());
		if (dx > l / 2)
			dx = l - dx;

		double dy = Math.abs(this.coordY - particle.getCoordY());
		if (dy > l / 2)
			dy = l - dy;

		return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2)) - radius - particle.getRadius();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}
		Particle particle = (Particle) o;
		return particleId == particle.particleId;
	}

	@Override
	public int compareTo(Particle o) {
		return this.particleId - o.getParticleId();
	}

	public void clearNeighbors() {
		this.neighbors.clear();
	}
}
