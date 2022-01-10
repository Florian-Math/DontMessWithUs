package Engine.collision;

import Engine.GameObject;
import Engine.Vector2;

public class CircleCollider extends Collider {
	
	private float radius;
	
	/**
	 * Construit une boite de collision ronde
	 * @param parent
	 * @param radius
	 * @param movable
	 */
	public CircleCollider(GameObject parent, float radius, boolean movable) {
		super(parent, ColliderType.circle, movable);
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public Vector2 getPosition() {
		return parent.getPosition();
	}

	@Override
	public Vector2[] getBounds() {
		return new Vector2[0];
	}
	
	

}
