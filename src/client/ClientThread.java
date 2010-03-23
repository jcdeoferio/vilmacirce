package client;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.MyConnection;

public class ClientThread extends Thread {
	private Socket socket;
	private MyConnection conn;
	private Pattern spawnMsgPattern;

	public ClientThread(Socket socket, MyConnection conn) throws IOException {
		this.socket = socket;
		this.conn = conn;
		spawnMsgPattern = Pattern.compile("SPAWN (\\d+)");
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			while (true) {
				String str = conn.getMessage();
				Matcher m = spawnMsgPattern.matcher(str);
				if (m.matches()) {
					System.out.println(m.group(1));
				}
			}
		} catch (Exception e) {
			System.err.println("ClientThread: Error happened!");
			e.printStackTrace();
		}
	}
}
