package ru.sherb.core.render;

import ru.sherb.core.Collection;
import ru.sherb.core.VisualObject;

public interface IRender {

    /**
     * Инициализация начальных значений, вызывается один раз перед первым запуском
     */
    void init();

    /**
     * Рисует на графическом контексте все визуальные компоненты
     * @param visualObjects все визуальные компоненты, которые нужно отрисовать
     */
    void paint(Collection parent, VisualObject... visualObjects);
}
