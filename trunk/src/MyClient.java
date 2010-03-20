import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.scene.Node;


public class MyClient {

	static HelloWorld app = null;
	
	public static void main(String[] args) {
		new ClientGame().start();
		
		Scanner sc = new Scanner(System.in);
		
		try{
			System.out.print("Host IP [127.0.0.1]: ");
			String line = sc.nextLine();
			String servIP = line.equals("")?"127.0.0.1":line;
			
			System.out.print("Host port [8888]: ");
			line = sc.nextLine().trim();
			int servPort = line.equals("")?8888:Integer.parseInt(line);
			
			System.out.println("Client: Connecting to server " + servIP + ":" + servPort);
			Socket socket = new Socket(servIP, servPort);
			System.out.println("Client: Connected!");
			
			new ClientThread(socket).start();
		}
		catch(Exception e){
			System.err.println("MyClient: Error happened!");
			e.printStackTrace();
		}
	}
	
	static class ClientGame extends Thread{
		public void run(){
			app = new HelloWorld();
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
				while(true){
					if(app != null){
						LinkedList<Fighter> fighters = (LinkedList<Fighter>)conn.getObject();
						app.setFighters(fighters);
					}
					
					Thread.sleep(100);
				}
			}
			catch(Exception e){
				System.err.println("ClientThread: Error happened!");
				e.printStackTrace();
			}
		}
	}

}
