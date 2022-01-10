package Objects;

import java.awt.Color;

import Engine.GameObject;
import Engine.Renderer;
import Engine.Vector2;
import Engine.collision.BoxCollider;
import Engine.collision.Collider;

/**
 * Classe de test representant un cube
 */
public class Cube extends GameObject{

	private int size;
	private Collider collider;
	
	public Cube(Vector2 position, int size) {
		super(position);
		this.size = size;
		this.collider = new BoxCollider(this, size, size, false);
	}

	@Override
	public void render(Renderer r) {
		r.fillRect(position, size, size, Color.BLACK);
	}

}
