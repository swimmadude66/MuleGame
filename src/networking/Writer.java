package networking;

/*
Custom Class by Preston Turner for reading and writing information to a stream.

	Methods:
		setStreams(out,in) // Must be called before anything else
		sendmessage()
		writebyte(int)
		writeshort(int)
		writeushort(int)
		writestring(String)
		readbyte()
		readshort()
		readushort()
		readstring()

*/

import java.io.*;

public class Writer{
	
	private BufferedInputStream in;
	private BufferedOutputStream out;
	
	public static final int MSG_SERVERINFO = 1;
	public static final int MSG_PLAYER = 2;
	public static final int MSG_JOIN = 3;
	public static final int MSG_NEWGAME = 4;
	public static final int MSG_BEGINSETUP = 5;
	public static final int MSG_NOGAMES = 6;
	public static final int MSG_PRESS = 7;
	public static final int MSG_RELEASE = 8;
	public static final int MSG_PLOTSALE = 9;
	public static final int MSG_BEGIN = 10;
	public static final int MSG_RANDOMEVENTS = 11;
	public static final int MSG_FINISHTURN = 12;
	public static final int MSG_READY = 13;
	public static final int MSG_GAMEEVENTS = 14;
	public static final int MSG_BUYPLOT = 15;
	
	public Writer(){}

	/* MUST be called before any other methods!! */
	public void setStreams(BufferedOutputStream out, BufferedInputStream in){
		this.in = in;
		this.out = out;
	}
	
	/* Clears the buffer (flushes - not needed) */
	public void clearbuffer(){
		try{
			out.flush();
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}
	}
	
	/* Sends the buffered message */
	public void sendmessage(){
		try{
			out.flush();
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}

	}

	public void sendmessage(boolean b) throws IOException{
		if (b)
			out.flush();
		else
			sendmessage();
	}
	
	/* Reads a 1-byte integer (0-255) */
	public int readbyte(){
		try {
			int num = 0;
			if (in.available() > 0)
				num = in.read();
			else
				System.err.println("Invalid reciept");
			return num;
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}
		return 0;
	}
	
	/* Writes a 1-byte integer (0-255) */
	public void writebyte(int num){
		try{
			out.write(num);
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}
	}
	
	/* Reads a 2-byte signed integer */
	public int readshort(){
		try{
			int num = 0;
			if (in.available() >= 2){
				num = (readbyte()*255);
				num += readbyte();
				num -= (int)(65536/2);
			}
			else{
				System.err.println("Invalid reciept");
			}
			return num;
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}
		return 0;
	}
	
	/* Writes a 2-byte signed integer */
	public void writeshort(int num){
		try {
			num += (int)(65536/2);
			writebyte((int)(num/255));
			writebyte(num%255);
		} catch (Exception e){
			System.err.println("Failed to clear buffer.");
		}
	}
	
	
	
	/* Reads a 2-byte UNSIGNED integer */
	public int readushort() throws IOException{
		int num = 0;
		if (in.available() >= 2){
			num = (in.read()*255);
			num += in.read();
		}
		else{
			System.err.println("Invalid reciept");
		}
		return num;
	}
	
	/* Writes a 2-byte UNSIGNED integer */
	public void writeushort(int num) throws IOException{
		out.write((int)(num/255));
		out.write(num%255);
	}
	
	/* Reads a 4-byte UNSIGNED integer */
	public int readuint() throws IOException{
		int num = 0;
		if (in.available() >= 4){
			num = (in.read()*255);
			num = (in.read()*255);
			num = (in.read()*255);
			num += in.read();
		}
		else{
			System.err.println("Invalid reciept");
		}
		return num;
	}
	
	/* Writes a 4-byte UNSIGNED integer */
	public void writeuint(int num) throws IOException{
		out.write((int)(num/=255));
		out.write((int)(num/=255));
		out.write((int)(num/=255));
		out.write(num%255);
	}
	
	/* Reads a String */
	public String readstring() throws IOException{
		String string = "";
		int len = readbyte();
		for (int i = 0; i<len; i++){
			int b = readbyte();
			string += ""+Character.toChars(b)[0];
		}
		return string;
	}
	
	/* Writes a String */
	public void writestring(String string) throws IOException{
		writebyte(string.length());
		byte[] array = string.getBytes();
		for (int i : array){
			writebyte(i);
		}
	}
	
	/* Reads a boolean */
	public boolean readboolean(){
		try{
			if (in.available() > 0)
				return (readbyte() == 1);
			else{
				System.err.println("Invalid reciept");
				return false;
			}
		} catch (Exception e) {
			System.err.println("erroooor");
		}
		return false;
	}
	
	/* Writes a boolean */
	public void writeboolean(boolean bool){
		try{
			if (bool)
				writebyte(1);
			else
				writebyte(0);
		} catch (Exception e) {
			System.err.println("failure");
		}
	}
	
	/* Returns the input Stream */
	public InputStream getInputStream(){
		return in;
	}
	
	/* Returns the output Stream */
	public OutputStream getOutputStream(){
		return out;
	}
}
