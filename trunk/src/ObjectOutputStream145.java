import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public class ObjectOutputStream145 extends ObjectOutputStream{

	public ObjectOutputStream145(OutputStream outputStream) throws IOException {
		super(outputStream);
	}
	
	public void writeObject145(Object obj) throws IOException{
		writeObject(obj);
		flush();
	}

}
