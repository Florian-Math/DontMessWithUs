package Network.data;

import Engine.GameObject;

public class DroneData extends ObjectData{

	public float progress = 0;
	public int startIndex;
	public int endIndex;
	
	public DroneData(GameObject object) {
		super(object);
	}

}
