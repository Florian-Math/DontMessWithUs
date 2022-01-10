package Engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.Thread.State;

import javax.swing.JFrame;

import Objects.PlayerType;
import Network.WorldData;
import UI.MainMenu;


/**
 * Classe principale du jeu
 */
public class Game extends Canvas implements Runnable {
	private static Game current;
	private static String playerName;
	
	public LevelManager levelManager;
	
	public static final String NAME = "Dont Mess With Us";
	
	private JFrame frame;
	private Screen screen;
	
	private boolean running;
	
	private BufferedImage image;
	private Renderer r;
	private BufferStrategy bs;
	private Graphics g;
	
	private Component currentMenu;
	private Thread gameThread;
	
	
	private Game() {
		current = this;
		screen = Screen.getCurrent();
		screen.setDimensions(1600);
		
		image = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		setMinimumSize(new Dimension(screen.getWidth(), screen.getHeight()));
		setMaximumSize(new Dimension(screen.getWidth(), screen.getHeight()));
		setPreferredSize(new Dimension(screen.getWidth(), screen.getHeight()));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		currentMenu = new MainMenu();
		frame.add(currentMenu, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
	public static PlayerType playerPlayed;
	
	public synchronized void start(PlayerType type, WorldData data) {
		playerPlayed = type;
		
		changeMenu(this);
		this.setBackground(Color.BLACK);
		this.createBufferStrategy(2);
		bs = this.getBufferStrategy();
		g = bs.getDrawGraphics();
		
		r = new Renderer(image);
		
		this.addKeyListener(Input.getInstance());
		this.addMouseMotionListener(Input.getInstance());
		
		playerName = data.getPlayer(type).name;
		
		levelManager = new LevelManager();
		levelManager.setPlayedPlayer(type);
		levelManager.loadLevel("map" + Map.getCurrentMap());
		
		if(gameThread == null)
			gameThread = new Thread(this);
		else if(gameThread.getState() == State.TERMINATED)
			gameThread = new Thread(this);
		else {
			this.stop();
			gameThread = new Thread(this);
		}
		
		running = true;
		gameThread.start();
	}
	
	public synchronized void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		double deltaTime = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			deltaTime = (now - lastTime) / 1000000000D;
			lastTime = now;
			
			tick(deltaTime);
			
			while(delta >= 1) {
				ticks++;
				fixedTick();
				delta--;
			}
			
			frames++;
			render();
			
			if(System.currentTimeMillis() - lastTimer >= 1000) { //update par seconde
				lastTimer += 1000;
				frame.setTitle(NAME + " (" + ticks + " ticks, " + frames + " frames)");
				frames = 0;
				ticks = 0;
			}
			
		}
	}
	
	private void tick(double deltaTime) {
		Input.getInstance().tick();
		
		levelManager.tick(deltaTime);
		
		screen.tick(deltaTime);
		
	}
	
	private void fixedTick() {
		levelManager.fixedTick();
		screen.fixedTick();
	}
	
	public void serverTick() {
		if(levelManager == null) return;
		levelManager.updateDataFromServer();
	}

	private void render() {
		r.clear();
		levelManager.render(r);
		g.drawImage(image, 0, 0, screen.getWidth(), screen.getHeight(), null);
		
		bs.show();
	}

	public void changeMenu(Component menu) {
		frame.remove(currentMenu);
		currentMenu = menu;
		frame.add(menu, BorderLayout.CENTER);
		frame.repaint();
		frame.revalidate();
	}
	
	public static void main(String[] args) {
		new Game();
	}
	
	public static Game getCurrent() {
		return current;
	}

	public static String getPlayerName() {
		return playerName;
	}

}
