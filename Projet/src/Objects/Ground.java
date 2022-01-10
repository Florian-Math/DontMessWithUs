package Objects;

import Engine.GameObject;
import Engine.Renderer;
import Engine.Screen;
import Engine.Vector2;
import Engine.graphics.Sprite;
import Engine.graphics.SpriteSheet;

public class Ground extends GameObject {

	private int size;
	Sprite sprite;
	float pixelSize;
	
	public Ground(Vector2 position) {
		super(position);
		this.size = 100;
		
		sprite = new Sprite("images/ground.png");
	}

	@Override
	public void render(Renderer r) {
		r.drawSprite(position, sprite);
	}
	
}
