package ru.sherb.go;

public class Biome {

    private final boolean generateLight;

    private final byte influence;

    public Biome(byte influence, boolean generateLight) {
        this.influence = influence;
        this.generateLight = generateLight;
    }
}
