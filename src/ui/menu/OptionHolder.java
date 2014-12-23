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
 * This class holds options from an option pool and gives the ability to cycle through them.
 * When an item is taken from an Option Pool, it cannot be chosen again unless it is returned.
 *
 * @param <T> The type of options to be held.
 */
public class OptionHolder<T> extends GameObject{

	private int num, min, max;
	private boolean wrap;
	private OptionPool<T> optionPool;
	private boolean lockedIn;
	
	public OptionHolder(int x, int y, OptionPool<T> optionPool, Sprite sprite){
		super(x, y, 64, 32, sprite);
		
		this.optionPool = optionPool;
		min = 0;
		max = optionPool.size()-1;
		this.num = 0;
		wrap = true;
		lockedIn = false;
	}
	
	public OptionHolder<T> setOption(int i){
		this.num = i;
		return this;
	}
	
	public OptionHolder<T> setWrap(boolean wrap){
		this.wrap = wrap;
		return this;
	}
	
	public void lockIn(){
		optionPool.takeOption(num);
		lockedIn = true;
	}
	
	public void unLock(){
		optionPool.returnOption(num);
		lockedIn = false;
	}
	
	public void increment(){
		if (++num > max)
			num = wrap ? min : max;
	}
	
	public void decrement(){
		if (--num < min)
			num = wrap ? max : min;
	}
	
	public int getOptionNum(){
		return num;
	}
	
	public T getOption(){
		return optionPool.getOption(num);
	}
	
	@Override
	public void update(float deltaTime){
		if (!lockedIn){
			int tries = optionPool.size();
			while (!optionPool.optionRemains(num) && tries > 0){
				tries--;
				num++;
				if (num > max)
					num = 0;
			}
		}
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
			g.drawString(optionPool.getOption(num).toString(), getX()+getWidth()/2-fm.stringWidth(optionPool.getOption(num).toString())/2, getY()+getHeight()/2+4);
		}
	}
}
