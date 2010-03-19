import com.jme.app.AbstractGame.ConfigShowMode;


public class MyClient {

	static HelloWorld app = null;
	
	public static void main(String[] args) {
		new ClientGame().start();
		
		
	}
	
	static class ClientGame extends Thread{
		public void run(){
			app = new HelloWorld();
			app.setConfigShowMode(ConfigShowMode.AlwaysShow);
			app.start();
		}
	}
	
	static class ClientThread extends Thread{
		
	}

}
