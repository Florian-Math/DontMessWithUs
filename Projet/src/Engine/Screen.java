package Engine;

import Math.Mathf;
import Objects.InfiltratedPlayer;

/**
 * Gere la taille de l'écran et le déplacement de la camera
 * @author mathi
 *
 */
public class Screen extends GameObject{
	public static final float SIZE = 2000f;
	public static final int GRID_SIZE = 100;
	
	private static Screen current;
	
	private int width;
	private int height;
	
	private float coef;
	
	// ---- fade
	
	private int fadeOpacity = 0;
	private int baseFadeOpacity = 0;
	private int targetFadeOpacity = 0;
	private float lerpVal = 0;
	private float lerpSpeed;
	
	private Callback callback;
	
	// ---- camera movement
	private GameObject target;
	
	private Screen() {
		super();
		this.renderingLayer = 10;
	}
	
	@Override
	public void tick(double deltaTime) {
		if(target != null) { // suivie camera
			position.set(Vector2.lerp(position, target.getPosition(), (float)(deltaTime * 10)));
		}
		
		if(lerpVal < 1) {
			lerpVal += lerpSpeed * deltaTime;
			fadeOpacity = (int)Mathf.lerp((float)baseFadeOpacity, (float)targetFadeOpacity, lerpVal);
		}else if(callback != null) {
			callback.call();
			callback = null;
		}
	}
	
	@Override
	public void render(Renderer r) {
		if(fadeOpacity > 0)
			r.fillAll(fadeOpacity * 0x01000000);
	}
	
	/**
	 * Assombri l'écran au cours du temps
	 * @param time temps de l'animation (en ms)
	 */
	public void fadeToBlack(float time, Callback callback) {
		if(this.callback != null || fadeOpacity >= 255) return;
		this.callback = callback;
		lerpVal = 0;
		baseFadeOpacity = 0;
		targetFadeOpacity = 255;
		
		lerpSpeed = 1000 / time;
	}
	
	/**
	 * Affiche l'écran au cours du temps
	 * @param time temps de l'animation (en ms)
	 */
	public void fadeToScene(float time) {
		lerpVal = 0;
		baseFadeOpacity = 255;
		targetFadeOpacity = 0;
		
		lerpSpeed = 1000 / time;
	}
	
	/**
	 * Change l'écran en noir
	 */
	public void setToBlack() {
		fadeOpacity = 255;
	}
	
	public int getWidth() {return width; }
	public int getHeight() {return height; }
	
	/**
	 * Mettre l'objet que la camera va suivre
	 * @param obj
	 */
	public void setTarget(GameObject obj) {this.target = obj; }

	/**
	 * Converti les coordonnées d'un point du monde en point dans la fenetre
	 * @param point
	 * @return
	 */
	public Vector2 toScreenPoint(Vector2 point) {
		return new Vector2(((point.x - position.x)*coef) + width/2, (-1*(point.y - position.y)*coef) + height/2);
	}
	
	/**
	 * Converti les coordonnées d'un point dans la fenetre en point du monde
	 * @param point
	 * @return
	 */
	public Vector2 toWorldPoint(Vector2 point) {
		return new Vector2(((point.x - width/2)/height*Screen.SIZE) + position.x, (-1*(point.y - height/2)*Screen.SIZE/height) + position.y);
	}
	
	public static Screen getCurrent() {
		if(current == null) current = new Screen();
		return current;
	}
	
	public void setDimensions(int width) {
		this.width = width;
		this.height = width/ 16 * 9;
		
		coef = 1/Screen.SIZE*height;
	}
}
