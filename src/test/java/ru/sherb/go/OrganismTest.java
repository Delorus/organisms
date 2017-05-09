package ru.sherb.go;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static ru.sherb.go.Organism.*;

class OrganismTest {
    OrganismFactory factory;
    Controller controller;

    @BeforeEach
    void setUp() {
        factory = new OrganismFactory();
        controller = new Controller(0, 0, null, null);
        factory.setController(controller);
        factory.setOrganismSize(new Point2D.Float(10, 10));
    }

    @Test
    void isDominant() {
        final Organism.Chromosome chromosome = new Organism.Chromosome(
                (byte) 30,
                (byte) 10,
                (byte) -15,
                (byte) 120,
                (byte) -100,
                (byte) 0,
                (byte) 5,
                (byte) -10,
                (byte) 0b0010_0001,
                (byte) 0b1000_0001,
                (byte) 0b0000_0001
        );

        assertTrue(chromosome.isDominant(Chromosome.EFFICIENCY));
        assertTrue(chromosome.isDominant(Chromosome.AGE));
        assertFalse(chromosome.isDominant(Chromosome.POWER));
        assertTrue(chromosome.isDominant(Chromosome.REPRODUCTION));
        assertFalse(chromosome.isDominant(Chromosome.SOCIALITY));
        assertTrue(chromosome.isDominant(Chromosome.SENSITIVITY));
        assertTrue(chromosome.isDominant(Chromosome.AGGRESSION));
        assertFalse(chromosome.isDominant(Chromosome.EMPATHY));
        assertTrue(chromosome.isDominant(Chromosome.RESISTANCE));
        assertFalse(chromosome.isDominant(Chromosome.NUTRITION));
        assertTrue(chromosome.isDominant(Chromosome.TYPE));
    }

    @Test
    void setDominant() {
        final Organism.Chromosome chromosome = new Organism.Chromosome(
                (byte) 30,
                (byte) 10,
                (byte) -15,
                (byte) 120,
                (byte) -100,
                (byte) 0,
                (byte) 5,
                (byte) -10,
                (byte) 0b0010_0001,
                (byte) 0b1000_0001,
                (byte) 0b0000_0001
        );

        assertTrue(chromosome.isDominant(Chromosome.EFFICIENCY));

        chromosome.setDominant(Chromosome.EFFICIENCY, false);

        assertFalse(chromosome.isDominant(Chromosome.EFFICIENCY));

        chromosome.setDominant(Chromosome.EFFICIENCY, true);

        assertTrue(chromosome.isDominant(Chromosome.EFFICIENCY));
    }


    @Test
    void testClone() {
        final Organism.Chromosome chromosome = new Organism.Chromosome(
                (byte) 30,
                (byte) 10,
                (byte) -15,
                (byte) 120,
                (byte) -100,
                (byte) 0,
                (byte) 5,
                (byte) -10,
                (byte) 0b0010_0001,
                (byte) 0b1000_0001,
                (byte) 0b0000_0001
        );

        final Organism.Chromosome chromosomeClone = chromosome.clone();

        assertTrue(chromosome.isDominant(Chromosome.EFFICIENCY));

        assertTrue(chromosomeClone.isDominant(Chromosome.EFFICIENCY));

        assertTrue(chromosome.equals(chromosomeClone));

        chromosomeClone.setDominant(Chromosome.EFFICIENCY, false);

        assertTrue(chromosome.isDominant(Chromosome.EFFICIENCY));

        assertFalse(chromosomeClone.isDominant(Chromosome.EFFICIENCY));

        assertFalse(chromosome.equals(chromosomeClone));
    }

    @Test
    void geneCombination() {
        Organism organism = factory.createWithRandChromosomes(new Point2D.Float(10, 10), 10);

        final byte combineGene = organism.geneCombination(new Byte[]{10, -5, -10, 5});
        assertEquals((10 ^ -10 ^ 5 ^ -5), combineGene);
    }

    @Test
    void getRandDominationGene() {
        Organism organism = factory.createWithRandChromosomes(new Point2D.Float(10, 10), 10);

        final byte dominantGene = organism.getRandDominationGene(new Byte[]{10, 5, 10, 5});
        //TODO доделать проверку
    }

    @Test
    void identifyPhenotype() {
        final Organism.Chromosome chromosome1 = new Organism.Chromosome(
                (byte) 30,
                (byte) -10,
                (byte) 15,
                (byte) -120,
                (byte) -100,
                (byte) -1,
                (byte) 0,
                (byte) -10,
                (byte) 0b1010_0001,
                (byte) 0b1000_0001,
                (byte) 0b1000_0001
        );

        final Organism.Chromosome chromosome2 = new Organism.Chromosome(
                (byte) -40,
                (byte) 11,
                (byte) -31,
                (byte) 119,
                (byte) 110,
                (byte) 0,
                (byte) -5,
                (byte) 14,
                (byte) 0b0000_0010,
                (byte) 0b0000_0100,
                (byte) 0b0010_1000
        );

        factory.addChromosome("chr1", chromosome1);
        factory.addChromosome("chr2", chromosome2);

        Organism organism = factory.createWithChromosomes(new Point2D.Float(10, 10), "chr1", "chr2");

        Chromosome expectedPhenotype = new Chromosome(
                (byte) 30,
                (byte) 11,
                (byte) 15,
                (byte) 119,
                (byte) 110,
                (byte) 0,
                (byte) 0,
                (byte) 14,
                (byte) 0b0000_0010,
                (byte) 0b0000_0100,
                (byte) 0b0010_1000
        );

        assertEquals(expectedPhenotype, organism.getPhenotype());

        //-------------------------------------

        final Organism.Chromosome chromosome3 = new Organism.Chromosome(
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 10,
                (byte) 0b0000_1010,
                (byte) 0b0000_1010,
                (byte) 0b0000_1010
        );
        factory.addChromosome("chr3", chromosome3);

        organism = factory.createWithChromosomes(new Point2D.Float(10, 10), "chr1", "chr2", "chr3");

        expectedPhenotype = new Chromosome(
                (byte) 20,
                (byte) 1,
                (byte) 5,
                (byte) 125,
                (byte) 100,
                (byte) 10,
                (byte) 10,
                (byte) 4,
                (byte) 0b0000_1000,
                (byte) 0b0000_1110,
                (byte) 0b0010_0010
        );

        assertEquals(expectedPhenotype, organism.getPhenotype());

        //-----------------------------------------------------

        System.out.println("chr1:");
        System.out.println(chromosome1);

        final Organism.Chromosome recessiveChromosome = new Organism.Chromosome(
                (byte) 1,
                (byte) 20,
                (byte) 1,
                (byte) 120,
                (byte) 11,
                (byte) 2,
                (byte) 1,
                (byte) 5,
                (byte) 0b0001_1100,
                (byte) 0b0000_0001,
                (byte) 0b0111_1111
        );
        for (int i = 0; i < Chromosome.NUM_OF_GENES; i++) {
            recessiveChromosome.setDominant(i, false);
        }

        System.out.println("recessive:");
        System.out.println(recessiveChromosome);

        factory.addChromosome("recessive", recessiveChromosome);

        organism = factory.createWithChromosomes(new Point2D.Float(10, 10), "chr1", "recessive"); //i don't understand how this works, but it works

        expectedPhenotype = new Chromosome(
                (byte) 30,
                (byte) -0,
                (byte) 5,
                (byte) 125,
                (byte) 100,
                (byte) 10,
                (byte) 10,
                (byte) 4,
                (byte) 0b0000_1000,
                (byte) 0b0000_1110,
                (byte) 0b0010_0010
        );
    }
}