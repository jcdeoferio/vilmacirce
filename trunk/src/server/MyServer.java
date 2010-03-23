package server;

import game.GameInterface;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import util.MyConnection;
import util.User;

import com.jme.app.AbstractGame.ConfigShowMode;

public class MyServer {

	public static void main(String[] args) {
		new MyServer(9999).start();
	}

	static GameInterface app = null;
	HashMap<String, MyConnection> loggedInUsersSockets;
	HashMap<String, Integer> loggedInUsersScores;
	int port;

	public MyServer(int port) {
		this.port = port;
	}

	public void start() {
		// new ServerGame().start();
		try {
			loggedInUsersSockets = new HashMap<String, MyConnection>();
			loggedInUsersScores = new HashMap<String, Integer>();

			ServerSocket ssocket = new ServerSocket(port);
			while (true) {
				System.out.println("Server: waiting for connections...");
				Socket socket = ssocket.accept();
				new ServerThread(socket).start();
			}
		} catch (Exception e) {
			System.err.println("MyServer: Server error happened!");
			e.printStackTrace();
		}
	}

	class ServerGame extends Thread {
		public void run() {
			app = new GameInterface();
			app.setConfigShowMode(ConfigShowMode.AlwaysShow);
			app.start();
		}
	}

	class ServerThread extends Thread {
		private Socket socket;
		private MyConnection conn;

		public ServerThread(Socket socket) throws IOException {
			this.socket = socket;
			this.conn = new MyConnection(socket);
		}

		public void run() {
			String clientip = socket.getInetAddress().toString() + ":"
					+ socket.getPort();
			System.out.println(getName() + ": " + clientip + " connected!");

			loggedInUsersSockets.put(clientip, conn);
			loggedInUsersScores.put(clientip, 0);
			
			while (true) {
				String msg = conn.getMessage();
				if (msg.startsWith("DONE")) { //client died/quit game FORMAT: DONE username score time
					String line[] = msg.split(" ");
					String username = line[0];
					String score = line[1];
					String time = line[2];
					// TODO save to file if top 10
				} else { //Score update from client every time a tie fighter dies? FORMAT: score 
					loggedInUsersScores.put(clientip, Integer.parseInt(msg));
					for (Entry<String, MyConnection> entry : loggedInUsersSockets
							.entrySet()) {
						int thisScore = loggedInUsersScores.get(clientip);
						int otherScore = loggedInUsersScores
								.get(entry.getKey());
						if (thisScore > otherScore
								&& thisScore - otherScore >= 10) {
							entry.getValue().sendMessage(
									"SPAWN " + (thisScore - otherScore) / 10);
						}
					}
				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
