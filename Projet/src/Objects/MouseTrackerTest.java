package Objects;

import java.awt.Color;

import Engine.GameObject;
import Engine.Input;
import Engine.Renderer;
import Engine.Screen;

public class MouseTrackerTest extends GameObject {

	@Override
	public void render(Renderer r) {
		r.fillRect(Screen.getCurrent().toWorldPoint(Input.getInstance().getMousePosition()), 20, 20, Color.red);
	}
	
}
