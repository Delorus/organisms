package ru.sherb.go;

import ru.sherb.core.VisualObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

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
            return new Chromosome(
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
    // Существующий алгоритм будет нормально функционировать только при значении eyeshot = 1
    // Т.к. для некоторый действий требуется, что бы организмы стояли вплотную друг к другу
    // Для нормального функционирования стоит реализовать механизм движения организма к его цели
    private byte eyeshot = 1; //TODO сделать зависимость от гена SENSITIVITY

    //TODO сделать кэширование соседей, если потребуется оптимизация


    private State state;

    enum State {
        DEAD,
        SLEEP,
        ESTRUS,
        REPRODUCE,
        ATTACK,
        EAT, //а надо ли?
        MOVE;

        static Organism other;

        void with(Organism other) {
            State.other = other;
        }

        Optional<Organism> with() {
            return Optional.ofNullable(other);
        }
    }



    private ArrayList<Organism> neighbors; //TODO изменить на LinkedList когда соседей станет больше 8


    Organism(Controller controller, Chromosome... chromosomes) {
        super(controller.getOrganismSize());

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
        phenotype = identifyPhenotype(this.chromosomes);
        this.age = 0;
        this.energy = phenotype.get(Chromosome.EFFICIENCY); //TODO решить, ограничивать ли количество энергии или нет
        //TODO доделать метод
        //init hereditaryChromosome;
        setShouldBeRender(true);
    }

    @Override
    public void update(float dt) {
        switch (controller.getPhase()) {
            case PREPARE:
                prepare();
                break;
            case ACTION:
                assert state != null;
                action();
                age++;
                setColorDependState();
                setShouldBeRender(true);
                System.out.println(toString());
                break;
        }
    }

    /**
     * Стадия подготовки к действию.
     * <br/>
     * На этой стадии организм решает каким образом он будет действовать в этом ходу.
     * Результатом его выбора является установка флага состояния в {@link Organism#state}
     *
     * <p/>
     * Алгоритм выбора состояния:
     * <ul>
     *      <li>Проверяется, привысила ли клетка заложенный максимальный возраст, если да, то она выставляет флаг смерти {@link State#DEAD} и завершает ход</li>
     *
     *      <li>Клетка подвергается воздействию среды (см. {@link Organism#getEffectFromEnvironment()})</li>
     *
     *      <li>Если энергия клетки равна 0, то клетка выставляет флаг сна {@link State#SLEEP} и завершает ход</li>
     *
     *      <li>Если энергия клетки меньше нуля, то она выставляет флаг смерти {@link State#DEAD} и завершает ход</li>
     *
     *      <li>Если энергия клетки больше или равна значению, обратно пропорциональному гену репродукции,
     *      то клетка проверяет у соседей наличие статуса "готов к спариванию" {@link State#ESTRUS},
     *      если такой найден, то клетка устанавливает статус "спаривание" ({@link State#REPRODUCE}) и указывает его в качестве партнера,
     *      если же таких нет, то она сама ставит статус "готов к спариванию" и завершает ход</li>
     *
     *      <li>Если энергии клетки меньше чем ее агрессия, то клетка начинает искать, кто из ее соседей подойдет ей в качестве пищи,
     *      если такой найден, то клетка выставляет статус нападения {@link State#ATTACK}, установив его в качестве своей цели, и завершает ход</li>
     *
     *      <li>Если количество соседей умноженное на 16, больше чем социальность организма,
     *      то организм ставит флаг перемещение({@link State#MOVE}) на любую незанятую соседнюю клетку</li>
     *
     *      <li>Если количество соседей, делающих одно и тоже, умноженное на 16, больше чем число, обратно пропорциональное гену эмпатии,
     *      то организм скопирует их поведение (флаги)</li>
     * </ul>
     */
    void prepare() { //TODO добавить поедание света и (?)мертвых клеток(?)
        if (isTooOld()) {
            state = State.DEAD;
            return;
        }

        getEffectFromEnvironment();

        if (energy == 0) {
            state = State.SLEEP;
            return;
        }
        if (energy < 0) {
            state = State.DEAD;
            return;
        }

        neighbors = null; //TODO из-за параллельного выполнение данные могут быть не актуальными

        if (energy >= (Byte.MAX_VALUE + 1 - phenotype.get(Chromosome.REPRODUCTION))) {
            //TODO изменить механизм: удалить состояние "спаривание", оставить только готовность
            final Optional<Organism> readyToMateOrg = checkNeighbors(eyeshot, organism -> State.ESTRUS.equals(organism.state));
            if (!readyToMateOrg.isPresent()) {
                state = State.ESTRUS;
                return;
            }

            state = State.REPRODUCE;
            state.with(readyToMateOrg.get());
            return;
        }

        if (energy < phenotype.get(Chromosome.AGGRESSION)) {
            final Optional<Organism> food = checkNeighbors(eyeshot, organism ->
                    checkForMatchGene(organism.phenotype.get(Chromosome.TYPE), phenotype.get(Chromosome.NUTRITION)) == 0);

            if (food.isPresent()) {
                state = State.ATTACK;
                state.with(food.get());
                return;
            }
        }

        //TODO доделать социальность и эмпатию
        state = State.SLEEP;
    }

    boolean isTooOld() {
        return age >= phenotype.get(Chromosome.AGE);
    }

    private void getEffectFromEnvironment() {
        final Optional<Biome> currentBiome = controller.getBiome(getPosition());
        if (currentBiome.isPresent()) {
            final int resistance = checkForMatchGene(currentBiome.get().getInfluence(), phenotype.get(Chromosome.RESISTANCE)); //~(~currentBiome.get().getInfluence() | phenotype.get(Chromosome.RESISTANCE));
            if (resistance > 0) {
                energy -= Integer.bitCount(resistance & 0xff);
            }
        }
    }

    private Optional<Organism> checkNeighbors(int range, Predicate<Organism> predicate) {
        final int x = (int) getPosition().x;
        final int y = (int) getPosition().y;

        if (neighbors == null) {
            neighbors = new ArrayList<>(8);
            for (int i = controller.motion(x - range);
                 i != controller.motion(x + range + 1);
                 i = controller.motion(i + 1)) {

                for (int j = controller.motion(y - range);
                     j != controller.motion(y + range + 1);
                     j = controller.motion(j + 1)) {

                    controller.getOrganism(i, j).ifPresent(organism -> {
                        if (!organism.equals(this)) {
                            neighbors.add(organism);
                        }
                    });
                }
            }
        }

        return neighbors.stream()
                .filter(predicate)
                .findFirst();
    }

    private int checkForMatchGene(byte effect, byte gene) {
        return ~(~effect | gene);
    }

    void setColorDependState() {
        //TODO перенести выбор цвета в num
        switch (state) {
            case DEAD:
                setColor(Color.YELLOW);
                break;
            case SLEEP:
                setColor(Color.CYAN);
                break;
            case ESTRUS:
                setColor(Color.PINK);
                break;
            case REPRODUCE:
                setColor(Color.MAGENTA);
                break;
            case ATTACK:
                setColor(Color.RED);
                break;
            case EAT:
                setColor(Color.GREEN);
                break;
            case MOVE:
                setColor(Color.BLUE);
                break;
        }
    }

    private void action() {
        //TODO написать метод
    }

    State getState() {
        return state;
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

    @Override
    public String toString() {
        return "Organism{" +
                "age=" + age +
                ", energy=" + energy +
                ", state=" + state +
                '}';
    }
}
