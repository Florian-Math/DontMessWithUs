package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import Network.Server;

public class UiServer extends JFrame {

	JTextArea text;
	JScrollPane scroll;
	public UiServer(Server serv) {
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				serv.close();
				System.exit(0);
			}
		});
		this.setLayout(new BorderLayout());
		
		text = new JTextArea();
		text.setEditable(false);
		
		scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(500, 700));
		
		getContentPane().add(scroll);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setTitle("Serveur");
		this.setVisible(true);
	}
	
	public void println(String message) {
		text.setText(text.getText() + message + "\n");
		text.setCaretPosition(text.getDocument().getLength());
	}
	
}
