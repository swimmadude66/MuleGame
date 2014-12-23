package domain;

import models.Store;

import ui.Game;



/**
 * GameState
 * @author The Moose Express
 *
 * This class handles saves the state of all Game variables
 */
public class GameState {

		public int id;
		public GameData data;
		public int currentPlayerNum;
		public long timer;
		public int round;
		public MapState map;
		public Store store;
		public boolean[] playersReady;
		public int gameState;
		public PlayerState[] players;
	
		
		public GameState(Game g){
			this.data=(g.getData());
			this.currentPlayerNum = g.getCurrentPlayer().getPlayerNum(); 
			this.timer = g.getTimer();
			this.round = g.getRound();
			this.map = new MapState(g.getMap());
			this.store = Game.getStore();
			this.playersReady = g.getPlayersReady();
			this.gameState = g.getGameState();
			this.players = new PlayerState[g.getData().numplayers];
			for(int i =0; i<g.getData().numplayers; i++){
				this.players[i] = new PlayerState(g.getPlayers()[i]);
			}	
		}
		
		public GameState(){
			
		}

		
	
}
