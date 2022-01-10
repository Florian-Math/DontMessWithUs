package Engine.graphics;

import Engine.Callback;
import Engine.Renderer;
import Engine.Vector2;

/**
 * Permet de gérer l'animation d'un sprite
 */
public class SpriteAnimator {

	private SpriteSheet sprites;
	private int frameTime;
	
	private int currentFrame;
	
	private long last;
	
	/**
	 * Crée un animateur
	 * @param sprites feuille de sprite
	 * @param frameTime temps pour passer à un autre sprite (en ms)
	 */
	public SpriteAnimator(SpriteSheet sprites, int frameTime) {
		this.sprites = sprites;
		this.frameTime = frameTime;
		
		last = System.currentTimeMillis();
	}
	
	Callback callback;
	
	/**
	 * Tick pour l'affichage des sprites
	 */
	public void tick() {
		if(System.currentTimeMillis() - last > frameTime) {
			last = System.currentTimeMillis();
			
			if(callback == null) this.next();
			else callback.call();
			
		}
	}
	
	public void next() {
		if(currentFrame + 1 >= this.getFrameNumber()) currentFrame = 0;
		else currentFrame++;
	}
	
	public void previous() {
		if(currentFrame - 1 < 0) currentFrame = getFrameNumber();
		else currentFrame--;
	}
	
	public Sprite getSprite(int i) {
		return sprites.getSprite(i);
	}
	
	public Sprite getSprite() {
		return sprites.getSprite(currentFrame);
	}
	
	public int getFrameNumber() {
		return sprites.getSpriteNumber();
	}
	
	public int getCurrentSpriteNumber() {
		return currentFrame;
	}
	
	public void setNextFunction(Callback call) {
		this.callback = call;
	}
	
	/**
	 * Reinitialise les animations
	 */
	public void resetAnimation() {
		currentFrame = 0;
	}
	
	/**
	 * Affiche le sprite
	 * @param position
	 * @param r
	 * @param angle
	 */
	public void drawSprite(Vector2 position, Renderer r, float angle) {
		r.drawRotatedSprite(position, this.getSprite(), angle);
	}

	public void drawSprite(Vector2 position, Renderer r) {
		r.drawSprite(position, this.getSprite());
	}
	
}
