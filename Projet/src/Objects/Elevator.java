package Objects;

import Engine.Callback;
import Engine.Game;
import Engine.GameObject;
import Engine.Map;
import Engine.Renderer;
import Engine.Screen;
import Engine.TimeSaver;
import Engine.Vector2;
import Engine.graphics.SpriteAnimator;
import Engine.graphics.SpriteSheet;
import Network.Client;
import Network.ServerRequest;
import Network.ServerRequestType;
import UI.LevelMenu;
import UI.LobbyMenu;
import UI.RetryMenu;

public class Elevator extends GameObject {
	
	private InfiltratedPlayer player;
	
	private SpriteAnimator animator;
	
	public Elevator(Vector2 position) {
		super(position);
		
		SpriteSheet elevatorSheet = new SpriteSheet("images/elevator.png", 16, 6, 2f);
		animator = new SpriteAnimator(elevatorSheet, 50);
		this.renderingLayer = 3;
	}
	
	@Override
	public void fixedTick() {
		int dist = (int)Vector2.minus(position, player.getPosition()).length();
		
		if(dist <= 60) {
			animator.setNextFunction(closing);
			animator.tick();
			
			// lock player
			player.lockMovement();
			
			// fade && load new level
			Screen.getCurrent().fadeToBlack(2000, new Callback() {
				
				@Override
				public void call() {
					Game.getCurrent().levelManager.reset();
					
					int time = TimeSaver.stopTimer();
					if(TimeSaver.load().getTime(Map.getCurrentMap() + 1) == -1) TimeSaver.load().setTime(Map.getCurrentMap() + 1, 0); // unlock new level
					TimeSaver.load().setTime(Map.getCurrentMap(), time); // store time
					TimeSaver.save();
					
					if(Client.getCurrent().getPlayerType() == PlayerType.infiltre) Game.getCurrent().changeMenu(new LevelMenu(time));
					else Game.getCurrent().changeMenu(new LobbyMenu());
					
					Client.getCurrent().stopGame();
				}
			});
			
		}else if(dist < 300){
			animator.setNextFunction(opening);
			animator.tick();
		}else {
			animator.setNextFunction(closing);
			animator.tick();
		}
	}
	
	@Override
	public void render(Renderer r) {
		animator.drawSprite(position, r);
	}
	
	public void setPlayer(InfiltratedPlayer player) {
		this.player = player;
	}
	
	private Callback opening = new Callback() {
		
		@Override
		public void call() {
			if(animator.getCurrentSpriteNumber() > 0)
				animator.previous();
		}
	};
	
	private Callback closing = new Callback() {
		
		@Override
		public void call() {
			if(animator.getCurrentSpriteNumber() + 1 < animator.getFrameNumber())
				animator.next();
		}
	};
}
