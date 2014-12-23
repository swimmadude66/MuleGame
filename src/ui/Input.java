package ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import networking.NetworkController;
import networking.Writer;
import ui.menu.InputReceiver;
import ui.menu.TextEntry;


public class Input implements KeyListener{

	private String up, down, left, right, a, b, start;
	
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int A = 5;
	public static final int B = 6;
	public static final int START = 7;
	
	private InputReceiver inputReceiver;
	
	private boolean ignoreFreezes;
	
	private Controller controller;
	private boolean controllerConnected;
	private boolean[] pressed;
	private boolean axis;
	
	private Presses presses;
	
	public Input(InputReceiver ir){
		this.inputReceiver = ir;
		ignoreFreezes = false;
		
		controllerConnected = false;
		presses = new Presses();
	}
	
	public Input setInputReceiver(InputReceiver ir){
		this.inputReceiver = ir;
		return this;
	}
	
	public void setIgnoreFreezes(boolean iff){
		this.ignoreFreezes = iff;
	}
	
	public void setController(Controller controller){
		this.controller = controller;
		if (controller != null){
			controllerConnected = true;
			pressed = new boolean[controller.getComponents().length];
		}
	}
	
	public void clearBindings(){
		String bind = "";
		setBindings(bind,bind,bind,bind,bind,bind,bind);
	}
	
	public Input setPresetBindings(int playerNum){
		if (playerNum == 0){
			setBindings(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_N, KeyEvent.VK_M, KeyEvent.VK_PERIOD);
		}
		else if (playerNum == 1){
			setBindings(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3);
		}
		else if (playerNum == 2){
			setBindings(KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9);
		}
		else if (playerNum == 3){
			setBindings(KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9);
		}
		return this;
	}
	
	public static ArrayList<Controller> searchForControllers() {
		System.out.println("Checking for controllers.");
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        ArrayList<Controller> foundControllers = new ArrayList<Controller>();
        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];
            if (	controller.getType() == Controller.Type.STICK || 
                    controller.getType() == Controller.Type.GAMEPAD || 
                    controller.getType() == Controller.Type.WHEEL ||
                    controller.getType() == Controller.Type.FINGERSTICK ) {
                foundControllers.add(controller);
                System.out.println(controller);
            }
        }
        System.out.println("Done checking.");
        return foundControllers;
    }
	
	public Controller checkForInput(){
		for (Controller c : searchForControllers()){
			if (c != null && c.poll()){
				Component[] components = c.getComponents();
				for(int i=0; i < components.length; i++){
	                Component component = components[i];
	                if (component.getPollData() > .9f){
	                	return c;
	                }
				}
			}
		}
		return null;
	}
	
	public void setBindings(String up, String down, String left, String right, String a, String b, String start){
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		this.a = a;
		this.b = b;
		this.start = start;
	}
	
	public void setBindings(int up, int down, int left, int right, int a, int b, int start){
		this.up = String.valueOf(up);
		this.down = String.valueOf(down);
		this.left = String.valueOf(left);
		this.right = String.valueOf(right);
		this.a = String.valueOf(a);
		this.b = String.valueOf(b);
		this.start = String.valueOf(start);
	}
	
	public void updateInput(){
		if (controller != null && !controllerConnected){
			searchForControllers();
		}
		if (Game.freezeInput && !ignoreFreezes)
			return;
		if (controller != null && controllerConnected && controller.poll()){
			Component[] components = controller.getComponents();
			for(int i=0; i < components.length; i++){
                Component component = components[i];
                String name = component.getName();
                if (axis && (name.equals(up) || name.equals(right))) {
                	System.out.println(component.getName()+", "+component.getPollData());
                	continue;
                }
                if (component.getPollData() > .8f){
                	if (!pressed[i]){
                		pressed[i] = true;
                		presses.actionPerformed(new ActionEvent(this, 1, component.getName()));
                	}
                } else if (component.getPollData() < .3f){
                	if (pressed[i]){
                		pressed[i] = false;
                		presses.actionPerformed(new ActionEvent(this, 2, component.getName()));
                	}
                }
			}
		} else if (controller != null && controllerConnected){
			System.out.println("Lost connection.");
			Game.freezeInput = true;
			controllerConnected = false;
		}
	}
	
	public void manualInput(int in, boolean press){
		switch (in){
		case UP:
			if (press)
				inputReceiver.pressUp();
			else
				inputReceiver.releaseUp();
			break;
		case DOWN:
			if (press)
				inputReceiver.pressDown();
			else
				inputReceiver.releaseDown();
			break;
		case LEFT:
			if (press)
				inputReceiver.pressLeft();
			else
				inputReceiver.releaseLeft();
			break;
		case RIGHT:
			if (press)
				inputReceiver.pressRight();
			else
				inputReceiver.releaseRight();
			break;
		case A:
			if (press)
				inputReceiver.pressA();
			else
				inputReceiver.releaseA();
			break;
		case B:
			if (press)
				inputReceiver.pressB();
			else
				inputReceiver.releaseB();
			break;
		case START:
			inputReceiver.pressStart();
			break;
		}
	}
	
	private class Presses implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getID() == 1){
				keyPress(e.getActionCommand());
			} else {
				keyRelease(e.getActionCommand());
			}
		}
		
		private void keyPress(String k){
			Writer writer = null;
			if (Game.s_instance.online)
				writer = NetworkController.writer;
			if (k.equals(up)){
				inputReceiver.pressUp();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(UP);
					writer.sendmessage();
				}
			}
			else if (k.equals(down)){
				inputReceiver.pressDown();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(DOWN);
					writer.sendmessage();
				}
			}
			else if (k.equals(left)){
				inputReceiver.pressLeft();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(LEFT);
					writer.sendmessage();
				}
			}
			else if (k.equals(right)){
				inputReceiver.pressRight();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(RIGHT);
					writer.sendmessage();
				}
			}
			else if (k.equals(a)){
				inputReceiver.pressA();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(A);
					writer.sendmessage();
				}
			}
			else if (k.equals(b)){
				inputReceiver.pressB();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(B);
					writer.sendmessage();
				}
			}
			else if (k.equals(start)){
				inputReceiver.pressStart();
				if (writer != null){
					writer.writebyte(Writer.MSG_PRESS);
					writer.writebyte(START);
					writer.sendmessage();
				}
			}
		}
		
		private void keyRelease(String k){
			Writer writer = null;
			if (Game.s_instance.online)
				writer = NetworkController.writer;
			if (k.equals(up)){
				inputReceiver.releaseUp();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(UP);
					writer.sendmessage();
				}
			}
			else if (k.equals(down)){
				inputReceiver.releaseDown();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(DOWN);
					writer.sendmessage();
				}
			}
			else if (k.equals(left)){
				inputReceiver.releaseLeft();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(LEFT);
					writer.sendmessage();
				}
			}
			else if (k.equals(right)){
				inputReceiver.releaseRight();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(RIGHT);
					writer.sendmessage();
				}
			}
			else if (k.equals(a)){
				inputReceiver.releaseA();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(A);
					writer.sendmessage();
				}
			}
			else if (k.equals(b)){
				inputReceiver.releaseB();
				if (writer != null){
					writer.writebyte(Writer.MSG_RELEASE);
					writer.writebyte(B);
					writer.sendmessage();
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e){
		String k = String.valueOf(e.getKeyCode());
		
		if (k.equals(String.valueOf(KeyEvent.VK_ENTER))){
			if (inputReceiver instanceof TextEntry){
				if (((TextEntry)inputReceiver).getFocus()){
					((TextEntry)inputReceiver).action();
				}
			}
		}
		
		if (Game.freezeInput && !ignoreFreezes)
			return;
		
		if (k.equals(String.valueOf(KeyEvent.VK_ESCAPE)))
			if (Game.EXIT_ON_ESCAPE)
				System.exit(0);
		
		if (controller == null){
			presses.actionPerformed(new ActionEvent(this, 1, k));
		}
	}

	public void keyReleased(KeyEvent e){
		String k = String.valueOf(e.getKeyCode());
		
		if (Game.freezeInput && !ignoreFreezes)
			return;
		
		if (controller == null)
			presses.actionPerformed(new ActionEvent(this, 2, k));
	}

	public void keyTyped(KeyEvent e){
		if (inputReceiver instanceof TextEntry){
			if (((TextEntry)inputReceiver).getFocus()){
				if (e.getKeyChar() == '\b'){
					((TextEntry)inputReceiver).backSpace();
				}
				else if (e.getKeyCode() != KeyEvent.VK_ENTER)
					((TextEntry)inputReceiver).addText(e.getKeyChar());
				else
					((TextEntry)inputReceiver).action();
			}
		}
	}
	
}
