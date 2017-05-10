package ru.sherb.go;

import ru.sherb.core.Collection;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Controller extends Collection<Organism> {
    private final Point.Float organismSize;
    private final int size;

    private Phase phase;

    private Point2D.Float cellScale;

    enum Phase {
        PREPARE,
        COLLISION,
        ACTION
    }

    public Controller(int universeSize) {
        this.size = universeSize;
        this.organismSize = new Point.Float(10, 10);
    }

    public Point.Float getOrganismSize() {
        return organismSize;
    }

    public void setCellScale(Point2D.Float scale) {
        this.cellScale = scale;

        getVisualObjects().forEach(cell -> cell.setScale(cellScale));
    }

    private List<Biome> biomes = new ArrayList<>();

    protected int torusMotion(int expectedPos) {
        return (((expectedPos % size) + size) % size);
    }

    protected int motion(int expectedPos) {
        return torusMotion(expectedPos);
    }

    @Override
    protected boolean addVisualObject(Organism visualObject) {
        return super.addVisualObject(visualObject);
    }

    public boolean addBiome(Biome biome) {
        if (this.biomes.contains(biome)) {
            return false;
        }

        this.biomes.add(biome);
        return true;
    }

    Optional<Organism> getOrganism(Point2D.Float pos) {
//        return getVisualObjects()
//                .stream()
//                .reduce((organism, organism2) ->
//                        organism.getPosition().equals(pos) ? organism :
//                                organism2.getPosition().equals(pos) ? organism2 : null)
//                .get();

        return getOrganism(pos.x, pos.y);
    }

    Optional<Organism> getOrganism(float x, float y) {

        return getVisualObjects()
                .stream()
                .filter((organism) -> organism.getPosition().x == x && organism.getPosition().y == y)
                .findFirst();
    }

    Optional<Biome> getBiome(Point2D.Float pos) {
        return biomes.stream()
                .filter(biome -> biome.AreaContains(pos))
                .findFirst();
    }

    @Override
    public void init() {
        super.init();
        phase = Phase.PREPARE;
    }

    @Override
    public void update(float dt) {
        switch (phase) {
            case PREPARE:
                //TODO распараллелить обновление
                for (Organism organism : getVisualObjects()) {
                    organism.update(dt);
                }
                phase = Phase.COLLISION;
                break;
            case COLLISION:
                //TODO удалить все "мертвые" объекты
                assert getVisualObjects() != null;
                final List<Organism> organisms = getVisualObjects().stream()
                        .filter(organism -> Organism.State.DEAD.equals(organism.getState()))
                        .collect(Collectors.toList());
                for (Organism organism : organisms) {
                    removeVisualObject(organism);
                }
                //TODO разобраться с коллизией
                phase = Phase.ACTION;
                break;
            case ACTION:
                //TODO обновить состояние игрового поля
                //TODO распараллелить действие
                for (Organism organism : getVisualObjects()) {
                    organism.update(dt);
                }
                phase = Phase.PREPARE;
                break;
        }
    }

    Phase getPhase() {
        return phase;
    }
}
