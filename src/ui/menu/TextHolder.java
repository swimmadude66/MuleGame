package ui.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import models.GameObject;
import ui.Sprite;

/**
 * 
 * @author The Moose Express
 *
 * Basically an OptionHolder for Strings. Does not use an option pool.
 * 
 */
public class TextHolder extends GameObject{

	private int num, min, max;
	private boolean wrap;
	private String[] options;
	
	public TextHolder(int x, int y, String[] options, Sprite sprite){
		super(x, y, 64, 32, sprite);
		
		this.options = options;
		if (options == null)
			options = new String[] {""};
		min = 0;
		max = options.length-1;
		this.num = 0;
		wrap = false;
	}
	
	public TextHolder setWrap(boolean wrap){
		this.wrap = wrap;
		return this;
	}
	
	public void increment(){
		if (++num > max)
			num = wrap ? min : max;
	}
	
	public void decrement(){
		if (--num < min)
			num = wrap ? max : min;
	}
	
	public String getText(){
		return options[num];
	}
	
	public int getNumber(){
		return num;
	}
	
	@Override
	public void paint(Graphics g){
		FontMetrics fm = g.getFontMetrics();
		if (getVisible()){
			super.paint(g);
			g.setColor(Color.GRAY);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.DARK_GRAY);
			g.drawRect(getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.drawString(options[num], getX()+getWidth()/2-fm.stringWidth(options[num])/2, getY()+getHeight()/2+4);
		}
	}
}
