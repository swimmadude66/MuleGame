package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import models.GameObject;
import models.Player;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import ui.menu.Menu;
import ui.menu.NumberHolder;
import ui.menu.TextDisplay;

public class InputCreator extends GameObject implements KeyListener{
	
	private int state;
	private Player[] players;
	private NumberHolder nh;
	private int num;
	private Menu menu;
	
	private int wait;
	
	private Controller controller;
	private ArrayList<Controller> controllerList;
	private boolean[] pressed;
	
	private String[] keys;
	private int key;
	
	private TextDisplay message;
	
	public InputCreator(Player[] players, NumberHolder nh, Menu containingMenu){
		super(0,0);
		
		this.players = players;
		this.nh = nh;
		this.num = nh.getNumber();
		this.menu = containingMenu;
		
		keys = new String[7];
		key = 0;
		message = new TextDisplay(320,240);
		message.setHAlign(2);
		message.setColor(Color.WHITE);
		setState(0);
	}
	
	public void begin(){
		num = nh.getNumber()-1;
		for (Player p : players){
			if (p != null)
				p.getInput().setInputReceiver(p);
		}
		wait = 20;
		message.setText("Press any button on Player "+(players[num].getPlayerNum()+1)+"'s controller");
		setState(1);
	}
	
	private void setState(int state){
		this.state = state;
		switch (state) {
		case 1:
			controllerList = Input.searchForControllers();
			break;
		case 2:
			if (controller != null)
				pressed = new boolean[controller.getComponents().length];
			key = 0;
			message.setText("Please press <UP>");
			break;
		case 4:
			key = 1;
			message.setText("Please press <DOWN>");
			break;
		case 5:
			key = 2;
			message.setText("Please press <LEFT>");
			break;
		case 6:
			key = 3;
			message.setText("Please press <RIGHT>");
			break;
		case 7:
			key = 4;
			message.setText("Please press <A>");
			break;
		case 8:
			key = 5;
			message.setText("Please press <B>");
			break;
		case 9:
			key = 6;
			message.setText("Please press <START>");
			break;
		case 10:
			players[num].getInput().setBindings(keys[0], keys[1], keys[2], keys[3], keys[4], keys[5], keys[6]);
			players[num].getInput().setController(controller);
			message.setText("Bindings set!");
			for (Player p : players){
				if (p != null){
					p.getInput().setInputReceiver(menu);
				}
			}
			break;
		}
	}
	
	public void checkForInput(){
		for (Controller c : controllerList){
			if (c != null && c.poll()){
				Component[] components = c.getComponents();
				for(int i=0; i < components.length; i++){
	                Component component = components[i];
	                if (component.getPollData() > .9f && wait < 1){
	                	controller = c;
	                	setState(2);
	                	break;
	                }
				}
			}
		}
	}
	
	public void updateInput(){
		if (state > 1 && controller != null && controller.poll()){
			Component[] components = controller.getComponents();
			for(int i=0; i < components.length; i++){
                Component component = components[i];
                String name = component.getName();
                if (component.getPollData() > .8f){
                	if (!pressed[i]){
                		pressed[i] = true;
                		keys[key] = name;
                		setState(state+1);
                	}
                } else if (component.getPollData() < .3f){
                	if (pressed[i]){
                		pressed[i] = false;
                	}
                }
			}
		} else controller = null;
	}
	
	public void keyPressed(KeyEvent e){
		if (controller == null && state > 1 && state < 10){
			String k = String.valueOf(e.getKeyCode());
			keys[key] = k;
			setState(state+1);
		} else if (state == 1 && wait < 1){
			controller = null;
			setState(2);
		}
	}
	
	@Override
	public void update(float deltaTime){
		if (wait > 0)
			wait--;
		if (state == 1){
			checkForInput();
		} else if (state > 1 && state < 10){
			updateInput();
		}
	}
	
	@Override
	public void paint(Graphics g){
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
