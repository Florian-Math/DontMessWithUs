package Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Network.data.ObjectData;
import Objects.PlayerType;

public class UdpServer extends Thread {

	private Server server;
	private DatagramSocket serverSocket;
	private boolean running;
	
	public UdpServer(Server server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			//long lastTime = System.currentTimeMillis();
			
			serverSocket = new DatagramSocket(4445);
			
			running = true;
			
	        while (running) {
	        	byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				serverSocket.receive(packet);
		        ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
		        
		        ServerRequest request = (ServerRequest) inStream.readObject();
	        	
	        	switch (request.requestType) {
				case getData:
					if(request.data != null)
						server.setPlayerAddressData((PlayerType)request.data, packet.getAddress(), packet.getPort());
					else
						sendData(packet.getAddress(), packet.getPort(), server.getWorldData());
					break;
				case setData:
					server.getWorldData().updateObjectData((HashMap<Long, ObjectData>)request.data);
					server.getWorldData().setLastUpdate();
					break;
				default:
					break;
				}
	        	/*
	        	if(System.currentTimeMillis() - lastTime > 10000) {
	        		server.showPlayersData();
	        		lastTime = System.currentTimeMillis();
	        	}*/
	            
	        }
	        serverSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void sendData(InetAddress address, int port, WorldData data) {
		try {
    		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bStream);
			
			outStream.writeObject(data);
			
			byte[] buf = bStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			serverSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void stopServer() {
		running = false;
	}
	
}
