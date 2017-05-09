package ru.sherb;

import ru.sherb.core.Game;
import ru.sherb.go.Biome;
import ru.sherb.go.Controller;
import ru.sherb.go.OrganismFactory;

import java.awt.*;

public class Launcher {
    public static void main(String[] args) {
        Game game = new Game(400, 400, "The Organisms", Color.WHITE);

        Controller controller = new Controller(10, Color.WHITE, Color.black);
        game.addCollection(controller);

        controller.addBiome(Biome.createParadise(new Point.Float(0, 0), new Point.Float(10, 10)));
        OrganismFactory factory = new OrganismFactory(controller);
        factory.createWithRandChromosomes(new Point.Float(0, 0), 2);

        game.start();
    }
}
