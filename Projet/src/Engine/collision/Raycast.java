package Engine.collision;

import java.util.ArrayList;

import Engine.Vector2;

public final class Raycast {

	/**
	 * Lance un rayon a partir d'une origine, une direction et une distance puis renvoie le premier point d'interation avec l'environnement
	 * @param origin
	 * @param dir
	 * @param dist
	 * @return
	 */
	public static Vector2 cast(Vector2 origin, Vector2 dir, float dist) {
		ArrayList<Vector2> points = new ArrayList<Vector2>();
		Collider[] colliders = Collider.getColliders();
		
		Vector2 dir2 = new Vector2(dir);
		dir2.normalize();
	
		Vector2 ray = Vector2.multiply(dir2, dist);
		Vector2 target = Vector2.plus(origin, ray);
		
		// calcul les points d'intersection
		for (Collider collider : colliders) {
			
			switch (collider.type) {
			case box:
				Vector2[] bounds = collider.getBounds();
				
				int j = 0;
				for (int i = 0; i < bounds.length; i++) {
					if(j < bounds.length - 1)
						j++;
					else
						j=0;
					
					Vector2 res = lineIntersection(origin, target, bounds[i], bounds[j]);
					
					if(res != null) {
						points.add(res);
					}
				}
				break;
			
			case circle:
				break;
				
			}
		}
		
		// recupère le plus proche
		if(points.size() > 0) {
			Vector2 closestPoint = points.get(0);
			float smallestDistance = Vector2.minus(points.get(0), origin).len2(); // utilisation de len2 car plus rapide
			float currentDistance;
			
			for (int i = 1; i < points.size(); i++) {
				currentDistance = Vector2.minus(points.get(i), origin).len2();
				if(currentDistance < smallestDistance) {
					smallestDistance = currentDistance;
					closestPoint = points.get(i);
				}
			}
			
			return closestPoint;
		}
		else
			return null;
	}
	
	/**
	 * Calcule le point d'intersection de deux segments et le renvoie
	 * sources : https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	 * 			 https://www.youtube.com/watch?v=c065KoXooSw
	 * @param o1 origine du segment 1
	 * @param t1 fin du segment 1
	 * @param o2 origine du segment 2
	 * @param t2 fin du segment 2
	 * @return point d'intersection des deux segments
	 */
	private static Vector2 lineIntersection(Vector2 o1, Vector2 t1, Vector2 o2, Vector2 t2) {
		Vector2 ray = Vector2.minus(t1, o1);
		Vector2 seg = Vector2.minus(t2, o2);
		
		//calcul de t et u pour o1 + t * dir1 = o2 + u * dir2
		float d = ray.x * seg.y - ray.y * seg.x;
		float u = ((o2.x - o1.x) * ray.y - (o2.y - o1.y) * ray.x) / d;
		float t = ((o2.x - o1.x) * seg.y - (o2.y - o1.y) * seg.x) / d;
		
		//intersection si 0 <= u <= 1 et 0 <= t <= 1
		if(u >= 0 && u <= 1 && t >= 0 && t <= 1) {
			return Vector2.plus(o1, Vector2.multiply(ray, t));
		}
		else
			return null;
	}
	
}
