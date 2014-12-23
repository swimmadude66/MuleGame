package models;

import java.awt.Color;
import java.awt.Graphics;

import networking.NetworkController;
import networking.Writer;
import ui.Game;

public class PlotSelectBuyer extends PlotBuyer{

	private Map map;
	private int tNum, timer;
	
	private Tile currentTile;
	
	private boolean[] chosen;
	
	public PlotSelectBuyer(Map map, int numPlayers){
		this.map = map;
		tNum = -1;
		timer = 180;
		
		chosen = new boolean[numPlayers];
		for (int i = 0; i < numPlayers; i++){
			chosen[i] = false;
		}
		
		currentTile = null;
	}
	
	public Tile getCurrentTile(){
		return currentTile;
	}
	
	public void select(Player player){
		if (currentTile == null)
			return;
		if (currentTile.getOwner() == null && chosen[player.getPlayerNum()] == false){
			if (!Game.s_instance.online){
				currentTile.setOwner(player);
				player.addTile(currentTile);
				timer = 60;
				chosen[player.getPlayerNum()] = true;
			} else {
				Writer w = NetworkController.writer;
				w.writebyte(Writer.MSG_BUYPLOT);
				w.writebyte(tNum);
				w.sendmessage();
			}
		}
	}
	
	public void select(Player player, int tNum){
		if (map.getTile(tNum/9, tNum%9).getOwner() == null){
			this.tNum = tNum;
			currentTile = map.getTile(tNum/9,tNum%9);
			currentTile.setOwner(player);
			player.addTile(currentTile);
			timer = 60;
			chosen[player.getPlayerNum()] = true;
		}
	}
	
	public boolean getFinished(){
		boolean finished = true;
		for (int i = 0; i < chosen.length; i++)
			if (!chosen[i])
				finished = false;
		if (timer > 2)
			finished = false;
		return finished;
	}
	
	@Override
	public void update(float deltaTime){
		timer--;
		if (timer < 1){
			tNum++;
			if (currentTile != null)
				currentTile.setSelected(false);
			currentTile = map.getTile(tNum/9, tNum%9);
			if (currentTile == null){
				for (int i = 0; i < chosen.length; i++)
					chosen[i] = true;
			}
			else{
				while (currentTile != null && (currentTile.type == Tile.TileType.Town || currentTile.getOwner() != null)){
					tNum++;
					currentTile = map.getTile(tNum/9, tNum%9);
				}
				if (currentTile == null)
					for (int i = 0; i < chosen.length; i++)
						chosen[i] = true;
				else
					currentTile.setSelected(true);
			}
			timer = 20;
		}
	}
	
	public void paint(Graphics g){
		if (currentTile != null){
			if (currentTile.getOwner() == null)
				g.setColor(Color.DARK_GRAY);
			else
				g.setColor(currentTile.getOwner().getColor());
			for (int i = 0; i < 5; i++){
				g.drawRect(currentTile.getX()+i,currentTile.getY()+i,currentTile.getWidth()-2*i-1,currentTile.getHeight()-2*i-1);
			}
		}
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("LAND GRANT FOR MONTH "+Game.s_instance.getRound(), 220, 32);
	}
	
}
