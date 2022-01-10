package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Engine.GameObject;
import Engine.TimeSaver;
import Math.Mathf;
import Objects.PlayerType;

public class TcpServer extends Thread {
	
	private Server server;
	private ServerSocket serverSocket;
	
	private boolean running;
	
	public ArrayList<ClientHandler> threads;
	
	public TcpServer(Server s) {
		threads = new ArrayList<ClientHandler>();
		this.server = s;
	}
	
	@Override
	public void run() {
		try {
			running = true;
			serverSocket = new ServerSocket(1030);
			
			while (running) {
				ClientHandler c = new ClientHandler(serverSocket.accept(), server, this);
				threads.add(c);
				c.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopServer() {
		running = false;
		while(threads.size() != 0) {
			threads.get(0).disconnect();
		}
	}
	
	private static class ClientHandler extends Thread {
		private Server server;
		
		private Socket clientSocket;
		InetAddress playerAddress;
		
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		private String clientName;
		private TcpServer tcp;
		
		public ClientHandler(Socket socket, Server server, TcpServer tcp) {
			this.clientSocket = socket;
			this.server = server;
			this.playerAddress = socket.getInetAddress();
			this.tcp = tcp;
		}
		
		/**
	     * Boucle du serveur tcp
	     */
		public void run() {
			try {
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in = new ObjectInputStream(clientSocket.getInputStream());
				
				Object data;
				while((data = in.readObject()) != null) {
					ServerRequest request = (ServerRequest)data;
					
					switch (request.requestType) {
					case getData:
						out.writeObject(server.getWorldData());
						break;
					case register:
						if(!registerPlayer(request)) {
							clientName = "";
							disconnect();
						}
							
						break;
					case launchGame:
						server.loadLevel((Integer)request.data);
						server.getWorldData().isLaunched = true;
						out.writeObject(server.getWorldData());
						break;
					case endGame:
						server.resetGame();
						out.writeObject(server.getWorldData());
						break;
					case changeLevel:
						server.getWorldData().level = (Integer)request.data;
						out.writeObject(server.getWorldData());
						break;
					case setTimeData:
						Integer[] d = (Integer[])request.data;
						server.getWorldData().timel = d[0];
						server.getWorldData().timet = d[1];
						server.out.println("Serveur : time: " + TimeSaver.convertTimeToString(d[1]));
						out.writeObject(server.getWorldData());
						break;
					case disconnect:
						disconnect();
						break;

					default:
						break;
					}
				}

				disconnect();
				
			} catch (IOException e) {
				disconnect();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				}
		}
		
		public void disconnect() {
			server.disconnect(clientName);
			try {
				in.close();
				out.close();
				clientSocket.close();
				tcp.threads.remove(this);
			} catch (IOException e1) { e1.printStackTrace();}
		}
		
		
		private boolean registerPlayer(ServerRequest request) throws IOException {
			PlayerDataConn data = (PlayerDataConn)request.data;
			
			clientName = data.name;
			if(server.registerPlayer(data.name, playerAddress)) {
				out.writeObject(server.getWorldData());
				return true;
			}
			else {
				out.writeObject(null);
				return false;
			}
	    }
		
	}
};
