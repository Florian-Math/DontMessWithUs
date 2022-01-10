package Objects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Engine.Callback;
import Engine.Game;
import Engine.GameObject;
import Engine.Renderer;
import Engine.Screen;
import Engine.Vector2;
import Engine.collision.Raycast;
import Engine.graphics.ShadowCaster;
import Engine.graphics.SpriteAnimator;
import Engine.graphics.SpriteSheet;
import Math.Mathf;
import Network.Client;
import Network.Sendable;
import Network.data.DroneData;
import Network.data.ObjectData;
import UI.RetryMenu;

/**
 * Objet representant le drone
 */
public class Drone extends GameObject implements Sendable {

	private float speed;
	private Vector2[] pathPoints;
	private float progress = 0;
	private int startIndex;
	private int endIndex;
	private float time = 1000;
	private float distance;
	private InfiltratedPlayer player;
	private int visionAngle = 15;
	
	private SpriteAnimator animator;
	
	BufferedImage zone;
	BufferedImage smallZone;
	
	ArrayList<Vector2> visionPoints = new ArrayList<>();
	ArrayList<Vector2> closeVisionPoints = new ArrayList<>();

	public Drone (Vector2[] positions) {
		super(positions[0]);
		this.pathPoints = positions;
		startIndex = 0;
		endIndex = 1;
		
		distance = Vector2.minus(pathPoints[startIndex], pathPoints[endIndex]).length();
		speed = time/distance;
		
		this.renderingLayer = 1;
		
		if(Game.playerPlayed == PlayerType.observateur) {
			int imsize = (int)(2400/Screen.SIZE*Screen.getCurrent().getHeight());
			zone = new BufferedImage(imsize, imsize, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < imsize; i++) {
				for (int j = 0; j < imsize; j++) {
					zone.setRGB(i, j, 0x00000000);
				}
			}
			
			Vector2 newPos = new Vector2(zone.getWidth()/2, zone.getHeight()/2);
			
			int intRadius = zone.getWidth()/2 - 1;
			
			// https://fr.wikipedia.org/wiki/Algorithme_de_trac%C3%A9_d'arc_de_cercle_de_Bresenham
			int x = 0;
			int y = intRadius;
			int m = 5 - 4 * intRadius;
			int color = 0x60FF0000;
			
			while (x <= y) {
				
				for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
					zone.setRGB(i, y + (int)newPos.y, color);
				}
				
				for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
					zone.setRGB(i, x + (int)newPos.y, color);
				}
				
				for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
					zone.setRGB(i, -y + (int)newPos.y, color);
				}
				
				for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
					zone.setRGB(i, -x + (int)newPos.y, color);
				}
				
				if(m > 0) {
					y--;
					m -= 8*y;
				}
				
				x++;
				m += 8*x + 4;
			}
			
			
			int imsize2 = (int)(300/Screen.SIZE*Screen.getCurrent().getHeight());
			smallZone = new BufferedImage(imsize2, imsize2, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < imsize2; i++) {
				for (int j = 0; j < imsize2; j++) {
					smallZone.setRGB(i, j, 0x00000000);
				}
			}
			
			newPos = new Vector2(smallZone.getWidth()/2, smallZone.getHeight()/2);
			
			intRadius = smallZone.getWidth()/2 - 1;
			
			// https://fr.wikipedia.org/wiki/Algorithme_de_trac%C3%A9_d'arc_de_cercle_de_Bresenham
			x = 0;
			y = intRadius;
			m = 5 - 4 * intRadius;
			color = 0x60FF0000;
			
			while (x <= y) {
				
				for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
					smallZone.setRGB(i, y + (int)newPos.y, color);
				}
				
				for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
					smallZone.setRGB(i, x + (int)newPos.y, color);
				}
				
				for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
					smallZone.setRGB(i, -y + (int)newPos.y, color);
				}
				
				for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
					smallZone.setRGB(i, -x + (int)newPos.y, color);
				}
				
				if(m > 0) {
					y--;
					m -= 8*y;
				}
				
				x++;
				m += 8*x + 4;
			}
		}
		
		
		SpriteSheet sprites = new SpriteSheet("images/droneFlyAnimation.png", 16, 4, 1.5f);
		animator = new SpriteAnimator(sprites, 80);
	}
	float old = 1f;
	@Override
	public void tick(double deltaTime) {
		this.position.set(Vector2.lerp(pathPoints[startIndex], pathPoints[endIndex], progress));
		progress += deltaTime*speed;

		//Arrivée au point voulu
		while (progress >= 1) {
			progress -= 1;
			startIndex++;
			endIndex++;
			if (startIndex >= pathPoints.length) {
				startIndex = 0;
			}
			if (endIndex >= pathPoints.length) {
				endIndex = 0;
			}
			old = speed;
			distance = Vector2.minus(pathPoints[startIndex], pathPoints[endIndex]).length();
			speed = time/distance;
			progress = (progress/old)*speed;
		}
		
		if(player != null && Game.playerPlayed == PlayerType.observateur) {
			ShadowCaster.getInstance().calculateLongDirectionalShadow(position, Vector2.minus(pathPoints[endIndex], pathPoints[startIndex]), visionPoints);
			ShadowCaster.getInstance().calculateCloseShadow(position, Vector2.minus(pathPoints[endIndex], pathPoints[startIndex]), closeVisionPoints);
			
		}
		
		if(player != null && Client.getCurrent().getPlayerType() == PlayerType.infiltre && foundPlayer()) {
			player.lockMovement();
			Screen.getCurrent().fadeToBlack(2000, new Callback() {
				
				@Override
				public void call() {
					Game.getCurrent().levelManager.reset();
					Game.getCurrent().changeMenu(new RetryMenu());
					
					Client.getCurrent().stopGame();
					//System.out.println("Endgame");
				}
			});
			
		}
	}
	
	@Override
	public void fixedTick() {
		animator.tick();
	}

	@Override
	public void render(Renderer r) {
		
		if(Game.playerPlayed == PlayerType.observateur) {
			r.clearStockedLight(0x00000000);
			r.stockPointLight(position, closeVisionPoints.toArray(new Vector2[0]), smallZone);
			r.stockPointLight(position, visionPoints.toArray(new Vector2[0]), zone);
			r.drawLight(0xD0FF0000);
		}
		
		r.drawRotatedSprite(position, animator.getSprite(), Vector2.minus(pathPoints[endIndex], pathPoints[startIndex]).angleRad() - (float)Math.PI/2);
		//animator.drawSprite(position, r, Vector2.minus(pathPoints[endIndex], pathPoints[startIndex]).angleRad() - (float)Math.PI/2);
	}
	
	public boolean foundPlayer() {
		
		Vector2 dirPlayer = Vector2.minus(player.getPosition(), position);
		
		if(dirPlayer.length() < 150 && Raycast.cast(position, Vector2.minus(player.getPosition(), position), dirPlayer.length()) == null){
			return true;
		}

		Vector2 dirDrone = Vector2.minus(pathPoints[endIndex], pathPoints[startIndex]);
		
		float min = dirDrone.angleDeg() - visionAngle;
		float max = dirDrone.angleDeg() + visionAngle;
		
		if(max > 360) max -= 360;
		if(min < 0) min += 360;
		
		if(Mathf.isBetweenAngle(dirPlayer.angleDeg(), min, max)){
			if(dirPlayer.length() < 1200 && Raycast.cast(position, dirPlayer, dirPlayer.length()) == null) {
				return true;
			}
		}
		return false;
	}
	
	public void setTarget(InfiltratedPlayer player) {
		this.player = player;
	}
	
	@Override
	public void updateDataFromServer(ObjectData lastData, ObjectData data, double delta) {
		if(data != null) {
			DroneData d = (DroneData) data;
			
			startIndex = d.startIndex;
			endIndex = d.endIndex;
			progress = d.progress;
		}
	}
	
	@Override
	public ObjectData getData() {
		DroneData d = new DroneData(this);
		
		d.startIndex = startIndex;
		d.endIndex = endIndex;
		d.progress = progress;
		
		return d;
	}
	
}