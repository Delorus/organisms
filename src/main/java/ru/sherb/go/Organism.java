package ru.sherb.go;

import ru.sherb.core.VisualObject;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

class Organism extends VisualObject {

    static class Chromosome implements Cloneable {
        public static final int EFFICIENCY = 0;
        public static final int AGE = 1;
        public static final int POWER = 2;
        public static final int REPRODUCTION = 3;
        public static final int SOCIALITY = 4;
        public static final int SENSITIVITY = 5;
        public static final int AGGRESSION = 6;
        public static final int EMPATHY = 7;
        public static final int RESISTANCE = 8;
        public static final int NUTRITION = 9;
        public static final int TYPE = 10;
        public static final int NUM_OF_GENES = 11;

        private byte[] genes;

        public Chromosome(byte efficiency, byte age, byte power, byte reproduction, byte sociality, byte sensitivity, byte aggression, byte empathy, byte resistance, byte nutrition, byte type) {
            genes = new byte[NUM_OF_GENES];
            genes[EFFICIENCY] = efficiency;
            genes[AGE] = age;
            genes[POWER] = power;
            genes[REPRODUCTION] = reproduction;
            genes[SOCIALITY] = sociality;
            genes[SENSITIVITY] = sensitivity;
            genes[AGGRESSION] = aggression;
            genes[EMPATHY] = empathy;
            genes[RESISTANCE] = resistance;
            genes[NUTRITION] = nutrition;
            genes[TYPE] = type;
        }

        byte get(int geneName) {
            return genes[geneName];
        }

        void set(int geneName, byte value) {
            genes[geneName] = value;
        }

        boolean isDominant(int geneName) {
            return (genes[geneName] & 0b1000_0000) == 0;
        }

        void setDominant(int geneName, boolean dominant) {
            if (dominant) {
                genes[geneName] &= 0b0111_1111;
            } else {
                genes[geneName] |= 0b1000_0000;
            }
        }

        @Override
        protected Chromosome clone() {
            final Chromosome clone = new Chromosome(
                    this.get(Chromosome.EFFICIENCY),
                    this.get(Chromosome.AGE),
                    this.get(Chromosome.POWER),
                    this.get(Chromosome.REPRODUCTION),
                    this.get(Chromosome.SOCIALITY),
                    this.get(Chromosome.SENSITIVITY),
                    this.get(Chromosome.AGGRESSION),
                    this.get(Chromosome.EMPATHY),
                    this.get(Chromosome.RESISTANCE),
                    this.get(Chromosome.NUTRITION),
                    this.get(Chromosome.TYPE)
            );
            return clone;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Chromosome that = (Chromosome) o;

            return Arrays.equals(genes, that.genes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(genes);
        }

        @Override
        public String toString() {
            return "Chromosome {" +
                    "\n efficiency = " + genes[EFFICIENCY] +
                    "\n age = " + genes[AGE] +
                    "\n power = " + genes[POWER] +
                    "\n reproduction = " + genes[REPRODUCTION] +
                    "\n sociality = " + genes[SOCIALITY] +
                    "\n sensivity = " + genes[SENSITIVITY] +
                    "\n aggression = " + genes[AGGRESSION] +
                    "\n empathy = " + genes[EMPATHY] +
                    "\n resistance = " + Integer.toBinaryString((genes[RESISTANCE] & 0xff)) +
                    "\n nutrition = " + Integer.toBinaryString((genes[NUTRITION] & 0xff)) +
                    "\n type = " + Integer.toBinaryString((genes[TYPE] & 0xff)) +
                    "\n}";
        }

    }


    private final Controller controller;

    private Chromosome phenotype;
    private Chromosome hereditaryChromosome; //TODO придумать как обойтись без рандома
    private Chromosome[] chromosomes;

    private byte age;
    private byte energy;


    Organism(Point2D.Float size, Controller controller, Chromosome... chromosomes) {
        super(size);

        assert chromosomes[0] != null;
        this.chromosomes = chromosomes.clone();

        this.controller = controller;
        this.controller.addVisualObject(this);

    }

    /**
     * Определяет фенотип организма из полученных хромосом, по следующим правилам:
     * <ul>
     * <li>Если в одном гене только одна доминантная аллель, то она становится фенотипом</li>
     * <li>Если в одном гене несколько доминантных аллелей, то они комбинируются операцией XOR и результат становится фенотипом</li>
     * <li>Если в одном гене все аллели рецесивные, то среди них случайным(?) образом выбирается одна аллель и становится доминантной</li>
     * </ul>
     *
     * @param chromosomes хромосомы, из которых будет сгенерирован фенотип
     * @return фенотип
     */
    private Chromosome identifyPhenotype(Chromosome[] chromosomes) {
        final byte[] phenotype = new byte[Chromosome.NUM_OF_GENES];

        for (int i = 0; i < Chromosome.NUM_OF_GENES; i++) {
            final LinkedList<Byte> dominantGenes = new LinkedList<>();
            for (Chromosome chromosome : chromosomes) {
                if (chromosome.isDominant(i)) {
                    dominantGenes.add(chromosome.get(i));
                }
            }

            if (dominantGenes.size() > 1) {
                phenotype[i] = geneCombination(dominantGenes.toArray(new Byte[0]));
            } else if (dominantGenes.size() == 1) {
                phenotype[i] = dominantGenes.pop();
            } else {
                final int geneName = i;
                phenotype[i] = getRandDominationGene(Arrays.stream(chromosomes)
                        .map(chromosome -> chromosome.get(geneName))
                        .toArray(Byte[]::new));

                final byte dominantGene = phenotype[i];
                Arrays.stream(chromosomes)
                        .filter(chromosome -> chromosome.get(geneName) == dominantGene)
                        .findFirst()
                        .get()
                        .setDominant(geneName, true);
            }
        }

        final Chromosome phenotypeChromosome = new Chromosome(
                phenotype[Chromosome.EFFICIENCY],
                phenotype[Chromosome.AGE],
                phenotype[Chromosome.POWER],
                phenotype[Chromosome.REPRODUCTION],
                phenotype[Chromosome.SOCIALITY],
                phenotype[Chromosome.SENSITIVITY],
                phenotype[Chromosome.AGGRESSION],
                phenotype[Chromosome.EMPATHY],
                phenotype[Chromosome.RESISTANCE],
                phenotype[Chromosome.NUTRITION],
                phenotype[Chromosome.TYPE]
        );

        for (int i = 0; i < Chromosome.NUM_OF_GENES; i++) {
            if (!phenotypeChromosome.isDominant(i)) {
                phenotypeChromosome.setDominant(i, true);
            }
        }

        return phenotypeChromosome;
    }

    /**
     * Комбинирует полученные гены операцией XOR
     *
     * @param genes гены для комбинации
     * @return результат объединение генов
     */
    /*private*/ byte geneCombination(final Byte... genes) {
        byte result = genes[0];
        for (int i = 1; i < genes.length; i++) {
            result ^= genes[i];
        }

        return result;
    }

    /**
     * Выбирает один ген из множества.
     * Выборка происходит по следующему алгоритму:
     * <ol>
     * <li>Вычисляется среднее значение от всех генов</li>
     * <li>Находится ген, самый ближайший к среднему значению</li>
     * </ol>
     *
     * @param genes гены, из которых выбирается доминантный ген
     * @return доминантный ген
     */
    /*private*/ byte getRandDominationGene(final Byte... genes) {
        final byte avrg = (byte) Math.round(Arrays.stream(genes)
                .mapToInt(Byte::intValue)
                .average()
                .getAsDouble());

        byte dominant = genes[0];
        for (int i = 1; i < genes.length; i++) {
            if (Math.subtractExact(genes[i], avrg) < Math.subtractExact(dominant, avrg)) {
                dominant = genes[i];
            }
        }
        return dominant;

//        return Arrays.stream(genes)
//                .max((o1, o2) -> o1.equals(o2) ? 0 :
//                        Math.subtractExact(avrg, o1) < Math.subtractExact(avrg, o2) ? 1 : -1)
//                .get();

    }

    Chromosome getPhenotype() {
        return phenotype;
    }

    @Override
    public void init() {
        this.phenotype = identifyPhenotype(this.chromosomes);

        //init hereditaryChromosome;
    }

    @Override
    public void update(float dt) {

    }

    boolean isToOld() {
        return age >= phenotype.get(Chromosome.AGE);
    }

    

    @Override
    public void end() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Organism organism = (Organism) o;

        return getPosition() != null ? getPosition().equals(((Organism) o).getPosition()) : organism.getPosition() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        return result;
    }
}
