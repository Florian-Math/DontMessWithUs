package Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import Engine.Game;
import Engine.Vector2;
import Network.data.ObjectData;
import Objects.PlayerType;
import UI.MainMenu;

public class Client {

	private static Client current;
	
    private DatagramSocket clientDataSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
	private InetAddress serverAddress; // server address
	
	private String playerName;
	private PlayerType playerType;
    
    private Client(InetAddress address) throws IOException {
    	this.serverAddress = address;
    	
    	objects = new HashMap<Long, ObjectData>();
    	
    	clientSocket = new Socket();
    	clientSocket.connect(new InetSocketAddress(address, 1030), 3000);
    	output = new ObjectOutputStream(clientSocket.getOutputStream());
    	input = new ObjectInputStream(clientSocket.getInputStream());
    	
    	clientDataSocket = new DatagramSocket();
    	clientDataSocket.setSoTimeout(3000);
    }
    
    private HashMap<Long, ObjectData> objects;
    
    /**
     * Update stocked data
     * @param objData
     */
    public void updateData(ObjectData objData) {
    	if(objects.containsKey(objData.getObjectID())) objects.replace(objData.getObjectID(), objData);
    	else objects.put(objData.getObjectID(), objData);
    }
    
    
    //------------------------------------
    
    /**
     * Demande au serveur d'envoyer les données du jeu à un interval régulié
     * @param playerType
     */
    public boolean askForConstantWorldDataUdp(PlayerType playerType) {
    	try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bStream);
			
	    	outStream.writeObject(new ServerRequest(ServerRequestType.getData, playerType));
			
			byte[] buf = bStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
			clientDataSocket.send(packet);
			
			WorldData data = getWorldDataUdp();
			if(data == null) return false;
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public WorldData askForWorldDataUdp() {
    	try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bStream);
			
	    	outStream.writeObject(new ServerRequest(ServerRequestType.getData));
			
			byte[] buf = bStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
			clientDataSocket.send(packet);
			
			return getWorldDataUdp();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
	
    public WorldData getWorldDataUdp() {
    	try {
    		byte[] buf = new byte[1024];
    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
			clientDataSocket.receive(packet);
	        ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
	        
	        return (WorldData) inStream.readObject();
			
		} catch (SocketTimeoutException e) {
			Game.getCurrent().stop();
			Game.getCurrent().changeMenu(new MainMenu());
			// stop current thread
			System.exit(0); // temp
			WorldData.stopUpdater();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) { 
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Send stocked data
     */
    public void sendWorldData() {
    	try {
    		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bStream);
			
			outStream.writeObject(new ServerRequest(ServerRequestType.setData, objects));
			
			byte[] buf = bStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
			clientDataSocket.send(packet);
			
			objects.clear();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	//---- TCP
	
	public WorldData sendTcpRequest(ServerRequest request) {
			try {
				output.writeObject(request);
				//System.out.println("send : " + request);
				
				return (WorldData)input.readObject();
				
			} catch (IOException e) { 
				return null;
			}catch (ClassNotFoundException e) {
				return null;
			}
    }
	
	public WorldData registerPlayer(String name) {
		try {
			output.writeObject(new ServerRequest(ServerRequestType.register, new PlayerDataConn(name, (PlayerType)null)));
			WorldData data = (WorldData)input.readObject();
			this.playerName = name;
			this.playerType = data.getPlayer(name).type;
			return data;
			
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public WorldData getWorldDataTcp() {
		try {
			output.writeObject(new ServerRequest(ServerRequestType.getData));
			
			return (WorldData)input.readObject();
			
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public WorldData stopGame() {
		return sendTcpRequest(new ServerRequest(ServerRequestType.endGame));
	}
	
	public WorldData launchGame(int level) {
		return sendTcpRequest(new ServerRequest(ServerRequestType.launchGame, level));
	}
	
	public WorldData changeLevel(int level) {
		return sendTcpRequest(new ServerRequest(ServerRequestType.changeLevel, level));
	}
	
	public static boolean connect(InetAddress address) {
		try {
			current = new Client(address);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public PlayerType getPlayerType() {
		return this.playerType;
	}
	
	public static Client getCurrent() {
		if(current != null) return current;
		return null;
	}
	
}
