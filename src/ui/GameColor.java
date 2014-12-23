package ui;

import java.awt.Color;

public class GameColor extends Color{

	// Trust me on this one
	private static final long serialVersionUID = -7570397863602590433L;
	
	public static final GameColor RED = new GameColor(1,0,0,"Red");
	public static final GameColor GREEN = new GameColor(0,.8f,0,"Green");
	public static final GameColor BLUE = new GameColor(.2f,.2f,1,"Blue");
	public static final GameColor YELLOW = new GameColor(.7f,.7f,0,"Yellow");
	
	private String name;
	
	public GameColor(Color c, String name){
		this(c.getRed(), c.getGreen(), c.getBlue(), name);
	}
	
	public GameColor(float r, float g, float b, String name){
		super(r,g,b);
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
}
