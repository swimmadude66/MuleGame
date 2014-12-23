package networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ui.Game;



// Class for adding online communications.
public abstract class NetworkController{

	public static Writer writer;
	
	public static final String hostname = "computer1forus.no-ip.org";
	public static final int port = 4546;
	
	private static boolean connected;
	
	private static Socket socket;
	
	public static Writer initialize(){
		connected = false;
		return writer = new Writer();
	}
	
	public static boolean connect(){
		return connect(hostname, port);
	}
	
	public static boolean connect(String hostname, int port){
		if (connected){
			System.err.println("Attempting to connect while already connected.");
			return true;
		}
		if (writer == null)
			if (initialize() == null)
				return false;
        try {
        	System.out.println("Connecting...");
            socket = new Socket(hostname, port);
            System.out.println("\nConnection successful thru "+hostname+":"+port);
	        socket.setTcpNoDelay(true);
            System.out.println("TCP No Delay: "+socket.getTcpNoDelay());
            writer.setStreams(new BufferedOutputStream(socket.getOutputStream()),
            				  new BufferedInputStream(socket.getInputStream()));
            connected = true;
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+hostname);
        } catch (IOException e) {
            System.err.println("Could not connect to '"+hostname+"'");
        }
        return false;
	}
	
	public static void listen(){
		try{
			while (writer.getInputStream().available() > 0){
			//	System.out.println("In! Buffer size: "+writer.getInputStream().available());
				userDefined(writer);
			}
		} catch (IOException ioe){
			System.err.println("Connection lost.");
			System.exit(1);
		}
	}
	
	public static void update(float deltaTime){
		if (writer != null)
			listen();
	}
	
	public static void userDefined(Writer writer){
		try{
			Game.s_instance.userDefined(writer);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
