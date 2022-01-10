package Network;

import java.io.Serializable;
import java.util.HashMap;

import Engine.Game;
import Engine.GameObject;
import Engine.TimeSaver;
import Network.data.ObjectData;
import Objects.PlayerType;

public class WorldData implements Serializable {
	
	private static WorldData current;
	private static WorldData last;
	
	private static DataUpdater updater;
	
	public boolean isLaunched = false;
	
	public int level = 1;
	public long lastUpdateTime;
	public int timel;
	public int timet;
	public PlayerDataConn players[] = new PlayerDataConn[2];
	
	public HashMap<Long, ObjectData> objects = new HashMap<Long, ObjectData>();
	
	
	public PlayerDataConn getPlayer(String name) {
		if(players[0] != null && players[0].name.equals(name)) return players[0];
		if(players[1] != null && players[1].name.equals(name)) return players[1];
		return null;
	}
	
	public PlayerDataConn getPlayer(PlayerType type) {
		if(players[0] != null && players[0].type == type) return players[0];
		if(players[1] != null && players[1].type == type) return players[1];
		return null;
	}
	
	public void stopGame() {
		isLaunched = false;
	}
	
	public void setLastUpdate() {
		lastUpdateTime = System.nanoTime();
	}
	
	public ObjectData getObjectData(GameObject object) {
		return objects.get(object.getID());
	}
	
	/**
	 * Modifie les données selon les données donné en parametre
	 * @param data
	 */
	public void updateObjectData(HashMap<Long, ObjectData> data) {
		for (Long key : data.keySet()) {
			if(!objects.containsKey(key)) objects.put(key, data.get(key));
			else objects.replace(key, data.get(key));
		}
	}
	
	/**
	 * Modifie les données selon la donnée donné en parametre
	 * @param data
	 */
	public void updateObjectData(ObjectData data) {
		if(!objects.containsKey(data.getObjectID())) objects.put(data.getObjectID(), data);
		else objects.replace(data.getObjectID(), data);
	}
	
	public void resetObjectsData() {
		objects.clear();
	}
	
	@Override
	public String toString() {
		return objects.toString();
	}
	
	public static WorldData getCurrent() {
		return current;
	}
	
	public static WorldData getLast() {
		return last;
	}
	
	public static void reset() {
		current = null;
		last = null;
	}
	
	public static void runUpdater() {
		if(updater != null) return;
		updater = new DataUpdater();
		updater.start();
	}
	
	public static void stopUpdater() {
		if(updater == null) return;
		updater.stopThread();
		updater = null;
	}
	
	private static class DataUpdater extends Thread {
		
		private boolean running;
		
		@Override
		public synchronized void start() {
			running = true;
			super.start();
		}
		
		@Override
		public void run() {
			while(running) {
				WorldData data = Client.getCurrent().getWorldDataUdp();
				if(data == null) {
					System.out.println("SKIP");
					continue;
				}
				Game.getCurrent().serverTick();
				last = current;
				current = data;
			}
		}
		
		public void stopThread() {
			running = false;
		}
	}
	
	
}
