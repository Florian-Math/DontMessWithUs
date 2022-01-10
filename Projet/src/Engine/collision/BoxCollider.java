package Engine.collision;

import Engine.GameObject;
import Engine.Vector2;

public class BoxCollider extends Collider {

	private float width;
	private float height;
	
	/**
	 * Construit une boite de collision rectangulaire
	 * @param parent
	 * @param width
	 * @param height
	 * @param movable
	 */
	public BoxCollider(GameObject parent, float width, float height, boolean movable) {
		super(parent, ColliderType.box, movable);
		this.width = width;
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public Vector2 getPosition() {
		return parent.getPosition();
	}

	
	@Override
	public Vector2[] getBounds() {
		Vector2[] bounds = new Vector2[4];
		
		bounds[0] = new Vector2(getPosition().x - width/2, getPosition().y + height/2);
		bounds[1] = new Vector2(getPosition().x + width/2, getPosition().y + height/2);
		bounds[2] = new Vector2(getPosition().x + width/2, getPosition().y - height/2);
		bounds[3] = new Vector2(getPosition().x - width/2, getPosition().y - height/2);
		
		return bounds;
	}
	
}
