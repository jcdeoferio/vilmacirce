import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jme.app.AbstractGame.ConfigShowMode;

public class MyServer {
	
	static HelloWorld app = null;

	public static void main(String[] args) {
		new ServerGame().start();
		
		try{
			ServerSocket ssocket = new ServerSocket(8888);
			while(true){
				System.out.println("Server: waiting for connections...");
				Socket socket = ssocket.accept();
				new ServerThread(socket).start();
			}
		}
		catch(Exception e){
			System.err.println("MyServer: Server error happened!");
			e.printStackTrace();
		}
	}
	
	static class ServerGame extends Thread{
		public void run(){
			app = new HelloWorld();
			app.setConfigShowMode(ConfigShowMode.AlwaysShow);
			app.start();		
		}
	}

	static class ServerThread extends Thread{
		private Socket socket;
		private MyConnection conn;
		
		public ServerThread(Socket socket) throws IOException{
			this.socket = socket;
			this.conn = new MyConnection(socket);
		}
		
		public void run(){
			String clientip = socket.getInetAddress().toString();
			System.out.println(getName() + ": " + clientip + " connected!");
			
			while(true){
				
			}
		}
	}

}
