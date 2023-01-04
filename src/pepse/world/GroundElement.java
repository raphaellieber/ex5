package pepse.world;

import danogl.GameObject;

import java.util.TreeMap;

public interface GroundElement {
    public TreeMap<Integer, GameObject[]> getGameObjectsMap();
    public void createInRange(int minX, int maxX);
    public float groundHeightAt(float x);
    public void extendRight();
    public void extendLeft();
}
