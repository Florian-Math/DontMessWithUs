package Objects;

import java.awt.Color;

import Engine.GameObject;
import Engine.Renderer;
import Engine.Vector2;
import Engine.collision.BoxCollider;
import Engine.collision.Collider;

/**
 * Objet representant un mur
 */
public class Wall extends GameObject {

	private Vector2 size;
	private Collider collider;
	
	/**
	 * Construit un mur (attention le mur ne peut etre que vertical ou horizontal)
	 * @param p1
	 * @param p2
	 */
	public Wall(Vector2 p1, Vector2 p2) {
		super();
		
		this.renderingLayer = 2;
		try {
			if(p1.x != p2.x && p1.y != p2.y) throw new Exception("le mur ne peut etre que vertical ou horizontal");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Vector2 dir = Vector2.minus(p2, p1);
		this.position = Vector2.plus(p1, Vector2.div(dir, 2));
		
		float length = dir.length();
		if(p1.x == p2.x) {
			size = new Vector2(100, length + 100);
		}else {
			size = new Vector2(length + 100, 100);
		}
		
		collider = new BoxCollider(this, size.x, size.y, false);
		
		//System.out.println(position + " " + size);
	}
	
	@Override
	public void render(Renderer r) {
		//r.fillRect(position, size.x, size.y, Color.BLACK);
	}
}
