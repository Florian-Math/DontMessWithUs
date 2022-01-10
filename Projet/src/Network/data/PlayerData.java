package Network.data;

import Engine.GameObject;
import Engine.Vector2;

public class PlayerData extends ObjectData {

	public Vector2 position;
	public Vector2 visionPosition;
	
	public PlayerData(GameObject object) {
		super(object);
	}

}
