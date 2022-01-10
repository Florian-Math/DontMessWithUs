package Network;

import java.io.Serializable;

public class ServerRequest implements Serializable {

	public ServerRequestType requestType;
	public Serializable data;
	
	public ServerRequest(ServerRequestType requestType) {
		this.requestType = requestType;
	}
	
	public ServerRequest(ServerRequestType requestType, Serializable data) {
		this.requestType = requestType;
		this.data = data;
	}
	
}
