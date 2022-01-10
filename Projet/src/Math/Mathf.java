package Math;

public final class Mathf {

	private Mathf() {}
	
	public static float clamp(float value, float min, float max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}
	
	/**
	 * Interpolation lin�aire entre 2 points
	 * @param start valeur de d�part
	 * @param end valeur d'arriv�e
	 * @param val valeur utilis� pour l'interpolation (entre 0 et 1)
	 * @return la valeur iterpol�
	 */
	public static float lerp(float start, float end, float val) {
		val = clamp(val, 0, 1);
		return start + val * (end - start);
	}
	
	/**
	 * Verifie si un angle est entre deux autres
	 * @param val angle courant
	 * @param min angle minimum
	 * @param max angle maximum
	 * @return
	 */
	public static boolean isBetweenAngle(float val, float min, float max) {
		if(min > max) return val < max || val > min;
		else return val > min && val < max;
	}
	
}
