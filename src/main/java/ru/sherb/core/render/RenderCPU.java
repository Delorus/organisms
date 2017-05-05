package ru.sherb.core.render;


import ru.sherb.core.Collection;
import ru.sherb.core.VisualObject;

import java.awt.*;
import java.util.Arrays;

public class RenderCPU implements IRender {
    /**
     * Графический контекст для рисования
     */
    private final Canvas canvas;
    private final Color backgroundColor;

    public RenderCPU(Canvas canvas, Color background) {
        this.canvas = canvas;
        this.backgroundColor = background;
    }

    @Override
    public void init() {
        canvas.setBackground(backgroundColor);
        canvas.paint(canvas.getGraphics());

    }

    @Override
    public void paint(Collection parent, VisualObject... visualObjects) {

        Arrays.stream(visualObjects).forEach(visualObject -> {
            final Graphics graphics = canvas.getGraphics();
            graphics.setColor(visualObject.getColor());
            graphics.fillRect(
                    (int) (visualObject.getPosition().y * (visualObject.getSize().y * visualObject.getScale().y)),
                    (int) (visualObject.getPosition().x * (visualObject.getSize().x * visualObject.getScale().x)),
                    (int) (visualObject.getSize().y * (visualObject.getScale().y)),
                    (int) (visualObject.getSize().x * (visualObject.getScale().x))
            );
            graphics.dispose();
        });
    }
}
