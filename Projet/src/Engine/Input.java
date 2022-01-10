package Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Classe permettant la gestion des inputs
 * @author mathi
 *
 */
public class Input implements KeyListener, MouseMotionListener {

	private static Input instance;
	
	private final int NUM_KEYS = 256;
	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];
	private Vector2 mousePosition;
	
	
	private Input() {
		this.mousePosition = new Vector2();
	}
	
	public void tick() {
		for (int i = 0; i < NUM_KEYS; i++) {
			keysLast[i] = keys[i];
		}
	}
	
	/**
	 * Retourne true si la touche est appuyé
	 * @param keyCode touche appuyé
	 * @return
	 */
	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}
	
	/**
	 * Retourne true la frame où la touche est relaché
	 * @param keyCode touche appuyé
	 * @return
	 */
	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && keysLast[keyCode];
	}
	
	/**
	 * Retourne true la frame où la touche est appuyé
	 * @param keyCode touche appuyé
	 * @return
	 */
	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	
	public static Input getInstance() {
		if(instance == null) instance = new Input();
		
		return instance;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		this.mousePosition.set(arg0.getX(), arg0.getY());
		
	}
	
	public Vector2 getMousePosition() {
		return this.mousePosition;
	}
	

}
