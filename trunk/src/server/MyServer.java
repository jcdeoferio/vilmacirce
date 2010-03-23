package server;

import game.GameInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
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
	HashMap<String, MyConnection> loggedInUsersConns;
	HashMap<String, Integer> loggedInUsersScores;
	int port;
	boolean done;

	public MyServer(int port) {
		this.port = port;
	}

	public void start() {
		// new ServerGame().start();
		try {
			loggedInUsersConns = new HashMap<String, MyConnection>();
			loggedInUsersScores = new HashMap<String, Integer>();

			ServerSocket ssocket = new ServerSocket(port);
			while (true) {
				System.out.println("Server: waiting for connections...");
				Socket socket = ssocket.accept();
				MyConnection conn = new MyConnection(socket);
				String clientip = socket.getInetAddress().toString() + ":"
						+ socket.getPort();

				System.out.println("MyServer: " + clientip + " connected!");

				loggedInUsersConns.put(clientip, conn);
				loggedInUsersScores.put(clientip, 0);

				new ServerWaiterThread(socket, conn, clientip).start();
				new ServerSenderThread(socket, conn, clientip).start();
			}
		} catch (Exception e) {
			System.err.println("MyServer: Server error happened!");
			e.printStackTrace();
		}
	}

	// class ServerGame extends Thread {
	// public void run() {
	// app = new GameInterface();
	// app.setConfigShowMode(ConfigShowMode.AlwaysShow);
	// app.start();
	// }
	// }

	class ServerSenderThread extends Thread {
		private Socket socket;
		private MyConnection conn;
		private String clientip;
		private Random rand;

		public ServerSenderThread(Socket socket, MyConnection conn,
				String clientip) throws IOException {
			this.socket = socket;
			this.conn = conn;
			this.clientip = clientip;
			rand = new Random();
		}

		public void run() {
			while (!done) {
				conn.sendMessage("SPAWN " + (rand.nextInt(5) + 5));
				try {
					Thread.sleep(10000 + Math.abs(rand.nextLong()) % 20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	class ServerWaiterThread extends Thread {
		private Socket socket;
		private MyConnection conn;
		private String clientip;

		public ServerWaiterThread(Socket socket, MyConnection conn,
				String clientip) throws IOException {
			this.socket = socket;
			this.conn = conn;
			this.clientip = clientip;
		}

		public void run() {
			while (true) {
				String msg = conn.getMessage();
				if (msg.startsWith("DONE")) { // client died/quit game FORMAT:
					// DONE username score time
					done = true;
					System.out.println(msg);
					String line[] = msg.split(" ");
					String username = line[1];
					int score = Integer.parseInt(line[2]);
					long time = Long.parseLong(line[3]);
					User thisUser = new User(username, score, time);
					// TODO save to file if top 10
					System.out.println(msg);
					int rank = -1;
					try {
						Scanner in = new Scanner(new File("scores.txt"));
						LinkedList<User> users = new LinkedList<User>();
						users.add(thisUser);
						while (in.hasNextLine()) {
							String ln[] = in.nextLine().split(" ");
							users.add(new User(ln[0], Integer.parseInt(ln[1]),
									Long.parseLong(ln[2])));
						}
						Collections.sort(users);
						in.close();
						PrintStream out = new PrintStream(new File("scores.txt"));
						for(int i=0;i<10 && i<users.size();i++){
							User user = users.get(i);
							if(user.equals(thisUser))
								rank = i+1;
							out.println(user);
						}
						out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if(rank == -1)
						conn.sendMessage("Sorry, you didn't get to the top 10");
					else
						conn.sendMessage("Congratulations! You got in the top ten! Your rank: " + rank);
					loggedInUsersConns.remove(clientip);
					loggedInUsersScores.remove(clientip);
					break;
				} else { // Score update from client every time a tie fighter
					// dies? FORMAT: score
					int thisScore = Integer.parseInt(msg);
					loggedInUsersScores.put(clientip, thisScore);
					for (Entry<String, MyConnection> entry : loggedInUsersConns
							.entrySet()) {
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
