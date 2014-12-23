package models;
import java.util.Random;

import domain.MapState;
import models.Tile.TileType;
import ui.Game;
import ui.Sprite;


public class Map {

	private Tile[][] tiles;
	
	private char[] row0 = {'p','p','x','p','r','p','z','p','p'};
	private char[] row1 = {'p','x','p','p','r','p','p','p','z'};
	private char[] row2 = {'z','p','p','p','t','p','p','p','x'};
	private char[] row3 = {'p','y','p','p','r','p','y','p','p'};
	private char[] row4 = {'p','p','y','p','r','p','p','p','y'};
	private char[][] grid = {row0, row1, row2, row3, row4};
	
	private char[] randtypes = {'p','x','p','y','p','z','p'};
	
	public PlotBuyer plotBuyer;
	
	private Random rand;
	
	public Map(int type){
		setTiles(new Tile[5][9]);
		rand = new Random();
		if (type == 1){			//random
			generateRandomMap();
		}						
		int stx = 103;
		int sty = 64;
		for(int i=0; i<5; i++){
			for(int j=0; j<9; j++){
				char rep = grid[i][j];
				TileType t;
				if(rep == 'p'){
					t= TileType.Plains;
				}
				else if(rep== 'r'){
					t = TileType.River;
				}
				else if(rep == 'x'){
					t = TileType.Mountain1;
				}
				else if(rep == 'y'){
					t = TileType.Mountain2;
				}
				else if(rep == 'z'){
					t= TileType.Mountain3;
				}
				else{
					t = TileType.Town;
				}
				
				getTiles()[i][j] = new Tile(stx + j*Tile.tileWidth,sty + i*Tile.tileHeight,t);
				getTiles()[i][j].setLocationNum(i*9+j);
			}
		}
		afterMapCreation();
	}
	
	public Map(MapState m){
		tiles = new Tile[5][9];
		rand = new Random();
		for(int i=0; i<5; i++){
			for(int j=0; j<9; j++){
				this.tiles[i][j] = new Tile(m.tiles[i][j]);
			}
		}
		afterMapCreation();
	}
	
	private void afterMapCreation(){
		rand = new Random();
		getTile(0,4).setSprite(new Sprite("/Resources/map_r1.png",true));
		getTile(1,4).setSprite(new Sprite("/Resources/map_r2.png",true));
		getTile(3,4).setSprite(new Sprite("/Resources/map_r3.png",true));
		getTile(4,4).setSprite(new Sprite("/Resources/map_r4.png",true));
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public void generateRandomMap(){
		for(int x=0; x<5; x++){
			for(int y=0; y<9; y++){
				int r = rand.nextInt(7);
				grid[x][y] = randtypes[r];
			}
		}
		
		grid[0][4] = 'r';
		grid[1][4] = 'r';
		grid[2][4] = 't';
		grid[3][4] = 'r';
		grid[4][4] = 'r';
		
		for(int i=0; i<5; i++){
			for(int j =0; j<9; j++){
				System.out.print(grid[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public void setPlotBuyer(PlotBuyer pb){
		this.plotBuyer = pb;
	}
	
	public Tile getTile(int row, int col){
		if (row < 5 && row > -1 && col < 9 && col > -1)
			return getTiles()[row][col];
		return null;
	}
	
	public Outfit getOutfit(int row, int col){
		Tile t = getTile(row, col);
		if (t != null)
			return t.getOutfit();
		return null;
	}
	
	public Tile getTile(int i){
		return getTiles()[i/9][i%9];
	}
	
	public Tile getRandomEmptyTile(){
		Tile output = getTile(rand.nextInt(45));
		while (output.getOwner() != null || output.type==Tile.TileType.Town)
			output = getTile(rand.nextInt(45));
		return output;
	}
	
	public Tile getTownTile(){
		return getTiles()[2][4];
	}
	
	public void showMap(){
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 9; j++){
				getTiles()[i][j].setLocationNum(i*9+j);
				getTiles()[i][j].addSelf();
			}
		}
	}
	
	public void hideMap(){
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 9; j++)
				Game.s_instance.removeObject(getTiles()[i][j]);
		}
	}
	
}
