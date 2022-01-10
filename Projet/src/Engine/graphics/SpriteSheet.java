package Engine.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Engine.Vector2;

/**
 * Représente une feuille de sprite
 */
public class SpriteSheet {

	private Sprite[] sprites;
	private int spriteNumber;
	
	
	/**
	 * Construit une feuille de sprite à partir d'une image
	 * @param image
	 * @param spritePixel taille d'une image en pixel
	 * @param spriteNumber nombre de sprite
	 */
	public SpriteSheet(String imageFile, int spritePixel, int spriteNumber, float scale) {
		
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imageFile));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		sprites = new Sprite[spriteNumber];
		
		Vector2 offset = new Vector2();
		for (int i = 0; i < spriteNumber; i++) {
			
			sprites[i] = new Sprite(image, spritePixel, offset, scale);
			
			if(offset.x + spritePixel >= image.getWidth()) {
				offset.y += spritePixel;
				offset.x = 0;
			}
			else offset.x += spritePixel;
		}
		
		this.spriteNumber = spriteNumber;
	}
	
	public Sprite getSprite(int i) {
		return sprites[i];
	}
	
	public int getSpriteNumber() {
		return this.spriteNumber;
	}
	
}
