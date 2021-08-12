package ar.edu.itba.ss.cellindexmethod;

public class Particle {
	private double coordX;
	private double coordY;
	private int particleId;
	private double radius;

	public Particle(double coordX, double coordY, int particleId, double radius) {
		this.particleId = particleId;
		this.coordX = coordX;
		this.coordY = coordY;
		this.radius = radius;
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
}
