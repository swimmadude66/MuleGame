package models;

import java.awt.Color;
import java.awt.Graphics;

import networking.NetworkController;
import networking.Writer;
import ui.Game;
import ui.Input;
import ui.Sprite;
import ui.menu.InputReceiver;
import domain.PlayerState;

public class Player extends GameObject implements InputReceiver {

	private String name;
	private int playerNum;
	private Tile[] tiles;
	int score;

	private Input myInput;
	
	private boolean sync;
	
	private boolean showInfo;

	private int money, food, energy, smithore;

	private boolean up, down, left, right;
	private boolean checkCollisions;

	private int state;
	private Race race;

	private int speed;

	private Mule mule;

	private enum Race {
		Human(600, new Sprite("/Resources/races_Human.png", true)), 
		Flapper(1600, new Sprite("/Resources/races_Flapper.png", true)), 
		Bonzoid(1000, new Sprite("/Resources/races_Bonzoid.png", true)), 
		Ugaite(1000, new Sprite("/Resources/races_Ugaite.png", true)), 
		Buzzite(1000, new Sprite("/Resources/races_Buzzite.png", true));

		private Sprite sprite;
		private int startmoney;

		private Race(int money, Sprite img) {
			this.startmoney = money;
			this.sprite = img;
		}

	}

	public Player(String name, final int playerNum, int x, int y) {
		super(x, y, 32, 32, null);
		this.name = name;
		this.playerNum = playerNum;
		this.tiles = new Tile[0];
		this.money = 0;
		// setVisible(false);

		state = 0;
		sync = false;
		
		showInfo = false;

		setUp(setDown(setLeft(setRight(false))));
		setCheckCollisions(true);

		mule = null;

		setSpeed(2);
	}
	
	public void setQuantities(int difficulty){
		switch(difficulty){
		case 0:
			food = 8;
			energy = 4;
			break;
		case 1:
		case 2:
			food = 4;
			energy = 2;
			break;
		}
		smithore = 0;
	}
	
	public Player(PlayerState p){
		super(p.x,p.y,32,32,null);
		this.name = p.name;
		this.playerNum = p.playerNum;
		this.money = p.money;
		this.state = p.state;
		this.setUp(p.up);
		this.setDown(p.down);
		this.setLeft(p.left);
		this.setRight(p.right);
		this.setCheckCollisions(p.checkCollisions);
		this.mule = Game.generateMule(p.mule);
		this.speed = p.speed;
		this.food = p.food;
		this.energy = p.energy;
		this.smithore = p.smithore;
		this.tiles = new Tile[p.tiles.length];
		for(int i=0; i<p.tiles.length; i++){
			this.tiles[i] = new Tile(p.tiles[i]);
		}
		setColor(new Color(p.r, p.g, p.b));
		setRace(p.raceid);
		sync = false;
	}
	
	public void setSync(boolean s){
		sync = s;
	}

	public void setCheckCollisions(boolean c) {
		checkCollisions = c;
	}

	public void changeMoney(int dMoney) {
		money += dMoney;
		if (money < 0)
			money = 0;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	/**
	 * @return the race
	 */
	public Race getRace() {
		return race;
	}
	
	public int getRaceid() {
		return race.ordinal();
	}
	
	public int getMuleid(){
		if (mule == null)
			return -1;
		else
			return mule.getOutfitID();
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param race the race to set
	 */
	public void setRace(Race race) {
		this.race = race;
	}

	public Mule giveMule(Mule mule) {
		this.mule = mule;
		return this.mule;
	}

	public Mule takeMule() {
		Mule m = mule;
		mule = null;
		return m;
	}

	public Mule getMule() {
		return mule;
	}

	/**
	 * What state the player is in.
	 * 
	 * @param state
	 *            state
	 */
	public void setState(int active) {
		this.state = active;
	}

	public int getState() {
		return state;
	}

	public void setInput(Input i) {
		this.myInput = i;
	}

	public Input getInput() {
		return myInput;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getResource(int resource) {
		if (resource == 1)
			return getFood();
		if (resource == 2)
			return getEnergy();
		if (resource == 3)
			return getSmithore();
		return 0;
	}

	public void setResource(int resource, int amount) {
		if (resource == 1)
			food = amount;
		if (resource == 2)
			energy = amount;
		if (resource == 3)
			smithore = amount;
	}

	public void changeResource(int resource, int amount) {
		if (resource == 1)
			changeFood(amount);
		if (resource == 2)
			changeEnergy(amount);
		if (resource == 3)
			changeSmithore(amount);
	}

	/**
	 * @return the checkCollisions
	 */
	public boolean isCheckCollisions() {
		return checkCollisions;
	}

	public int getFood() {
		return food;
	}

	public int getEnergy() {
		return energy;
	}

	public int getSmithore() {
		return smithore;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setSmithore(int smithore) {
		this.smithore = smithore;
	}

	public void changeFood(int dFood) {
		food += dFood;
	}

	public void changeEnergy(int dEnergy) {
		energy += dEnergy;
	}

	public void changeSmithore(int dSmithore) {
		smithore += dSmithore;
	}

	/**
	 * @return the right
	 */
	public boolean isRight() {
		return right;
	}

	/**
	 * @return the up
	 */
	public boolean isUp() {
		return up;
	}

	/**
	 * @param up the up to set
	 */
	public void setUp(boolean up) {
		this.up = up;
	}

	/**
	 * @return the down
	 */
	public boolean isDown() {
		return down;
	}

	/**
	 * @param down the down to set
	 */
	public boolean setDown(boolean down) {
		this.down = down;
		return down;
	}

	/**
	 * @return the left
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public boolean setLeft(boolean left) {
		this.left = left;
		return left;
	}

	/**
	 * @param right the right to set
	 */
	public boolean setRight(boolean right) {
		this.right = right;
		return right;
	}

	public void debug() {
		System.out.println("---------------Player Info--------------");
		System.out.println("Name: '" + this.name + "'");
		System.out.println("Race: " + this.getRace().name());
		System.out.println("Money: " + this.money);
		String ownedtiles = "[ ";
		for (Tile t : tiles) {
			int x = ((t.getX()) / t.getWidth()) - 2;
			int y = ((t.getY()) / t.getHeight()) - 1;
			ownedtiles += "{(" + x + ", " + y + ") " + t.type.name() + "} ";
		}
		ownedtiles += "]";
		System.out.println("Tiles: " + ownedtiles);
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
	}

	public void setRace(int racenum) {
		this.race = Race.values()[racenum];
		setSprite(race.sprite);
		setSprite(Tools.colorSprite(getSprite(), getColor()));
		getSprite().addFrame(race.sprite);
		getSprite().setImageSpeed(0);
		getSprite().setImageSingle(0);
		changeMoney(race.startmoney);
		setWidth(getSprite().spriteWidth());
		setHeight(getSprite().spriteHeight());
	}

	public void setScore(int value) {
		this.score = value;
	}

	public int getScore() {
		return score;
	}

	public void addTile(Tile t) {
		Tile[] temp = tiles;
		tiles = new Tile[tiles.length + 1];
		for (int i = 0; i < temp.length; i++) {
			tiles[i] = temp[i];
		}
		tiles[tiles.length - 1] = t;
	}

	public Tile[] getTiles() {
		return tiles;
	}
	
	public Tile[] getTiles(Outfit outfit){
		int amount = 0;
		for (Tile t : tiles)
			if (t.getOutfit() == outfit)
				amount++;
		int i = 0;
		Tile[] output = new Tile[amount];
		for (Tile t : tiles)
			if (t.getOutfit() == outfit)
				output[i++] = t;
		return output;
	}

	public int getMoney() {
		return this.money;
	}

	@Override
	public void update(float deltaTime) {
		if (state == 10) {
			if (isUp()) {
				if (getBottom() > Game.s_instance.getAuction().getTopY(this))
					changeY(-1);
			} else if (isDown()) {
				if (getBottom() < Game.s_instance.getAuction().getBaseY(this))
					changeY(1);
			}
		} else if (state == 7) {

			int muleDist = (getWidth() * 3) / 2;

			int dx = 0;
			int dy = 0;
			if (isUp())
				dy -= getSpeed();
			if (isDown())
				dy += getSpeed();
			if (isLeft())
				dx -= getSpeed();
			if (isRight())
				dx += getSpeed();
			if (dx != 0) {
				if (!isCheckCollisions() || placeFree(getX() + dx, getY())) {
					if (mule != null) {
						if (Math.abs(mule.getCenterY() - getCenterY()) > getSpeed()) {
							boolean move = false;
							if (dx < 0 && mule.getCenterX() > getCenterX())
								move = true;
							else if (dx > 0 && mule.getCenterX() < getCenterX())
								move = true;
							if (move)
								mule.changeY(mule.getCenterY() > getCenterY() ? -getSpeed()
										: getSpeed());
						} else if (Tools.pointDistance(mule.getCenterX(), 0,
								getCenterX(), 0) > muleDist) {
							mule.changeX(dx);
						}
					}
					changeX(dx);
				}
			} else if (dy != 0) {
				if (!isCheckCollisions() || placeFree(getX(), getY() + dy)) {
					if (mule != null) {
						if (Math.abs(mule.getCenterX() - getCenterX()) > getSpeed()) {
							boolean move = false;
							if (dy < 0 && mule.getCenterY() > getCenterY())
								move = true;
							else if (dy > 0 && mule.getCenterY() < getCenterY())
								move = true;
							if (move)
								mule.changeX(mule.getCenterX() > getCenterX() ? -getSpeed()
										: getSpeed());
						} else if (Tools.pointDistance(mule.getCenterY(), 0,
								getCenterY(), 0) > muleDist) {
							mule.changeY(dy);
						}
					}
					changeY(dy);
				}
			}
			if (mule != null) {
				if (mule.getCenterX() - getCenterX() > muleDist)
					mule.changeX(-getSpeed());
				else if (getCenterX() - mule.getCenterX() > muleDist)
					mule.changeX(getSpeed());
				if (mule.getCenterY() - getCenterY() > muleDist)
					mule.changeY(-getSpeed());
				else if (getCenterY() - mule.getCenterY() > muleDist)
					mule.changeY(getSpeed());
			}
		} else if (state >= 20 && state % 2 == 0 && state < 30) {
			if (getX() > 32)
				changeX(-4);
			else {
				setX(32);
				state += 1;
			}
		} else if (state >= 20 && state % 2 == 1 && state < 30) {
			changeY(-4);
			int place = (state - 20) / 2;
			int finalY = 64 + 96 * place;
			if (getY() < finalY) {
				setY(finalY);
				setState(30);
			}
		}
	}

	private void drawInfo(Graphics g, int x, int y){
		Tile.drawOutfit(g, Outfit.Food, x+12, y-4);
		g.drawString(": "+food, x + 24, y);
		Tile.drawOutfit(g, Outfit.Energy, x+12, y+14);
		g.drawString(": "+energy, x + 24, y+18);
		Tile.drawOutfit(g, Outfit.Smithore, x+12, y+32);
		g.drawString(": "+smithore, x + 24, y+36);
		g.drawString("$", x+8, getY()+54);
		g.drawString(": "+money, x + 24, y+54);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (getVisible() && state == 7){
			if (showInfo){
				g.setColor(Color.BLACK);
				drawInfo(g, getX()+getWidth()+6, getY()+2);
				g.setColor(Color.WHITE);
				drawInfo(g, getX()+getWidth()+4, getY());
			}
		}
		else if (getVisible() && state >= 10 && state < 20) {
			g.setColor(Color.WHITE);
			g.drawString("" + money, getX(), 400);
		} else if (getVisible() && state == 30) {
			g.setColor(getColor());
			g.drawString("Money: " + money, 128, getY() + 8);
			g.drawString("Land: " + calculateLandScore(), 128, getY() + 16 + 8);
			g.drawString("Goods: " + calculateGoodsScore(), 128,
					getY() + 32 + 8);
			g.drawString("Total: " + calculateTotalScore(), 128,
					getY() + 48 + 8);
		}
	}

	public int calculateLandScore() {
		int output = 0;
		for (Tile t : getTiles()) {
			output += t.getWorth();
		}
		return output;
	}

	public int calculateGoodsScore() {
		int output = 0;
		Store store = Game.getStore();
		output += getFood() * store.getPrice(1);
		output += getEnergy() * store.getPrice(2);
		output += getSmithore() * store.getPrice(3);
		return output;
	}

	public int calculateTotalScore() {
		int output = getMoney();
		//System.out.println(output);
		output += calculateLandScore();
		//System.out.println(output);
		output += calculateGoodsScore();
		//System.out.println(output);
		return output;
	}

	@Override
	public void pressLeft() {
		setLeft(true);
	}
	
	public void sendPosition(){
		if (sync && Game.s_instance.online){
			Writer w = NetworkController.writer;
			w.writebyte(Writer.MSG_PLAYER);
			w.writeshort(getX());
			w.writeshort(getY());
			w.sendmessage();
		}
	}

	@Override
	public void releaseLeft() {
		setLeft(false);
		if (sync){
			sendPosition();
		}
	}

	@Override
	public void pressRight() {
		setRight(true);
	}

	@Override
	public void releaseRight() {
		setRight(false);
		if (sync){
			sendPosition();
		}
	}

	@Override
	public void pressUp() {
		setUp(true);
	}

	@Override
	public void releaseUp() {
		setUp(false);
		if (sync){
			sendPosition();
		}
	}

	@Override
	public void pressDown() {
		setDown(true);
	}

	@Override
	public void releaseDown() {
		setDown(false);
		if (sync){
			sendPosition();
		}
	}

	@Override
	public void pressA() {
		if (state == 4 && (!Game.s_instance.online || sync)) {
			Game.s_instance.getMap().plotBuyer.select(this);
		}
		if (state == 6 || state == 30) {
			Game.s_instance.playerReady(this);
		}
	}

	@Override
	public void releaseA() {
		if (sync){
			if (state == 7 || state == 8)
				sendPosition();
		}
	}

	@Override
	public void pressB() {
		showInfo = true;
	}

	@Override
	public void releaseB() {
		showInfo = false;
	}

	public void pressStart() {
		if (state == 5) {
			Game.s_instance.getMap().plotBuyer.select(this);
		}
	}
}
