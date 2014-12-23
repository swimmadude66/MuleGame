package models;

import java.awt.Color;
import java.awt.Graphics;

import networking.NetworkController;
import networking.Writer;
import ui.Game;
import ui.menu.TextDisplay;

public class ResourceAuctionHandler extends AuctionHandler{

	private int resource;
	private String resourceName;
	private boolean begin;
	private int timerA, timerB, timerC;
	private int seconds;
	
	private Store store;
	
	private boolean trading;
	
	private Player lowestSeller;
	private Player highestBidder;
	
	private Player[] players;
	private boolean[] seller;
	private int[] amounts;
	private int[] drawAmounts;
	private int[] requirement;
	
	private TextDisplay title;
	private TextDisplay status;
	
	private int baseY, topY;
	
	private int stage;
	
	public ResourceAuctionHandler(int resource, Player[] players){
		this.resource = resource;
		this.players = players;
		this.seller = new boolean[players.length];
		this.amounts = new int[players.length];
		drawAmounts = new int[amounts.length];
		requirement = new int[amounts.length];
		for (int i = 0; i < amounts.length; i++){
			setAmount(i, this.players[i].getResource(resource));
			seller[i] = false;
		}
		begin = false;
		this.timerA = 0;
		this.timerB = 0;
		this.timerC = 0;
		
		store = Game.getStore();
		
		if (resource == 1)
			resourceName = "Food";
		else if (resource == 2)
			resourceName = "Energy";
		else if (resource == 3)
			resourceName = "Smithore";
		
		lowestSeller = null;
		highestBidder = null;
		
		baseY = 300;
		topY = 180;
		
		(title = new TextDisplay(320,24).setHAlign(2)).setColor(Color.WHITE);
		(status = new TextDisplay(320,440).setHAlign(2)).setColor(Color.WHITE);
		
		setStage(1);
	}
	
	public void begin(){
		begin = true;
	}
	
	public int reAdjAmount(int adjAmount){
		return adjAmount/6;
	}
	
	public int adjAmount(int amount){
		return amount*6;
	}
	
	public void setAmount(int i, int amount){
		amounts[i] = adjAmount(amount);
	}
	
	public void changeAmount(int i, int amount){
		amounts[i] += adjAmount(amount);
	}
	
	public int findPlayerInt(Player p){
		for (int i = 0; i < players.length; i++)
			if (players[i] == p)
				return i;
		return -1;
	}
	
	public int getBaseY(Player p){
		if (seller[findPlayerInt(p)]){
			if (highestBidder == null)
				return baseY;
			else
				return highestBidder.getBottom();
		}
		else
			return baseY+distBelow;
	}
	
	public int getTopY(Player p){
		if (seller[findPlayerInt(p)])
			return topY-distAbove;
		else{
			if (lowestSeller == null)
				return topY;
			else
				return lowestSeller.getBottom();
		}
	}
	
	public int getResource(){
		return resource;
	}
	
	public void setStage(int stage){
		this.stage = stage;
		switch (stage){
		case 1:
			for (Player p : players){
				if (p != null){
					p.setState(11);
					p.setVisible(true);
					p.setX(128 + 96 * p.getPlayerNum());
					p.setBottom(baseY + distBelow);
					Game.s_instance.bringToFront(p);
					Game.s_instance.focusInputOnPlayer(p);
				}
			}
			timerA = 4*60;
			title.setText("Status for Month "+Game.s_instance.getRound()+" : "+resourceName);
			status.setText("Previous amounts");
			break;
		case 2:
			if (resource == 3){
				setStage(4);
				break;
			}
			if (resource == 1){
				for (int i = 0; i < players.length; i++){
					changeAmount(i, -Tools.foodRequirement(Game.s_instance.getRound()));
					if (amounts[i] < 0)
						amounts[i] = 0;
				}
			} else if (resource == 2){
				for (int i = 0; i < players.length; i++){
					changeAmount(i, -energyRequirement(players[i]));
					if (amounts[i] < 0)
						amounts[i] = 0;
				}
			}
			timerA = 3*60;
			status.setText("Usage");
			break;
		case 3:
			for (int i = 0; i < players.length; i++){
				if (resource == 1){
					changeAmount(i, -reAdjAmount(amounts[i])/2);
				} else if (resource == 2){
					changeAmount(i, -reAdjAmount(amounts[i])/4);
				}
				if (amounts[i] < 0)
					amounts[i] = 0;
			}
			timerA = 3*60;
			status.setText("Spoilage");
			break;
		case 4:
			timerA = 3*60;
			for (int i = 0; i < players.length; i++){
				changeAmount(i,calculateProduction(players[i],resource));
			}
			status.setText("Production");
			break;
		case 5:
			for (int i = 0; i < requirement.length; i++){
				requirement[i] = 0;
				if (resource == 1){
					requirement[i] = Tools.foodRequirement(Game.s_instance.getRound()+1);
				}
				if (resource == 2){
					requirement[i] = energyRequirement(players[i]); // Calculate energy requirement
				}
			}
			timerA = 4*60;
			if (resource != 3)
				status.setText("Surplus & Shortages");
			else{
				status.setText("Final Amounts");
				for (int i = 0; i < seller.length; i++)
					if (players[i].getResource(resource) > 0)
						seller[i] = true;
			}
			break;
		case 6:
			title.setText("Auction Set for Month "+Game.s_instance.getRound()+" : "+resourceName);
			status.setText("Press UP or DOWN to set as Buyer or Seller");
			for (Player p : players)
				p.setState(10);
			for (int i = 0; i < seller.length; i++){
				setSeller(i, seller[i]);
				players[i].setResource(resource,reAdjAmount(amounts[i]));
			}
			seconds = timerA = 4*60;
			break;
		case 7:
			if (Game.s_instance.online){
				begin = false;
				Writer w = NetworkController.writer;
				w.writebyte(Writer.MSG_READY);
				w.sendmessage();
			}
			seconds = timerA = 30*60;
			timerB = 0;
			trading = false;
			status.setText("Auction underway");
			break;
		case 8:
			for (Player p : players)
				p.setState(11);
			timerA = 4*60;
			timerB = 0;
			trading = false;
			status.setText("Auction over");
			break;
		default:
			System.err.println("Invalid auction state");
			break;
		}
	}
	
	public int energyRequirement(Player p){
		int requirement = 0;
		for (Tile t : p.getTiles()){
			if (t.getMule() != null && t.getMule().getOutfit() != Outfit.Energy)
				requirement++;
		}
		return requirement;
	}
	
	public int calculateProduction(Player p, int r){
		Tile[] tiles = p.getTiles();
		int total = 0;
		for(Tile t : tiles){
			if (t.getOutfit() != null && t.getOutfit().id == r)
				total += t.getProduction();
		}
		return total;
	}
	
	public int getPlayerPrice(Player p){
		return store.getPrice(resource) + (int)(35 * (baseY-p.getBottom())/((float)baseY-topY));
	}
	
	public String getPlayerPriceString(Player p){
		if (seller[findPlayerInt(p)]){
			if (p.getBottom() >= topY)
				return getPlayerPrice(p)+"";
			else
				return "SELL";
		} else{
			if (p.getBottom() <= baseY)
				return getPlayerPrice(p)+"";
			else
				return "BUY";
		}
	}
	
	public int getBuyerPrice(){
		if (highestBidder == null)
			return store.getPrice(resource);
		return getPlayerPrice(highestBidder);
	}
	
	public int getSellerPrice(){
		if (lowestSeller == null)
			return store.getPrice(resource)+35;
		return getPlayerPrice(lowestSeller);
	}
	
	public void setSeller(int i, boolean seller){
		this.seller[i] = seller;
		if (this.seller[i])
			players[i].setBottom(topY-distAbove);
		else
			players[i].setBottom(baseY+distBelow);
	}
	
	@Override
	public void update(float deltaTime){
		switch (stage){
		case 6:
			for (int i = 0; i < players.length; i++){
				Player p = players[i];
				if (p.getBottom() < baseY+distBelow && p.getBottom() > (topY+baseY)/2){
					setSeller(i,true);
				}
				if (p.getBottom() > topY-distAbove && p.getBottom() < (topY+baseY)/2){
					setSeller(i,false);
				}
			}
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			if (begin){
				timerA--;
				if (timerA < 1)
					setStage(stage+1);
			}
			break;
		case 7:
			checkBidders();
			
			for (int i = 0; i < players.length; i++){
				
				if (seller[i] == true){
					if (players[i].getResource(resource) <= requirement[i] || players[i].getResource(resource) < 1){
						if (players[i].getBottom() > topY){
							TextDisplay td;
							if (players[i].getResource(resource) >= 1)
								td = new TextDisplay(players[i].getCenterX(),240,"Seller at critical level!");
							else
								td = new TextDisplay(players[i].getCenterX(),240,"Seller is out!");
							td.setColor(Color.WHITE);
							td.setTimer(3*60);
							td.setHAlign(2);
							trading = false;
						}
						players[i].setBottom(topY-distAbove);
					}
					if (players[i].getBottom() >= topY && (lowestSeller == null || players[i].getBottom() > lowestSeller.getBottom()))
						lowestSeller = players[i];
				} else {
					while (players[i].getBottom() <= baseY && getPlayerPrice(players[i]) > players[i].getMoney())
						players[i].changeY(1);
					if (players[i].getBottom() <= baseY && (highestBidder == null || players[i].getBottom() < highestBidder.getBottom()))
						highestBidder = players[i];
				}
			}
			
			if (!trading){
				timerB = 0;
				if (highestBidder != null && lowestSeller != null){
					if (highestBidder.getBottom() == lowestSeller.getBottom()){
						trading = true;
						timerB = 30;
						timerC = 135;
					}
				}
				else if (highestBidder != null){
					if (highestBidder.getBottom() == topY && store.getResource(resource)>0){
						trading = true;
						timerB = 30;
						timerC = 135;
					}
				}
				else if (lowestSeller != null){
					if (lowestSeller.getBottom() == baseY){
						trading = true;
						timerB = 30;
						timerC = 135;
					}
				}
				if ((lowestSeller == null && highestBidder == null) && timerA < (seconds*2)/3)
					timerA-=2;
				timerA--;
			}
			else {
				timerB--;
				if (highestBidder != null && lowestSeller == null && highestBidder.getBottom() != topY)
					trading = false;
				else if (lowestSeller != null && highestBidder == null && lowestSeller.getBottom() != baseY)
					trading = false;
				if (highestBidder!= null && lowestSeller != null && highestBidder.getBottom() != lowestSeller.getBottom())
					trading = false;
				else {
					if (timerB < 1){
						if (highestBidder != null && lowestSeller != null){
							highestBidder.changeResource(resource, 1);
							lowestSeller.changeResource(resource, -1);
							highestBidder.changeMoney(-getBuyerPrice());
							lowestSeller.changeMoney(getSellerPrice());
							setAmount(findPlayerInt(highestBidder), highestBidder.getResource(resource));
							setAmount(findPlayerInt(lowestSeller), lowestSeller.getResource(resource));
						} else if (highestBidder != null){
							if (store.getResource(resource) > 0){
								store.changeResource(resource, -1);
								highestBidder.changeMoney(-(store.getPrice(resource)+35));
								highestBidder.changeResource(resource, 1);
								setAmount(findPlayerInt(highestBidder), highestBidder.getResource(resource));
							}
							if (store.getResource(resource) < 1){
								trading = false;
							}
						} else if (lowestSeller != null){
							store.changeResource(resource, 1);
							lowestSeller.changeMoney(store.getPrice(resource));
							lowestSeller.changeResource(resource, -1);
							setAmount(findPlayerInt(lowestSeller), lowestSeller.getResource(resource));
						}
						timerC*=2;
						timerC/=3;
						if (timerC < 20)
							timerC = 20;
						timerB = timerC;
					}
				}
			}
			if (timerA < 1){
				setStage(stage+1);
			}
			break;
		case 8:
			timerA--;
			break;
		}
	}
	
	private void checkBidders(){
		if (highestBidder != null)
			if (highestBidder.getBottom() > baseY){
				highestBidder = null;
				timerB = 30;
				timerC = 135;
			}
		if (lowestSeller != null)
			if (lowestSeller.getBottom() < topY){
				lowestSeller = null;
				timerB = 30;
				timerC = 135;
			}
	}
	
	public boolean getFinished(){
		return (stage == 8 && timerA < 1);
	}
	
	public void paint(Graphics g){
		
		if (stage > 5 && stage < 8){
			g.setColor(Color.LIGHT_GRAY);
			Tools.drawTimeBar(g, 200, 36, 240, timerA/((float)seconds));
		}
		if (stage > 5){
			g.setColor(Color.white);
			g.drawLine(80, baseY, 560, baseY);
			g.drawLine(80, topY, 560, topY);
			
			for (int i = 0; i < players.length; i++)
				drawAmounts[i] = amounts[i];
			
			int lineY2 = lowestSeller == null ? topY : lowestSeller.getBottom();
			if (lowestSeller == null)
				g.setColor(Color.ORANGE);
			else
				g.setColor(lowestSeller.getColor());
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 110; j+=4)
					if (j%8 != 0)
						g.drawLine(100+j*4, lineY2-i+2, 100+(j+4)*4-1, lineY2-i+2);
			
			if (store.getResource(resource) > 0){
				g.setColor(Color.ORANGE);
				g.drawString(store.getPrice(resource)+35+"", 600, topY+4);
				Tile.drawHouse(g, 580, topY-4);
			}
			
			int lineY1 = highestBidder == null ? baseY : highestBidder.getBottom();
			if (highestBidder == null)
				g.setColor(Color.ORANGE);
			else
				g.setColor(highestBidder.getColor());
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 110; j+=4)
					if (j%8 == 0)
						g.drawLine(100+j*4, lineY1-i+2, 100+(j+4)*4-1, lineY1-i+2);
			
			g.setColor(Color.ORANGE);
			g.drawString(store.getPrice(resource)+"", 36, baseY+4);
			Tile.drawHouse(g, 60, baseY-4);
			
			for (int i = 0; i < players.length; i++){
				if (players[i] != null){
					g.setColor(players[i].getColor());
					int dry = players[i].getY()+5;
					int drx = players[i].getX();
					if (seller[i])
						drx -= 32;
					else
						drx += 64;
					g.drawString(getPlayerPriceString(players[i]), drx, dry);
				}
			}
			
			for (Player p : players){
				if (p != null){
					p.getSprite().setImageSingle(0);
				}
			}
			
			if (trading){
				g.setColor(Color.WHITE);
				if (timerB > 4 && timerC/timerB < 2){
					if (lowestSeller != null){
						lowestSeller.getSprite().setImageSingle(1);
					} else {
						g.setColor(Color.WHITE);
						g.drawString(store.getPrice(resource)+35+"", 600, topY+4);
						Tile.drawHouse(g, 580, topY-4);
					}
				} else {
					if (highestBidder != null){
						highestBidder.getSprite().setImageSingle(1);
					} else {
						g.setColor(Color.WHITE);
						g.drawString(store.getPrice(resource)+"", 36, baseY+4);
						Tile.drawHouse(g, 60, baseY-4);
					}
				}
			}
		} else if (stage < 6){
			for (int i = 0; i < players.length; i++){
				if (drawAmounts[i] < amounts[i])
					drawAmounts[i]++;
				else if (drawAmounts[i] > amounts[i])
					drawAmounts[i]--;
				int x = players[i].getX() + 13;
				g.setColor(players[i].getColor());
				g.fillRect(x, 300-drawAmounts[i], 20, drawAmounts[i]);
				g.setColor(Color.LIGHT_GRAY);
				if (stage == 5){
					int surplus = (reAdjAmount(amounts[i])-requirement[i]);
					if (surplus > 0)
						seller[i] = true;
					String surplusString = surplus > 0 ? "+"+surplus : ""+surplus;
					g.drawString(surplusString, x-27, 300-adjAmount(requirement[i])+4);
					g.drawLine(x-2, 300-(adjAmount(requirement[i])), x+21, 300-adjAmount(requirement[i]));
				}
			}
		}
		for (int i = 0; i < players.length; i++){
			if (players[i] != null){
				g.setColor(Color.WHITE);
				g.drawString(reAdjAmount(drawAmounts[i])+"", players[i].getX(), 416);
			}
		}
		g.setColor(Color.WHITE);
		g.drawString("MONEY : ", 32, 400);
		g.drawString("UNITS : ", 32, 416);
	}

	
	// Not used //
	@Override
	public Player getWinningBidder(){return null;}
	@Override
	public Tile getPlot(){return null;}
	@Override
	public int getPrice(){return 0;}
}
