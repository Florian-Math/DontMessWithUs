package Engine;

import java.io.Serializable;
import java.util.Comparator;

import Math.Mathf;

/**
 * Représentation d'un vecteur 2D
 */
public class Vector2 implements Serializable, Cloneable{

	public static final Vector2 zero = new Vector2(0, 0);
	public static final Vector2 right = new Vector2(1, 0);
	public static final Vector2 up = new Vector2(0, 1);
	public static final Vector2 left = new Vector2(-1, 0);
	public static final Vector2 down = new Vector2(0, -1);
	
	public float x;
	public float y;
	
	/**
	 * Construit un vecteur en (0, 0)
	 * @param x
	 * @param y
	 */
	public Vector2() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Construit un vecteur à partir de ses coordonnées
	 * @param x
	 * @param y
	 */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Construit un vecteur à partir des coordonnées du paramètre
	 * @param x
	 * @param y
	 */
	public Vector2(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	/**
	 * Additionne le vecteur à des coordonnées
	 * @param x
	 * @param y
	 */
	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Additionne le vecteur à un autre
	 * @param v
	 */
	public void add(Vector2 v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	/**
	 * Soustrait le vecteur à des coordonnées
	 * @param x
	 * @param y
	 */
	public void minus(float x, float y) {
		this.x -= x;
		this.y -= y;
	}
	
	/**
	 * Soustrait le vecteur à un autre
	 * @param v
	 */
	public void minus(Vector2 v) {
		this.x -= v.x;
		this.y -= v.y;
	}
	
	/**
	 * Additionne le vecteur à une valeur
	 * @param m
	 */
	public void multiply(float m) {
		this.x *= m;
		this.y *= m;
	}
	
	/**
	 * Retourne la distance du vecteur
	 * @return distance
	 */
	public float length() {
        return (float)Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Retourne la distance du vecteur au carré (permet une opération en moins ce qui est plus rapide)
	 * @return distance
	 */
	public float len2() {
        return x*x + y*y;
	}
	
	
	/**
	 * Normalise le vecteur
	 * @return le meme vecteur normalisé
	 */
	public Vector2 normalize() {
		float length = this.length();
		
        if(length > 0) {
			x = x / length;
			y = y / length;
        }
        
        return this;
	}
	
	/**
	 * Set le vecteur aux coordonnées en paramètre
	 * @param v
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * Set le vecteur aux coordonnées du vecteur en paramètre
	 * @param v
	 */
	public void set(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	/**
	 * Fait tourner le vecteur par un angle en radian
	 * @param angle
	 * @return
	 */
	public Vector2 rotateRad(float angle) {
		//float length = this.length();
		float newX = (float) (Math.cos(angle) * x - Math.sin(angle) * y);
		float newY = (float) (Math.sin(angle) * x + Math.cos(angle) * y);
		
		this.x = newX;
		this.y = newY;
		
		return this;
	}
	
	/**
	 * Fait tourner le vecteur par un angle en degrée
	 * @param angle
	 * @return
	 */
	public Vector2 rotateDeg(float angle) {
		return rotateRad((float) (angle*Math.PI/180));
	}
	
	/**
	 * Calcul l'angle en degrée et le retourne
	 * @return
	 */
	public float angleDeg() {
		return (float) (angleRad()*180/Math.PI);
	}
	
	/**
	 * Calcul l'angle en radian et le retourne
	 * @return
	 */
	public float angleRad() {
		float angle = (float) Math.atan2(y, x);
		if(angle<0) {
			float yes = (float)(Math.PI + angle);
			angle = (float)(Math.PI +yes);
		}
		return angle;
	}
	
	/**
	 * Interpolation linéaire entre 2 vecteurs
	 * @param start vecteur de départ
	 * @param target vecteur d'arrivée
	 * @param val valeur utilisé pour l'interpolation (entre 0 et 1)
	 * @return le vecteur iterpolé
	 */
	public static Vector2 lerp(Vector2 start, Vector2 target, float val) {
		val = Mathf.clamp(val, 0, 1);
		return new Vector2(Mathf.lerp(start.x, target.x, val), Mathf.lerp(start.y, target.y, val));
	}
	
	
	// ----------- static operator -----------
	
	public static Vector2 plus(Vector2 v1, Vector2 v2) {
		return new Vector2(v1.x + v2.x, v1.y + v2.y);
	}
	
	public static Vector2 minus(Vector2 v1, Vector2 v2) {
		return new Vector2(v1.x - v2.x, v1.y - v2.y);
	}
	
	public static Vector2 multiply(Vector2 v1, float m) {
		return new Vector2(v1.x * m, v1.y * m);
	}
	
	public static Vector2 div(Vector2 v1, float d) {
		return new Vector2(v1.x / d, v1.y / d);
	}
	
	
	@Override
	public Vector2 clone() {
		return new Vector2(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		Vector2 o = (Vector2)obj;
		
		return (o.x == x && o.y == y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
	public static class ComparatorVector2Angle implements Comparator<Vector2> {

		private Vector2 origin;
		private float offset;
		
		public ComparatorVector2Angle(Vector2 origin, float offset) {
			this.origin = origin;
			this.offset = offset;
		}
		
		@Override
		public int compare(Vector2 o1, Vector2 o2) {
			Vector2 dir1 = Vector2.minus(o1, origin);
			Vector2 dir2 = Vector2.minus(o2, origin);
			
			float angle1 = dir1.angleDeg()+offset;
			float angle2 = dir2.angleDeg()+offset;
			
			if(angle1 > 360) angle1 -= 360;
			if(angle1 < 0) angle1 += 360;
			
			if(angle2 > 360) angle2 -= 360;
			if(angle2 < 0) angle2 += 360;
			
			if(angle1 < angle2) return 1;
			if(angle1 > angle2) return -1;
			return 0;
		}
		
	}
	
}
