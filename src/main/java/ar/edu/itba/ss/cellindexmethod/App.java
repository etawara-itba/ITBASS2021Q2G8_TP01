package ar.edu.itba.ss.cellindexmethod;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class App {
    public static void main(String[] args) throws IOException {
        List<Particle> particleList = new ArrayList<>();
        double maxRadius = 0;

        File staticFile = new File("C:\\Users\\Laptop\\Desktop\\ITBASS2021Q2G8_TP01\\inputGenerator\\StaticFile.txt");
        File dynamicFile = new File("C:\\Users\\Laptop\\Desktop\\ITBASS2021Q2G8_TP01\\inputGenerator\\DynamicFile.txt");

        BufferedReader staticReader = new BufferedReader(new FileReader(staticFile));
        BufferedReader dynamicReader = new BufferedReader(new FileReader(dynamicFile));





        int N = Integer.parseInt(staticReader.readLine());
        int L = Integer.parseInt(staticReader.readLine());
        dynamicReader.readLine();
        int i;
        double radius, coordX, coordY;
        for (i = 0; i < N; i++) {
            String staticLine = staticReader.readLine();
            String[] splitStaticLines = staticLine.split("\\t+");
            radius = Double.parseDouble(splitStaticLines[0]);
            if(radius > maxRadius){
                maxRadius = radius;
            }
            String dynamicLine = dynamicReader.readLine();
            String[] splitDynamicLines = dynamicLine.split("\\t+");
            coordX = Double.parseDouble(splitDynamicLines[0]);
            coordY = Double.parseDouble(splitDynamicLines[1]);
            particleList.add(new Particle(coordX, coordY, i, radius));
        }
        BufferedReader staticReaderRadius = new BufferedReader(new FileReader(staticFile));
        staticReaderRadius.readLine();
        staticReaderRadius.readLine();
        String staticLineRadius = staticReaderRadius.readLine();
        String[] splitStaticLinesRadius = staticLineRadius.split("\\t+");
        Double r = Double.parseDouble(splitStaticLinesRadius[0]);


        // Obtengo el óptimo número de celdas
        int bestM = -1;
        double radius1 = 0;
        double radius2 = 0;
        long timeForM = 10000000;
        int M;
        for(Particle particle : particleList) {
            if(particle.getRadius() >= radius1){
                radius2 = radius1;
                radius1 = particle.getRadius();
            } else if(particle.getRadius() > radius2)
                radius2 = particle.getRadius();
        }
        // tomo L = 20, Rc = 1
        double bound = 20/(1 + radius1 + radius2);
        for(M = 1 + (int) bound; M > 0; M--) {
            if(20 / M > (1 + radius1 + radius2)){
                CellIndexMethod cimForM = new CellIndexMethod(M, 20 ,1, true);
                Instant startForM = Instant.now();
                cimForM.run(particleList);
                Instant endForM = Instant.now();
                Duration total = Duration.between(startForM,endForM);
                if( total.toMillis() < timeForM){
                    bestM = M;
                    timeForM = total.toMillis();
                }
            }
        }
        System.out.println("Best M: " + bestM);






        BruteForceMethod bf = new BruteForceMethod(N,L,r,particleList);
        // calculo el tiempo que tarda en ejecutar
        Instant startTimeBF = Instant.now();
        Map<Particle, Set<Particle>> resultsBF = bf.algorithm();
        Instant endTimeBF = Instant.now();
        Duration totalTimeBF = Duration.between(startTimeBF, endTimeBF);

        FileWriter closestsParticlesBF = new FileWriter("outputBF.txt");

        closestsParticlesBF.write("Method:\t" + BruteForceMethod.methodKey + "\n");
        closestsParticlesBF.write("Total time:\t" + totalTimeBF.toMillis() + "\t[ms]\n");
        // itero por todas las claves
        for (Particle particle : resultsBF.keySet()) {
            // si la partícula tiene valores asociados (partículas cercanas) las escribo en el output
            if (resultsBF.get(particle).size() > 0) {
                closestsParticlesBF.write("[");
                closestsParticlesBF.write(String.valueOf(particle.getParticleId()));
                // itero por todas las partículas cercanas asociadas con la key
                for (Particle closestParticle : resultsBF.get(particle)) {
                    closestsParticlesBF.write("\t" + closestParticle.getParticleId());
                }
                closestsParticlesBF.write("]");
                closestsParticlesBF.write("\n");
            }
        }
        System.out.println("Salida" + BruteForceMethod.methodKey + "generada");
        closestsParticlesBF.close();

        int m = 10;
        CellIndexMethod cim = new CellIndexMethod(m, L, r, true);
        Instant startTimeCim = Instant.now();
        cim.run(particleList);
        Instant endTimeCim = Instant.now();
        Duration totalTimeCim = Duration.between(startTimeCim, endTimeCim);

        FileWriter closestsParticlesCim = new FileWriter("outputCim.txt");

        closestsParticlesCim.write("Method:\t" + CellIndexMethod.methodKey + "\n");
        closestsParticlesCim.write("Total time:\t" + totalTimeCim.toMillis() + "\t[ms]\n");
        // itero por todas las claves
        for (Particle particle : particleList) {
            // si la partícula tiene valores asociados (partículas cercanas) las escribo en el output
            if (particle.hasNeighbor()) {
                closestsParticlesCim.write("[");
                closestsParticlesCim.write(String.valueOf(particle.getParticleId()));
                // itero por todas las partículas cercanas asociadas con la key
                for (Particle closestParticle : particle.getNeighbors()) {
                    closestsParticlesCim.write("\t" + closestParticle.getParticleId());
                }
                closestsParticlesCim.write("]");
                closestsParticlesCim.write("\n");
            }
        }
        System.out.println("Salida" + CellIndexMethod.methodKey + "generada");
        closestsParticlesCim.close();
    }
}
