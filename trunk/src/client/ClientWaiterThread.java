package client;

import game.SpawnTieFighter;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.MyConnection;

public class ClientWaiterThread extends Thread {
	private Socket socket;
	private MyConnection conn;
	private Pattern spawnMsgPattern;
	private MyClient client;

	public ClientWaiterThread(Socket socket, MyConnection conn,
			MyClient myClient) throws IOException {
		this.socket = socket;
		this.conn = conn;
		client = myClient;
		spawnMsgPattern = Pattern.compile("SPAWN (\\d+)");
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			while (true) {
				String str = conn.getMessage();
				Matcher m = spawnMsgPattern.matcher(str);
				System.out.println(str);
				if (m.matches()) {
					int n = Integer.parseInt(m.group(1));
					if (client.app != null && client.app.initialized)
						SpawnTieFighter.spawnRandom(n, client.app);
				}
			}
		} catch (Exception e) {
			System.err.println("ClientThread: Error happened!");
			e.printStackTrace();
		}
	}
}
