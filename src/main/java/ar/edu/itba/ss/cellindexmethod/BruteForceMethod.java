package ar.edu.itba.ss.cellindexmethod;

import java.util.*;

import static java.lang.Math.sqrt;

public class BruteForceMethod {
	private long N;
	private long L;
	private double Rc;
	private List<Particle> particlesList;
	private Map<Particle, Set<Particle>> closestParticles;

	public BruteForceMethod(long N, long L, double Rc, List<Particle> particlesList) {
		this.N = N;
		this.L = L;
		this.Rc = Rc;
		this.particlesList = particlesList;
		this.closestParticles = new HashMap<>();
	}

	private boolean isClosestParticle (Particle particle1, Particle particle2) {
		double result = sqrt(Math.pow((particle1.getCoordX() - particle2.getCoordX()),2) + Math.pow((particle1.getCoordY() - particle2.getCoordY()),2)) - particle1.getRadius() - particle2.getRadius();
		if (result < 0){
			result = 0;
		}
		return result < this.Rc ? true : false;
	}

	public Map<Particle,Set<Particle>> algorithm() {
		Set<Particle> particlesUsed = new HashSet<>();
		for (Particle particle1 : this.particlesList) {
			for (Particle particle2 : this.particlesList) {
				if ( !particlesUsed.contains(particle2) && !particle2.equals(particle1)) {
					if (isClosestParticle(particle1, particle2)) {
						if(this.closestParticles.get(particle1).isEmpty()){
							this.closestParticles.put(particle1, new HashSet<>());
						}
						if(this.closestParticles.get(particle2).isEmpty()){
							this.closestParticles.put(particle2, new HashSet<>());
						}
						this.closestParticles.get(particle1).add(particle2);
						this.closestParticles.get(particle2).add(particle1);
					}
				}
			}
			particlesUsed.add(particle1);
		}
		return this.closestParticles;
	}

}
