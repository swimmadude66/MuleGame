package models;

import java.awt.Graphics;

import domain.TileState;
import ui.Game;
import ui.Sprite;


public class Tile extends GameObject{
	
	public TileType type;
	
	public static final int tileWidth = 48;
	public static final int tileHeight = 64;
	
	private boolean selected;
	
	private int locationNum;
	
	private Outfit outfit;
	
	private int ownerID;
	private int playerId;
	private Mule mule;
	
	private int production;
	private int shownProduction;
	
	public static int maxProduction = 10;
	
	public enum TileType{
		River (4,2,0), 
		Mountain1 (1,1,2),
		Mountain2 (1,1,3),
		Mountain3 (1,1,4),
		Plains (2,3,1),
		Town (0,0,0);
		
		public int foodProduction;
		public int energyProduction;
		public int oreProduction;
		
		TileType(int foodProd, int energyProd, int oreProd){
			this.foodProduction = foodProd;
			this.energyProduction = energyProd;
			this.oreProduction = oreProd;
		}
	}
	
	public Tile(int x, int y, TileType t){
		super(x,y,tileWidth,tileHeight,null);
		this.type = t;
		setSelected(false);
		setOwnerID(-1);
		mule = null;
		
		production = 0;
		shownProduction = 0;
		
		switch(type){
		case Plains:
			setSprite(new Sprite("/Resources/map_plain.png",true));
			break;
		case Mountain1:
			setSprite(new Sprite("/Resources/map_m1.png",true));
			break;
		case Mountain2:
			setSprite(new Sprite("/Resources/map_m2.png",true));
			break;
		case Mountain3:
			setSprite(new Sprite("/Resources/map_m3.png",true));
			break;
		case Town:
			setSprite(new Sprite("/Resources/map_town.png",true));
			break;
		case River:
			break;
		default:
			System.err.println("Incorrect typing on tile (" + getX()+", " +getY()+").");
			break;
		}
	}
	
	public Tile (TileState t){
		super(t.x,t.y,tileWidth,tileHeight,null);
		this.type = TileType.values()[t.typeId];
		this.selected = t.selected;
		this.mule = Game.generateMule(t.mule);
		this.outfit = t.outfit;
		this.ownerID = t.ownerID;
		this.playerId = t.playerId;
		this.production = t.production;
		
		switch(type){
		case Plains:
			setSprite(new Sprite("/Resources/map_plain.png",true));
			break;
		case Mountain1:
			setSprite(new Sprite("/Resources/map_m1.png",true));
			break;
		case Mountain2:
			setSprite(new Sprite("/Resources/map_m2.png",true));
			break;
		case Mountain3:
			setSprite(new Sprite("/Resources/map_m3.png",true));
			break;
		case Town:
			setSprite(new Sprite("/Resources/map_town.png",true));
			break;
		case River:
			break;
		default:
			System.err.println("Incorrect typing on tile (" + getX()+", " +getY()+").");
			break;
		}
	}
	
	public void setLocationNum(int n){
		this.locationNum = n;
	}
	
	public int getLocationNum(){
		return locationNum;
	}
	
	public int getProduction(){
		return production;
	}
	
	public void setProduction(int prod){
		production = prod;
		shownProduction = 0;
		if (production > maxProduction)
			production = maxProduction;
	}
	
	public void changeProduction(int dprod){
		production += dprod;
		if (production > maxProduction)
			production = maxProduction;
	}
	
	public int getShownProduction(){
		return shownProduction;
	}
	
	public void updateShownProduction(){
		if (shownProduction < production)
			shownProduction++;
	}
	
	public int getTypeId(){
		return type.ordinal();
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @return the ownerID
	 */
	public int getOwnerID() {
		return ownerID;
	}

	/**
	 * @param ownerID the ownerID to set
	 */
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	/**
	 * @return the playerId
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setMule(Mule mule){
		this.mule = mule;
		this.outfit = mule.getOutfit();
	}
	
	public Mule getMule(){
		return mule;
	}
	
	public Mule takeMule(){
		Mule m = mule;
		mule = null;
		outfit = null;
		return m;
	}
	
	public void setOutfit(Outfit o){
		this.outfit = o;
	}
	
	public Outfit getOutfit(){
		return outfit;
	}
	
	public int getMuleid(){
		if (mule == null)
			return -1;
		else
			return mule.getOutfitID();
	}
	
	public int getWorth(){
		return 500 + (outfit == null ? 0 : outfit.worth);
	}
	
	public void setOwner(Player p){
		this.setOwnerID(p.getPlayerNum());
	}
	
	public Player getOwner(){
		return Game.s_instance.findPlayerById(getOwnerID());
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public void paint(Graphics g){
		if (getVisible()){
			if (getSprite() != null)
				getSprite().drawSprite(g, getX(), getY(), getWidth(), getHeight());
			
			if (getOwnerID() != -1){
				g.setColor(Game.s_instance.findPlayerById(getOwnerID()).getColor());
				for (int i = 0; i < 5; i++){
					g.drawRect(getX()+i,getY()+i,getWidth()-2*i-1,getHeight()-2*i-1);
				}
				if (mule == null)
					drawHouse(g,getX()+22,getY()+32);
				else
					drawOutfit(g,mule.getOutfit(),getX()+22,getY()+32);
				//g.drawString(owner.getColor().toString(), getX()+12, getY()+16);
				if (shownProduction > 0){
					for (int i = 0; i < shownProduction; i++)
						g.fillRect(getRight()-12-(i%2)*6, getBottom()-12-(i/2)*6, 4, 4);
				}
			}
		}
	}
	
	public int calculateProduction(){
		int total = 0;
		if (getOutfit() != null){
			if (getOutfit() == Outfit.Food){
				total += type.foodProduction;
			}
			else if (getOutfit() == Outfit.Energy){
				total +=  type.energyProduction;
			}
			else if (getOutfit() == Outfit.Smithore){
				total +=  type.oreProduction;
			}
		}
		if (total > maxProduction)
			total = maxProduction;
		production = total;
		return total;
	}
	
	public static void drawOutfit(Graphics g, Outfit outfit, int x, int y){
		if (outfit == Outfit.Food){
			g.drawLine(x,y-6,x,y+6);
			g.drawLine(x+1,y-6,x+1,y+6);
			g.fillRect(x-3, y-4, 2, 2);
			g.fillRect(x+3, y-2, 2, 2);
			g.fillRect(x-3, y, 2, 2);
		} else if (outfit == Outfit.Energy){
			g.drawLine(x-4,y-1,x+6,y-1);
			g.drawLine(x-5,y,x+5,y);
			g.drawLine(x-6,y+1,x+4,y+1);
			g.drawLine(x-6,y+1,x+1,y-6);
			g.drawLine(x-5,y+1,x+2,y-6);
			g.drawLine(x+6,y-1,x-1,y+6);
			g.drawLine(x+5,y-1,x-2,y+6);
		} else if (outfit == Outfit.Smithore){
			g.drawLine(x-6,y+6,x+6,y-6);
			g.drawLine(x-6,y+5,x+5,y-6);
			g.drawLine(x-5,y+6,x+6,y-5);
			
			g.drawLine(x-3,y-6,x+6,y-6);
			g.drawLine(x-4,y-5,x+5,y-5);
			g.drawLine(x-3,y-6,x-5,y-4);
			g.drawLine(x-4,y-6,x-5,y-5);
			
			g.drawLine(x+6,y-6,x+6,y+3);
			g.drawLine(x+5,y-5,x+5,y+4);
			g.drawLine(x+6,y+3,x+4,y+5);
			g.drawLine(x+6,y+4,x+5,y+5);
		} else if (outfit == null){
			g.drawLine(x-4,y+3,x+3,y-4);
			g.drawLine(x-4,y+4,x+4,y-4);
			g.drawLine(x-3,y+4,x+4,y-3);
			
			g.drawLine(x-3,y-4,x+4,y+3);
			g.drawLine(x-4,y-4,x+4,y+4);
			g.drawLine(x-4,y-3,x+3,y+4);
		}
	}
	
	public static void drawHouse(Graphics g, int x, int y){
		int width = 14;
		int height = 12;
		g.fillRect(x,y,width/4,height);
		g.fillRect(x,y,width,height/2);
		g.fillRect(x+width/2,y,width/2,height);
		for (int i = 0; i < 5; i++){
			g.drawLine(x-2+2*i,y-i,x+width+1-2*i,y-i);
		}
	}
}
