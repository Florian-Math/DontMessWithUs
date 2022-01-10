package UI;
import javax.swing.JComponent;

import Engine.Screen;
import Math.Mathf;

/**
 * Permet de gerer la taille des Jcomponent selon la resolution de l'écran
 */
public class ScreenUiScaler {
		
		private Screen screen;

		public ScreenUiScaler(Screen screen) {
			this.screen = screen;
		}
		
		/**
		 * Gere la taille d'un Jcomponent
		 * @param comp 		composant
		 * @param x 		coordonnée x
		 * @param y 		coordonnée y
		 * @param width 	longueur
		 * @param height 	hauteur
		 */
		public void setBounds(JComponent comp, int x, int y, int width, int height) {
			float sX = (float)screen.getWidth() / 1000;
			float sY = (float)screen.getHeight() / 1000;
			
			comp.setBounds((int)(x*sX - (width*sX)/2), (int)(y*sY - (height*sY)/2), (int)(width*sX), (int)(height*sY));
		}
		
		/**
		 * Gere la taille d'un Jcomponent
		 * @param comp 		composant
		 * @param x 		coordonnée x
		 * @param y 		coordonnée y
		 * @param width 	longueur
		 * @param height 	hauteur
		 * @param pivotX	point de pivot x (entre 0 et 1)
		 * @param pivotY	point de pivot y (entre 0 et 1)
		 */
		public void setBounds(JComponent comp, int x, int y, int width, int height, float pivotX, float pivotY) {
			float sX = (float)screen.getWidth() / 1000;
			float sY = (float)screen.getHeight() / 1000;
			
			pivotX = Mathf.clamp(pivotX, 0, 1)*2;
			pivotY = Mathf.clamp(pivotY, 0, 1)*2;
			
			comp.setBounds((int)(x*sX - ((width*sX)/2)*pivotX), (int)(y*sY - ((height*sY)/2)*pivotY), (int)(width*sX), (int)(height*sY));
		}
		
		public float getScaleX() {
			return (float)screen.getWidth() / 1000;
		}
		
		public float getScaleY() {
			return (float)screen.getHeight() / 1000;
		}
		
	}