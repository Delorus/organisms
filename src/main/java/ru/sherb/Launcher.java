package ru.sherb;

import ru.sherb.core.Game;
import ru.sherb.go.Biome;
import ru.sherb.go.Controller;

import java.awt.*;

public class Launcher {
    public static void main(String[] args) {
        Game game = new Game(400, 400, "The Organisms", Color.WHITE);

        Controller controller = new Controller(10, 1, Color.WHITE, Color.black);
        game.addCollection(controller);

//        controller.addBiome(Biome.createParadise());

        game.start();
    }
}
