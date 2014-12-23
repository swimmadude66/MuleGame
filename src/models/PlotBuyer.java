package models;

public abstract class PlotBuyer extends GameObject{
	
	public PlotBuyer(){
		super(0,0);
	}
	
	public abstract void select(Player player);
	public abstract void select(Player p, int tNum);
	public abstract Tile getCurrentTile();
	public abstract boolean getFinished();
}