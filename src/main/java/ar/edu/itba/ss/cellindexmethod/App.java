package ar.edu.itba.ss.cellindexmethod;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class App
{
    public static void main( String[] args ) throws IOException {
        List<Particle> particleList = new ArrayList<>();

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
            String[] splitStaticLines = staticLine.split("\\s+");
            radius = Double.parseDouble(splitStaticLines[0]);
            String dynamicLine = dynamicReader.readLine();
            String[] splitDynamicLines = dynamicLine.split("\\s+");
            coordX = Double.parseDouble(splitDynamicLines[0]);
            coordY = Double.parseDouble(splitDynamicLines[1]);
            particleList.add(new Particle(coordX, coordY, i, radius));
        }
        BufferedReader staticReaderRadius = new BufferedReader(new FileReader(staticFile));
        staticReaderRadius.readLine();
        staticReaderRadius.readLine();
        String staticLineRadius = staticReaderRadius.readLine();
        String[] splitStaticLinesRadius = staticLineRadius.split("\\s+");
        Double r = Double.parseDouble(splitStaticLinesRadius[0]);


        BruteForceMethod bf = new BruteForceMethod(N,L,r,particleList);
        // calculo el tiempo que tarda en ejecutar
        Instant startTimeBF = Instant.now();
        Map<Particle, Set<Particle>> resultsBF = bf.algorithm();
        Instant endTimeBF = Instant.now();
        Duration totalTimeBF = Duration.between(startTimeBF,endTimeBF);

        FileWriter closestsParticlesBF = new FileWriter("outputBF.txt");

        closestsParticlesBF.write("Total time: " + totalTimeBF.toMillis() + " [ms]\n");
        // itero por todas las claves
        for(Particle particle : resultsBF.keySet()) {
            // si la partícula tiene valores asociados (partículas cercanas) las escribo en el output
            if( resultsBF.get(particle).size() > 0 ) {
                closestsParticlesBF.write(particle.getParticleId() + " : ");
                // itero por todas las partículas cercanas asociadas con la key
                for (Particle closestParticle : resultsBF.get(particle)) {
                    closestsParticlesBF.write(closestParticle.getParticleId() + " ");
                }
                closestsParticlesBF.write("\n");
            }
        }
        System.out.println("Salida generada");
        closestsParticlesBF.close();









    }
    

}
