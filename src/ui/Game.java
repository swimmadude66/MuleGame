// TODO
// Sync player's info at the end of every auction.

package ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.Timer;

import models.AuctionHandler;
import models.GameObject;
import models.LandAuctionHandler;
import models.Map;
import models.Mule;
import models.Outfit;
import models.Player;
import models.PlotPreAuction;
import models.PlotSelectBuyer;
import models.ProductionModeler;
import models.ResourceAuctionHandler;
import models.Store;
import models.Tile;
import models.Tools;
import models.Town;
import networking.NetworkController;
import networking.Writer;
import ui.menu.Button;
import ui.menu.Menu;
import ui.menu.NumberHolder;
import ui.menu.OptionHolder;
import ui.menu.OptionPool;
import ui.menu.TextDisplay;
import ui.menu.TextEntry;
import ui.menu.TextHolder;

import com.google.gson.Gson;

import domain.DataDao;
import domain.GameData;
import domain.GameState;

/**
 * Game Class
 * @author The Moose Express
 *
 * This class handles all Game architecture and handling.
 */
public class Game extends JComponent{

	// This is because we are a JComponent.
	private static final long serialVersionUID = 1L;
	
	public static final boolean DEVELOPER 	= false;
	public static final boolean SAVE 		= false;
	public static final boolean EXIT_ON_ESCAPE = true;
	
	private GameData data;
	
	public boolean online;
	public boolean host;
	public boolean[] onlinePlayersReady;
	
	private Player[] players;
	private int[] randomEvents;
	private Input[] inputs;
	private Player currentPlayer;
	private long timer;
	private int round;
	private static Map map;
	private static Store store;
	private static ProductionModeler productionModeler;
	
	private Random rand;
	
	private boolean[] playersReady;
	
	private ArrayList<GameObject> objects, toAdd, toRemove;
	
	private ArrayList<Tile> plotsForSale;
	
	private int windowWidth, windowHeight;
	private int fps, fpscounter;
	private long fpsStartTime;
	
	private int gameState;
	
	public static boolean freezeInput;
	
	private Timer gameRunner;
	//private enum currentGameState;
	
	private Background background;
	
	private Gson gson;
	private GameState state;
	
	public static Game s_instance;
	
	private AuctionHandler auction;
	
	private Color bg_color;
	
	public Game(int windowWidth, int windowHeight){
		if (s_instance == null)
			s_instance = this;
		
		this.setWindowWidth(windowWidth);
		this.setWindowHeight(windowHeight);
		
		// All of our arraylists for objects
		objects = new ArrayList<GameObject>();
		setToAdd(new ArrayList<GameObject>());
		setToRemove(new ArrayList<GameObject>());
		gson = new Gson();
		init();
	}
	

	public GameData getData() {
		return data;
	}


	public void setData(GameData data) {
		this.data = data;
	}


	public Player[] getPlayers() {
		return players;
	}


	public void setPlayers(Player[] players) {
		this.players = players;
	}


	public Input[] getInputs() {
		return inputs;
	}


	public void setInputs(Input[] inputs) {
		this.inputs = inputs;
	}


	public Player getCurrentPlayer() {
		return currentPlayer;
	}


	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}


	public long getTimer() {
		return timer;
	}


	public void setTimer(long timer) {
		this.timer = timer;
	}


	/**
	 * @return the playersReady
	 */
	public boolean[] getPlayersReady() {
		return playersReady;
	}


	/**
	 * @param playersReady the playersReady to set
	 */
	public void setPlayersReady(boolean[] playersReady) {
		this.playersReady = playersReady;
	}


	/**
	 * @return the toAdd
	 */
	public ArrayList<GameObject> getToAdd() {
		return toAdd;
	}


	/**
	 * @param toAdd the toAdd to set
	 */
	public void setToAdd(ArrayList<GameObject> toAdd) {
		this.toAdd = toAdd;
	}


	/**
	 * @return the toRemove
	 */
	public ArrayList<GameObject> getToRemove() {
		return toRemove;
	}


	/**
	 * @param toRemove the toRemove to set
	 */
	public void setToRemove(ArrayList<GameObject> toRemove) {
		this.toRemove = toRemove;
	}


	/**
	 * @return the plotsForSale
	 */
	public ArrayList<Tile> getPlotsForSale() {
		return plotsForSale;
	}


	/**
	 * @param plotsForSale the plotsForSale to set
	 */
	public void setPlotsForSale(ArrayList<Tile> plotsForSale) {
		this.plotsForSale = plotsForSale;
	}


	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		return windowWidth;
	}


	/**
	 * @param windowWidth the windowWidth to set
	 */
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}


	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}


	/**
	 * @param windowHeight the windowHeight to set
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}


	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}


	/**
	 * @param fps the fps to set
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}


	/**
	 * @return the fpscounter
	 */
	public int getFpscounter() {
		return fpscounter;
	}


	/**
	 * @param fpscounter the fpscounter to set
	 */
	public int setFpscounter(int fpscounter) {
		this.fpscounter = fpscounter;
		return fpscounter;
	}


	/**
	 * @return the gameState
	 */
	public int getGameState() {
		return gameState;
	}


	/**
	 * @param gameState the gameState to set
	 */
	public void setGameState(int gameState) {
		this.gameState = gameState;
	}


	/**
	 * @return the gameRunner
	 */
	public Timer getGameRunner() {
		return gameRunner;
	}


	/**
	 * @param gameRunner the gameRunner to set
	 */
	public void setGameRunner(Timer gameRunner) {
		this.gameRunner = gameRunner;
	}


	/**
	 * @return the bg_color
	 */
	public Color getBg_color() {
		return bg_color;
	}


	/**
	 * @param bg_color the bg_color to set
	 */
	public void setBg_color(Color bg_color) {
		this.bg_color = bg_color;
	}
	
	public void saveGame(){
		if (online)
			return;
		try {
			state = new GameState(this);
			//String saveFile = gson.toJson(state);
			/*
			DomainReadWrite writer = new DomainReadWrite();
			writer.saveGame(saveFile);
			*/
			DataDao dao= new DataDao();
			//dao.saveSerializedToDB(saveFile);
			dao.saveGameToDB(state);
			TextDisplay td = new TextDisplay(32,48,"Game saved.");
			td.setColor(Color.WHITE);
			td.setTimer(3*60);
		} catch (Exception e) {
			System.err.println("Could not save!");
			TextDisplay td = new TextDisplay(32,48,"Failed to save game.");
			td.setColor(Color.WHITE);
			td.setTimer(3*60);
		}
	}
	
	public void loadGame(){
		//DomainReadWrite reader = new DomainReadWrite();
		//String loadFile = reader.loadGame();
		DataDao dao = new DataDao();
		//String loadFile = dao.loadSerializedFromDB();
		//if(loadFile != null){
			//state = gson.fromJson(loadFile, GameState.class);
			state = dao.loadGameFromDB();
			//re-initialize values
			this.data = state.data;
			this.timer = state.timer;
			this.round = state.round;
			this.round++;
			map = new Map(state.map);
			store = state.store;
			this.playersReady = state.playersReady;
			this.gameState = state.gameState;
			this.players = new Player[state.players.length];
			for (int i=0; i<state.players.length; i++){
				this.players[i] = new Player(state.players[i]);
				this.players[i].setInput(getInputs()[state.players[i].playerNum]);
			}
			this.currentPlayer = players[state.currentPlayerNum];
			
			
			initState(4, true);
			TextDisplay td = new TextDisplay(32,48,"Loaded previous game.");
			td.setColor(Color.WHITE);
			td.setTimer(3*60);
		/*}
		else{
			TextDisplay td = new TextDisplay(32,48,"COULD NOT LOAD GAME");
			td.setColor(Color.WHITE);
			td.setTimer(3*60);
		}
		*/
	}
	


	/**
	 * Initializes everything for the game.
	 */
	public void init(){
		round = 0;
		setGameState(1);
		
		setFps(setFpscounter(0));
		
		rand = new Random();
		
		setFocusable(true);
		
		background = new Background(getWindowWidth(), getWindowHeight());
		background.addBackground(new Sprite("/Resources/Menu.png",true));
		
		freezeInput = false;
		
		online = false;
		
		setAuction(null);
		
		setPlayers(new Player[4]);
		setInputs(new Input[4]);
		
		// Sets up all four players and their input
		for (int i = 0; i < 4; i++){
			getPlayers()[i] = new Player("Name", i, 0, 0);
			getPlayers()[i].setState(1);
			getPlayers()[i].setVisible(false);
			getInputs()[i] = new Input(getPlayers()[i]).setPresetBindings(i);
			getPlayers()[i].setInput(getInputs()[i]);
			addKeyListener(getInputs()[i]);
		}
		setCurrentPlayer(null);
		
		setPlotsForSale(new ArrayList<Tile>());
		
		initState(getGameState(), true);
		
		// This is the game loop. Runs at ~60 FPS
		setGameRunner(new Timer(15,new GameRunner()));
		getGameRunner().start();
	}
	
	/**
	 * Sets up everything necessary for the current state of the game.
	 * 
	 * @param state the current state
	 */
	public void initState(int state, boolean clear){
		setGameState(state);
		if (DEVELOPER)
			System.out.println("INIT STATE "+state);
		if (clear)
			clear();
		switch(state){
		case (1):
			Menu menu = new Menu();
			for (int i = 0; i < 4; i++)
			if (getInputs()[i] != null)
				getInputs()[i].setInputReceiver(menu);
			setBg_color(Color.WHITE);
			// Title screen.
			GameObject title = new GameObject(0,0,new Sprite("/Resources/mainimg.png", true));
			title.setCenter(320, 200);
			Button startGame = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(51,true);
									}
									}, 0, title.getY()+title.getHeight(), "Start New Game");
			Button loadGame = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										Game.s_instance.loadGame();
									}
									}, 0, title.getY()+title.getHeight(), "Load Game");
			Button editInput = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(50,true);
									}
									}, 0, title.getY()+title.getHeight(), "Edit Settings");
			startGame.setWidth(title.getWidth()/3-16);
			loadGame.setWidth(title.getWidth()/3-16);
			editInput.setWidth(title.getWidth()/3-16);
			startGame.setCenter(title.getX()+title.getWidth()/6, startGame.getCenterY());
			loadGame.setCenter(title.getX()+3*title.getWidth()/6, startGame.getCenterY());
			editInput.setCenter(title.getX()+5*title.getWidth()/6, startGame.getCenterY());
			menu.addMenuOption(startGame);
			menu.addMenuOption(loadGame);
			menu.addMenuOption(editInput);
			break;
		
		case 2:
			setupGameConfig();
			break;
		case 3:
			setupPlayerJoinMenu();
			break;
		case 4:
			// START NEW ROUND //
			setupLandGrant();
			break;
		case 5:
			setupPreAuction();
			break;
		case 6:
			setupPreTurn();
			break;
		case 7:
			setupTown();
			break;
		case 8:
			// End of turn timer
			setTimer(4*60);
			break;
		case 9:
			setupDevelopmentView();
			break;
		case 10:
			setupLandAuction();
			break;
		case 30:
			setupResourceAuction(3);
			break;
		case 31:
			setupResourceAuction(1);
			break;
		case 32:
			setupResourceAuction(2);
			break;
		case 40:
			setupScoreDisplay();
			System.out.println("Saving game...");
			saveGame();
			break;
		case 50:
			setupPlayerInputConfig();
			break;
		case 51:
			Menu menu2 = new Menu();
			for (int i = 0; i < 4; i++)
			if (getInputs()[i] != null)
				getInputs()[i].setInputReceiver(menu2);
			Button localButton = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(2,true);
									}
									}, 0, 0, "Local Game");
			Button onlineButton = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(52,true);
									}
									}, 0, 0, "Online Game");
			localButton.setWidth(128);
			onlineButton.setWidth(128);
			localButton.setCenter(320,200);
			onlineButton.setCenter(320,280);
			menu2.addMenuOption(localButton);
			menu2.addMenuOption(onlineButton);
			break;
		case 52:
			initState(53,true);
			NetworkController.initialize();
			if (NetworkController.connect()){
				online = true;
			} else {
				initState(1,true);
				new TextDisplay(16,32,"Could not connect to central server.").setColor(Color.WHITE);
			}
			break;
		case 53:
			new TextDisplay(280,220,"Please wait...").setColor(Color.WHITE);
			break;
		case 54:
			Menu menu3 = new Menu();
			for (int i = 0; i < 4; i++)
			if (getInputs()[i] != null)
				getInputs()[i].setInputReceiver(menu3);
			Button createButton = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(2,true);
										host = true;
										onlinePlayersReady = new boolean[players.length];
									}
									}, 0, 0, "Create Game");
			Button joinButton = new Button(new ActionListener(){
									public void actionPerformed(ActionEvent e) {
										initState(53,true);
										host = false;
										Writer w = NetworkController.writer;
										w.clearbuffer();
										w.writebyte(Writer.MSG_JOIN);
										w.sendmessage();
									}
									}, 0, 0, "Join Game");
			createButton.setWidth(128);
			joinButton.setWidth(128);
			createButton.setCenter(320,200);
			joinButton.setCenter(320,280);
			menu3.addMenuOption(createButton);
			menu3.addMenuOption(joinButton);
			break;
		default:
			System.err.println("Invalid Game state : "+state);
			break;
		}
	}
	
	public void setupPlayerInputConfig(){
		Menu menu = new Menu();
		for (Input i : inputs){
			if (i != null)
				i.setInputReceiver(menu);
		}
		for (Player p : players){
			if (p != null){
				p.setState(50);
				p.setVisible(false);
			}
		}
		new TextDisplay(320,20,"Change Input of Player: ").setHAlign(2).setColor(Color.WHITE);
		final NumberHolder nh = new NumberHolder(0, 40, 1, 4, null);
		nh.setCenter(320,nh.getCenterY());
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				nh.decrement();
			}
			
		}, 320-80, 32, "<"));
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				nh.increment();
			}
			
		}, 320+32, 32, ">"));
		final InputCreator ic = new InputCreator(players, nh, menu);
		addKeyListener(ic);
		Button b;
		menu.addMenuOption(b = new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ic.begin();
			}
		}, 0, 32, "Edit Bindings"));
		b.setWidth(104);
		b.setHeight(32);
		b.setCenter(320,112);
		menu.addMenuOption(b = new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Game.s_instance.initState(1, true);
			}
		}, 32, 480-96, "Back"));
	}
	
	public void playerReady(Player p){
		for (int i = 0; i < getPlayers().length; i++){
			if (getPlayers()[i] == p)
				getPlayersReady()[i] = true;
		}
	}
	
	/**
	 * Sets up the menu for choosing overall game settings.
	 * 
	 * @param menu
	 */
	public void setupGameConfig(){
		Menu menu = new Menu();
		for (int i = 0; i < 4; i++)
			if (getInputs()[i] != null)
				getInputs()[i].setInputReceiver(menu);
		// This is where we begin the menu.
		int xStart = 282;
		int yStart = 92;
		int yBreak = 96;
		
		// Choose number of players
		new TextDisplay(xStart-148,yStart+28,"Number of Players: ").setHAlign(1).setColor(Color.WHITE);
		final NumberHolder nh = new NumberHolder(xStart+80, yStart+8, 2, 4, null);
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				nh.decrement();
			}
			
		}, xStart, yStart, "<"));
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				nh.increment();
			}
			
		}, xStart + 144, yStart, ">"));
		
		// Choose difficulty
		new TextDisplay(xStart-148,yStart+28+yBreak,"Difficulty: ").setHAlign(1).setColor(Color.WHITE);
		final TextHolder th = new TextHolder(xStart+64, yStart+8+yBreak, new String[] {"Beginner", "Standard", "Hard"}, null);
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				th.decrement();
			}
			
		}, xStart, yStart + yBreak, "<"));
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				th.increment();
			}
			
		}, xStart + 144, yStart+yBreak, ">"));
		th.increment();
		
		
		// Choose map style
		new TextDisplay(xStart-148,yStart+28+yBreak*2,"Map Style: ").setHAlign(1).setColor(Color.WHITE);
		final TextHolder th2 = new TextHolder(xStart+64, yStart+8+yBreak*2, new String[] {"Standard", "Random"}, null);
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				th2.decrement();
			}
			
		}, xStart, yStart+yBreak*2, "<"));
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				th2.increment();
			}
			
		}, xStart + 144, yStart+yBreak*2, ">"));
		
		
		// The next button.
		menu.addMenuOption(new Button(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setData(new GameData(nh.getNumber(), th2.getNumber()==1, th.getNumber()));
				// This for loops deletes every player && input that is not playing.
				for (int p = getData().numplayers; p < 4; p++){
					getInputs()[p] = null;
					getPlayers()[p] = null;
				}
				
				if (!online)
					initState(3,true);
				else {
					initState(53, true);
					Writer w = NetworkController.writer;
					w.writebyte(Writer.MSG_NEWGAME);
					w.writebyte(getData().numplayers);
					w.writeboolean(getData().isRandom);
					w.writebyte(getData().difficultyId);
					w.sendmessage();
				}
			}}, 180, 396, new Sprite("/Resources/Button_next.png",true)));
	}
	
	/**
	 * Sets up the menu for players to choose their settings.
	 */
	public void setupPlayerJoinMenu(){
		// Coordinates of each menu.
		final int[] xStart = {8, 328, 8, 328};
		final int[] yStart = {32, 32, 196, 196};
		
		TextDisplay pStart = new TextDisplay(320,180,"Press START when ready");
		pStart.setHAlign(2);
		pStart.setColor(Color.WHITE);
		
		// This is our OptionPool that holds various colors. If a color is taken, it cannot be taken again.
		final OptionPool<GameColor> op = new OptionPool<GameColor>(4);
		op.addOption(GameColor.BLUE); op.addOption(GameColor.RED); op.addOption(GameColor.GREEN); op.addOption(GameColor.YELLOW);
		for (int i = 0; i < getData().numplayers; i++){
			// Create a new menu for every player and set that player's Input to go to that menu.
			final Menu menu = new Menu();
			getInputs()[i].setInputReceiver(menu);
			
			new GameObject(xStart[i],yStart[i],new Sprite("/Resources/player_human.png",true));
			new TextDisplay(xStart[i]+24,yStart[i]+116,"Player "+(i+1)).setHAlign(1).setColor(Color.BLACK);
			
			// Race chooser
			final TextHolder th = new TextHolder(xStart[i]+104, yStart[i]+8, new String[] {"Human", "Flapper", "Bonzoid", "Ugaite", "Buzzite"}, null).setWrap(true);
			th.setWidth(96);
			menu.addMenuOption(new Button(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					th.increment();
					//ih.increment();
				}
				
			}, xStart[i] + 208, yStart[i]+8, "Change Race").setSize(96,32));

			// Color chooser
			final OptionHolder<GameColor> oh = new OptionHolder<GameColor>(xStart[i]+104, yStart[i] + 48, op, null).setOption(i);
			oh.setWidth(96);
			menu.addMenuOption(new Button(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					oh.increment();
				}
			}, xStart[i] + 208, yStart[i]+48, "Change Color").setSize(96,32));
			
			final int j = i;
			// Name chooser
			final TextEntry te = new TextEntry(
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							freezeInput();
					}},
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							unFreezeInput();
					}}, xStart[i] + 140, yStart[i]+88, 128, 32);
			addKeyListener(new Input(te));
			menu.addMenuOption(te);
			
			// Press Start listener
			menu.setStartListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					focusInputOnPlayer(getPlayers()[j]);
					oh.lockIn();
					menu.deactivate();
					getPlayers()[j].setState(2);
					getPlayers()[j].setName("".equals(te.getText()) ? "Player "+(j+1) : te.getText());
					getPlayers()[j].setColor(oh.getOption());
					getPlayers()[j].setRace(th.getNumber());
					new TextDisplay(xStart[j]+48,yStart[j]+102,"READY").setHAlign(2).setColor(Color.RED);
				}
			});
		}
	}
	
	/**
	 * This creates the game map.
	 */
	public void setupGameMap(){
		map = new Map(getData().isRandom ?1:0);
		store = new Store(getData().difficultyId);
		
		productionModeler = new ProductionModeler(map, getPlayers());
		
		reOrderPlayers(true);
	}
	
	/**
	 * This is the setup for Land Grant
	 */
	public void setupLandGrant(){
		if (data.difficultyId == 0)
			Tile.maxProduction = 8;
		else if (data.difficultyId == 1)
			Tile.maxProduction = 10;
		else
			Tile.maxProduction = 12;
		
		for (Player p : getPlayers())
			if (p != null){
				p.setState(4);
				p.setVisible(false);
				focusInputOnPlayer(p);
			}
		for (int i = 0; i < 45; i++){
			map.getTile(i).setProduction(0);
		}
		map.showMap();
		if (!online || host){
			//new TextDisplay(16,32,"generating tiles for sale").setColor(Color.WHITE);
			int amount = 1;
			if (round > 1)
				amount = rand.nextInt(4);
			for (int i = 0; i < amount; i++){
				Tile newTile = map.getRandomEmptyTile();
				if (!getPlotsForSale().contains(newTile))
					getPlotsForSale().add(newTile);
			}
			if (online){
				Writer w = NetworkController.writer;
				w.writebyte(Writer.MSG_PLOTSALE);
				w.writebyte(getPlotsForSale().size());
				for (Tile t : getPlotsForSale())
					w.writebyte(t.getLocationNum());
				w.sendmessage();
			}
		}
		map.setPlotBuyer( new PlotSelectBuyer(map, getData().numplayers) );
	}
	
	/**
	 * This is the step before the land auction at the start of most rounds
	 */
	public void setupPreAuction(){
		// Check for tiles to be sold
		map.showMap();
		Tile toSell = null;
		
		for (int i = 0; i < getPlotsForSale().size(); i++){
			if (getPlotsForSale().get(i).getOwner() != null){
				getPlotsForSale().remove(i);
				if (round == 1 && !online)
					getPlotsForSale().add(map.getRandomEmptyTile());
			}
		}
		
		if (getPlotsForSale().size() > 0){
			toSell = getPlotsForSale().remove(0);
		}
		
		for (Player p : getPlayers()){
			if (p != null){
				p.setState(5);
				bringToFront(p);
				focusInputOnPlayer(p);
			}
		}
		
		if (toSell == null){
			beginTurns();
		} else {
			if (getPlotsForSale().size() > 0)
				new TextDisplay(32,48,getPlotsForSale().size() + " plots remaining for sale").setColor(Color.WHITE);
			map.setPlotBuyer(new PlotPreAuction(map, toSell, getData().numplayers));
		}
	}
	
	/**
	 * This sets up the land auction at the start of most rounds.
	 */
	public void setupLandAuction(){
		for (Player p : getPlayers()){
			if (p != null){
				p.setState(10);
				p.setVisible(true);
				p.setX(128 + 96 * p.getPlayerNum());
				bringToFront(p);
				focusInputOnPlayer(p);
				if (!online){
					getAuction().begin();
				}
				else {
					Writer w = NetworkController.writer;
					w.writebyte(Writer.MSG_READY);
					w.sendmessage();
				}
			}
		}
	}
	
	/**
	 * This is the step before a players turn.
	 */
	public void setupPreTurn(){
		for (int i = 0; i < getPlayersReady().length; i++)
			getPlayersReady()[i] = false;
		for (Player p : getPlayers())
			if (p != null){
				if (p != getCurrentPlayer())
					playerReady(p);
				p.setVisible(false);
			}
		map.showMap();
		getCurrentPlayer().setState(6);
		getCurrentPlayer().setVisible(true);
		getCurrentPlayer().setWidth(32);
		getCurrentPlayer().setHeight(32);
		getCurrentPlayer().setCenter(320,228);
		bringToFront(getCurrentPlayer());
		if (getCurrentPlayer().getMule() != null){
			getCurrentPlayer().getMule().setVisible(true);
			bringToFront(getCurrentPlayer().getMule());
		}
		
		int y = 410;
		//TODO
		if (runRandomEvent(randomEvents[findPlayerIntByPlayer(getCurrentPlayer())],getCurrentPlayer()))
			y += 25;
		
		new TextDisplay(120, y, getCurrentPlayer().getName()+", press A to begin turn").setColor(Color.WHITE);
		
		// Calculates turn time
		int tableRequirement = Tools.foodRequirement(round);
		int seconds = 50;
		if (getCurrentPlayer().getFood() < tableRequirement){
			seconds = 5 + (int)((45f * getCurrentPlayer().getFood())/tableRequirement);
		}
		if (getCurrentPlayer().getFood() == 0)
			seconds = 5;
		
		setTimer(60*seconds);
	}
	
	public boolean runRandomEvent(int event, Player p){
		if (event >= 7)
			return false;
		TextDisplay td = new TextDisplay(640,410);
		td.setColor(Color.WHITE);
		td.setSpeed(-2,0);
		td.setTimer(60*20);
		int m = 25*(round/4+1);
		switch(event){
		case 0:
			p.changeFood(3);
			p.changeEnergy(2);
			td.setText("YOU JUST RECEIVED A PACKAGE FROM THE GT ALUMNI CONTAINING 3 FOOD AND 2 ENERGY UNITS");
			break;
		case 1:
			p.changeSmithore(2);
			td.setText("A WANDERING TECH STUDENT REPAID YOUR HOSPITALITY BY LEAVING TWO BARS OF ORE");
			break;
		case 2:
			int amount1 = 8*m;
			p.changeMoney(amount1);
			td.setText("THE MUSEUM BOUGHT YOUR ANTIQUE PERSONAL COMPUTER FOR "+amount1);
			break;
		case 3:
			int amount2 = 2*m;
			p.changeMoney(amount2);
			td.setText("YOU FOUND A DEAD MOOSE RAT AND SOLD THE HIDE FOR "+amount2);
			break;
		case 4:
			int amount3 = 4*m;
			p.changeMoney(-amount3);
			td.setText("FLYING CATBUGS ATE THE ROOF OFF YOUR HOUSE. REPAIRS COST "+amount3);
			break;
		case 5:
			p.changeFood(-p.getFood()/2);
			td.setText("MISCHIEVOUS UGA STUDENTS BROKE INTO YOUR STORAGE SHED AND STOLE HALF YOUR FOOD");
			break;
		case 6:
			int amount4 = 6*m;
			p.changeMoney(-amount4);
			td.setText("YOUR SPACE GYPSY INLAWS MADE A MESS OF THE TOWN. IT COST YOU "+ amount4 +" TO CLEAN IT UP");
			break;
		}
		return true;
	}
	
	public int randomEvent(Player p){
		int event = rand.nextInt(7);
		if ((p == getPlayers()[0] && event > 3) || rand.nextInt(10) > 4)
			return 7;
		return event;
		
/*		
		YOU JUST RECEIVED A PACKAGE FROM THE GT ALUMNI CONTAINING 3 FOOD AND 2 ENERGY UNITS.
		A WANDERING TECH STUDENT REPAID YOUR HOSPITALITY BY LEAVING TWO BARS OF ORE.
		THE MUSEUM BOUGHT YOUR ANTIQUE PERSONAL COMPUTER FOR $ 8*m.
		YOU FOUND A DEAD MOOSE RAT AND SOLD THE HIDE FOR $2*m.
		FLYING CAT-BUGS ATE THE ROOF OFF YOUR HOUSE. REPAIRS COST $4*m.
		MISCHIEVOUS UGA STUDENTS BROKE INTO YOUR STORAGE SHED AND STOLE HALF YOUR FOOD.
		YOUR SPACE GYPSY INLAWS MADE A MESS OF THE TOWN. IT COST YOU $6*m TO CLEAN IT UP.
*/
	}
	
	/**
	 * Sets up the town in game for the current player
	 */
	public void setupTown(){
		Town t = new Town(0,64,getCurrentPlayer());
		getCurrentPlayer().setState(7);
		getCurrentPlayer().getInput().setInputReceiver(t);
		bringToFront(getCurrentPlayer());
		if (getCurrentPlayer().getMule() != null)
			getCurrentPlayer().getMule().addSelf();
	}
	
	public void setupDevelopmentView(){
		for (Player p : getPlayers()){
			if (p != null){
				p.setVisible(false);
				calculateTotalProduction(p);
			}
		}
		map.showMap();
		TextDisplay td = new TextDisplay(320,32,"PRODUCTION FOR MONTH "+round);
		td.setColor(Color.WHITE);
		td.setHAlign(2);
		reOrderPlayers(true);
		productionModeler.randomEvent();
	}
	
	public void calculateTotalProduction(Player p){
		int totalEnergy = p.getEnergy();
		
		int[] total = {0,0,0,0};
		for (Tile t : p.getTiles()){
			t.calculateProduction();
			if (t.getOutfit() != null && t.getOutfit() != Outfit.Energy)
				totalEnergy--;
			if (t.getOutfit() != null)
				total[t.getOutfit().id]++;
		}
		for (int y = 0; y < 5; y++){
			for (int x = 0; x < 9; x++){
				Tile t = map.getTile(y,x);
				if (p == t.getOwner() && t.getOutfit() != null && t.getProduction() > 0){
					t.changeProduction(total[t.getOutfit().id]/3);
					if (t.getOutfit() == map.getOutfit(y,x-1))
						t.changeProduction(1);
					else if (t.getOutfit() == map.getOutfit(y,x+1))
						t.changeProduction(1);
					else if (t.getOutfit() == map.getOutfit(y-1,x))
						t.changeProduction(1);
					else if (t.getOutfit() == map.getOutfit(y+1,x))
						t.changeProduction(1);
				}
			}
		}
		while (totalEnergy < 0){
			int takeAway = rand.nextInt(p.getTiles().length);
			Tile t = p.getTiles()[takeAway];
			if (t.getProduction() > 0 && t.getOutfit() != null){
				totalEnergy++;
				t.setProduction(0);
			}
		}
		
	}
	
	public void setupScoreDisplay(){
		calculatePlayerScores();
		reOrderPlayers(true);
		new TextDisplay(320,48,"Score Summary for Month "+round).setHAlign(2).setColor(Color.WHITE);
		new TextDisplay(320,440,"All Players press A to continue").setHAlign(2).setColor(Color.WHITE);
		for (int i = 0; i < getPlayers().length; i++){
			getPlayersReady()[i] = false;
			if (getPlayers()[i] != null){
				getPlayers()[i].setVisible(true);
				getPlayers()[i].setState(20 + i*2);
				getPlayers()[i].setX(640+96*i);
				getPlayers()[i].setBottom(480-32);
			}
		}
	}
	
	public void setupResourceAuction(int r){
		for (Player p : getPlayers())
			p.scaleToSprite();
		setAuction(new ResourceAuctionHandler(r, getPlayers()));
		if (!online)
			getAuction().begin();
		else {
			Writer w = NetworkController.writer;
			w.writebyte(Writer.MSG_READY);
			w.sendmessage();
		}
	}
	
	
	
	public void beginGame(){
		setupGameMap();
		if (host || !online){
			productionModeler.generateRandomEvents();
		}
		round = 1;
		for (Player p : players)
			if (p != null)
				p.setQuantities(data.difficultyId);
		
		setPlayersReady(new boolean[getData().numplayers]);
		for (int i = 0; i < getPlayersReady().length; i++)
			getPlayersReady()[i] = true;
		initState(4,true);
	}
	
	public void back(){
		if (getGameState() == 2){
			clear();
			initState(1,true);
		}
	}
	
	public void setBackground(int backgroundNum){
		this.background.setBackground(backgroundNum);
	}
	
	public Map getMap(){
		return map;
	}
	
	/**
	 * Adds an object so we can update and draw it.
	 * 
	 * @param o The object
	 */
	public void addObject(GameObject o){
		if (!getToAdd().contains(o))
			getToAdd().add(o);
	}
	
	/**
	 * Removes the object from our update and drawing.
	 * 
	 * @param o The object
	 */
	public void removeObject(GameObject o){
		getToRemove().add(o);
	}
	
	public void bringToFront(GameObject o){
		getToRemove().add(o);
		getToAdd().add(o);
	}
	
	
	/**
	 * Freezes the input.
	 * If the input is frozen, no input will be recieved EXCEPT for typing into a textbox.
	 */
	public void freezeInput(){
		freezeInput = true;
	}
	
	/**
	 * Unfreezes the input.
	 * See freezeInput();
	 */
	public void unFreezeInput(){
		freezeInput = false;
	}
	
	/**
	 * Called on every game loop.
	 * 
	 * @param deltaTime The amount of time since the last iteration.
	 */
	public void update(float deltaTime){
		
		if (online){
			NetworkController.update(deltaTime);
		}
		
		for (Input i : inputs)
			if (i != null)
				i.updateInput();
		for (GameObject o : getToAdd()){
			objects.add(o);
		}
		getToAdd().clear();
		
		for (GameObject o : getToRemove()){
			objects.remove(o);
		}
		getToRemove().clear();
		
		updateObjects(deltaTime);
		
		if (getGameState() >= 1 && getGameState() <= 3)
			updateMenu(deltaTime);
		else if ((getGameState() >= 4 && getGameState() <= 9) || getGameState() == 40)
			updateGame(deltaTime);
		else if (getGameState() >= 10 && getGameState() < 40)
			updateAuction(deltaTime);
	}
	
	public void updateObjects(float deltaTime){
		for (GameObject o : objects)
			o.update(deltaTime);
	}
	
	public void updateMenu(float deltaTime){
		if (getGameState() == 3){
			boolean ready = true;
			for (Player p : getPlayers())
				if (p != null)
					if (p.getState() != 2)
						ready = false;
			if (ready){
				beginGame();
			}
		}
	}
	
	public Player findPlayerById(int id){
		for (int i = 0; i < getPlayers().length; i++)
			if (getPlayers()[i].getPlayerNum() == id)
				return getPlayers()[i];
		return null;
	}
	
	public int findPlayerIntByPlayer(Player p){
		for (int i = 0; i < getPlayers().length; i++)
			if (getPlayers()[i] == p)
				return i;
		return -1;
	}
	
	public void updateGame(float deltaTime){
		if (getGameState() == 4){
			if (map.plotBuyer.getFinished()){
				removeObject(map.plotBuyer);
				map.setPlotBuyer(null);
				initState(5, false);
			}
		}
		else if (getGameState() == 5){
			if (map.plotBuyer.getFinished()){
				setAuction(new LandAuctionHandler(map.plotBuyer.getCurrentTile(), getPlayers()));
				removeObject(map.plotBuyer);
				map.setPlotBuyer(null);
				initState(10, true);
				getAuction().addSelf();
			}
		}
		else if (getGameState() == 6){
			if (allPlayersReady()){
				for (int i = 0; i < getPlayersReady().length; i++)
					getPlayersReady()[i] = false;
				initState(7, true);
			}
		}
		else if (getGameState() == 7){
			setTimer(getTimer() - 1);
			if (getTimer() < 1)
				finishTurn(0);
		}
		else if (getGameState() == 8){
			setTimer(getTimer() - 1);
			if (getTimer() < 1){
				int p = -1;
				for (int i = 0; i < getPlayers().length; i++)
					if (getPlayers()[i] == getCurrentPlayer()){
						p = i;
						break;
					}
				p++;
				if (p < getPlayers().length){
					setCurrentPlayer(getPlayers()[p]);
					initState(6, true);
				} else {
					initState(9, true);
				}
			}
		}
		else if (getGameState() == 9){
		} else if (getGameState() == 40){
			if (allPlayersReady()){
				round++;
				initState(4, true);
			}
		}
	}
	
	public void beginTurns(){
		calculatePlayerScores();
		reOrderPlayers(false);
		setCurrentPlayer(getPlayers()[0]);
		store.generateMules();
		if (online && host){
			for (int i = 0; i < randomEvents.length; i++){
				randomEvents[i] = randomEvent(players[i]);
			}
			Writer w = NetworkController.writer;
			w.writebyte(Writer.MSG_RANDOMEVENTS);
			w.writebyte(randomEvents.length);
			for (int i : randomEvents)
				w.writebyte(i);
			w.sendmessage();
		}
		if (!online)
			actualBeginTurns();
		else
			initState(53, false);
	}
	
	public void actualBeginTurns(){
		initState(6,true);
	}
	
	public void updateAuction(float deltaTime){
		if (getGameState() < 30 && getAuction().getFinished()){
			Player winner = getAuction().getWinningBidder();
			if (winner != null){
				winner.addTile(getAuction().getPlot());
				if (getAuction().getPlot().getOwner() != null)
					getAuction().getPlot().getOwner().changeMoney(getAuction().getPrice());
				getAuction().getPlot().setOwner(winner);
				winner.changeMoney(-getAuction().getPrice());
			}
			for (Player p : getPlayers()){
				if (p != null){
					p.setVisible(false);
					p.setScore(calculateOverallScore(p));
				}
			}
			initState(5,true);
		} else if (getGameState() >= 30 && getAuction().getFinished()){
			if (getGameState() < 32)
				initState(getGameState()+1, true);
			else
				initState(40, true);
		}
	}
	
	public void finishTurn(int bonus){
		if (online && host){
			Writer w = NetworkController.writer;
			w.writebyte(Writer.MSG_FINISHTURN);
			w.writebyte(bonus);
			w.sendmessage();
		}
		getCurrentPlayer().setState(8);
		if (getCurrentPlayer().getMule() != null){
			//currentPlayer.getMule().setVisible(false);
		}
		if (bonus > 0){
			getCurrentPlayer().changeMoney(bonus);
			new TextDisplay(260,448,"Made "+bonus+" gambling!").setColor(Color.WHITE);
		}
		else {
			new TextDisplay(260,448,"Out of time!").setColor(Color.WHITE);
		}
		initState(8, false);
	}
	
	public void calculatePlayerScores(){
		for (Player p : getPlayers()){
			if (p != null){
				calculateOverallScore(p);
				bringToFront(p);
				p.setState(0);
			}
		}
	}
	
	public void reOrderPlayers(boolean highestFirst){
		if (highestFirst)
			reOrderHighestFirst();
		else
			reOrderLowestFirst();
	}
	
	private void reOrderHighestFirst(){
		Player[] newPlayers = new Player[getData().numplayers];
		if (host)
			onlinePlayersReady = new boolean[players.length];
		randomEvents = new int[newPlayers.length];
		int left = newPlayers.length;
		while (left > 0){
			int max = -1;
			for (int i = 0; i < getPlayers().length; i++){
				if (getPlayers()[i] != null){
					if (max == -1 || getPlayers()[i].getScore() > getPlayers()[max].getScore()){
						max = i;
					}
				}
			}
			newPlayers[newPlayers.length-left] = getPlayers()[max];
			getPlayers()[max] = null;
			left--;
		}
		setPlayers(newPlayers);
	}
	
	private boolean allPlayersReady(){
		for (int i = 0; i < getPlayersReady().length; i++)
			if (getPlayersReady()[i] == false)
				return false;
		return true;
	}
	
	private void reOrderLowestFirst(){
		Player[] newPlayers = new Player[getData().numplayers];
		if (host)
			onlinePlayersReady = new boolean[players.length];
		randomEvents = new int[newPlayers.length];
		int left = newPlayers.length;
		while (left > 0){
			int min = -1;
			for (int i = 0; i < getPlayers().length; i++){
				if (getPlayers()[i] != null){
					if (min == -1 || getPlayers()[i].getScore() < getPlayers()[min].getScore()){
						min = i;
					}
				}
			}
			newPlayers[newPlayers.length-left] = getPlayers()[min];
			getPlayers()[min] = null;
			left--;
		}
		setPlayers(newPlayers);
	}
	
	public int calculateOverallScore(Player player){
		int output = player.calculateTotalScore();
		player.setScore(output);
		return output;
	}
	
	public void focusInputOnPlayer(Player p){
		p.getInput().setInputReceiver(p);
	}
	
	/**
	 * Clears all objects that are not players.
	 */
	public void clear(){
		for (GameObject o : objects)
			if (!(o instanceof Player))
				getToRemove().add(o);
	}
	
	public void playerprint(){
		if (!DEVELOPER);
			//return;
		for(Player p : getPlayers()){
			if (p!= null)
				p.debug();
		}
	}
	
	public ArrayList<GameObject> getObjects(){
		return objects;
	}
	
	/**
	 * @author PrestonT
	 *
	 * The game loop itself.
	 */
	private class GameRunner implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			float deltaTime = 1f;
			
			update(deltaTime);
			
			// Repaint is a JComponent method. It calls paint(Graphics) with the correct graphics.
			repaint();
			
			workFrameRate();
		}
	}
	
	/**
	 * Calculates our framerate.
	 */
	private void workFrameRate(){
		setFpscounter(getFpscounter() + 1);
		if (System.currentTimeMillis()-fpsStartTime > 1000){
			setFps(getFpscounter() < 60 ? getFpscounter() : 60);
			setFpscounter(0);
			fpsStartTime = System.currentTimeMillis();
		}
	}
	
	public void userDefined(Writer writer) throws IOException{
		int msgID = writer.readbyte();
		int pNum;
		Player p;
		switch(msgID){
		case Writer.MSG_READY:
			int b = writer.readbyte();
			if (host){
				System.out.println("Player "+b+" ready.");
				onlinePlayersReady[b] = true;
				boolean go = true;
				for (boolean bb : onlinePlayersReady)
					if (!bb)
						go = false;
				if (go){
					System.out.println("Sending begin...");
					writer.writebyte(Writer.MSG_BEGIN);
					writer.sendmessage();
					for (int i = 0; i < onlinePlayersReady.length; i++)
						onlinePlayersReady[i] = false;
				}
			}
			break;
		case Writer.MSG_BEGIN:
			System.out.println("BEGIN.");
			getAuction().begin();
			break;
		case Writer.MSG_RANDOMEVENTS:
			randomEvents = new int[writer.readbyte()];
			for (int i = 0; i < randomEvents.length; i++)
				randomEvents[i] = new Integer(writer.readbyte());
			actualBeginTurns();
			break;
		case Writer.MSG_PLOTSALE:
			int numPlots = writer.readbyte();
			plotsForSale.clear();
			for (int i = 0; i < numPlots; i++){
				plotsForSale.add(map.getTile(writer.readbyte()));
			}
			break;
		case Writer.MSG_PLAYER:
			pNum = writer.readbyte();
			p = findPlayerById(pNum);
			p.setX(writer.readshort());
			p.setY(writer.readshort());
			break;
		case Writer.MSG_FINISHTURN:
			finishTurn(writer.readbyte());
			break;
		case Writer.MSG_PRESS:
			pNum = writer.readbyte();
			p = findPlayerById(pNum);
			p.getInput().manualInput(writer.readbyte(), true);
			break;
		case Writer.MSG_RELEASE:
			pNum = writer.readbyte();
			p = findPlayerById(pNum);
			p.getInput().manualInput(writer.readbyte(), false);
			break;
		case Writer.MSG_SERVERINFO:
			int games = writer.readbyte();
			System.out.println("Info recieved "+games);
			initState(54,true);
			break;
		case Writer.MSG_GAMEEVENTS:
			productionModeler = new ProductionModeler(map, getPlayers());
			System.out.println("Receiving random events!");
			ArrayList<Integer> events = new ArrayList<Integer>();
			for (int i = 0; i < 12; i++)
				events.add(new Integer(writer.readbyte()));
			productionModeler.setEvents(events);
			break;
		case Writer.MSG_NEWGAME:
			new TextDisplay(260,260,"Waiting for players...").setColor(Color.WHITE);
			break;
		case Writer.MSG_NOGAMES:
			initState(1,true);
			new TextDisplay(16,32,"No open games available").setColor(Color.WHITE);
			break;
		case Writer.MSG_BUYPLOT:
			int pnum = writer.readbyte();
			int tNum = writer.readbyte();
			map.plotBuyer.select(findPlayerById(pnum), tNum);
			break;
		case Writer.MSG_BEGINSETUP:
			
			setData(new GameData(writer.readbyte(), writer.readboolean(), writer.readbyte()));
			for (int pl = getData().numplayers; pl < 4; pl++){
				getInputs()[pl] = null;
				getPlayers()[pl] = null;
			}
			
			int num = writer.readbyte();
			
			Input input = players[0].getInput();
			Input old = players[num].getInput();
			inputs[0] = old;
			inputs[num] = input;
			input.setInputReceiver(players[num]);
			old.setInputReceiver(players[0]);
			players[num].setInput(input);
			players[num].setSync(true);
			players[0].setInput(old);
			for (int i = 0; i < 4; i++){
				if (i != num){
					if (players[i] != null){
						players[i].getInput().clearBindings();
					}
				}
			}
			
			reOrderHighestFirst();
			
			initState(3,true);
			break;
		}
	}
	
	public AuctionHandler getAuction() {
		return auction;
	}
	
	public static void setStore(Store s){
		store = s;
	}
	
	public static Store getStore(){
		return store;
	}

	public void setAuction(AuctionHandler auction) {
		this.auction = auction;
	}

	public int getTurn(){
		return getRound();
	}
	
	public int getRound(){
		return round;
	}
	
	public static Mule generateMule(int muleID){
		if (muleID >= 0){
			Mule m = new Mule(0,0);
			if (muleID == 1)
				m.setOutfit(Outfit.Food);
			else if (muleID == 2)
				m.setOutfit(Outfit.Energy);
			else if (muleID == 3)
				m.setOutfit(Outfit.Smithore);
			m.removeSelf();
			return m;
		}
		return null;
	}
	
	/**
	 * Draws everything.
	 * 
	 * @param g2d The Graphics to draw to.
	 */
	public void paint(Graphics g2d){
		// Converting to Graphics2D makes our graphics run faster.
		Graphics2D g = (Graphics2D)g2d;
		
		// Clears the background so we can redraw on top of it.
		g.setColor(getBg_color());
		g.fillRect(0, 0, getWindowWidth(), getWindowHeight());
		background.paint(g);
		
		// Draws the FPS, but only if Developer mode is on.
		if (DEVELOPER){
			g.setColor(Color.LIGHT_GRAY);
			String s = "FPS: "+getFps()+"/60 ";
			if (online){
				s += host;
				if (host){
					s += " [ ";
					for (int i = 0; i < onlinePlayersReady.length; i++)
						s += onlinePlayersReady[i];
					s += "]";
				}
			}
			g.drawString(s, 12, 20);
		}
		
		if (getGameState() > 5 && getGameState() < 9){
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("DEVELOPMENT FOR MONTH "+Game.s_instance.getRound(), 230, 24);
			if (getGameState() < 8 && getTimer() > 0){
				Tools.drawTimeBar(g, 200, 36, 240, getTimer()/(60f*50));
			}
		}
		
		// Calls paint for every object that needs to be drawn.
		for (GameObject o : objects)
			o.paint(g);
	}
}
