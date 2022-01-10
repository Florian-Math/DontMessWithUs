package Network;

import java.io.Serializable;

public enum ServerRequestType implements Serializable {
	getData,
	launchGame,
	register,
	getStatut,
	disconnect,
	setData, 
	endGame,
	setTimeData,
	changeLevel

}
