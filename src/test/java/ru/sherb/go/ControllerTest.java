package ru.sherb.go;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by sherb on 04.05.2017.
 */
class ControllerTest {
    OrganismFactory factory;
    Controller controller;

    @BeforeEach
    void setUp() {
        factory = new OrganismFactory(controller);
        controller = new Controller(0, null, null);
    }

    @Test
    void addVisualObject() {
        factory.createWithRandChromosomes(new Point2D.Float(2, 3), 3);
    }

    @Test
    void getOrganism() {
        factory.createWithRandChromosomes(new Point2D.Float(2, 2), 3);
        Organism organism = controller.getOrganism(2, 2).get();
        assertNotNull(organism);

    }
}