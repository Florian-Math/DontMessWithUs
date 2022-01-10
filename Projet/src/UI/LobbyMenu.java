package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Engine.Game;
import Engine.GameObject;
import Engine.Map;
import Engine.Screen;
import Engine.TimeSaver;
import Math.Mathf;
import Objects.PlayerType;
import Network.Client;
import Network.Sendable;
import Network.Server;
import Network.WorldData;
import Network.data.ObjectData;

/**
 * Lobby : affiche les joueurs connectés
 */
public class LobbyMenu extends JPanel implements Runnable{
	
	private Font labelFont = new Font("Times New Roman", Font.PLAIN, 30);
	private Font buttonFont = new Font("Times New Roman", Font.PLAIN, 28);
	private Color blue = new Color(21, 24, 52);
	private Color yellow = new Color(255, 217, 102);
	private Color gray = new Color(217, 217, 217, 150);
	private Color lightGray = new Color(217, 217, 217);
	private Color darkGray = new Color(122, 123, 137);
	
	
	JLabel playerInf;
	JLabel playerObs;

	WorldData world;
	
	Thread dataThread;
	
	boolean searching;
	boolean fading;
	
	JLabel levelLabel;
	
	BufferedImage infUI;
	BufferedImage obsUI;
	BufferedImage waiting;
	
	JLabel observateurTitleLabel;
	JLabel infiltreTitleLabel;
	JLabel observateurDescrLabel;
	JLabel infiltreDescrLabel;
	
	JButton playButton;
	
	JLabel timeLabel;
	JLabel scoreLabel;
	
	ScreenUiScaler scaler;
	
	public LobbyMenu() {
		super();
		try {
			infUI = ImageIO.read(new File("images/InfUI.png"));
			obsUI = ImageIO.read(new File("images/ObsUI.png"));
			waiting = ImageIO.read(new File("images/Waiting.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		scaler = new ScreenUiScaler(Screen.getCurrent());
		
		
		this.setPreferredSize(new Dimension(Screen.getCurrent().getWidth(), Screen.getCurrent().getHeight()));
		this.setLayout(null);
		
		this.setBackground(blue);
		
		JLabel titleLabel = new JLabel("Lobby :");
		titleLabel.setFont(labelFont);
		titleLabel.setOpaque(true);
		titleLabel.setBackground(yellow);
		titleLabel.setForeground(blue);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(titleLabel, 500, 50, 100, 70);
		
		infiltreTitleLabel = new JLabel("");
		infiltreTitleLabel.setFont(labelFont);
		infiltreTitleLabel.setForeground(blue);
		scaler.setBounds(infiltreTitleLabel, 205, 260, 60, 40, 0, 0);
		
		observateurTitleLabel = new JLabel("");
		observateurTitleLabel.setFont(labelFont);
		observateurTitleLabel.setForeground(blue);
		scaler.setBounds(observateurTitleLabel, 735, 260, 100, 40, 0, 0);
		
		observateurDescrLabel = new JLabel("");
		observateurDescrLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		observateurDescrLabel.setForeground(blue);
		scaler.setBounds(observateurDescrLabel, 680, 360, 200, 300, 0, 0);
		this.add(observateurDescrLabel);
		
		infiltreDescrLabel = new JLabel("");
		infiltreDescrLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		infiltreDescrLabel.setForeground(blue);
		scaler.setBounds(infiltreDescrLabel, 130, 360, 200, 300, 0, 0);
		this.add(infiltreDescrLabel);
		
		if(Client.getCurrent().getPlayerType() == PlayerType.infiltre) {
			playButton = new JButton("Jouer");
			playButton.setFont(buttonFont);
			playButton.setFont(buttonFont);
			playButton.setBackground(yellow);
			playButton.setFocusPainted(false);
			scaler.setBounds(playButton, 500, 850, 250, 60);
			
			playButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(TimeSaver.load().getTime(Map.getCurrentMap()) == -1) return;
					
					if(world != null && world.players[0] != null && world.players[1] != null) {
						if(Client.getCurrent().launchGame(world.level).isLaunched) {
							searching = false;
						}
					}
				}
			});
			
			this.add(playButton);
			
			
			JButton nextMapButton = new JButton(">");
			nextMapButton.setFont(buttonFont);
			nextMapButton.setFont(buttonFont);
			nextMapButton.setBackground(yellow);
			nextMapButton.setFocusPainted(false);
			scaler.setBounds(nextMapButton, 600, 300, 40, 60);
			
			nextMapButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Map.setCurrentMap(world.level);
					Map.nextMap();
					levelLabel.setText("Niveau : " + Map.getCurrentMap());
					Client.getCurrent().changeLevel(Map.getCurrentMap());
					
				}
			});
			
			this.add(nextMapButton);
			
			JButton previousMapButton = new JButton("<");
			previousMapButton.setFont(buttonFont);
			previousMapButton.setFont(buttonFont);
			previousMapButton.setBackground(yellow);
			previousMapButton.setFocusPainted(false);
			scaler.setBounds(previousMapButton, 400, 300, 40, 60);
			
			previousMapButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Map.setCurrentMap(world.level);
					Map.previousMap();
					levelLabel.setText("Niveau : " + Map.getCurrentMap());
					Client.getCurrent().changeLevel(Map.getCurrentMap());
				}
			});
			
			this.add(previousMapButton);
			
			
		}else {
			JLabel waiting = new JLabel("En attente de l'autre joueur ...");
			waiting.setFont(labelFont);
			waiting.setBackground(yellow);
			waiting.setOpaque(true);
			waiting.setHorizontalAlignment(SwingConstants.CENTER);
			scaler.setBounds(waiting, 500, 850, 250, 60);
			
			this.add(waiting);
		}
		
		levelLabel = new JLabel("Level : ");
		levelLabel.setFont(labelFont);
		levelLabel.setOpaque(true);
		levelLabel.setBackground(yellow);
		levelLabel.setForeground(blue);
		levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(levelLabel, 500, 300, 150, 60);
		
		this.add(levelLabel);
		
		timeLabel = new JLabel("");
		timeLabel.setFont(labelFont);
		timeLabel.setForeground(lightGray);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(timeLabel, 500, 400, 400, 200);
		this.add(timeLabel);
		
		scoreLabel = new JLabel("");
		scoreLabel.setFont(labelFont);
		scoreLabel.setForeground(lightGray);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaler.setBounds(scoreLabel, 500, 475, 400, 200);
		this.add(scoreLabel);
		
		
		playerInf = new JLabel("");
		playerInf.setFont(labelFont);
		playerInf.setForeground(blue);
		scaler.setBounds(playerInf, 130, 330, 250, 40, 0, 0);
		
		playerObs = new JLabel("");
		playerObs.setFont(labelFont);
		playerObs.setForeground(blue);
		scaler.setBounds(playerObs, 680, 330, 250, 40, 0, 0);
		
		
		this.add(titleLabel);
		this.add(infiltreTitleLabel);
		this.add(observateurTitleLabel);
		
		
		this.add(playerInf);
		this.add(playerObs);
		
		fading = true;
		searching = true;
		dataThread = new Thread(this);
		dataThread.start();
		repaint();
	}
	
	private int fade;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(new Color(0x01000000 * fade, true));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		/*
		g.setColor(gray);
		g.fillRect(200, 80, this.getWidth() - 400, 685);*/
		
		float sX = scaler.getScaleX();
		float sY = scaler.getScaleY();
		
		if(world == null) return;
		if(world.getPlayer(PlayerType.infiltre) != null)
			g.drawImage(infUI, (int)(100*sX), (int)(100*sY), (int)(260*sX), (int)(260*sX * 1.4f), null);
		else
			g.drawImage(waiting, (int)(100*sX), (int)(100*sY), (int)(260*sX), (int)(260*sX * 1.4f), null);
		
		if(world.getPlayer(PlayerType.observateur) != null)
			g.drawImage(obsUI, (int)(650*sX), (int)(100*sY), (int)(260*sX), (int)(260*sX * 1.4f), null);
		else
			g.drawImage(waiting, (int)(650*sX), (int)(100*sY), (int)(260*sX), (int)(260*sX * 1.4f), null);
	}
	
	boolean exit = false;
	
	@Override
	public void run() {
		
		while(searching) {
			world = Client.getCurrent().askForWorldDataUdp();
			if(world == null) continue;
			Map.setCurrentMap(world.level);
			
			TimeSaver.load().setTime(world.timel, world.timet);
			
			
			if(playButton != null) {
				playButton.setEnabled(TimeSaver.load().getTime(Map.getCurrentMap()) != -1);
			}

			if(world.players[0] != null) {
				infiltreTitleLabel.setText("<html><u>Infiltré</u></html>");
				infiltreDescrLabel.setText("<html>Role : Le joueur infiltré doit se déplacer en terrain hostile, afin que tu puisses t'échapper par l'ascenseur il va falloir suivre les indications du guide, les drones sont sur ton chemin, s'ils te repèrent s'en est fini de toi, alors laisse-toi guider et ne te fait surtout pas repérer.</html>");
				if(world.players[0].type == PlayerType.infiltre)
					playerInf.setText("Nom : " + world.players[0].name);
				else 
					playerObs.setText("Nom : " + world.players[0].name);
			}else {
				infiltreDescrLabel.setText("");
				infiltreTitleLabel.setText("");
				playerInf.setText("");
			}
				
				
			if(world.players[1] != null) {
				observateurTitleLabel.setText("<html><u>Observateur</u></html>");
				observateurDescrLabel.setText("<html>Role : Le joueur observateur doit observer et guider le joueur infiltré afin qu'il puisse s'échapper par l'ascenseur, mais attention des drones garde le chemin, toi seul peut voir leur champ de vision alors guide bien notre infiltré et ne le laisse pas se faire repérer.</html>");
				if(world.players[1].type == PlayerType.infiltre)
					playerInf.setText("Nom : " + world.players[1].name);
				else 
					playerObs.setText("Nom : " + world.players[1].name);
			}else {
				observateurDescrLabel.setText("");
				observateurTitleLabel.setText("");
				playerObs.setText("");
			}
			
			if(TimeSaver.load().getTime(Map.getCurrentMap()) == 0) {
				timeLabel.setText("Non complété");
				scoreLabel.setText("");
			}else if(TimeSaver.load().getTime(Map.getCurrentMap()) == -1){
				timeLabel.setText("Bloqué");
				scoreLabel.setText("");
			}else {
				timeLabel.setText("Temps : " + TimeSaver.convertTimeToString(TimeSaver.load().getTime(Map.getCurrentMap())));
				scoreLabel.setText("<html>Score : " + Map.getScore(TimeSaver.load().getTime(Map.getCurrentMap())) + "</html>");
			}
			
			
			levelLabel.setText("Niveau : " + world.level);
			
			if(world.isLaunched) {
				searching = false;
				
			}
			
			if(exit) return;
			
			repaint();
		}
		
		float lerpVal = 0;
		long now;
		double deltaTime;
		long lastTime = System.nanoTime();
		while(fading) {
			if(exit) return;
			now = System.nanoTime();
			deltaTime = (now - lastTime) / 1000000000D;
			lastTime = now;
			
			lerpVal += deltaTime / 2;
			fade = (int)Mathf.lerp(0, 255, lerpVal);
			
			repaint();
			
			if(lerpVal >= 1) // si transition fini alors charge le jeu
				fading = false;
		}
		if(exit) return;
		Map.setCurrentMap(world.level);
		Game.getCurrent().start(Client.getCurrent().getPlayerType(), world);
		
	}
	
	
}
