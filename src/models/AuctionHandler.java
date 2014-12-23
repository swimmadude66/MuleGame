package models;

public abstract class AuctionHandler extends GameObject{

	protected int distBelow, distAbove;
	
	public AuctionHandler(){
		super(0,0);
		
		distBelow = 60;
		distAbove = 20;
	}
	
	public void setDistBelow(int d){
		distBelow = d;
	}
	
	public void setDistAbove(int d){
		distAbove = d;
	}
	
	public int getResource(){
		return 0;
	}
	
	public int getDistBelow(){
		return distBelow;
	}
	public int getDistAbove(){
		return distAbove;
	}
	
	public abstract int getBaseY(Player p);
	public abstract int getTopY(Player p);
	public abstract boolean getFinished();
	public abstract Player getWinningBidder();
	public abstract Tile getPlot();
	public abstract int getPrice();
	public abstract void begin();
	
}
