package Engine.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Engine.Screen;
import Engine.Vector2;

/**
 * Represente une image carré
 */
public class Sprite {

	private int[] image;
	
	private int size;
	
	/**
	 * Construit un sprite à partir d'une plus grande image 
	 * @param image image de base
	 * @param spritePixel taille en pixel de la nouvelle image
	 * @param offset offset par rapport de l'image de base
	 */
	public Sprite(BufferedImage image, int spritePixel, Vector2 offset, float scale) {
		/*
		this.image = new int[spritePixel*spritePixel];
		this.size = spritePixel;
		
		for (int y = 0; y < spritePixel; y++) {
			for (int x = 0; x < spritePixel; x++) {
				this.image[x + y*size] = image.getRGB(x + (int)offset.x, y + (int)offset.y);
			}
		}*/
		
		float pixelSize = (Screen.GRID_SIZE / Screen.SIZE * Screen.getCurrent().getHeight())/(spritePixel/scale);
		this.size = Math.round(spritePixel*pixelSize);
		this.image = new int[this.size*this.size];
		
		
		for (int y = 0; y < this.size; y++) {
			for (int x = 0; x < this.size; x++) {
				this.image[x + y*size] = image.getRGB((int)(x/pixelSize) + (int)offset.x, (int)(y/pixelSize) + (int)offset.y);
			}
		}
		
	}
	
	public Sprite(String imagePath) {
		BufferedImage im = null;
		try {
			im = ImageIO.read(new File(imagePath));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		float pixelSize = (Screen.GRID_SIZE / Screen.SIZE * Screen.getCurrent().getHeight())/im.getWidth();
		this.size = Math.round(im.getWidth()*pixelSize);
		this.image = new int[this.size*this.size];
		
		for (int y = 0; y < im.getWidth()*pixelSize; y++) {
			for (int x = 0; x < im.getWidth()*pixelSize; x++) {
				this.image[x + y*size] = im.getRGB((int)(x/pixelSize), (int)(y/pixelSize));
			}
		}
	}
	
	/**
	 * Retourne la taille en pixel de l'image
	 * @return
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Retourne la couleur du pixel
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixel(int x, int y) {
		return image[x + y*size];
	}
	
}
