package Network.data;

import java.io.Serializable;

import Engine.GameObject;

public abstract class ObjectData implements Serializable{
	private long objectID;
	
	public ObjectData(GameObject object) {
		this.objectID = object.getID();
	}
	
	public long getObjectID() {
		return objectID;
	}
	
}
