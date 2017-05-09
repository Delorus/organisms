package ru.sherb.core;

import ru.sherb.core.render.IRender;
import ru.sherb.core.render.RenderCPU;

import javax.swing.*;
import java.awt.*;

public final class Game extends Collection implements Runnable {
    private static IRender render;

    private static void setRender(IRender render) {
        if (Game.render == null) Game.render = render;
    }

    static IRender getRender() {
        if (render == null) throw new NullPointerException("render must be initialized");

        return render;
    }

    private final String title;
    private final int width;
    private final int height;

    private final Canvas area = new Canvas();

    private boolean running = false;

    public Game(int width, int height, String title, Color backgroundColor) {
        this.title = title;
        this.width = width;
        this.height = height;

        setRender(new RenderCPU(area, backgroundColor));
    }

    @Override
    public boolean addCollection(Collection collection) {
        return super.addCollection(collection);
    }

    public void start() {
        init();

        area.setPreferredSize(new Dimension(width, height));

        final JFrame shell = new JFrame(title);
        shell.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        shell.setLayout(new BorderLayout());
        shell.add(area, BorderLayout.CENTER);
        shell.pack();
        shell.setResizable(false);
        shell.setLocationRelativeTo(null);
        shell.setVisible(true);

        Game.getRender().init();

        running = true;
        new Thread(this).start();
    }

    public void stop() {
        running = false;
        end();
    }


    @Override
    public void run() {
        render();

        final float SECONDS_PER_UPDATE = 1 / 20.f; // Фиксированная частота обновления графической и логической части
        float lag = 0.f;
        float frameTime = 0.f;
        int frameDrawn = 0;
        int tick = 0;
        long lastTime = System.nanoTime();
        boolean shouldRender = false;

        while (running) {
            long now = System.nanoTime();
            float elapsed = (now - lastTime) * 1e-9f; // перевод в секунды
            lag += elapsed;
            frameTime += elapsed;
            lastTime = System.nanoTime();

            while (lag >= SECONDS_PER_UPDATE) {
                update(lag);
                lag -= SECONDS_PER_UPDATE;
                tick++;
                shouldRender = true;
            }

            // Блокировка отрисовки на количество логических проходов
            if (shouldRender) {
                render();
                frameDrawn++;
                shouldRender = false;
            }

            if (frameTime >= 1.0) {
                int fps = (int) (frameDrawn / frameTime);
                System.out.println("FPS: " + fps +
                        "\ntick: " + tick);
                frameDrawn = 0;
                frameTime = 0.f;
                tick = 0;
            }

        }
    }
}
