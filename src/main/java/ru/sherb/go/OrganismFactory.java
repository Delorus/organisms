package ru.sherb.go;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Stream;

public class OrganismFactory {
    private Controller controller;
    private HashMap<String, Organism.Chromosome> chromosomes = new HashMap<>();
    private Point2D.Float organismSize = new Point2D.Float();

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addChromosome(String name, Organism.Chromosome chromosome) {
        chromosomes.put(name, chromosome);
    }

    public void removeChromosome(String name) {
        chromosomes.remove(name);
    }

    public void setOrganismSize(Point2D.Float size) {
        organismSize.setLocation(size);
    }

    public Organism createWithOneChromosome(Point2D.Float position, String chromosomeName) {
        final Organism organism = new Organism(organismSize, controller, chromosomes.get(chromosomeName));
        organism.setPosition(position);
        return organism;
    }

    public Organism createWithChromosomes(Point2D.Float position, String... chromosomeNames) {
        final Organism.Chromosome[] chromosomes = new Organism.Chromosome[chromosomeNames.length];
        for (int i = 0; i < chromosomeNames.length; i++) {
            chromosomes[i] = this.chromosomes.get(chromosomeNames[i]);
        }

        final Organism organism = new Organism(organismSize, controller, chromosomes);
        organism.setPosition(position);
        return organism;
    }

    public Organism createWithAnyChromosomes(Point2D.Float position) {
        final Organism.Chromosome[] randChromosomes = chromosomes.values()
                .stream()
                .filter(o -> Math.random() <= 0.5)
                .toArray(Organism.Chromosome[]::new);

        final Organism organism = new Organism(organismSize, controller, randChromosomes);
        organism.setPosition(position);
        return organism;
    }

    public Organism createWithRandChromosomes(Point2D.Float position, int countChromosomes) {

        final Stream<Organism.Chromosome> chromosomeStream = Stream.generate(() -> {
            final byte[] values = new byte[Organism.Chromosome.NUM_OF_GENES];
            new Random().nextBytes(values);
            return new Organism.Chromosome(
                    values[Organism.Chromosome.EFFICIENCY],
                    values[Organism.Chromosome.AGE],
                    values[Organism.Chromosome.POWER],
                    values[Organism.Chromosome.REPRODUCTION],
                    values[Organism.Chromosome.SOCIALITY],
                    values[Organism.Chromosome.SENSITIVITY],
                    values[Organism.Chromosome.AGGRESSION],
                    values[Organism.Chromosome.EMPATHY],
                    values[Organism.Chromosome.RESISTANCE],
                    values[Organism.Chromosome.NUTRITION],
                    values[Organism.Chromosome.TYPE]
            );
        });

        final Organism organism = new Organism(organismSize, controller, chromosomeStream.limit(countChromosomes).toArray(Organism.Chromosome[]::new));
        organism.setPosition(position);
        return organism;
    }
}
