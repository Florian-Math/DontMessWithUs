package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Engine.Game;
import Engine.Map;
import Engine.Screen;
import Network.Client;
import Network.WorldData;

public class RetryMenu extends JPanel implements Runnable {

	private Font bigTitleFont = new Font("Times New Roman", Font.PLAIN, 200);
	private Font labelFont = new Font("Times New Roman", Font.PLAIN, 30);
	private Font buttonFont = new Font("Times New Roman", Font.PLAIN, 28);
	
	private Color blue = new Color(21, 24, 52);
	private Color yellow = new Color(255, 217, 102);
	private Color gray = new Color(217, 217, 217, 150);
	private Color lightGray = new Color(217, 217, 217);
	private Color darkGray = new Color(122, 123, 137);
	
	public RetryMenu() {
		
		Screen screen = Screen.getCurrent();
		ScreenUiScaler scaler = new ScreenUiScaler(screen);
		
		this.setPreferredSize(new Dimension(screen.getWidth(), screen.getHeight()));
		this.setLayout(null);
		this.setBackground(blue);
		
		JLabel titleLabel = new JLabel("Game Over");
		titleLabel.setFont(bigTitleFont);
		titleLabel.setForeground(lightGray);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(titleLabel, 200, 300, 600, 200, 0, 0);
		this.add(titleLabel);
		
		
		JButton retryButton = new JButton("Rejouer");
		retryButton.setFont(buttonFont);
		retryButton.setBackground(yellow);
		retryButton.setFocusPainted(false);
		scaler.setBounds(retryButton, 500, 730, 250, 60);
		
		retryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.getCurrent().launchGame(Map.getCurrentMap());
			}
		});
		
		this.add(retryButton);
		
		JButton menuLevelButton = new JButton("Retour au menu");
		menuLevelButton.setFont(buttonFont);
		menuLevelButton.setBackground(yellow);
		menuLevelButton.setFocusPainted(false);
		scaler.setBounds(menuLevelButton, 500, 810, 250, 60);
		
		menuLevelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Game.getCurrent().changeMenu(new LobbyMenu());
				running = false;
			}
		});
		
		this.add(menuLevelButton);
		
		new Thread(this).start();
	}
	
	
	WorldData world;
	boolean running;
	@Override
	public void run() {
		running = true;
		while(running) {
			world = Client.getCurrent().getWorldDataUdp();
			if(world.isLaunched) {
				running = false;
				Map.setCurrentMap(world.level);
				Game.getCurrent().start(Client.getCurrent().getPlayerType(), world);
			}
			
			//repaint();
		}
		/*
		float lerpVal = 0;
		long now;
		double deltaTime;
		long lastTime = System.nanoTime();
		while(fading) {
			
			now = System.nanoTime();
			deltaTime = (now - lastTime) / 1000000000D;
			lastTime = now;
			
			lerpVal += deltaTime / 2;
			fade = (int)Mathf.lerp(0, 255, lerpVal);
			
			repaint();
			
			if(lerpVal >= 1) // si transition fini alors charge le jeu
				fading = false;
		}
		
		jeu.start(world.getPlayer(name).type, world);
		*/
	}
	
	
}
