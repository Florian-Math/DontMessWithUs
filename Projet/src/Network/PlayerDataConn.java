package Network;

import java.io.Serializable;

import Objects.PlayerType;

public class PlayerDataConn implements Serializable{
	
	public String name;
	public PlayerType type;
	
	public PlayerDataConn(String name, PlayerType type) {
		this.name = name;
		this.type = type;
	}

	public PlayerDataConn(String name) {
		this.name = name;
	}
	
}
