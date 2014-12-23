package domain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Mule;
import models.Outfit;
import models.Store;

public class DataDao {
	Connection con;
	Statement stmt;
	
	public void connectToDatabase() { 
		System.out.println("Connecting to Database...");
	    try {
			con = DriverManager.getConnection(
			                     "jdbc:sqlserver://228Son.com;databaseName=MULE;",
			                     "mulegame",
			                     "MooseExpress9");
			 System.out.println("Connection Established!");

		} catch (SQLException e) {
			System.out.println("Connection Failed!");
			e.printStackTrace();
		}
	}

/**
 * This function saves a serialized JSON string to the database as one field, tied to a gameId. This ID increments, meaning each load
 * will only grab the latest save, but theoretically any saved state could be selected if that is implemented later
 * @param state
 */
	public void saveSerializedToDB(String state){
		System.out.println("Saving...");
		try{
			connectToDatabase();
			stmt = con.createStatement();
			String sql = "INSERT INTO MULE.dbo.Serialized"
					+ " VALUES('" + state +"')";
			stmt.executeUpdate(sql);
			System.out.println("GAME SAVED TO DB!");
		}
		catch(Exception e){
			System.out.println("Save to Serialized Failed");
			e.printStackTrace();
		}
	}
	
	public String loadSerializedFromDB(){
		System.out.println("Loading...");
		try{
			connectToDatabase();
			stmt = con.createStatement();
			String query = "SELECT *"
					+ " FROM MULE.dbo.Serialized"
					+ " WHERE ID = (SELECT MAX(ID) FROM MULE.dbo.Serialized)";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			System.out.println("Loaded from DB!");
			return(rs.getString("GameJSON"));
		}
		catch(Exception e){
			System.out.println("Load Serialized from DB Failed!");
			e.printStackTrace();
			return null;
		}
	}
	
/**
 * This Section Saves actual game data to the database. It is responsible for writing each value to a column, then extracting and reassembling
 * the raw data in to game classes, which can be re loaded.
 */
	
public int saveGameToDB(GameState state){
	System.out.println("Saving...");
	try{
		connectToDatabase();
		stmt = con.createStatement();
		String binary = "";
		for(int i=0; i<state.data.numplayers; i++){
			int var = 0;
			if(state.playersReady[i]){
				var =1;
			}
			binary+=var;
		}
		int rand = 0;
		if(state.data.isRandom){
			rand =1;
		}
		
		String sql = "INSERT INTO MULE.dbo.GameModel"
				+ " VALUES("+state.timer+", "+state.round+", "+state.gameState+", "+binary+", "+state.data.numplayers+", "
						+ rand +", "+state.data.difficultyId+", "+state.store.getResource(1)+", "
								+ state.store.getResource(2)+", "+state.store.getResource(3)+", "+state.store.getResource(4)+", " +state.currentPlayerNum+")";
		stmt.executeUpdate(sql);
		
		sql = "SELECT id"
				+" FROM MULE.dbo.GameModel "
				+"WHERE id = (SELECT MAX(id) FROM MULE.dbo.GameModel)";
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		int id = rs.getInt("id");
		savePlayers(state, id);
		saveMap(state.map.tiles, id);
		
		
		System.out.println("GAME SAVED TO DB!");
		return id;
	}
	catch(Exception e){
		System.out.println("Save to DB Failed");
		e.printStackTrace();
		return -1;
	}
}

private void savePlayers(GameState state, int gameid){
	try{
		//connectToDatabase();
		stmt = con.createStatement();
		
		for(PlayerState p : state.players){
			int up=0, down=0, left=0, right=0, checkcollisions=0;
			if(p.up)
				up=1;
			if(p.down)
				down=1;
			if(p.left)
				left=1;
			if(p.right)
				right=1;
			if(p.checkCollisions)
				checkcollisions=1;
			
			String sql = "INSERT INTO MULE.dbo.PlayerModel"
					+ " VALUES('"+p.name+"', " + p.playerNum +", " + p.score+", "+p.money+", "+p.food+", "+p.energy+", "+p.smithore+", "
							+ up+", " + down+", " +left+", "+right+", "+checkcollisions+", "+p.state+", "+p.raceid+", "+p.speed+", "
									+ p.x+", "+p.y+", "+p.r+", "+p.g+", "+p.b+", "+gameid+", "+p.mule+")"; 
			stmt.executeUpdate(sql);
			
			sql = "Select id"
					+" FROM MULE.dbo.PlayerModel"
					+" WHERE id = (SELECT MAX(id) FROM MULE.dbo.PlayerModel)";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			int id = rs.getInt("id");
			for(TileState t : p.tiles){
				t.playerId = id;
			}
				
		}
		
	}
	catch(Exception e){
		
	}
}


private int saveMap(TileState[][] map, int gameid){
	try{
		//connectToDatabase();
		stmt = con.createStatement();
		String sql = "INSERT INTO MULE.dbo.MapModel"
				+ " VALUES("+gameid+")";
		stmt.executeUpdate(sql);
		
		sql = "Select id"
				+" FROM MULE.dbo.MapModel"
				+" WHERE id = (SELECT MAX(id) FROM MULE.dbo.MapModel)";
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		int id = rs.getInt("id");
		
		for(int i=0; i<5; i++){
			for(int j=0; j<9; j++){
				TileState t = map[i][j];
				int sel =0;
				if(t.selected){
					sel =1;
				}
				
				int outfitid = -1;
				if(t.outfit != null){
					outfitid = t.outfit.id;
				}
				
				sql= "INSERT INTO MULE.dbo.TileModel"
						+ " VALUES("+t.typeId+", "+sel+", "+t.ownerID+", "+t.playerId+", "+t.production +", "+ t.x+", "
								+ t.y+", "+id+", "+outfitid+", "+t.mule+")";
				stmt.executeUpdate(sql);
			}
		}		
		return id;
	}
	catch(Exception e){
		System.out.println("Save to DB Failed");
		e.printStackTrace();
		return -1;
	}
}
	
	public GameState loadGameFromDB(){
		connectToDatabase();
		GameState state = new GameState();
		try {
			stmt = con.createStatement();
			String query = "SELECT * "
					+ "FROM MULE.dbo.GameModel "
					+ " WHERE id = (SELECT MAX(id) FROM MULE.dbo.GameModel)";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			state.id = rs.getInt("id");
			state.data = new GameData(rs.getInt("numplayers"),rs.getBoolean("isRandom"),rs.getInt("difficultyId"));
			state.timer = rs.getInt("timer");
			state.round = rs.getInt("round");
			state.playersReady = new boolean[state.data.numplayers];
			String binary = rs.getString("playersReady");
			for (int i=0; i<state.data.numplayers; i++){
				state.playersReady[i] = (binary.charAt(i)=='1');
			}
			state.gameState = rs.getInt("gamestate");
			state.store = new Store(state.data.difficultyId);
			state.store.setResource(1,rs.getInt("foodamount"));
			state.store.setResource(2,rs.getInt("energyamount"));
			state.store.setResource(3,rs.getInt("oreamount"));
			state.store.setResource(4,rs.getInt("chrystiteamount"));
			state.currentPlayerNum = rs.getInt("currentPlayerNum");
			state.players = getPlayers(state.id, state.data.numplayers);
			MapState map= new MapState();
			TileState[] rawmap = new TileState[45];
			rawmap = getMap(state.id);
			map.tiles = new TileState[5][9];
			for(int i = 0; i<5; i++){
				for(int j=0; j<9; j++){
					map.tiles[i][j]= rawmap[(i*9)+j];
				}
			}
			state.map = map;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	
	private PlayerState[] getPlayers(int gameid, int numplayers){
		String query = "SELECT *"
				+ " FROM MULE.dbo.PlayerModel"
				+ " WHERE gameId = " + gameid;
		PlayerState[] players = new PlayerState[numplayers];
		int i = 0;
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				PlayerState ps = new PlayerState();
				ps.id = rs.getInt("id");
				ps.r = rs.getInt("r");
				ps.g =rs.getInt("g");
				ps.b =rs.getInt("b");
				ps.energy= rs.getInt("energy");
				ps.food = rs.getInt("food");
				ps.money = rs.getInt("money");
				ps.mule = rs.getInt("muleId");
				ps.playerNum = rs.getInt("playerNum");
				ps.raceid = rs.getInt("raceid");
				ps.score = rs.getInt("score");
				ps.smithore= rs.getInt("smithore");
				ps.speed = rs.getInt("speed");
				ps.state = rs.getInt("state");
				ps.x = rs.getInt("x");
				ps.y =rs.getInt("y");
				ps.name = rs.getString("name");
				players[i] = ps;
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(PlayerState ps : players){
			ps.tiles = getTiles(ps.id);
		}
		return players;
	}
	
	private TileState[] getTiles(int playerId){
		String query = "SELECT *"
				+ " FROM MULE.dbo.TileModel"
				+ " WHERE id = " + playerId;
		List<TileState> list = new ArrayList<TileState>();
		TileState[] tiles = new TileState[0];
		int i = 0;
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				TileState t = new TileState();
				t.id = rs.getInt("id");
				t.ownerID = rs.getInt("ownerId");
				t.playerId = rs.getInt("playerid");
				t.production=rs.getInt("production");
				t.selected=rs.getBoolean("selected");
				t.typeId=rs.getInt("typeId");
				t.x=rs.getInt("x");
				t.y=rs.getInt("y");
				t.mule = rs.getInt("muleId");
				if(t.mule < 1){
					t.outfit = null;
				}
				else {
					t.outfit = Outfit.values()[rs.getInt("outfitId")-1];
				}
				list.add(t);
				i++;
			}
			tiles = new TileState[i];
			int j =0;
			for(TileState ts:list){
				tiles[j] = ts;
				j++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tiles;
	}
	
	private TileState[] getMap(int gameId){
		List<TileState> list = new ArrayList<TileState>();
		TileState[] tiles = new TileState[0];
		int i = 0;
		try {
			String query = "SELECT id"
					+ " FROM MULE.dbo.MapModel"
					+ " WHERE GameId = " + gameId;
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int mapid= rs.getInt("id");
			query = "SELECT *"
					+ " FROM MULE.dbo.TileModel"
					+ " WHERE Mapid = " + mapid;
			
			rs = stmt.executeQuery(query);
			while(rs.next()){
				TileState t = new TileState();
				t.id = rs.getInt("id");
				t.ownerID = rs.getInt("ownerId");
				t.playerId = rs.getInt("playerid");
				t.production=rs.getInt("production");
				t.selected=rs.getBoolean("selected");
				t.typeId=rs.getInt("typeId");
				t.x=rs.getInt("x");
				t.y=rs.getInt("y");
				t.mule = rs.getInt("muleId");
				if(t.mule < 1){
					t.outfit = null;
				}
				else {
					t.outfit = Outfit.values()[rs.getInt("outfitId")-1];
				}
				list.add(t);
				i++;
			}
			tiles = new TileState[i];
			int j =0;
			for(TileState ts:list){
				tiles[j] = ts;
				j++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tiles;
	}
	
	
	
}
