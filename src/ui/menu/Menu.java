package ui.menu;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import models.GameObject;

/**
 * 
 * @author The Moose Express
 * 
 * Allows us to use Menus and cycle through them using key presses.
 *
 */
public class Menu extends GameObject implements InputReceiver{
	
	private ArrayList<MenuOption> menu;
	private int menuOption;
	
	private ActionListener startListener;
	
	public Menu(){
		super(0,0,0,0,null);
		menu = new ArrayList<MenuOption>();
	}
	
	public void setStartListener(ActionListener al){
		this.startListener = al;
	}

	public MenuOption addMenuOption(MenuOption mo){
		menu.add(mo);
		menuOption = 0;
		renderMenuOptions();
		return mo;
	}
	
	public void deactivate(){
		for (int i = 0; i < menu.size(); i++)
			menu.get(i).deselect();
	}
	
	private void renderMenuOptions(){
		for (int i = 0; i < menu.size(); i++){
			menu.get(i).deselect();
			if (i == menuOption){
				menu.get(i).select();
			}
		}
	}
	
	public void next(){
		if (menu.size() <= 0)
			return;
		if (++menuOption >= menu.size())
			menuOption = 0;
		renderMenuOptions();
	}
	
	public void previous(){
		if (menu.size() <= 0)
			return;
		if (--menuOption < 0)
			menuOption = menu.size()-1;
		renderMenuOptions();
	}
	
	public void click(){
		if (menu.size() <= 0)
			return;
		menu.get(menuOption).click();
	}

	@Override
	public void pressLeft() {
		previous();
	}

	@Override
	public void releaseLeft() {
	}

	@Override
	public void pressRight() {
		next();
	}

	@Override
	public void releaseRight() {
	}

	@Override
	public void pressUp() {
		previous();
	}

	@Override
	public void releaseUp() {
	}

	@Override
	public void pressDown() {
		next();
	}

	@Override
	public void releaseDown() {
	}

	@Override
	public void pressA() {
		click();
	}

	@Override
	public void releaseA() {
	}

	@Override
	public void pressB() {
	}

	@Override
	public void releaseB() {
	}
	
	@Override
	public void pressStart(){
		if (startListener != null)
			startListener.actionPerformed(new ActionEvent(this, 0, null));
	}
	
	@Override
	public void paint(Graphics g){};
	
}
