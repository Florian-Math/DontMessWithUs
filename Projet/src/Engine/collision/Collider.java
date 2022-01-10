package Engine.collision;

import java.util.ArrayList;

import Engine.GameObject;
import Engine.Vector2;
import Math.Mathf;

public abstract class Collider {

	private static ArrayList<Collider> colliders;
	
	protected ColliderType type;
	protected GameObject parent;
	protected boolean movable;
	
	public enum ColliderType{
		box,
		circle
	}
	
	public Collider(GameObject parent, ColliderType type, boolean movable) {
		if(colliders == null) colliders = new ArrayList<Collider>();
		colliders.add(this);
		
		this.parent = parent;
		this.type = type;
		this.movable = movable;
	}
	
	public void dispose() {
		colliders.remove(this);
	}
	
	/**
	 * Verifie la collision et calcul les nouvelles positions
	 * @return
	 */
	public final boolean collide() {
		boolean isColliding = false;
		boolean currentlyColliding = false;
		
		byte index = 0;
		if(type == ColliderType.circle) index += 1;
		
		for (Collider collider : colliders) {
			if(collider == this) continue; // skip si le collider est lui meme
			
			if(collider.getType() == ColliderType.circle) index += 2;
			
			//compare les colliders
			switch (index) {
			case 0:
				currentlyColliding = collide((BoxCollider)this, (BoxCollider)collider); // box / box
				break;
			case 1:
				currentlyColliding = collide((BoxCollider)collider, (CircleCollider)this); // circle / box
				break;
			case 2:
				currentlyColliding = collide((BoxCollider)this, (CircleCollider)collider); // box / circle
				break;
			case 3:
				currentlyColliding = collide((CircleCollider)this, (CircleCollider)collider); // circle / circle
				break;
			}
			
			if(currentlyColliding) isColliding = true;
		}
		
		return isColliding;
	}
	
	private boolean collide(BoxCollider c1, BoxCollider c2) {
		
		return false;
	}
	
	private boolean collide(BoxCollider c1, CircleCollider c2) { // source : https://www.coding-daddy.xyz/node/29
		Vector2 posC1 = c1.getPosition();
		Vector2 posC2 = c2.getPosition();
		
		float closestX = Mathf.clamp(posC2.x, posC1.x - c1.getWidth()/2, posC1.x + c1.getWidth()/2);
		float closestY = Mathf.clamp(posC2.y, posC1.y - c1.getHeight()/2, posC1.y + c1.getHeight()/2);
		
		Vector2 distance = new Vector2(posC2.x - closestX, posC2.y - closestY);
		
		if(distance.len2() < Math.pow(c2.getRadius(), 2)) {
			
			if(c1.movable) { // calcul position cube
				
			}else if(c2.movable) { // calcul position cercle
		        Vector2 normalizedDist = new Vector2(distance).normalize();
		        
		        float cx = posC2.x + normalizedDist.x*(Math.abs(normalizedDist.x)*c2.getRadius()-Math.abs(distance.x));
				float cy = posC2.y + normalizedDist.y*(Math.abs(normalizedDist.y)*c2.getRadius()-Math.abs(distance.y));

				c2.parent.setPosition(new Vector2(cx, cy));
			}
			
			return true;
		}else 
			return false;
	}
	
	private boolean collide(CircleCollider c1, CircleCollider c2) {
		
		return false;
	}
	
	/**
	 * Retourne le type du collider
	 * @return
	 */
	public final ColliderType getType() {
		return type;
	}
	
	public static final Collider[] getColliders() {
		return colliders.toArray(new Collider[0]);
	}
	
	/**
	 * Retourne les coins de la boite de collision 
	 * @return
	 */
	public abstract Vector2[] getBounds();
	
	public static void resetCollider() {
		colliders.clear();
	}
	
}
