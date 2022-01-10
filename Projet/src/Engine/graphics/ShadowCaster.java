package Engine.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import Engine.Renderer;
import Engine.Vector2;
import Engine.collision.Collider;
import Engine.collision.Raycast;
import Math.Mathf;

/**
 * Classe permettant de calculer et afficher les ombres dans le jeu 
 */
public class ShadowCaster {
	private static ShadowCaster instance;
	
	private Vector2 lightSource;
	private Vector2 lightDirection;
	
	private ArrayList<Vector2> castEndPoints;
	private ArrayList<Vector2> castCloseEndPoints;
	
	// Vecteur delimitant le terrain des ombres à calculer
	private Vector2[] borderRays;
	private Vector2[] closeBorderRays;
	
	private int castDistance = 4000;
	private int closeCastDistance = 2000;
	
	// Images
	private BufferedImage lightImage;
	private BufferedImage lightImage2;
	
	private ShadowCaster() {
		castEndPoints = new ArrayList<Vector2>();
		castCloseEndPoints = new ArrayList<Vector2>();
		
		borderRays = new Vector2[4];
		borderRays[0] = new Vector2(castDistance, castDistance);
		borderRays[1] = new Vector2(castDistance, -castDistance);
		borderRays[2] = new Vector2(-castDistance, castDistance);
		borderRays[3] = new Vector2(-castDistance, -castDistance);
		
		closeBorderRays = new Vector2[4];
		closeBorderRays[0] = new Vector2(closeCastDistance, closeCastDistance);
		closeBorderRays[1] = new Vector2(closeCastDistance, -closeCastDistance);
		closeBorderRays[2] = new Vector2(-closeCastDistance, closeCastDistance);
		closeBorderRays[3] = new Vector2(-closeCastDistance, -closeCastDistance);
		
		try {
			lightImage = ImageIO.read(new File("images/Light.png"));
			
			// inverse l'image
			for (int y = 0; y < lightImage.getHeight(); y++) {
				for (int x = 0; x < lightImage.getWidth(); x++) {
					
					lightImage.setRGB(x, y, 0xFFFFFFFF - lightImage.getRGB(x, y));
				}
			}
			
			lightImage2 = ImageIO.read(new File("images/Light2.png"));
			
			// inverse l'image
			for (int y = 0; y < lightImage2.getHeight(); y++) {
				for (int x = 0; x < lightImage2.getWidth(); x++) {
					
					lightImage2.setRGB(x, y, 0xFFFFFFFF - lightImage2.getRGB(x, y));
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	/**
	 * Renseigne la source de la lumière
	 * @param lightSource
	 */
	public void setLightSource(Vector2 lightSource) {
		this.lightSource = lightSource;
	}
	
	/**
	 * Renseigne la direction de la lumière
	 * @param lightSource
	 */
	public void setLightDirection(Vector2 dir) {
		this.lightDirection = dir;
		this.lightDirection.normalize();
	}
	
	/**
	 * Calcule les ombres
	 */
	public void calculateShadows() {
		if(lightSource == null) return;
		
		calculateLongDirectionalShadow(lightSource, lightDirection, castEndPoints);
		calculateCloseShadow(lightSource, lightDirection, castCloseEndPoints);
	}
	
	public void calculateLongDirectionalShadow(Vector2 source, Vector2 lightDir, ArrayList<Vector2> res) {
		res.clear();
		lightDir.normalize();
		
		Collider[] colliders = Collider.getColliders();
		
		float angle2 = lightDir.angleDeg();
		float angleG  = angle2+20;
		float angleD = angle2-20;
		
		if(angleG > 360) {
			angleG -= 360;
		}
		if(angleD < 0) {
			angleD += 360;
		}

		// cast borders ray
		for (Vector2 ray : borderRays) {
			if(Raycast.cast(source, ray, castDistance) == null && Mathf.isBetweenAngle(ray.angleDeg(), angleD, angleG)) res.add(Vector2.plus(source, ray));
		}
		
		// cast lightdir borders
		Vector2 rightRay = new Vector2(lightDir);
		Vector2 leftRay = new Vector2(lightDir);
		rightRay.rotateDeg(-20);
		leftRay.rotateDeg(20);
		
		rightRay.multiply(castDistance);
		leftRay.multiply(castDistance);
		
		Vector2 p1 = Raycast.cast(source, rightRay, castDistance);
		Vector2 p2 = Raycast.cast(source, leftRay, castDistance);
		
		
		if(p1 == null) res.add(new Vector2(Vector2.plus(source, rightRay)));
		else res.add(new Vector2(p1));
		
		if(p2== null) res.add(new Vector2(Vector2.plus(source, leftRay)));
		else res.add(new Vector2(p2));
		
		// cast for every colliders
		for (Collider c : colliders) {
			switch (c.getType()) {
			case box:
				Vector2[] bounds = c.getBounds();

				for (Vector2 bound : bounds) {
					
					Vector2 dir = Vector2.minus(bound, source);
					dir.normalize();

					
					if(Mathf.isBetweenAngle(dir.angleDeg(), angleD, angleG)) {
						
						float angle = 0.0001f;
						
						Vector2 v1 = Raycast.cast(source, dir, castDistance);
						Vector2 v2 = Raycast.cast(source, dir.rotateRad(angle), castDistance);
						Vector2 v3 = Raycast.cast(source, dir.rotateRad(-angle*2), castDistance);
						
						dir.rotateRad(angle);
						
						if(v1 != null) res.add(v1);
						else res.add(Vector2.plus(source, Vector2.multiply(dir, castDistance)));
						dir.rotateRad(angle);
						if(v2 != null) res.add(v2);
						else res.add(Vector2.plus(source, Vector2.multiply(dir, castDistance)));
						dir.rotateRad(-angle*2);
						if(v3 != null) res.add(v3);
						else res.add(Vector2.plus(source, Vector2.multiply(dir, castDistance)));
						
					}
					
				}

				break;
			
			case circle:
				break;
				
			}
		}
		
		
		res.sort(new Vector2.ComparatorVector2Angle(source, (360 - rightRay.angleDeg()) + 1));
		res.add(source);
	}
	
	
	/**
	 * Calcule les ombres de courte distance
	 */
	public void calculateCloseShadow(Vector2 source, Vector2 lightDir, ArrayList<Vector2> res) {
		res.clear();
		
		Collider[] colliders = Collider.getColliders();

		// cast borders ray
		for (Vector2 ray : closeBorderRays) {
			if(Raycast.cast(source, ray, closeCastDistance) == null) res.add(Vector2.plus(source, ray));
		}
		
		// cast for every colliders
		for (Collider c : colliders) {
			switch (c.getType()) {
			case box:
				Vector2[] bounds = c.getBounds();

				for (Vector2 bound : bounds) {
					
					Vector2 dir = Vector2.minus(bound, source);
					dir.normalize();

					float angle = 0.0001f;
					
					Vector2 v1 = Raycast.cast(source, dir, closeCastDistance);
					Vector2 v2 = Raycast.cast(source, dir.rotateRad(angle), closeCastDistance);
					Vector2 v3 = Raycast.cast(source, dir.rotateRad(-angle*2), closeCastDistance);
					
					dir.rotateRad(angle);
					
					if(v1 != null) res.add(v1);
					else res.add(Vector2.plus(source, Vector2.multiply(dir, closeCastDistance)));
					dir.rotateRad(angle);
					if(v2 != null) res.add(v2);
					else res.add(Vector2.plus(source, Vector2.multiply(dir, closeCastDistance)));
					dir.rotateRad(-angle*2);
					if(v3 != null) res.add(v3);
					else res.add(Vector2.plus(source, Vector2.multiply(dir, closeCastDistance)));
				}

				break;
			
			case circle:
				break;
				
			}
		}
		
		
		res.sort(new Vector2.ComparatorVector2Angle(source, 0));
		
		//castCloseEndPoints.add(lightSource);
	}
	
	/**
	 * Affiche les ombres
	 * @param r
	 */
	public void render(Renderer r) {
		
		r.clearStockedLight();
		r.stockPointLight(lightSource, castCloseEndPoints.toArray(new Vector2[0]), lightImage2);
		r.stockPointLight(lightSource, castEndPoints.toArray(new Vector2[0]), lightImage);
		
		r.drawLight();
		
		/*
		for (Vector2 vector2 : castCloseEndPoints) {
			r.fillCircle(vector2, 10, Color.red);
		}*/
	}
	
	public static synchronized ShadowCaster getInstance() {
		if(instance == null) {
			instance = new ShadowCaster();
		}
		return instance;
	}
	
	
	
	
}
