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
        factory = new OrganismFactory();
        controller = new Controller();
        factory.setController(controller);
        factory.setOrganismSize(new Point2D.Float(10, 10));
    }

    @Test
    void addVisualObject() {
        factory.createWithRandChromosomes(new Point2D.Float(2, 3), 3);
    }

    @Test
    void getOrganism() {
        factory.createWithRandChromosomes(new Point2D.Float(2, 2), 3);
        Organism organism = controller.getOrganism(new Point2D.Float(2, 2));
        assertNotNull(organism);

    }
}