package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;

public class MyConnection {
	
	protected Socket socket;
	protected ObjectOutputStream145 oout;
	protected ObjectInputStream oin;
	protected JMEExporter jmee;
	protected JMEImporter jmei;
	
	protected PrintWriter sout;
	protected BufferedReader sin;
	
	public MyConnection(Socket socket) throws IOException {
		this.socket = socket;
		
		this.jmee = new BinaryExporter();
		this.jmei = new BinaryImporter();
		
		this.oout = new ObjectOutputStream145(socket.getOutputStream());
		this.oin = new ObjectInputStream(socket.getInputStream());
		
		OutputStream ostream = socket.getOutputStream();
		OutputStreamWriter owriter = new OutputStreamWriter(ostream);
		this.sout = new PrintWriter(owriter);
		
		InputStream istream = socket.getInputStream();
		InputStreamReader ireader = new InputStreamReader(istream);
		this.sin = new BufferedReader(ireader);

		System.out.println("end of constructor");
	}
	
	public boolean sendObject(Serializable obj){
//		System.out.println("Sending:"+obj);

		try {
//			jmee.save(sav, oout);
			oout.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
			return(false);
		}
//		System.out.println("Done sending");
		return(true);
	}
	
	public Object getObject() throws IOException{
//		return(jmei.load(oin));
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
