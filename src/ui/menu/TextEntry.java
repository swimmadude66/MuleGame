// STILL WORKING ON THIS //

package ui.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import models.GameObject;

public class TextEntry extends GameObject implements MenuOption, InputReceiver{

	private String text = "", renderedText;
	private boolean focus, selected;
	private FontMetrics fm;
	private int lines;
	private boolean rendered;

	private int cursor;

	private int blinker, blinkerSpeed;
	
	private ActionListener actionOne, actionTwo;

	public TextEntry(ActionListener action, ActionListener action2, int x, int y, int width, int height){
		super(x,y,width,height,null);
		this.actionOne = action;
		this.actionTwo = action2;
		renderedText = "";
		focus = false;
		this.text = renderedText;
		cursor = text.length();
		rendered = false;
		selected = false;
		blinker = 0;
		blinkerSpeed = 30;
	}

	public void addText(char c){
		if (fm == null)
			return;
		blinker = 0;
		String oldText = text;
		text = text + c;
		cursor += 1;
		renderText();
		if (getLines()*fm.getHeight() > getHeight()-2){
			text = oldText;
			cursor -= 1;
			renderText();
		}
	}

	public void moveCursor(int dir){
		// LEFT, RIGHT, UP, DOWN = 1, 2, 3, 4 respectively
		blinker = 1;
		if (dir == 1)
			cursor -= 1;
		else if (dir == 2)
			cursor += 1;
		else if (dir == 3){
			int i = renderedText.substring(0,cursor).lastIndexOf('\n');
			if (i < 0)
				i = 0;
			int i2 = renderedText.substring(0,i).lastIndexOf('\n');
			if (i2 < 0)
				i2 = 0;
			if (i == 0)
				cursor = 0;
			else
				cursor = i2 + (cursor - i);
		}
		else if (dir == 4){
			int i = renderedText.substring(0,cursor).lastIndexOf('\n');
			if (i < 0)
				i = 0;
			int i2 = renderedText.substring(0,i).length()+renderedText.substring(i+1).indexOf('\n')+1;
			if (i2 <= 0 || i2 <= i)
				i2 = renderedText.length();
			cursor = i2 + (cursor - i);
		}

		if (cursor < 0)
			cursor = 0;
		else if (cursor > text.length())
			cursor = text.length();
	}

	public String getText(){
		return text;
	}

	public String getRenderedText(){
		return renderedText;
	}

	public void backSpace(){
		cursor -= 1;
		if (cursor < 0)
			cursor = 0;
		if (text.length() > 0){
			text = text.substring(0,text.length()-1);
		}
		renderText();
	}

	public int getLines(){
		return lines;
	}

	public int renderText(){
		if (fm == null)
			return 0;
		String backup = text;
		renderedText = "";
		int ind = 0;
		lines = 1;
		int lastSpace = 0;
		while (text.length() > 0){
			String tryString = text.substring(0,ind);
			if (text.charAt(ind) == ' '){
				lastSpace = ind;
			}
			if (text.charAt(ind) == '\n'){
				renderedText += text.substring(0,ind+1);
				text = text.substring(ind+1);
				ind = 0;
				lines += 1;
				lastSpace = 0;
			}
			if (fm.stringWidth(tryString) >= getWidth() - 8){
				if (lastSpace == 0)
					lastSpace = ind-1;
				renderedText += text.substring(0,lastSpace+1) + '\n';
				text = text.substring(lastSpace+1);
				//renderedText += text.substring(0,ind) + '\n';
				//text = text.substring(ind);
				ind = 0;
				lines += 1;
				lastSpace = 0;
			}
			if (ind >= text.length()-1){
				renderedText += text;
				text = "";
			}
			ind += 1;
		}
		//if (focus)
		//	renderedText += "|";
		text = backup;
		return lines;
	}

	public void setText(String text){
		this.text = text;
		rendered = false;
	}

	public void setFocus(boolean focus){
		blinker = 0;
		this.focus = focus;
	}

	public boolean getFocus(){
		return focus;
	}

	public void paint(Graphics g){
		fm = g.getFontMetrics();
		blinker += 1;
		if (blinker > 2*blinkerSpeed)
			blinker = 0;
		if (!rendered){
			rendered = false;
			renderText();
		}
		Color BGCOLOR1 = new Color(220,220,220);
		Color BGCOLOR2 = new Color(190,190,190);
		if (focus){
			BGCOLOR1 = Color.WHITE;
			BGCOLOR2 = new Color(225,225,225);
		}
		g.setColor(BGCOLOR1);
		g.fillRect(getX(),getY(),getWidth(),getHeight()/2);
		g.setColor(BGCOLOR2);
		g.fillRect(getX(),getY()+getHeight()/2,getWidth(),getHeight()/2);

		g.setColor(Color.BLACK);
		g.drawRect(getX(),getY(),getWidth(),getHeight());
		g.drawString(renderedText,getX()+12,getY()+20);
		
		/*int kcursor = cursor + (getLines(renderedText.substring(0,cursor)) - getLines(text.substring(0,cursor)));
		if (focus && blinker <= blinkerSpeed)
			drawText(g,"|",getX()+fm.stringWidth(getLastLine(renderedText.substring(0,kcursor))),getY()-2+(getLines(renderedText.substring(0,kcursor))-1)*fm.getHeight(),1);
		g.setFont(Controller.font1);
		drawText(g,title,getX(),getY()-24,1);
		g.setFont(Controller.font0);*/
		
		if (selected){
			g.setColor(Color.CYAN);
			for (int i = -3; i < 4; i++)
				g.drawRect(getX()-i,getY()-i,getWidth()+2*i, getHeight()+2*i);
		}
	}

	@Override
	public void select() {
		selected = true;
	}

	@Override
	public void deselect() {
		selected = false;
	}

	@Override
	public void click() {
		actionOne.actionPerformed(new ActionEvent(this, 0, null));
		setFocus(true);
		renderedText = text = "";
	}
	
	public void action(){
		actionTwo.actionPerformed(new ActionEvent(this, 0, null));
		setFocus(false);
	}

	@Override
	public void pressLeft() {
	}

	@Override
	public void releaseLeft() {
	}

	@Override
	public void pressRight() {
	}

	@Override
	public void releaseRight() {
	}

	@Override
	public void pressUp() {
	}

	@Override
	public void releaseUp() {
	}

	@Override
	public void pressDown() {
	}

	@Override
	public void releaseDown() {
	}

	@Override
	public void pressA() {
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
	public void pressStart() {
	}

}