package ru.sherb.go;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Biome {

    private boolean generateLight;
    private byte influence;
    private Set<Point2D.Float> area; //TODO сделать механизм обработки пересечений биомов, они не должны пересекаться

    public static Biome createParadise(Point2D.Float startPos, Point2D.Float endPos) {
        return createSquareBiome((byte) 0, true, startPos, endPos);
    }

    public static Biome createHell(Point2D.Float startPos, Point2D.Float endPos) {
        return createSquareBiome(Byte.MAX_VALUE, false, startPos, endPos);
    }

    public static Biome createRandBiome(Point2D.Float startPos, Point2D.Float endPos) {
        final byte influence = (byte) new Random().nextInt(128);
        final boolean generateLight = new Random().nextBoolean();
        return createSquareBiome(influence, generateLight, startPos, endPos);
    }


    /**
     * Генерирует квадратный биом начиная с {@code startPos} включительно и заканчивая {@code endPos} (исключительно)
     * @param influence тип воздействия на организмы (см. {@link Biome#influence}
     * @param generateLight будет ли биом генерировать свет (свет является пищей для некоторых организмов)
     * @param startPos начальная точка биома, должна быть выше и левее, чем {@code endPos} (включительно)
     * @param endPos конечная точна биома, должна быть ниже и правее, чем {@code startPos} (исключительно)
     * @return сгенерированный биом, с указанными параметрами
     */
    public static Biome createSquareBiome(byte influence, boolean generateLight, Point2D.Float startPos, Point2D.Float endPos) {

        assert startPos.x < endPos.x && startPos.y < endPos.y;
        final int biomeSquare = (int) ((endPos.x - startPos.x) * (endPos.y - startPos.y));
        final HashSet<Point2D.Float> area = new HashSet<>(biomeSquare);
        for (float i = startPos.x; i <= endPos.x; i++) {
            for (float j = startPos.y; j <= endPos.y; j++) {
                area.add(new Point2D.Float(i, j));
            }
        }
        return new Biome(influence, generateLight, area);
    }

    public Biome(byte influence, boolean generateLight, Set<Point2D.Float> area) {
        assert influence >= 0;
        this.influence = influence;
        this.generateLight = generateLight;

        assert area != null;
        this.area = area;
    }



    public boolean isGenerateLight() {
        return generateLight;
    }

    public byte getInfluence() {
        return influence;
    }

    public void setArea(Set<Point2D.Float> area) {
        this.area = area;
    }

    public void addArea(java.util.Collection<Point2D.Float> addArea) {
        this.area.addAll(addArea);
    }

    public void addArea(Point2D.Float addArea) {
        this.area.add(addArea);
    }

    public int areaSize() {
        return area.size();
    }

    public boolean AreaIsEmpty() {
        return area.isEmpty();
    }

    public boolean AreaContains(Point2D.Float area) {
        return this.area.contains(area);
    }

    public boolean AreaRemove(Point2D.Float area) {
        return this.area.remove(area);
    }

    public boolean containsAll(java.util.Collection<Point2D.Float> c) {
        return area.containsAll(c);
    }

    public boolean retainAll(java.util.Collection<Point2D.Float> c) {
        return area.retainAll(c);
    }

    public boolean removeAll(java.util.Collection<Point2D.Float> c) {
        return area.removeAll(c);
    }
}
