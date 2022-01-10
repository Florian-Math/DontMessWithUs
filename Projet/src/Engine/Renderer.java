package Engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Engine.graphics.Sprite;

/**
 * Classe permettant de gérer l'affichage
 * @author mathi
 *
 */
public class Renderer {

	private Screen s;
	
	private int[] p;
	
	public Renderer(BufferedImage image) {
		this.s = Screen.getCurrent();
		this.p = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		im = new int[p.length];
		clear();
	}
	
	/**
	 * Clear l'affichage
	 */
	public void clear() {
		for (int i = 0; i < p.length; i++) {
			p[i] = 0xFF000000;
		}
	}
	
	/**
	 * Change le pixel d'une image a une valeur
	 * @param im
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 * @param value
	 */
	private void setPixel(int[] im, int width, int height, int x, int y, int value) {
		if(x < 0 || y < 0 || x >= width || y >= height) return;
		
		im[x + y*width] = value;
	}
	
	/**
	 * Retourne la couleur du pixel
	 * @param x
	 * @param y
	 * @return
	 */
	private int getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= s.getWidth() || y >= s.getHeight()) return 0x00000000;
		
		return p[x + y*s.getWidth()];
	}
	
	/**
	 * Change le pixel de l'affichage a une certaine couleur donnée
	 * @param x
	 * @param y
	 * @param value
	 */
	private void setPixel(int x, int y, int value) {
		if(x < 0 || y < 0 || x >= s.getWidth() || y >= s.getHeight()) return;
		if(((value>>24)&0xFF) == 0) return;
		
		p[x + y*s.getWidth()] = value;
	}
	/*
	private void setBlendedPixel(int x, int y, int value) {
		if(x < 0 || y < 0 || x >= s.getWidth() || y >= s.getHeight()) return;
		
		p[x + y*s.getWidth()] = blendColor(p[x + y*s.getWidth()], value);
	}*/
	
	private int blendColor(int c1, int c2) {
		int va = (c2>>24)&0xFF;
		
		if(va == 255) return c2;
		else if(va == 0) return c1;
		
		int cr = (((c2>>16)&0xFF) * va) + (((c1>>16)&0xFF) * (255 - va))>>8;
		int cg = (((c2>>8)&0xFF) * va) + (((c1>>8)&0xFF) * (255 - va))>>8;
		int cb = (((c2)&0xFF) * va) + (((c1)&0xFF) * (255 - va))>>8;
		
		int blendedColor = 65536 * cr + 256 * cg + cb;
		
		return blendedColor;
	}
	
	/**
	 * Retourne le mélange des deux couleurs par transparence (c2 n'acceptant que du noir)
	 * @param c1
	 * @param c2
	 * @return
	 */
	private int multiplyBlendColor(int c1, int c2) {
		if(((c2>>24)&0xFF) == 255) return 0xFF000000;
		else if(((c2>>24)&0xFF) == 0) return c1;
		
		float d = (1 - ((c2>>24)&0xFF)/255.0f);
		
		return 65536 * (int)(((c1>>16)&0xFF)*d) + 256 * (int)(((c1>>8)&0xFF)*d) + (int)((c1&0xFF)*d);
	}

	/**
	 * Affiche un rectangle
	 * @param pos
	 * @param width
	 * @param height
	 * @param c
	 */
	public void fillRect(Vector2 pos, float width, float height, Color c) {
		Vector2 newPos = s.toScreenPoint(new Vector2(pos));
		
		float intWidth = width/Screen.SIZE*s.getHeight();
		float intHeight = height/Screen.SIZE*s.getHeight();
		
		newPos.minus(intWidth/2, intHeight/2);
		
		if(newPos.x > s.getWidth() || newPos.y > s.getHeight()) return;
		if(newPos.x + intWidth < 0 || newPos.y + intHeight < 0) return;
		
		int maxX, maxY;
		
		if(newPos.x + intWidth > s.getWidth()) maxX = s.getWidth();
		else maxX = Math.round(newPos.x + intWidth);
		if(newPos.y + intHeight > s.getHeight()) maxY = s.getHeight();
		else maxY = Math.round(newPos.y + intHeight);
		if(newPos.x < 0) newPos.x = 0;
		if(newPos.y < 0) newPos.y = 0;
		
		int rgb = c.getRGB();
		
		for (int i = (int)newPos.x; i < maxX; i++) {
			for (int j = (int)newPos.y; j < maxY; j++) {
				setPixel(i, j, rgb);
			}
		}
	}
	
	/**
	 * Affiche un cercle
	 * @param pos
	 * @param radius
	 * @param c
	 */
	public void fillCircle(Vector2 pos, float radius, Color c) {
		Vector2 newPos = s.toScreenPoint(pos);
		
		int intRadius = (int)(radius/Screen.SIZE*s.getHeight());
		
		// https://fr.wikipedia.org/wiki/Algorithme_de_trac%C3%A9_d'arc_de_cercle_de_Bresenham
		int x = 0;
		int y = intRadius;
		int m = 5 - 4 * intRadius;
		int color = c.getRGB();
		
		while (x <= y) {
			
			for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
				setPixel(i, y + (int)newPos.y, color);
			}
			
			for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
				setPixel(i, x + (int)newPos.y, color);
			}
			
			for (int i = -x + (int)newPos.x; i <= x + (int)newPos.x; i++) {
				setPixel(i, -y + (int)newPos.y, color);
			}
			
			for (int i = -y + (int)newPos.x; i <= y + (int)newPos.x; i++) {
				setPixel(i, -x + (int)newPos.y, color);
			}
			
			if(m > 0) {
				y--;
				m -= 8*y;
			}
			
			x++;
			m += 8*x + 4;
		}
	}
	
	// utilisé pour inversé une image (crée du lag si déclarée dans une methode)
	private int[] im;
	
	/**
	 * Clear l'affichage des lumières
	 */
	public void clearStockedLight() {
		for (int i = 0; i < im.length; i++) {
			im[i] = 0xFF000000;
		}
	}
	
	public void clearStockedLight(int color) {
		for (int i = 0; i < im.length; i++) {
			im[i] = color;
		}
	}
	
	/**
	 * Stock l'affichage d'une lumière
	 * @param position
	 * @param points
	 * @param image
	 */
	public void stockPointLight(Vector2 position, Vector2[] points, BufferedImage image) {
		if(points.length == 0) return;
		
		Vector2 newCentre = s.toScreenPoint(position);
		newCentre.minus(image.getWidth()/2, image.getHeight()/2);
		
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		
		Vector2 newPos = null;
		for (int i = 0; i < points.length; i++) {
			newPos = s.toScreenPoint(points[i]);
			xPoints[i] = (int)newPos.x;
			yPoints[i] = (int)newPos.y;
		}
		
		// --------------------------------------------
		
		//sources : https://alienryderflex.com/polygon_fill/
		
		// calcul Max Y
		int maxY = yPoints[0];
		for (int i = 1; i < yPoints.length; i++) {
			if(yPoints[i] > maxY) maxY = yPoints[i];
		}
		
		// calcul Min Y
		int minY = yPoints[0];
		for (int i = 1; i < yPoints.length; i++) {
			if(yPoints[i] < minY) minY = yPoints[i];
		}
		
		int nodes, j;
		int[] nodeX = new int[points.length];
		
		// parcours des lignes
		for (int pY = minY; pY < maxY; pY++) {
			nodes = 0; j = points.length - 1;
			
			for (int i = 0; i < points.length; i++) {
				if(yPoints[i] < pY && yPoints[j] >= pY || yPoints[j] < pY && yPoints[i] >= pY) {
					nodeX[nodes] = (int)((xPoints[i] + (double)(pY - yPoints[i]) / (yPoints[j] - yPoints[i])*(xPoints[j] - xPoints[i])));
					nodes ++;
				}
				
				j = i;
			}
			
			// sort
			int i = 0;
			int tmp = 0;
			while (i < nodes-1) {
				
				if(nodeX[i] > nodeX[i+1]) {
					tmp = nodeX[i];
					nodeX[i] = nodeX[i+1];
					nodeX[i+1] = tmp;
					if(i != 0) i --;
				}else {
					i++;
				}
			}
			
			int finalX = 0;
			int finalY = 0;
			
			//draw
			for (i = 0; i < nodes; i+=2) {
				for (int pX = nodeX[i]; pX < nodeX[i+1]; pX++) {
					finalX = pX - (int)newCentre.x;
					finalY = pY - (int)newCentre.y;
					
					if(finalX >= 0 && finalX < image.getWidth() && finalY >= 0 && finalY < image.getHeight())
						setPixel(im, s.getWidth(), s.getHeight(), pX, pY, image.getRGB(finalX, finalY));
				}
			}

			
		}

	}
	
	/**
	 * Affiche les lumières
	 */
	public void drawLight() {
		for (int i = 0; i < im.length; i++) {
			p[i] = multiplyBlendColor(p[i],  im[i]);
		}
	}
	
	public void drawLight(int color) {
		for (int i = 0; i < im.length; i++) {
			if(im[i] == 0x00000000) continue;
			p[i] = blendColor(p[i],  im[i]);
			//p[i] = multiplyBlendColor(p[i],  im[i]);
		}
	}
	
	
	/**
	 * Affiche un sprite avec une rotation donnée
	 * @param position
	 * @param sprite
	 * @param pixelSize
	 * @param angle
	 */
	public void drawRotatedSprite(Vector2 position, Sprite sprite, int pixelSize, float angle) {
		Vector2 newPos = s.toScreenPoint(position);
		newPos.minus((sprite.getSize()*pixelSize)/2, (sprite.getSize()*pixelSize)/2);
		
		float center = (sprite.getSize()*pixelSize)/2;
		
		int newX, newY;
		int xp, yp;
		
		// rotation
		//https://www.codingame.com/playgrounds/2524/basic-image-manipulation/transformation
		//xp = (x - center_x) * cos(angle) - (y - center_y) * sin(angle) + center_x
		//yp = (x - center_x) * sin(angle) + (y - center_y) * cos(angle) + center_y
		for (int y = 0; y < sprite.getSize()*pixelSize; y++) {
			for (int x = 0; x < sprite.getSize()*pixelSize; x++) {
				newX = (int)newPos.x + x;
				newY = (int)newPos.y + y;
				
				xp = (int) Math.round((x - center) * Math.cos(angle) - (y - center) * Math.sin(angle) + center);
				yp = (int) Math.round((x - center) * Math.sin(angle) + (y - center) * Math.cos(angle) + center);
				
				if(0 <= xp && xp < sprite.getSize()*pixelSize && 0 <= yp && yp < sprite.getSize()*pixelSize)
					setPixel(newX, newY, sprite.getPixel(xp/pixelSize, yp/pixelSize));
				
			}
		}
	}
	
	public void drawRotatedSprite(Vector2 position, Sprite sprite, float angle) {
		Vector2 newPos = s.toScreenPoint(position);
		newPos.minus(sprite.getSize()/2, sprite.getSize()/2);
		
		if(newPos.x > s.getWidth() || newPos.y > s.getHeight()) return;
		if(newPos.x + sprite.getSize() < 0 || newPos.y + sprite.getSize() < 0) return;
		
		float center = sprite.getSize()/2;
		
		int newX, newY;
		int xp, yp;
		
		// rotation
		//https://www.codingame.com/playgrounds/2524/basic-image-manipulation/transformation
		//xp = (x - center_x) * cos(angle) - (y - center_y) * sin(angle) + center_x
		//yp = (x - center_x) * sin(angle) + (y - center_y) * cos(angle) + center_y
		for (int y = 0; y < sprite.getSize(); y++) {
			for (int x = 0; x < sprite.getSize(); x++) {
				newX = (int)newPos.x + x;
				newY = (int)newPos.y + y;
				
				xp = (int) Math.round((x - center) * Math.cos(angle) - (y - center) * Math.sin(angle) + center);
				yp = (int) Math.round((x - center) * Math.sin(angle) + (y - center) * Math.cos(angle) + center);
				
				if(0 <= xp && xp < sprite.getSize() && 0 <= yp && yp < sprite.getSize())
					setPixel(newX, newY, sprite.getPixel(xp, yp));
				
			}
		}
	}
	
	
	/**
	 * Affiche un sprite
	 * @param position
	 * @param sprite
	 * @param pixelNumber
	 * @param angle
	 */
	public void drawSprite(Vector2 position, Sprite sprite) {
		Vector2 newPos = s.toScreenPoint(position);
		newPos.minus(sprite.getSize()/2, sprite.getSize()/2);
		
		if(newPos.x > s.getWidth() || newPos.y > s.getHeight()) return;
		if(newPos.x + sprite.getSize() < 0 || newPos.y + sprite.getSize() < 0) return;
		
		for (int y = 0; y < sprite.getSize(); y++) {
			for (int x = 0; x < sprite.getSize(); x++) {
				setPixel((int)newPos.x + x, (int)newPos.y + y, sprite.getPixel(x, y));
			}
		}
	}
	
	
	/**
	 * Remplit tout l'écran d'une couleur
	 * @param color
	 */
	public void fillAll(int color) {
		for (int i = 0; i < p.length; i++) {
			p[i] = multiplyBlendColor(p[i], color);
		}
	}

	
	
	
}
