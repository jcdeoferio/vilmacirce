package client;

import com.jme.app.AbstractGame.ConfigShowMode;

public class ClientGame extends Thread {
	MyClient client;
	public ClientGame(MyClient client) {
		this.client = client;
	}

	public void run() {
		client.app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		client.app.start();
	}
}