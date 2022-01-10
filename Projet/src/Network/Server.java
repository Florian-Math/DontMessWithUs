package Network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

import Engine.GameObject;
import Engine.Map;
import Engine.Vector2;
import Network.data.ObjectData;
import Objects.InfiltratedPlayer;
import Objects.PlayerType;
import UI.UiServer;

public class Server implements Runnable {
	public UiServer out;
	private UdpServer udpServer;
	private TcpServer tcpServer;
	
    private boolean running;
    
    private WorldData worldData;
    
    private InetAddress[] addresses;
    private int[] ports;
    
    private Server() {
    	out = new UiServer(this);
    	out.println("Server : starting server");
    	addresses = new InetAddress[2];
    	ports = new int[2];
    	
        worldData = new WorldData();
        
        udpServer = new UdpServer(this);
        tcpServer = new TcpServer(this);
        
        udpServer.start();
        tcpServer.start();
        
        new Thread(this).start();
    }
    
    ArrayList<GameObject> objects;

	@Override
	public void run() {
		running = true;
		out.println("Server : server started");
		
		
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 10D;
		
		double delta = 0;
		
		objects = new ArrayList<GameObject>();
		
		long now;
		
		// ----
		
		while (running) { // server loop
			
			now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			while(delta >= 1) {
				if(addresses[0] != null) udpServer.sendData(addresses[0], ports[0], worldData);
				if(addresses[1] != null) udpServer.sendData(addresses[1], ports[1], worldData);
				
				for (GameObject gameObject : objects) {
					gameObject.tick(1 / 10D);
					
					ObjectData d = ((Sendable)gameObject).getData();
					if(d != null)
						worldData.updateObjectData(d);
				}
				
				delta--;
			}
			
		}
	}
	
	public void resetGame() {
		getWorldData().stopGame();
		GameObject.reset();
		objects.clear();
		worldData.resetObjectsData();
	}
	
	public void loadLevel(int level) {
		out.println("Server : Loading " + level);
		Map.setCurrentMap(level);
		worldData.level = level;
		Map map = new Map("map" + level);
		
		// parcours
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				
				// skip si transparent
				if(map.isEmpty(x, y)) continue;
				
				
				if(map.isDrone(x, y)) {
					map.processDrone(objects, x, y);
					continue;
				}
				
				/*
				if(tile == 0xFF00FF00) {
					// end
					continue;
				}*/
				
				if(map.isPlayer(x, y)) {
					new InfiltratedPlayer(new Vector2(0, 0), false);
					continue;
				}
				
				// sinon
				//new Cube(Vector2.zero, 0);
			}
		}
		
		out.println("Server : Map loaded");
	}
	
	// ------------------------
    

    
    public void showPlayersData() {
    	out.println("[" + worldData.players[0] + ", " + worldData.players[1] + "]");
    }
    
    public boolean registerPlayer(String name, InetAddress playerAddress) { // a revoir
    	out.println("Server : registering new player");
    	
    	if(worldData.getPlayer(name) != null) {
    		out.println("Server : player " + name + " already connected");
    		return false;
    	}
    	
    	if(worldData.players[0] == null) {
    		worldData.players[0] = new PlayerDataConn(name, PlayerType.infiltre);
    		out.println("Server : player " + name + " registered");
    		return true;
    	}else if(worldData.players[1] == null) {
    		if(worldData.players[0].type == PlayerType.infiltre)
    			worldData.players[1] = new PlayerDataConn(name, PlayerType.observateur);
    		else
    			worldData.players[1] = new PlayerDataConn(name, PlayerType.infiltre);
    		out.println("Server : player " + name + " registered");
    		return true;
    	}
    	else {
    		out.println("Server : player " + name + " lobby full");
    		return false;
    	}
    }
    
    public void setPlayerAddressData(PlayerType playerType, InetAddress address, int port) {
    	if(playerType == PlayerType.infiltre) {
    		addresses[0] = address;
    		ports[0] = port;
    	}else {
    		addresses[1] = address;
    		ports[1] = port;
    	}
    }
    
    public WorldData getWorldData() {
    	return worldData;
    }
    
    public void disconnect(String clientName) {
		out.println("Server : " + clientName + " disconnected");
		if(worldData.players[0] != null && worldData.players[0].name.equals(clientName)) {
			
			worldData.players[0] = null;
			addresses[0] = null;
		}
		if(worldData.players[1] != null && worldData.players[1].name.equals(clientName)) {
			
			worldData.players[1] = null; 
			addresses[1] = null;
		}
		
		resetGame();
	}
    
    public void close() {
    	running = false;
    	udpServer.stopServer();
    	tcpServer.stopServer();
    }
	
	// ----------------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
		new Server();
	}
	
}


