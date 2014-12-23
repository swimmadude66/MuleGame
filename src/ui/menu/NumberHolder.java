package ui.menu;

import java.awt.Color;
import java.awt.Graphics;

import models.GameObject;
import ui.Sprite;

/**
 * 
 * @author The Moose Express
 *
 * Basically an OptionHolder for Integers. Does not use an option pool.
 * 
 */
public class NumberHolder extends GameObject{

	private int min, max, num;
	
	public NumberHolder(int x, int y, int min, int max, Sprite sprite){
		super(x, y, 32, 32, sprite);
		
		this.min = min;
		this.max = max;
		this.num = min;
	}
	
	public void increment(){
		if (++num > max)
			num = max;
	}
	
	public void decrement(){
		if (--num < min)
			num = min;
	}
	
	public int getNumber(){
		return num;
	}
	
	@Override
	public void paint(Graphics g){
		if (getVisible()){
			super.paint(g);
			g.setColor(Color.GRAY);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.DARK_GRAY);
			g.drawRect(getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.drawString(""+num, getX()+getWidth()/2-4, getY()+getHeight()/2+4);
		}
	}
}
