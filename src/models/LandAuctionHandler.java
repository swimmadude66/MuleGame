package models;

import java.awt.Color;
import java.awt.Graphics;

import ui.Game;

public class LandAuctionHandler extends AuctionHandler{

	private Tile plot;
	private Player[] players;
	private Player highestBid;
	
	private int price, minPrice;
	
	private int baseY, topY;
	
	private boolean tie;
	
	private boolean begin;
	
	int time, seconds;
	
	public LandAuctionHandler(Tile plot, Player[] players){
		this.plot = plot;
		this.players = players;
		highestBid = this.players[0];

		minPrice = 60 + (60 * Game.s_instance.getRound());
		price = minPrice;
		
		begin = false;
		
		seconds = time = 60*30;
		
		baseY = 300;
		topY = 180;
		
		for (Player p : players){
			p.setBottom(baseY + distBelow);
		}
	}
	
	public void begin(){
		begin = true;
	}
	
	public int getBaseY(Player p){
		return baseY+distBelow;
	}
	
	public int getTopY(Player p){
		return topY;
	}
	
	public Player getWinningBidder(){
		return highestBid;
	}
	
	public int getPrice(){
		return cPrice();
	}
	
	public boolean getFinished(){
		return time < -60*10;
	}
	
	private boolean getTimeOver(){
		return time < 1;
	}
	
	public Tile getPlot(){
		return plot;
	}
	
	@Override
	public void update(float deltaTime){
		
		if (begin)
			time --;
		if (time < (seconds*2)/3 && highestBid == null){
			time -= 2;
		}
		
		if (getTimeOver()){
			for (Player p : players){
				if (p != null)
					p.setState(11);
			}
			return;
		}
		
		for (Player p : players){
			if (p != null){
				if (p.getBottom() <= baseY){
					if ((baseY - p.getBottom()) > (p.getMoney() - minPrice))
						p.changeY(1);
				}
			}
		}
		
		if (highestBid != null){
			if (highestBid.getBottom() > baseY)
				highestBid = null;
			else{
				if (highestBid.getBottom() <= topY)
					minPrice += (time%10 == 0 ? 4 : 0);
				price = minPrice + (baseY - highestBid.getBottom());
			}
		}
		tie = false;
		for (Player p : players){
			if (p != null){
				if (p.getBottom() <= baseY){
					if (highestBid == null)
						highestBid = p;
					else if (p.getBottom() < highestBid.getBottom())
						highestBid = p;
					/*else if (p.getBottom() == highestBid.getBottom() && highestBid != p){
						tie = true;
						if (rand.nextInt(2) == 1)
							highestBid = p;
					}*/
				}
			}
		}
		
		if (price - minPrice > 120)
			minPrice = price-120;
	}
	
	public void paint(Graphics g){
		if (!getTimeOver())
			paintDuring(g);
		else
			paintFinished(g);
	}
	
	private int cPrice(){
		return price - price%4;
	}
	
	public void drawArrow(Graphics g, int x, int y){
		g.drawLine(x,y,x-4,y+4);
		g.drawLine(x,y,x+4,y+4);
		g.drawLine(x-4,y+4,x-2,y+4);
		g.drawLine(x+4,y+4,x+2,y+4);
		g.drawLine(x+2,y+4,x+2,y+8);
		g.drawLine(x-2,y+4,x-2,y+8);
		g.drawLine(x-2,y+8,x+2,y+8);
	}
	
	public void paintDuring(Graphics g){
		g.setColor(Color.WHITE);
		g.drawString("Current Bid: "+cPrice(), 480, 64);
		g.drawString("Lowest allowed: "+minPrice, 480, 96);
		g.drawString("Highest bidder: "+ (highestBid == null ? "None" : highestBid.getName()), 480, 128);
		g.setColor(Color.white);
		g.drawLine(80, baseY, 560, baseY);
		if (highestBid != null && highestBid.getBottom() == topY)
			g.setColor(highestBid.getColor());
		drawArrow(g,70,topY-4);
		drawArrow(g,570,topY-4);
		g.drawLine(80, topY, 560, topY);
		int lineY = baseY - (price-minPrice);
		if (highestBid == null || tie)
			g.setColor(Color.LIGHT_GRAY);
		else
			g.setColor(highestBid.getColor());
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 110; j+=4)
				if (j%8 == 0)
					g.drawLine(100+j*4, lineY-i+2, 100+(j+4)*4-1, lineY-i+2);
		//for (int i = 0; i < 3; i++)
		//	g.drawLine(100, lineY-i+2, 540, lineY-i+2);
		g.setColor(Color.WHITE);
		g.drawString(cPrice()+"", 564, lineY+2);
		g.drawString("MONEY : ", 32, 400);
		g.setColor(Color.CYAN);
		Tools.drawTimeBar(g, 200, 48, 240, time/((float)seconds));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("LAND AUCTION - MONTH "+Game.s_instance.getRound(), 220, 32);
	}
	
	public void paintFinished(Graphics g){
		g.setColor(Color.WHITE);
		g.drawString("Winning Bid: "+cPrice(), 480, 64);
		g.drawString("Winning bidder: "+ (highestBid == null ? "None" : highestBid.getName()), 480, 128);
		g.setColor(Color.white);
		g.drawLine(80, baseY, 560, baseY);
		g.drawLine(80, topY, 560, topY);
		int lineY = baseY - (price-minPrice);
		if (highestBid == null)
			g.setColor(Color.LIGHT_GRAY);
		else
			g.setColor(highestBid.getColor());
		for (int i = 0; i < 3; i++)
			g.drawLine(100, lineY-i+2, 540, lineY-i+2);
		g.setColor(Color.WHITE);
		g.drawString(cPrice()+"", 564, lineY);
		g.drawString("MONEY : ", 32, 400);
	}
	
}
