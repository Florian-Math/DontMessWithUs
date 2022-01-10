package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Engine.Game;
import Engine.Screen;
import Engine.TimeSaver;
import Network.Client;
import Network.Server;
import Network.WorldData;

/**
 * Menu principal : permet de se connecter avec un nom et une addresse ip
 */
public class MainMenu extends JPanel {

	private Font labelFont = new Font("Times New Roman", Font.PLAIN, 30);
	private Font buttonFont = new Font("Times New Roman", Font.PLAIN, 20);
	private BufferedImage logo;
	
	private Color blue = new Color(21, 24, 52);
	private Color yellow = new Color(255, 217, 102);
	private Color gray = new Color(217, 217, 217, 150);
	private Color lightGray = new Color(217, 217, 217);
	private Color darkGray = new Color(122, 123, 137);
	
	public MainMenu() {
		super();
		Screen screen = Screen.getCurrent();
		ScreenUiScaler scaler = new ScreenUiScaler(screen);
		this.setPreferredSize(new Dimension(screen.getWidth(), screen.getHeight()));
		this.setLayout(null);
		
		try {
			logo = ImageIO.read(new File("images/DontMessWithTheLogo.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		this.setBackground(blue);
		
		JLabel titleLabel = new JLabel("Rejoindre serveur :");
		titleLabel.setFont(labelFont);
		titleLabel.setForeground(blue);
		scaler.setBounds(titleLabel, 100, 700, 500, 40, 0, 0);
		
		JLabel ipLabel = new JLabel("   Adresse ip du serveur :");
		ipLabel.setFont(labelFont);
		ipLabel.setForeground(blue);
		ipLabel.setOpaque(true);
		ipLabel.setBackground(darkGray);
		scaler.setBounds(ipLabel, 200, 850, 200, 40, 0, 0);
		
		JLabel nameLabel = new JLabel("   Nom :");
		nameLabel.setFont(labelFont);
		nameLabel.setForeground(blue);
		nameLabel.setOpaque(true);
		nameLabel.setBackground(darkGray);
		scaler.setBounds(nameLabel, 200, 780, 200, 40, 0, 0);
		
		JTextField nameField = new JTextField();
		nameField.setFont(labelFont);
		nameField.setBackground(lightGray);
		nameField.setBorder(null);
		scaler.setBounds(nameField, 450, 780, 350, 40, 0, 0);
		
		JTextField ipField = new JTextField();
		ipField.setFont(labelFont);
		ipField.setBackground(lightGray);
		ipField.setBorder(null);
		scaler.setBounds(ipField, 450, 850, 350, 40, 0, 0);
		
		JButton ipButton = new JButton("Connection");
		ipButton.setFont(buttonFont);
		ipButton.setBackground(yellow);
		ipButton.setFocusPainted(false);
		
		scaler.setBounds(ipButton, 500, 953, 350, 40);
		ipButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println(ipField.getText());
				try {
					if(Client.connect(InetAddress.getByName(ipField.getText()))) {
						WorldData world = Client.getCurrent().registerPlayer(nameField.getText());
						
						if(world == null) throw new Exception("");
						boolean t = Client.getCurrent().askForConstantWorldDataUdp(Client.getCurrent().getPlayerType());
						if(!t) throw new Exception();
						
						WorldData.runUpdater();
						TimeSaver.load();
						
						Game.getCurrent().changeMenu(new LobbyMenu());
						return;
					}
				} catch (Exception e1) {}
				// failed to connect
				System.out.println("failed to connect");
			}
		});
		
		this.add(titleLabel);
		this.add(nameLabel);
		this.add(nameField);
		this.add(ipLabel);
		this.add(ipField);
		this.add(ipButton);
		
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(blue);
		g.fillRect(0, 0, this.getWidth(), logo.getHeight()/6);
		g.setColor(gray);
		g.fillRect(0, logo.getHeight()/6, this.getWidth(), this.getHeight()/80);
		g.setColor(gray);
		g.fillRect(100, 650, this.getWidth() - 200, 203);
		g.setColor(yellow);
		g.fillRect(120, 615, 300, 70);
		
		g.drawImage(logo, this.getWidth()/2 - logo.getWidth()/12, 0, logo.getWidth()/6, logo.getHeight()/6, null);
		
		
	}
	
}
