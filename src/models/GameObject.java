package models;
import java.awt.Color;
import java.awt.Graphics;

import ui.Game;
import ui.Sprite;

// Allows an object to be added to the "grid"
//	- Basically allows the object to have coordinates and be moved
public class GameObject {

	private float x, y;
	private float width, height;
	
	private Sprite sprite;
	private Color myColor;
	private boolean visible;
	
	private boolean solid;
	
	public GameObject(float x, float y){
		this(x,y,1,1,null);
	}
	
	public GameObject(float x, float y, float width, float height){
		this(x,y,width,height,null);
	}
	
	public GameObject(float x, float y, Sprite sprite){
		this(x, y, sprite.spriteWidth(), sprite.spriteHeight(), sprite);
	}
	
	public GameObject(float x, float y, float width, float height, Sprite sprite){
		this.x = x;
		this.y = y;
		setWidth(width);
		setHeight(height);
		this.sprite = sprite;
		setColor(Color.WHITE);
		visible = true;
		solid = false;
		if (Game.s_instance != null)
			addSelf();
	}
	
	public GameObject setSolid(boolean solid){
		this.solid = solid;
		return this;
	}
	
	public boolean getSolid(){
		return solid;
	}
	
	public void addSelf(){
		Game.s_instance.addObject(this);
	}
	
	public void removeSelf(){
		Game.s_instance.removeObject(this);
	}
	
	public void setColor(Color color){
		this.myColor = color;
	}
	
	public Color getColor(){
		return myColor;
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
	
	public Sprite getSprite(){
		return sprite;
	}
	
	public void delete(){
		Game.s_instance.removeObject(this);
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void changeX(float dx){
		this.x += dx;
	}
	
	public void changeY(float dy){
		this.y += dy;
	}
	
	public int getX(){
		return (int)x;
	}
	
	public int getY(){
		return (int)y;
	}
	
	public void setBottom(int bottom){
		y = bottom - height;
	}
	
	public int getBottom(){
		return (int)y + (int)height;
	}
	
	public int getRight(){
		return (int)x + (int)width;
	}
	
	public void setDimensions(float width, float height){
		setWidth(width);
		setHeight(height);
	}
	
	public void setCoordinates(float x, float y){
		setX(x);
		setY(y);
	}
	
	public void scaleToSprite(){
		setWidth(getSprite().spriteWidth());
		setHeight(getSprite().spriteHeight());
	}
	
	public GameObject setSize(int width, int height){
		this.width = width;
		this.height = height;
		return this;
	}
	
	public void setWidth(float width){
		this.width = width;
	}
	
	public void setHeight(float height){
		this.height = height;
	}
	
	public void setCenter(int cx, int cy){
		this.x = cx - width/2;
		this.y = cy - height/2;
	}
	
	public void changeWidth(float dWidth){
		setWidth(width+dWidth);
	}
	
	public void changeHeight(float dHeight){
		setHeight(height+dHeight);
	}
	
	public int getWidth(){
		return (int)width;
	}
	
	public int getHeight(){
		return (int)height;
	}
	
	public int getCenterX(){
		return (int)x + (int)width/2;
	}
	
	public int getCenterY(){
		return (int)y + (int)height/2;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean getVisible(){
		return visible;
	}
	
	public boolean placeFree(int x, int y){
		int oldx = getX();
		int oldy = getY();
		setX(x);
		setY(y);
		boolean output = true;
		for (GameObject o : Game.s_instance.getObjects()){
			if (o.getSolid() && Tools.checkCollision(this, o)){
				output = false;
				break;
			}
		}
		setX(oldx);
		setY(oldy);
		return output;
	}
	
	public void update(float deltaTime){
		
	}
	
	public void paint(Graphics g){
		if (visible){
			if (sprite != null)
				sprite.drawSprite(g, getX(), getY(), getWidth(), getHeight());
			else{
				g.setColor(myColor);
				g.fillRect(getX(), getY(), getWidth(), getHeight());
			}
		}
	}
	
}
