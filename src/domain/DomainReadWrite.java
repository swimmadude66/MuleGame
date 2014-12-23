package domain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class DomainReadWrite {
	
	Writer writer = null;
	
	
	public void saveGame(String state){
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("MuleSave.txt"), "utf-8"));
		    writer.write(state);
		} catch (IOException ex){
		  System.out.println("GAME NOT SAVED!");
		} finally {
		   try {writer.flush();
			   writer.close();
			   System.out.println("GAME SAVED!");
			   } 
		   catch (Exception ex) {} 
		}
	}
	
	public String loadGame(){
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader("MuleSave.txt"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append('\n');
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    } 
		catch (Exception ex){
			System.out.println("SAVE CANNOT BE FOUND");
		}
		
		finally {
			try{
				if(br!=null)
					br.close();
			}
			catch(Exception ex){
				
			}
	    }
		return everything;
	}
}
