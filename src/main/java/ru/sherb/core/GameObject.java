package ru.sherb.core;

public abstract class GameObject {
    private final int id;

    /**
     * Уникальность {@code id} обеспечивается вызывающим объектом.
     * @param id Номер текущего объекта, назначается вызывающим объектом
     */
    public GameObject(int id) {
        this.id = id;
    }

    /**
     * Конструктор по умолчанию. В {@code id} сохраняется хэш-сумма созданного объекта.
     */
    public GameObject() {
        this.id = System.identityHashCode(this);
    }

    /**
     * Метод для инициализации начальных значений объекта.
     * <br>Рекомендуется инициализировать значения здесь, а не в конструкторе, что бы избежать ошибок.</br>
     */
    public abstract void init();

    /**
     * Метод для обновления состояния игрового объекта.
     * <br>Рекомендуется после обновления ставить флаг {@code shouldBeRender = true}</br>
     * @param dt Время, прощедшее с последнего обновления
     */
    public abstract void update(float dt);

    /**
     * Метод вызывается при удалении объекта или прекращении игровой сессии
     */
    public abstract void end();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObject that = (GameObject) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
