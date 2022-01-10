package Network;

import Network.data.ObjectData;

/**
 * Interface designant un objet pouvant �tre envoy� au serveur
 */
public interface Sendable {

	public void updateDataFromServer(ObjectData lastData, ObjectData data, double delta);
	public ObjectData getData();
	
}
