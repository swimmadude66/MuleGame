package models;

import java.awt.Color;
import java.awt.Graphics;

import ui.Game;
import ui.Sprite;

public class Mule extends GameObject{
	
	private Outfit outfit;
	
	private boolean runAway;
	
	public Mule(int x, int y){
		super(x,y,new Sprite("/Resources/Mule_front_50.png", true));
		runAway = false;
	}
	
	public void setOutfit(Outfit outfit){
		this.outfit = outfit;
	}
	
	public int getOutfitID(){
		if (outfit == null)
			return 0;
		else
			return outfit.id;
	}
	
	public Outfit getOutfit(){
		return outfit;
	}
	
	public void runAway(){
		runAway = true;
	}
	
	@Override
	public void update(float deltaTime){
		if (runAway == true){
			changeX(-4);
			if (getRight() < 0)
				Game.s_instance.removeObject(this);
		}
	}
	
	public void paint(Graphics g){
		if (getVisible()){
			super.paint(g);
			g.setColor(Color.WHITE);
			Tile.drawOutfit(g,outfit,getCenterX(),getY());
		}
	}

}
