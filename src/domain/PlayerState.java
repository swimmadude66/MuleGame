package domain;

import models.Player;

public class PlayerState {

	int id;
	public String name;
	public int playerNum;
	public TileState[] tiles;
	int score;

	public int money, food, energy, smithore;

	public boolean up, down, left, right;
	public boolean checkCollisions;

	public int state;
	public int raceid;

	public int speed;

	public int mule;
	public int x,y;
	public int r,g,b;
	
	
	public PlayerState(Player p){
		this.x = p.getX();
		this.y = p.getY();
		this.name = p.getName();
		this.playerNum = p.getPlayerNum();
		this.tiles = new TileState[p.getTiles().length];
		for(int i=0; i<p.getTiles().length; i++){
			this.tiles[i] = new TileState(p.getTiles()[i]);
		}
		this.score = p.getScore();
		this.money = p.getMoney();
		this.food = p.getFood();
		this.energy = p.getEnergy();
		this.smithore = p.getSmithore();
		this.up = p.isUp();
		this.down=p.isDown();
		this.left = p.isLeft();
		this.right = p.isRight();
		this.checkCollisions = p.isCheckCollisions();
		this.state = p.getState();
		this.raceid = p.getRaceid();
		this.speed = p.getSpeed();
		this.mule = p.getMuleid();
		this.r = p.getColor().getRed();
		this.g = p.getColor().getGreen();
		this.b = p.getColor().getBlue();
	}
	
	public PlayerState(){
		
	}
	

}

