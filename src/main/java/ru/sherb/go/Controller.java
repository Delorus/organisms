package ru.sherb.go;

import ru.sherb.core.Collection;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;

public final class Controller extends Collection<Organism> {

    @Override
    protected boolean addVisualObject(Organism visualObject) {
        //TODO сделать проверку на уникальность
        return super.addVisualObject(visualObject);
    }

    Organism getOrganism(Point2D.Float pos) {
//        return getVisualObjects()
//                .stream()
//                .reduce((organism, organism2) ->
//                        organism.getPosition().equals(pos) ? organism :
//                                organism2.getPosition().equals(pos) ? organism2 : null)
//                .get();

        return getVisualObjects()
                .stream()
                .filter((organism) -> organism.getPosition().equals(pos))
                .findFirst()
                .orElse(null);
    }


}
