import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MyConnection {
	
	protected Socket socket;
	protected ObjectOutputStream oout;
	protected ObjectInputStream oin;
	
	protected PrintWriter sout;
	protected BufferedReader sin;
	
	public MyConnection(Socket socket) throws IOException {
		this.socket = socket;
		
		this.oout = new ObjectOutputStream(socket.getOutputStream());
		this.oin = new ObjectInputStream(socket.getInputStream());
		
		OutputStream ostream = socket.getOutputStream();
		OutputStreamWriter owriter = new OutputStreamWriter(ostream);
		this.sout = new PrintWriter(owriter);
		
		InputStream istream = socket.getInputStream();
		InputStreamReader ireader = new InputStreamReader(istream);
		this.sin = new BufferedReader(ireader);

	}
	
	public boolean sendObject(String obj){
		try {
			oout.writeObject(obj);
			oout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return(true);
	}
	
	public Object getObject() throws IOException{
		try {
			
			return(oin.readObject());
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			return(null);
			
		}
	}
	
	public boolean sendMessage(String msg){
		sout.println(msg);
		sout.flush();
		
		return(true);
	}
	
	public String getMessage() throws IOException{
		return(sin.readLine());
	}
	
}
