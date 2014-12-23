package ui.menu;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import models.GameObject;
import ui.Sprite;


public class Button extends GameObject implements MenuOption{

	private ActionListener action;
	
	private String text;
	
	private boolean selected;
	
	public Button(ActionListener action, int x, int y, Sprite sprite){
		this(action,x,y,sprite,"");
	}
	
	public Button(ActionListener action, int x, int y, String text){
		this(action,x,y,null,text);
	}
	
	private Button(ActionListener action, int x, int y, Sprite sprite, String text){
		super(x, y, 48, 48, sprite);
		if (sprite != null){
			setWidth(sprite.spriteWidth());
			setHeight(sprite.spriteHeight());
		}
		this.action = action;
		this.text = text;
		this.selected = false;
	}
	
	@Override
	public Button setSize(int width, int height){
		super.setSize(width,height);
		return this;
	}
	
	public void click(){
		action.actionPerformed(new ActionEvent(this, 0, null));
	}
	
	public void select(){
		selected = true;
	}
	
	public void deselect(){
		selected = false;
	}
	
	@Override
	public void paint(Graphics g){
		FontMetrics fm = g.getFontMetrics();
		if (getVisible()){
			if (getSprite() != null)
				super.paint(g);
			else{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(getX(), getY(), getWidth(), getHeight());
				g.setColor(Color.DARK_GRAY);
				g.drawRect(getX(), getY(), getWidth(), getHeight());
				g.setColor(Color.BLACK);
				g.drawString(text, getX()+getWidth()/2-fm.stringWidth(text)/2, getY()+getHeight()/2+4);
			}
			if (selected){
				g.setColor(Color.CYAN);
				for (int i = -3; i < 4; i++)
					g.drawRect(getX()-i,getY()-i,getWidth()+2*i, getHeight()+2*i);
			}
		}
	}
	
}
