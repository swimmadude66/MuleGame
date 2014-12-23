package domain;

import models.Map;

public class MapState {
	public TileState[][] tiles;
	int id;
	
	public MapState(Map m){
		tiles = new TileState[5][9];
		for(int i =0; i<5; i++){
			for(int j=0; j<9; j++){
				this.tiles[i][j] = new TileState(m.getTiles()[i][j]);
			}
		}
	}
	
	public MapState(){
		
	}
}
