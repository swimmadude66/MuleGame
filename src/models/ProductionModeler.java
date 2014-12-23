package models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import networking.NetworkController;
import networking.Writer;
import ui.Game;
import ui.menu.TextDisplay;

public class ProductionModeler extends GameObject{

	private ArrayList<Tile> productionTiles;
	private ArrayList<Integer> events;
	
	private Random rand;
	private Player[] players;
	
	private Map map;
	
	private int timer;
	
	private boolean begin;
	
	public ProductionModeler(Map map, Player[] players){
		super(0,0);
		
		this.players = players;
		this.map = map;
		productionTiles = new ArrayList<Tile>();
		rand = new Random();
		reset();
		/*
		Pest Attack	3
		Pirate Ship	2
		Acid Rain Storm	3
		Planetquake	3
		Sunspot Activity	3
		Meteorite Strike	2
		Radiation	2
		Fire in Store	2
		*/
	}
	
	private void reset(){
		begin = false;
		timer = 60*4;
	}
	
	public void generateRandomEvents(){
		events = new ArrayList<Integer>();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int[] event = {1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8};
		for (int i = 0; i < event.length; i++){
			temp.add(event[i]);
		}
		for (int i = 0; i < 12; i++){
			int e = rand.nextInt(temp.size());
			events.add(temp.remove(e));
		}
		if (Game.s_instance.online){
			System.out.println("Sending round events.");
			Writer w = NetworkController.writer;
			w.writebyte(Writer.MSG_GAMEEVENTS);
			for (int i = 0; i < 12; i++)
				w.writebyte(events.get(i));
			w.sendmessage();
		}
	}
	
	public void setEvents(ArrayList<Integer> e){
		System.out.println("Setting events.");
		this.events = e;
	}
	
	public void randomEvent(){
		addSelf();
		productionTiles.clear();
		for (int i = 0; i < 45; i++){
			if (map.getTile(i).getOwner() != null){
				productionTiles.add(map.getTile(i));
			}
		}
		
		begin = true;
		TextDisplay td = new TextDisplay(640,410);
		td.setColor(Color.WHITE);
		td.setSpeed(-2,0);
		td.setTimer(60*20);
		
		Tile[] tiles;
		int e = events.remove(0);
		switch(e){
		case 1:
			td.setText("PEST ATTACK! SOME FOOD PRODUCTION REDUCED");
			tiles = players[0].getTiles(Outfit.Food);
			if (tiles.length > 0)
				tiles[rand.nextInt(tiles.length)].setProduction(0);
			tiles = players[1].getTiles(Outfit.Food);
			if (tiles.length > 0)
				tiles[rand.nextInt(tiles.length)].setProduction(0);
			break;
		case 2:
			td.setText("SMITHORE PIRATE ATTACK. ALL SMITHORE UNITS HAVE BEEN STOLEN.");
			Game.getStore().clearResource(3);
			for (Player p : players){
				if (p != null){
					p.setResource(3, 0);
					for (Tile t : p.getTiles(Outfit.Smithore)){
						t.setProduction(0);
					}
				}
			}
			break;
		case 3:
			td.setText("RAIN STORM OF UNKNOWN ORIGIN.");
			int row = rand.nextInt(5);
			for (int i = 0; i < 9; i++){
				Tile t = map.getTile(row,i);
				if (t == null)
					continue;
				if (t.getOutfit() == Outfit.Food)
					t.changeProduction(3);
				if (t.getOutfit() == Outfit.Energy)
					t.changeProduction(-2);
			}
			for (int i = 0; i < 9; i++){
				Tile t = map.getTile(row-1,i);
				if (t == null)
					continue;
				if (t.getOutfit() == Outfit.Food)
					t.changeProduction(1);
				if (t.getOutfit() == Outfit.Energy)
					t.changeProduction(-1);
			}
			for (int i = 0; i < 9; i++){
				Tile t = map.getTile(row+1,i);
				if (t == null)
					continue;
				if (t.getOutfit() == Outfit.Food)
					t.changeProduction(1);
				if (t.getOutfit() == Outfit.Energy)
					t.changeProduction(-1);
			}
			break;
		case 4:
			td.setText("PLANETQUAKE. ALL MINING PRODUCTION HALVED.");
			for (Tile t : productionTiles){
				if (t.getOutfit() == Outfit.Smithore)
					t.setProduction(t.getProduction()/2);
			}
			break;
		case 5:
			td.setText("SUNSPOT ACTIVITY. ENERGY LEVELS RISE. CROPS HURT.");
			for (Tile t : productionTiles){
				if (t.getOutfit() == Outfit.Energy)
					t.changeProduction(3);
				else if (t.getOutfit() == Outfit.Food)
					t.changeProduction(-1);
			}
			break;
		case 6:
			td.setText("METEOR STRIKE! RARE MINERALS DISCOVERED. COLONY RECIEVED BONUS.");
			for (Player p : players){
				if (p != null){
					p.changeMoney(55*Game.s_instance.getRound());
				}
			}
			break;
		case 7:
			td.setText("UNDERGROUND RADIATION DISCOVERED. UNKNOWN CONSEQUENCES.");
			tiles = players[0].getTiles();
			if (tiles.length > 0){
				tiles[rand.nextInt(tiles.length)].setProduction(0);
				tiles[rand.nextInt(tiles.length)].setProduction(0);
			}
			tiles = players[1].getTiles();
			if (tiles.length > 0)
				tiles[rand.nextInt(tiles.length)].setProduction(0);
			break;
		case 8:
			td.setText("FIRE IN STORE! ALL STORE GOODS LOST IN FIRE.");
			for (int i = 1; i < 5; i++){
				Game.getStore().clearResource(i);
			}
			break;
		}
		begin = true;
	}
	
	public void update(float deltaTime){
		if (begin){
			timer--;
			if (timer < 1){
				if (productionTiles.size() > 0){
					timer = 15;
					int i = rand.nextInt(productionTiles.size());
					Tile t = productionTiles.get(i);
					t.updateShownProduction();
					if (t.getShownProduction() == t.getProduction()){
						productionTiles.remove(t);
					}
					if (productionTiles.size() == 0)
						timer = 60*6;
				} else {
					Game.s_instance.initState(30, true);
					reset();
				}
			}
		}
	}
	
}
