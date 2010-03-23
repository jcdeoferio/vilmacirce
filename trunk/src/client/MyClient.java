package client;

import game.GameInterface;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import util.MyConnection;

public class MyClient {

	public GameInterface app;
	private Socket socket;
	private MyConnection conn;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Scanner sc = new Scanner(System.in);

		System.out.print("Host IP [127.0.0.1]: ");
		String line = sc.nextLine();
		String servIP = line.equals("") ? "127.0.0.1" : line;

		System.out.print("Host port [9999]: ");
		line = sc.nextLine().trim();
		int servPort = line.equals("") ? 9999 : Integer.parseInt(line);

		new MyClient(servIP, servPort).start();
	}

	public MyClient(String host, int port) {
		System.out.println("Client: Connecting to server " + host + ":" + port);
		try {
			socket = new Socket(host, port);
			conn = new MyConnection(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Client: Connected!");

		app = new GameInterface();
	}

	public void start() {
		try {
			new ClientWaiterThread(socket, conn, this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new ClientGame(this).start();
	}

	public void playerFinish(String username, int score, long time) {
		conn.sendMessage("DONE "+username + " " + score + " " + time);
	}
}
