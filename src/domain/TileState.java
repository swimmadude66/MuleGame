package domain;

import models.Outfit;
import models.Tile;

public class TileState {
	
	int id;
	public int typeId;
	
	public static final int tileWidth = 48;
	public static final int tileHeight = 64;
	
	public boolean selected;
	
	public Outfit outfit;
	
	public int ownerID;
	public int playerId;
	public int mule;
	
	public int production;
	
	public int x;
	public int y;
	
	public TileState(Tile t){
		this.typeId = t.getTypeId();
		this.selected = t.isSelected();
		this.outfit = t.getOutfit();
		this.ownerID = t.getOwnerID();
		this.playerId = t.getPlayerId();
		this.mule = t.getMuleid();
		this.x = t.getX();
		this.y = t.getY();
		
	}
	
	public TileState(){
		
	}

}
