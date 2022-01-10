package Objects;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Engine.*;
import Engine.collision.CircleCollider;
import Engine.collision.Collider;
import Engine.graphics.ShadowCaster;
import Engine.graphics.SpriteAnimator;
import Engine.graphics.SpriteSheet;
import Network.Client;
import Network.Sendable;
import Network.data.ObjectData;
import Network.data.PlayerData;

/**
 * Objet representant le joueur infiltre
 */
public class InfiltratedPlayer extends GameObject implements Sendable {
	private static final int baseSpeed = 500;
	
	private Collider collider;
	private SpriteAnimator animator;
	
	private int speed = 500;
    private boolean isPlayed;
    private boolean isMoving;
    private float playerRotation;
    
    private boolean lock;
    
    // ----- Joueur non controllé
    
    public PlayerData lastData;
    public PlayerData currentData;
    public double dataDelta = 0;
    public float lerpVal = 0;
    
    // -----
	
	public InfiltratedPlayer(Vector2 position, boolean p) {
		super(position);
		isPlayed = p;
		
		isMoving = false;
		
		collider = new CircleCollider(this, 50, true);
		
		SpriteSheet sprites = new SpriteSheet("images/playerWalkAnimationSpriteSheet.png", 16, 8, 1.5f);
		animator = new SpriteAnimator(sprites, 100);
		lock = false;
    }
	
	@Override
	public void tick(double deltaTime) {
		if(isPlayed) {
			if(lock) {
				isMoving = false;
				return;
			}
			
			move(deltaTime);
			
			playerRotation = this.calculateVisionVector().angleRad();
			ShadowCaster.getInstance().setLightDirection(this.calculateVisionVector());
			
			Client.getCurrent().updateData(this.getData()); // envoie au serveur
		}else if(lastData != null){
			// lerp pos
			position.set(Vector2.lerp(lastData.position, currentData.position, lerpVal));
			playerRotation = Vector2.minus(Vector2.lerp(lastData.visionPosition, currentData.visionPosition, lerpVal), position).angleRad();
			
			ShadowCaster.getInstance().setLightDirection(Vector2.minus(Vector2.lerp(lastData.visionPosition, currentData.visionPosition, lerpVal), position));
			
			lerpVal += deltaTime / dataDelta;
		}
		
	}
	
	@Override
	public void fixedTick() {
		if(isMoving)
			animator.tick();
		else
			animator.resetAnimation();
	}
	
	@Override
	public void updateDataFromServer(ObjectData lastData, ObjectData data, double delta) {
		if(lastData != null && !isPlayed) {
			PlayerData lastd = (PlayerData)lastData;
			PlayerData d = (PlayerData)data;
			
			this.position.set(lastd.position);
			this.dataDelta = delta;
			
			this.currentData = d;
			this.lastData = lastd;
			lerpVal = 0;
			
			if(lastd.position.equals(d.position)) isMoving = false;
	        else isMoving = true;
		}
	}

	@Override
	public void render(Renderer r) {
		//animator.drawSprite(position, r, playerRotation - (float)Math.PI / 2 );
		r.drawRotatedSprite(position, animator.getSprite(), playerRotation - (float)Math.PI / 2);
	}
	
	private void move(double deltaTime) {
		Vector2 dir = new Vector2();
        
        if(Input.getInstance().isKey(81)) dir.add(-1, 0);
        if(Input.getInstance().isKey(68)) dir.add(1, 0);
        if(Input.getInstance().isKey(90)) dir.add(0, 1);
        if(Input.getInstance().isKey(83)) dir.add(0, -1);
        
        if(Input.getInstance().isKey(KeyEvent.VK_SHIFT)) speed = (int)(baseSpeed*1.5f);
        else speed = baseSpeed;
        
        if(dir.equals(Vector2.zero)) isMoving = false;
        else isMoving = true;
        
        //calcule la nouvelle position
        dir.normalize();
        dir.multiply((float)(speed*deltaTime));
		position.add(dir);
        
        //calcul des positions via collision
        collider.collide();
	}
	
	@Override
	public ObjectData getData() {
		PlayerData newData = new PlayerData(this);
		
		newData.position = position;
		newData.visionPosition = Screen.getCurrent().toWorldPoint(Input.getInstance().getMousePosition());
		
		return newData;
	}
	
	public void lockMovement() {
		this.lock = true;
	}
	
	
	public Vector2 calculateVisionVector() {
		return Vector2.minus(Screen.getCurrent().toWorldPoint(Input.getInstance().getMousePosition()), position);
	}
	
	
	public void setPosition(Vector2 position) {
		this.position.set(position);
	}
}
