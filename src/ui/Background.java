package ui;

import java.awt.Graphics;
import java.util.ArrayList;

public class Background{

	private ArrayList<Sprite> backgrounds;
	private int currentBackground;
	
	private int width, height;
	
	public Background(int width, int height){
		this.width = width;
		this.height = height;
		backgrounds = new ArrayList<Sprite>();
		currentBackground = 0;
	}
	
	public void addBackground(Sprite sprite){
		backgrounds.add(sprite);
	}
	
	public void setBackground(int background){
		this.currentBackground = background;
		if (currentBackground >= backgrounds.size())
			currentBackground = 0;
	}
	
	public void paint(Graphics g){
		if (currentBackground < backgrounds.size()){
			backgrounds.get(currentBackground).drawSprite(g, 0, 0, width, height);
		}
	}
	
}
