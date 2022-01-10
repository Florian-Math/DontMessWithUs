package Engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Objects.Cube;
import Objects.Drone;
import Objects.Ground;
import Objects.Wall;

/**
 * Représente un niveau
 */
public class Map {

	public static final int numberOfMaps = 5;
	private static int currentMap = 1;
	
	private BufferedImage image;
	private Vector2[] dirs = new Vector2[] {Vector2.right, Vector2.up, Vector2.left, Vector2.down};
	
	private int[] scores;
	
	/**
	 * Charge un niveau
	 * @param mapName nom du niveau
	 */
	public Map(String mapName) {
		try {
			image = ImageIO.read(new File("images/" + mapName + ".png"));
			
			scores = new int[4];
			for (int i = 0; i < 4; i++) {
				scores[i] = image.getRGB(i, 0)&0xFFFFFF;
				//System.out.println("Score : " + scores[i]);
				image.setRGB(i, 0, 0xFF303030);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	/**
	 * Verifie si un pixel est vide
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmpty(int x, int y) {
		return ((image.getRGB(x, y)>>24)&0xFF) == 0;
	}
	
	/**
	 * Verifie si un pixel est le joueur
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isPlayer(int x, int y) {
		return image.getRGB(x, y) == 0xFF0000FF;
	}
	
	/**
	 * Verifie si un pixel est un drone
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isDrone(int x, int y) {
		return image.getRGB(x, y) == 0xFFFF0000;
	}
	
	/**
	 * Verifie si un pixel est vide
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isVoid(int x, int y) {
		return image.getRGB(x, y) == 0xFF303030;
	}
	
	/**
	 * Verifie si un pixel est le sol
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isGround(int x, int y) {
		return image.getRGB(x, y) == 0x00000000 || isDrone(x, y) || isPlayer(x, y) || image.getRGB(x, y) == 0xFF700000 || image.getRGB(x, y) == 0xFF400000;
	}
	
	/**
	 * Verifie si un pixel est un mur
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isWall(int x, int y) {
		if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return false;
		
		return image.getRGB(x, y) == 0xFF000000;
	}
	
	public boolean isEnd(int x, int y) {
		if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return false;
		
		return image.getRGB(x, y) == 0xFF00FF00;
	}
	
	/**
	 * Permet de charger les murs
	 * @param objects
	 * @param x
	 * @param y
	 */
	public void processWall(ArrayList<GameObject> objects, int x, int y) {
		if(!isWall(x, y)) return;
		
		image.setRGB(x, y, 0x00000001); // suppr pixel
		
		Vector2 start = new Vector2(x*100, -y*100);
		
		Vector2 dir = null;
		// recup direction
		for (Vector2 vector2 : dirs) {
			if(isWall(x + (int)vector2.x, y + (int)vector2.y)) {
				dir = vector2;
				
				x += (int)dir.x;
				y += (int)dir.y;
				image.setRGB(x, y, 0x00000001); // suppr pixel
				
				break;
			}
		}
		
		if(dir == null) {
			objects.add(new Cube(start, 100));
			return; // mur == cube
		}
		
		while (isWall(x + (int)dir.x, y + (int)dir.y)) {
			x += (int)dir.x;
			y += (int)dir.y;
			image.setRGB(x, y, 0x00000001); // suppr pixel
		}
		
		Vector2 end = new Vector2(x*100, -y*100);
		
		objects.add(new Wall(start, end));
	}
	
	/**
	 * Permet de charger les drones
	 * @param objects
	 * @param x
	 * @param y
	 */
	public void processDrone(ArrayList<GameObject> objects, int x, int y) {
		if(image.getRGB(x, y) != 0xFFFF0000) return;
		
		ArrayList<Vector2> dronePoints = new ArrayList<Vector2>();
		dronePoints.add(new Vector2(x*100, -y*100)); // ajout du point de depart
		
		// tant qu'il ne boucle pas ...
		boolean haslooped = false;
		while (!haslooped) {
			
			// recup direction
			Vector2 dir = null;
			for (Vector2 vector2 : dirs) {
				if(image.getRGB(x + (int)vector2.x, y + (int)vector2.y) == 0xFF700000) {
					dir = vector2;
					x += (int)dir.x;
					y += (int)dir.y;
					//image.setRGB(x, y, 0x00000000);
					
					break;
				}
			}
			
			// continue dans la meme dir tant qu'il ne trouve pas le bon pixel
			while (image.getRGB(x + (int)dir.x, y + (int)dir.y) != 0xFF400000 && !isDrone(x + (int)dir.x, y + (int)dir.y)) {
				x += (int)dir.x;
				y += (int)dir.y;
				//System.out.println(x + " " + y + "c:" + String.format("0x%08X", image.getRGB(x, y)));
			}
			//System.out.println(x + " " + y + "c:" + String.format("0x%08X", image.getRGB(x, y)));
			
			x += (int)dir.x;
			y += (int)dir.y;
			
			if(image.getRGB(x, y) == 0xFF400000) {
				// si nouvelle dir continue
				dronePoints.add(new Vector2(x*100, -y*100));
				//this.processGround(objects, x, y);
			}
			else { 														// si point de depart ajout du drone
				//image.setRGB(x, y, 0x00000000);
				Drone d = new Drone(dronePoints.toArray(new Vector2[0]));
				objects.add(d);
				haslooped = true;
			}
		}
	}
	
	public void processGround(ArrayList<GameObject> objects, int x, int y) {
		if(!isGround(x, y)) return;
		
		objects.add(new Ground(new Vector2(x*100, -y*100)));
	}
	
	
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
	
	// --------------- STATIC

	public static int nextMap() {
		currentMap++;
		if(currentMap > numberOfMaps) currentMap = numberOfMaps;
		return currentMap;
	}
	
	public static int previousMap() {
		currentMap--;
		if(currentMap <= 0) currentMap = 1;
		return currentMap;
	}
	
	public static int getCurrentMap() {
		return currentMap;
	}
	
	public static void setCurrentMap(int i) {
		Map.currentMap = i;
	}
	
	private static String[] marks = new String[] {"<a style=\"color: #c23636;\">D</a>", "<a style=\"color: #c1cbcb;\">C</a>", "<a style=\"color: #406fe9;\">B</a>", "<a style=\"color: #58e940;\">A</a>", "<a style=\"color: #e5d92f;\">S</a>"};
	public static String getScore(int time) {
		Map m = new Map("map" + currentMap);
		int score = 0;
		for (int i = 0; i < m.scores.length; i++) {
			if(time <= m.scores[i]) score++;
		}
		
		return marks[score];
	}
	
}
