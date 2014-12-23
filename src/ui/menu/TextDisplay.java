package ui.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import models.GameObject;
import ui.Game;

public class TextDisplay extends GameObject{

	private int halign;
	private String text;
	
	private float dx, dy;
	
	private int timer;
	
	public TextDisplay(int x, int y){
		this(x,y,"");
	}
	
	public TextDisplay(int x, int y, String text){
		super(x,y,1,1,null);
		this.text = text;
		halign = 1;
		timer = -1;
		dx = dy = 0;
		setColor(Color.DARK_GRAY);
	}
	
	public TextDisplay setHAlign(int align){
		this.halign = align;
		return this;
	}
	
	public TextDisplay setText(String text){
		this.text = text;
		return this;
	}
	
	public void setTimer(int timer){
		this.timer = timer;
	}
	
	public void setSpeed(float dx, float dy){
		this.dx = dx;
		this.dy = dy;
	}
	
	public void update(float deltaTime){
		if (dx != 0)
			changeX(dx);
		if (dy != 0)
			changeY(dy);
		if (timer > 0)
			timer--;
		if (timer == 0)
			Game.s_instance.removeObject(this);
	}
	
	public void paint(Graphics g){
		FontMetrics fm = g.getFontMetrics();
		if (getVisible()){
			g.setColor(getColor());
			if (halign == 1)
				g.drawString(text, getX(), getY());
			else if (halign == 2)
				g.drawString(text, getX()-fm.stringWidth(text)/2, getY());
			else if (halign == 3)
				g.drawString(text, getX()-fm.stringWidth(text), getY());
		}
	}
	
}
