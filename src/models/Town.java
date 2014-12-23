package models;

import java.awt.Color;
import java.awt.Graphics;

import ui.Game;
import ui.Sprite;
import ui.menu.InputReceiver;
import ui.menu.TextDisplay;

public class Town extends GameObject implements InputReceiver{

	private Player player;
	
	private GameObject[] buildings;
	
	private Map map;
	
	public Town(int x, int y, Player player){
		super(x,y);
		
		this.player = player;
		setupPlayer();
		map = Game.s_instance.getMap();
		buildings = new GameObject[6];
		new GameObject(x,y-4,640,368).setColor(new Color(40,40,40));
		buildings[0] = new Store(x, y);
		//buildings[1] = new AssayOffice(x+340, y);
		buildings[1] = new Outfitter(x+320, y, Outfit.Food);
		buildings[2] = new Outfitter(x+420, y, Outfit.Energy);
		buildings[3] = new Outfitter(x+520, y, Outfit.Smithore);
		buildings[4] = new Pub(x, y+220);
		buildings[5] = new LandOffice(x+340, y+220);
		
		//setSprite(new Sprite("/Resources/inTown.png",true));
	}
	
	private void setupPlayer(){
		player.scaleToSprite();
		player.setCenter(64,240);
		player.setCheckCollisions(true);
		player.setSpeed(2);
		if (player.getMule() != null){
			player.getMule().scaleToSprite();
			player.getMule().setCenter(player.getCenterX()-32, player.getCenterY());
		}
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		for (GameObject o : buildings)
			o.setVisible(visible);
	}
	
	@Override
	public void update(float deltaTime){
		if (getVisible()){
			if (player.getCenterX() < 0 || player.getCenterX() > 640){
				player.sendPosition();
				player.setCheckCollisions(false);
				setVisible(false);
				map.showMap();
				player.setWidth(32);
				player.setHeight(32);
				player.setCenter(320,228);
				player.setSpeed(1);
				Game.s_instance.bringToFront(player);
				if (player.getMule() != null){
					player.getMule().scaleToSprite();
					player.getMule().setWidth(player.getMule().getWidth()/2);
					player.getMule().setHeight(player.getMule().getHeight()/2);
					player.getMule().setCenter(player.getCenterX()+16, player.getCenterY());
					Game.s_instance.bringToFront(player.getMule());
				}
			}
		} else {
			
		}
	}
	
	@Override
	public void releaseA() {
		if (!getVisible()){
			if (Tools.centerDist(player, map.getTownTile()) < 20){
				map.hideMap();
				setVisible(true);
				setupPlayer();
			} else {
				Tile closestTile = map.getTile(0);
				for (int i = 0; i < 45; i++){
					if (Tools.centerDist(player, map.getTile(i)) < Tools.centerDist(player, closestTile))
						closestTile = map.getTile(i);
				}
				if (closestTile.getOwner() == player){
					if (closestTile.getMule() == null){
						if (player.getMule() != null){
							closestTile.setMule(player.takeMule());
							closestTile.getMule().removeSelf();
							TextDisplay td = new TextDisplay(320,410,"Mule Installed!").setHAlign(2);
							td.setColor(Color.WHITE);
							td.setTimer(60*3);
						}
					} else {
						if (player.getMule() != null)
							player.takeMule().runAway();
						else{
							player.giveMule(closestTile.takeMule());
							player.getMule().addSelf();
							player.getMule().scaleToSprite();
							player.getMule().setWidth(player.getMule().getWidth()/2);
							player.getMule().setHeight(player.getMule().getHeight()/2);
							player.getMule().setCenter(closestTile.getCenterX(), closestTile.getCenterY());
							TextDisplay td = new TextDisplay(320,410,"Mule Uninstalled").setHAlign(2);
							td.setColor(Color.WHITE);
							td.setTimer(60*3);
						}
					}
				} else {
					if (player.getMule() != null)
						player.takeMule().runAway();
				}
			}
		}
	}

	@Override
	public void pressA() {
	}

	@Override
	public void pressB() {
		player.pressB();
	}

	@Override
	public void releaseB() {
		player.releaseB();
	}
	
	private class Outfitter extends GameObject{
		
		private int outfitting;
		private Outfit outfit;
		
		private GameObject mask;
		
		public Outfitter(int x, int y, Outfit outfit){
			super(x,y);
			setWidth(96);
			setHeight(140);
			this.outfit = outfit;
			outfitting = 0;
			(mask = new GameObject(x+16,y+getHeight()-32,getWidth()-32,16)).setVisible(false);
			new GameObject(x,y,16,getHeight()).setSolid(true).setVisible(false);
			new GameObject(x+getWidth()-16,y,16,getHeight()).setSolid(true).setVisible(false);
		}
		
		public void update(float deltaTime){
			if (!getVisible())
				return;
			if (outfitting == 0){
				if (Tools.checkCollision(player, mask)){
					player.setY(mask.getY()+mask.getHeight()+1);
					if (player.getMule() == null)
						return;
					if (player.getMoney() > outfit.cost && player.getMule().getOutfit() != outfit){
						outfitting = 1;
						player.setState(8);
					} else {
						TextDisplay td = new TextDisplay(getX()+16,getY()+getHeight()+18,"Cannot buy outfit.");
						td.setColor(Color.WHITE);
						td.setTimer(2*60);
						td.setHAlign(3);
					}
				}
			} else if (outfitting == 1){
				if (Math.abs(player.getMule().getCenterX() - getCenterX()) < 2)
					player.getMule().setCenter(getCenterX(), player.getMule().getCenterY());
				else if (player.getMule().getCenterX() < getCenterX())
					player.getMule().changeX(2);
				else if (player.getMule().getCenterX() > getCenterX())
					player.getMule().changeX(-2);
				if (player.getMule().getY() > getY()+32)
					player.getMule().changeY(-1.5f);
				else{
					player.changeMoney(-outfit.cost);
					player.getMule().setOutfit(outfit);
					outfitting = 2;
				}
			} else if (outfitting == 2){
				player.getMule().changeY(2);
				if (player.getMule().getY() > getY()+getHeight()){
					outfitting = 0;
					player.setState(7);
				}
			}
		}
		
		public void paint(Graphics g){
			if (getVisible()){
				g.setColor(Color.ORANGE);
				g.fillRect(getX(),getY(),96,16);
				g.fillRect(getX(), getY(), 16, getHeight());
				g.fillRect(getX()+getWidth()-16, getY(), 16, getHeight());
				g.setColor(Color.WHITE);
				Tile.drawOutfit(g, outfit, getX()+getWidth()/2, getY()+32);
				g.drawString(""+outfit.cost,getX()+getWidth()/2-8, getY()+56);
			}
		}
	}
	
	private class Store extends GameObject{
		
		private int inStore;
		private GameObject mask;
		private Sprite muleSprite;
		
		private GameObject[] things;
		
		public Store(int x, int y){
			super(x,y, new Sprite("/Resources/inTown_store.png", true));
			inStore = 0;
			(mask = new GameObject(x+32,y+70,64,48)).setVisible(false);
			things = new GameObject[4];
			(things[0] = new GameObject(x,y,getWidth(),16).setSolid(true)).setColor(Color.LIGHT_GRAY);
			(things[1] = new GameObject(x,y,16,getHeight()).setSolid(true)).setColor(Color.LIGHT_GRAY);
			(things[2] = new GameObject(x+getWidth()-16,y,16,getHeight()).setSolid(true)).setColor(Color.LIGHT_GRAY);
			(things[3] = new GameObject(x+96,y+getHeight()-16,96,16)).setSolid(true).setColor(Color.LIGHT_GRAY);
			(new GameObject(x+96,y+getHeight()-16,getWidth()-96,16).setSolid(true)).setVisible(false);
			
			muleSprite = new Sprite("/Resources/Mule_front_50.png",true);
		}
		
		@Override
		public void setVisible(boolean visible){
			super.setVisible(visible);
			for (GameObject o : things)
				o.setVisible(visible);
		}
		
		public void update(float deltaTime){
			if (!getVisible())
				return;
			if (inStore == 0){
				if (Game.getStore().getMules() > 0 && Tools.checkCollision(mask,player)){
					if (player.getMule() == null && player.getMoney() >= Game.getStore().getMulePrice()){
						player.setState(8);
						inStore = 1;
					} else if (player.getMoney() < Game.getStore().getMulePrice()){
						TextDisplay td = new TextDisplay(getX()+96,getY()+getHeight()+18,"Not enough money.");
						td.setColor(Color.WHITE);
						td.setTimer(3*60);
						player.setY(mask.getY()+mask.getHeight()+1);
					}
				}
			} else if (inStore == 1) {
				if (player.getY() > getY()+48)
					player.changeY(-1.5f);
				else if (player.getRight() < getX()+getWidth()-32)
					player.changeX(1.5f);
				else{
					inStore = 2;
					player.changeMoney(-Game.getStore().getMulePrice());
					player.giveMule(Game.getStore().takeMule(player));
				}
			} else if (inStore == 2) {
				if (player.getY() < getY()+getHeight()+1)
					player.changeY(1.5f);
				else{
					inStore = 0;
					player.setState(7);
				}
			}
		}
		
		public void paint(Graphics g){
			if (getVisible()){
				super.paint(g);
				g.setColor(new Color(40,40,40));
				g.fillRect(getX()+33,getY()+90,63,55);
				g.setColor(Color.WHITE);
				Tools.drawArrow(g,getX()+48,getY()+getHeight()-32);
				g.drawString(Game.getStore().getMulePrice()+"",getX()+60,getY()+getHeight()-24);
				for (int i = 0; i < Game.getStore().getMules(); i++){
					muleSprite.drawSprite(g,getX()+24+i*8,getY()+32);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private class AssayOffice extends GameObject{
		public AssayOffice(int x, int y){
			super(x,y, new Sprite("/Resources/inTown_assayOffice.png", true));
			new GameObject(x,y,getWidth(),getHeight()).setSolid(true).setVisible(false);
		}
	}
	
	private class LandOffice extends GameObject{
		public LandOffice(int x, int y){
			super(x,y, new Sprite("/Resources/inTown_landOffice.png", true));
			new GameObject(x,y,getWidth(),getHeight()).setSolid(true).setVisible(false);
		}
	}
	
	private class Pub extends GameObject{
		
		private GameObject mask;
		private int inPub;
		
		public Pub(int x, int y){
			super(x,y, new Sprite("/Resources/inTown_pub.png",true));
			inPub = 0;
			(mask = new GameObject(x+32,y+16,64,48)).setVisible(false);
			new GameObject(x,y,32,140).setSolid(true).setVisible(false);
			new GameObject(x+96,y,196,140).setSolid(true).setVisible(false);
			new GameObject(x+32,y+75,64,70).setSolid(true).setVisible(false);
			new GameObject(x+32,y,62,75).setColor(new Color(40,40,40));
		}
		
		public void update(float deltaTime){
			if (!getVisible())
				return;
			if (inPub == 1){
				player.setState(8);
				player.setCenter(mask.getCenterX(), player.getCenterY());
				if (player.getY() < mask.getY() - 2)
					player.changeY(2);
				else{
					inPub = 2;
					if (!Game.s_instance.online || Game.s_instance.host){
						int bonus = Tools.calculateBonus(Game.s_instance.getRound(), Game.s_instance.getTimer());
						Game.s_instance.finishTurn(bonus);
					}
				}
			}
			else if (inPub == 0 && Tools.checkCollision(mask, player)){
				if (player.getMule() == null)
					inPub = 1;
				else{
					player.setY(mask.getY()-player.getHeight()-2);
					TextDisplay td = new TextDisplay(mask.getRight() + 28,getY()-16,"Cannot bring a MULE into the Pub");
					td.setTimer(60*3);
					td.setColor(Color.WHITE);
				}
			}
		}
	}

	@Override
	public void pressLeft() {
		player.pressLeft();
	}

	@Override
	public void releaseLeft() {
		player.releaseLeft();
	}

	@Override
	public void pressRight() {
		player.pressRight();
	}

	@Override
	public void releaseRight() {
		player.releaseRight();
	}

	@Override
	public void pressUp() {
		player.pressUp();
	}

	@Override
	public void releaseUp() {
		player.releaseUp();
	}

	@Override
	public void pressDown() {
		player.pressDown();
	}

	@Override
	public void releaseDown() {
		player.releaseDown();
	}

	@Override
	public void pressStart() {
		player.pressStart();
	}
	
}
