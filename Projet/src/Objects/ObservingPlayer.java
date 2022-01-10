package Objects;

import Engine.GameObject;
import Engine.Input;
import Engine.Game;
import Engine.Renderer;
import Engine.Vector2;

/**
 * Objet representant le joueur observateur
 */
public class ObservingPlayer extends GameObject {

	private int speed = 1000;
    
    private boolean isPlayed;
	
	public ObservingPlayer(Vector2 position, boolean p) {
		super(position);
		isPlayed = p;
    }
	
	@Override
	public void tick(double deltaTime) {		
		if(isPlayed) {
			move(deltaTime);
		}
        
	}
	
	private void move(double deltaTime) {
		Vector2 dir = new Vector2();
        
        if(Input.getInstance().isKey(81)) dir.add(-1, 0);
        if(Input.getInstance().isKey(68)) dir.add(1, 0);
        if(Input.getInstance().isKey(90)) dir.add(0, 1);
        if(Input.getInstance().isKey(83)) dir.add(0, -1);
        
        //calcule la nouvelle position
        dir.normalize();
        dir.multiply((float)(speed*deltaTime));
		position.add(dir);
	}
	
}
