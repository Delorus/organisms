package ru.sherb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Collection<E extends VisualObject> extends GameObject {
    private List<E> visualObjects; //TODO попробовать организовать сет
    private List<Collection> collections;

    protected void setVisualObjects(java.util.Collection<E> visualObjects) {
        this.visualObjects = new ArrayList<>(visualObjects);
    }

    protected boolean removeVisualObject(E visualObject) {
        if (this.visualObjects.remove(visualObject)) {
            visualObject.end();
            return true;
        }
        return false;
    }

    protected boolean addVisualObject(E visualObject) {
        if (visualObjects == null) {
            visualObjects = new ArrayList<>();
        }

        if (!this.visualObjects.contains(visualObject)) {
            visualObject.init();
            return this.visualObjects.add(visualObject);
        }

        return false;
    }

    protected List<E> getVisualObjects() {
        return visualObjects == null ? Collections.EMPTY_LIST : visualObjects;
    }

    protected void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    protected boolean removeCollection(Collection collection) {
        if (this.collections.remove(collection)) {
            collection.end();
            return true;
        }
        return false;
    }

    protected boolean addCollection(Collection collection) {
        if (collections == null) {
            collections = new ArrayList<>();
        }

        if (!this.collections.contains(collection)) {
            collection.init();
            return this.collections.add(collection);
        }
        return false;
    }

    protected List<Collection> getCollections() {
        return collections == null ? Collections.emptyList() : collections;
    }

    @Override
    public void init() {
        if (collections != null) {
            for (Collection collection : collections) {
                collection.init();
            }
        }

        if (visualObjects != null) {
            for (VisualObject o : visualObjects) {
                o.init();
            }
        }
    }

    @Override
    public void update(float dt) {
        if (collections != null) {
            for (Collection collection : collections) {
                collection.update(dt);
            }
        }

        if (visualObjects != null) {
            for (VisualObject o : visualObjects) {
                o.update(dt);
            }
        }
    }

    @Override
    public void end() {
        if (collections != null) {
            for (Collection collection : collections) {
                collection.end();
            }
        }

        if (visualObjects != null) {
            for (VisualObject o : visualObjects) {
                o.end();
            }
        }
    }

    public void render() {
        if (collections != null) {
            for (Collection collection : collections) {
                collection.render();
            }
        }

        if (visualObjects != null) {
            for (VisualObject vo : visualObjects) {
                if (vo.isShouldBeRender()) {
                    Game.getRender().paint(this, vo);
                }
            }
        }
    }
}
