package ru.sherb.core;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class VisualObject extends GameObject {
//    public static final Point2D.Float DEFAULT_SIZE = new Point2D.Float(1, 1);

    /**
     * Размер в двумерном измерении x - ширина, y - высота
     */
    private final Point2D.Float size;

    /**
     * Модификатор на который изменяется размер двумерного игрового объекта
     */
    private Point2D.Float scale = new Point2D.Float(1, 1);

    private Point2D.Float position;
    private Color color;
    private boolean shouldBeRender;

    public VisualObject(Point2D.Float size) {
        this.size = size;
    }

    public VisualObject(int id, Point2D.Float size) {
        super(id);
        this.size = size;
    }


    public Point2D.Float getPosition() {
        return position;
    }

    public void setPosition(Point2D.Float position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isShouldBeRender() {
        return shouldBeRender;
    }

    public void setShouldBeRender(boolean shouldBeRender) {
        this.shouldBeRender = shouldBeRender;
    }

    public Point2D.Float getScale() {
        return scale;
    }

    public void setScale(Point2D.Float scale) {
        this.scale = scale;
    }

    public Point2D.Float getSize() {
        return size;
    }
}
