package client;

import game.GameInterface;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import util.Fighter;
import util.MyConnection;
import util.SerializableSpatial;

import com.jme.app.AbstractGame.ConfigShowMode;

public class OldMyClient {

	static GameInterface app = null;
	
	public static void main(String[] args) {		
		Scanner sc = new Scanner(System.in);
		
		try{
			System.out.print("Host IP [127.0.0.1]: ");
			String line = sc.nextLine();
			String servIP = line.equals("")?"127.0.0.1":line;
			
			System.out.print("Host port [9999]: ");
			line = sc.nextLine().trim();
			int servPort = line.equals("")?9999:Integer.parseInt(line);
			
			System.out.println("Client: Connecting to server " + servIP + ":" + servPort);
			Socket socket = new Socket(servIP, servPort);
			System.out.println("Client: Connected!");
			
			new ClientThread(socket).start();
		}
		catch(Exception e){
			System.err.println("MyClient: Error happened!");
			e.printStackTrace();
			System.exit(1);
		}
		
		new ClientGame().start();
	}
	
	static class ClientGame extends Thread{
		public void run(){
			app = new GameInterface();
			app.setConfigShowMode(ConfigShowMode.AlwaysShow);
			app.start();
		}
	}
	
	static class ClientThread extends Thread{
		private Socket socket;
		private MyConnection conn;
		
		public ClientThread(Socket socket) throws IOException{
			this.socket = socket;
			this.conn = new MyConnection(socket);
		}
		
		@SuppressWarnings("unchecked")
		public void run(){
			try{
				int counter = 0;
				while(true){
					if(counter == 0){
						System.out.println("app = "+app);
						counter++;
					}
					if(app != null){
						if(counter == 1){
							System.out.println("got fighters");
							counter++;
						}
						LinkedList<Fighter> fighters = (LinkedList<Fighter>)conn.getObject();
						app.setFighters(fighters);
						
						LinkedList<SerializableSpatial> lasers = (LinkedList<SerializableSpatial>)conn.getObject();
						app.setLasers(lasers);
					}
				}
			}
			catch(Exception e){
				System.err.println("ClientThread: Error happened!");
				e.printStackTrace();
			}
		}
	}

}
