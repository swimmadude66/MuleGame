package models;

import java.awt.Color;
import java.awt.Graphics;

public class PlotPreAuction extends PlotBuyer{

	private boolean[] readyup;
	
	private Tile buyTile;
	
	public PlotPreAuction(Map map, Tile tile, int pNums){
		this.readyup = new boolean[pNums];
		
		buyTile = tile;
	}
	
	public Tile getCurrentTile(){
		return buyTile;
	}
	
	public void select(Player p){
		readyup[p.getPlayerNum()] = true;
	}
	
	public void select(Player p, int tNum){
		select(p);
	}
	
	public boolean getFinished(){
		boolean finished = true;
		for (int i = 0; i < readyup.length; i++)
			if (!readyup[i])
				finished = false;
		return finished;
	}
	
	public void paint(Graphics g){
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < 5; i++){
			g.drawRect(buyTile.getX()+i,buyTile.getY()+i,buyTile.getWidth()-2*i-1,buyTile.getHeight()-2*i-1);
		}
		g.setColor(Color.WHITE);
		g.drawString("Land for Auction is Highlighted", 120, 410);
		g.drawString("All players press START to continue", 120, 430);
	}
}
