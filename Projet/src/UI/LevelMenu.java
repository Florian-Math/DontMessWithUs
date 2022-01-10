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
import Engine.TimeSaver;
import Network.Client;
import Network.ServerRequest;
import Network.ServerRequestType;
import Network.WorldData;

public class LevelMenu extends JPanel implements Runnable {

	private Font bigTitleFont = new Font("Times New Roman", Font.PLAIN, 100);
	private Font labelFont = new Font("Times New Roman", Font.PLAIN, 40);
	private Font buttonFont = new Font("Times New Roman", Font.PLAIN, 28);
	
	private Color blue = new Color(21, 24, 52);
	private Color yellow = new Color(255, 217, 102);
	private Color gray = new Color(217, 217, 217, 150);
	private Color lightGray = new Color(217, 217, 217);
	private Color darkGray = new Color(122, 123, 137);
	
	public LevelMenu(int time) {
		
		Client.getCurrent().sendTcpRequest(new ServerRequest(ServerRequestType.setTimeData, new Integer[] {Map.getCurrentMap(), time}));
		
		Screen screen = Screen.getCurrent();
		ScreenUiScaler scaler = new ScreenUiScaler(screen);
		
		this.setPreferredSize(new Dimension(screen.getWidth(), screen.getHeight()));
		this.setLayout(null);
		this.setBackground(blue);
		
		JLabel titleLabel = new JLabel("Niveau " + Map.getCurrentMap() + " réussi");
		titleLabel.setFont(bigTitleFont);
		titleLabel.setForeground(lightGray);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(titleLabel, 200, 50, 600, 200, 0, 0);
		this.add(titleLabel);
		
		JLabel timeLabel = new JLabel("Temps : " + TimeSaver.convertTimeToString(time));
		timeLabel.setFont(labelFont);
		timeLabel.setForeground(lightGray);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(timeLabel, 500, 400, 400, 200);
		this.add(timeLabel);
		
		JLabel scoreLabel = new JLabel("<html>Score : " + Map.getScore(time) + "</html>");
		scoreLabel.setFont(labelFont);
		scoreLabel.setForeground(lightGray);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(scoreLabel, 500, 500, 400, 200);
		this.add(scoreLabel);
		
		JButton nextLevelButton = new JButton("Niveau suivant");
		nextLevelButton.setFont(buttonFont);
		nextLevelButton.setFont(buttonFont);
		nextLevelButton.setBackground(yellow);
		nextLevelButton.setFocusPainted(false);
		scaler.setBounds(nextLevelButton, 500, 750, 250, 60);
		
		nextLevelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.getCurrent().launchGame(Map.nextMap());
			}
		});
		
		this.add(nextLevelButton);
		
		JButton replayLevelButton = new JButton("Rejouer");
		replayLevelButton.setFont(buttonFont);
		replayLevelButton.setFont(buttonFont);
		replayLevelButton.setBackground(yellow);
		replayLevelButton.setFocusPainted(false);
		scaler.setBounds(replayLevelButton, 500, 830, 250, 60);
		
		replayLevelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.getCurrent().launchGame(Map.getCurrentMap());
			}
		});
		
		this.add(replayLevelButton);
		
		JButton menuLevelButton = new JButton("Retour au menu");
		menuLevelButton.setFont(buttonFont);
		menuLevelButton.setFont(buttonFont);
		menuLevelButton.setBackground(yellow);
		menuLevelButton.setFocusPainted(false);
		scaler.setBounds(menuLevelButton, 500, 910, 250, 60);
		
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
	
	boolean running;
	WorldData world;
	@Override
	public void run() {
		running = true;
		while(running) {
			world = Client.getCurrent().getWorldDataUdp();
			
			if(world.isLaunched) {
				Map.setCurrentMap(world.level);
				Game.getCurrent().start(Client.getCurrent().getPlayerType(), world);
				running = false;
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
