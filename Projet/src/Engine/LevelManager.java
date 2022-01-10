package Engine;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Engine.GameObject.RenderingSort;
import Engine.collision.Collider;
import Engine.graphics.ShadowCaster;
import Objects.Drone;
import Objects.Elevator;
import Objects.InfiltratedPlayer;
import Objects.ObservingPlayer;
import Objects.PlayerType;
import UI.LobbyMenu;
import UI.RetryMenu;
import Network.Client;
import Network.Sendable;
import Network.WorldData;

public class LevelManager {

	private Screen screen;
	private ArrayList<GameObject> objects;
	private ArrayList<GameObject> renderingOrderObjects;
	
	private ShadowCaster shadowCaster;
	
	private PlayerType playerPlayed;
	/*
	WorldData lastWorld;
	WorldData world;*/
	
	InfiltratedPlayer inf = null;
	ObservingPlayer obs = null;
	
	boolean loaded;
	
	//DataUpdater dataupdate;
	
	public LevelManager() {
		this.screen = Screen.getCurrent();
		shadowCaster = ShadowCaster.getInstance();
		
		loaded = false;
	}
	
	public void setPlayedPlayer(PlayerType type) {
		this.playerPlayed = type;
	}
	
	/**
	 * Charge un niveau
	 * @param level nom du niveau
	 */
	public void loadLevel(String level) {
		WorldData.reset();
		objects = new ArrayList<GameObject>();
		renderingOrderObjects = new ArrayList<GameObject>();
		
		this.screen.setToBlack();
		objects.add(this.screen);
		
		Map map = new Map(level);
		
		Elevator end = null;
		
		// parcours
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				
				// skip si transparent
				if(map.isVoid(x, y)) continue;
				
				if(map.isWall(x, y)) {
					map.processWall(objects, x, y);
					continue;
				}
					
				if(map.isDrone(x, y)) {
					map.processDrone(objects, x, y);
					map.processGround(objects, x, y);
					continue;
				}
				
				if(map.isGround(x, y)) map.processGround(objects, x, y);
				
				
				if(map.isEnd(x, y)) {
					end = new Elevator(new Vector2(x*100, -y*100));
					continue;
				}
				
				if(map.isPlayer(x, y)) {
					switch (playerPlayed) {
					case infiltre:
						inf = new InfiltratedPlayer(new Vector2(x*100, -y*100), true); // j1
						obs = new ObservingPlayer(new Vector2(x*100, -y*100), false);
						
						shadowCaster.setLightSource(inf.getPosition());
						
						screen.setTarget(inf); // ajout du joueur sur la camera
						screen.setPosition(inf.getPosition());
						break;

					case observateur:
						inf = new InfiltratedPlayer(new Vector2(x*100, -y*100), false); // j2
						obs = new ObservingPlayer(new Vector2(x*100, -y*100), true);
						
						//shadowCaster.setLightSource(inf.getPosition());
						
						screen.setTarget(obs); // ajout du joueur sur la camera
						screen.setPosition(obs.getPosition());
						break;
					}
					
					map.processGround(objects, x, y);
					continue;
				}
				
				
			}
		}
		
		// temp ?
		for (GameObject o : objects) {
			if(o instanceof Drone) ((Drone)o).setTarget(inf);
		}
		
		end.setPlayer(inf);
		
		objects.add(inf);
		objects.add(obs);
		objects.add(end);
		
		// trie pour les rendering layer
		renderingOrderObjects.addAll(objects);
		renderingOrderObjects.sort(new RenderingSort());
		
		System.out.println("Game Loaded");
		screen.fadeToScene(2000);
		loaded = true;
		TimeSaver.startTimer();
	}
	
	public void tick(double deltaTime) {
		try {
			for (GameObject gameObject : objects) {
				gameObject.tick(deltaTime);
			}
		}catch (ConcurrentModificationException e) {}
		
		
		shadowCaster.calculateShadows();
		Client.getCurrent().sendWorldData();
	}
	
	public void fixedTick() {
		try {
			for (GameObject gameObject : objects) {
				gameObject.fixedTick();
			}
		}catch (ConcurrentModificationException e) {}
		
		
		if(WorldData.getCurrent() != null && Client.getCurrent().getPlayerType() == PlayerType.observateur && !WorldData.getCurrent().isLaunched) {
			// load level Menu
			Screen.getCurrent().fadeToBlack(2000, new Callback() {
				
				@Override
				public void call() {
					Game.getCurrent().levelManager.reset();
					Game.getCurrent().changeMenu(new LobbyMenu());
				}
			});
		}
	}
	
	public void render(Renderer r) {
		try {
			for (GameObject gameObject : renderingOrderObjects) {
				gameObject.render(r);
			}
		}catch (ConcurrentModificationException e) {}
		
		if(playerPlayed == PlayerType.infiltre) shadowCaster.render(r);
	}
	
	/**
	 * Met a jour les données du jeu par rapport à ce que le serveur a envoyé
	 */
	public void updateDataFromServer() {
		try {
		if(WorldData.getLast() != null && WorldData.getCurrent().isLaunched && loaded) {
			//System.out.println(WorldData.getCurrent().objects);
			for (GameObject gameObject : objects) {
				if(gameObject.getID() < 0)
					((Sendable)gameObject).updateDataFromServer(WorldData.getLast().getObjectData(gameObject), WorldData.getCurrent().getObjectData(gameObject), (WorldData.getCurrent().lastUpdateTime - WorldData.getLast().lastUpdateTime) / 1000000000D);
			}
		}
		}catch (ConcurrentModificationException e) {}
	}
	
	public void reset() {
		objects.clear();
		GameObject.reset();
		Collider.resetCollider();
		Game.getCurrent().stop();
	}
	
}
