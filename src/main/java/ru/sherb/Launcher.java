package ru.sherb;

import ru.sherb.core.Game;
import ru.sherb.go.Biome;
import ru.sherb.go.Controller;
import ru.sherb.go.OrganismFactory;

import java.awt.*;
import java.util.Random;

public class Launcher {
    public static void main(String[] args) {
        final int formSize = 300;
        final int universeSize = 10;
        final String title = "The Organism";

        Game game = new Game(formSize, formSize, title, Color.white);

        Controller controller = new Controller(universeSize);
        game.addCollection(controller);

        controller.addBiome(Biome.createParadise(new Point.Float(0, 0), new Point.Float(10, 10)));
        OrganismFactory factory = new OrganismFactory(controller);

        for (int i = 0; i < universeSize; i++) {
            for (int j = 0; j < universeSize; j++) {
                if (new Random().nextDouble() < 0.1) {
                    factory.createWithRandChromosomes(new Point.Float(i, j), 2);
                }
            }
        }

        game.start();
    }
}
