package Engine;

import java.util.Comparator;

import Network.Sendable;

/**
 * Classe abstraite d'un objet du jeu
 */
public abstract class GameObject {
	private static long ID = 0;
	private static long sendableID = -1;
	private long id;
	
	protected Vector2 position;
	protected int renderingLayer;
	
	protected GameObject() {
		this.position = new Vector2();
		
		if(this instanceof Sendable) {
			this.id = sendableID;
			sendableID--;
		}else {
			this.id = ID;
			ID++;
		}
	}
	
	protected GameObject(Vector2 position) {
		this.position = new Vector2(position);
		renderingLayer = 0;
		
		if(this instanceof Sendable) {
			this.id = sendableID;
			sendableID--;
		}else {
			this.id = ID;
			ID++;
		}
	}
	
	/**
	 * Methode se lancant le plus vite possible
	 */
	public void tick(double deltaTime) {};
	
	/**
	 * Methode se lancant à chaque tick 60 ticks par seconde
	 */
	public void fixedTick() {};
	
	/**
	 * Methode permettant l'affichage de l'objet
	 * @param r renderer
	 */
	public void render(Renderer r) {};
	
	public Vector2 getPosition() {return position; }
	
	public int getRenderingLayer() {return renderingLayer; }

	public void setPosition(Vector2 pos) {
		position.set(pos.x, pos.y);
	}
	
	public long getID() {
		return this.id;
	}
	
	public static void reset() {
		ID = 0;
		sendableID = -1;
	}
	
	public static class RenderingSort implements Comparator<GameObject> {

		@Override
		public int compare(GameObject o1, GameObject o2) {
			if(o1.getRenderingLayer() > o2.getRenderingLayer()) return 1;
			if(o1.getRenderingLayer() < o2.getRenderingLayer()) return -1;
			return 0;
		}
		
	}
	
}
