/**
 * M10 Test methods for non-trivial Methods
 */

package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import models.Mule;
import models.Outfit;
import models.Player;
import models.Store;
import models.Tile;
import models.Tile.TileType;
import models.Tools;

import ui.Game;


/**
 * @author The Moose Express
 *
 */
public class M10 {

	/**
	 * @author Adam
	 * Tests whether or not RAW production values correspond to the outfits of the tile
	 */
	/*
	 * TYPE	 |	Food  |  Energy  |  Ore  |
	 * ------|--------|----------|-------|		
	 * RIVER |    4   |    2     |   0   |
	 * PLAIN |    2   |    3     |   1   |
	 * MNTN1 |    1   |    1     |   2   |
	 * MNTN2 |    1   |    1     |   3   |
	 * MNTN3 |    1   |    1     |   4   |
	 */
	@Test
	public void testCalculateProduction() {
		Outfit[] outfitarray = {Outfit.Food, Outfit.Energy, Outfit.Smithore};
		TileType[] typearray = {TileType.River, TileType.Plains, TileType.Mountain1, TileType.Mountain2, TileType.Mountain3};
		int[] riverarray = {4,2,0};
		int[] plainarray = {2,3,1};
		int[] mntn1array = {1,1,2};
		int[] mntn2array = {1,1,3};
		int[] mntn3array = {1,1,4};
		int[][] prod = {riverarray, plainarray, mntn1array, mntn2array, mntn3array};
		
		Tile tile;
		Mule mule;
		
		for(int i=0; i<5; i++){
			for(int j=0; j<3; j++){
				tile = new Tile(0,0, typearray[i]);
				mule = new Mule(0,0);
				mule.setOutfit(outfitarray[j]);
				tile.setMule(mule);
				assertEquals(tile.calculateProduction(), prod[i][j]);
			}
		}
	}
	
	/**
	 * @author Preston
	 * Tests that the bonus given for entering the pub is within a valid range
	 */
	@Test
	public void testCalculateBonus(){
		int bonus;

		// Because Tools uses a randomizer, the test is run 100 times to make sure it is working.
		int runs = 100;
		for (int i = 0; i < runs; i++){
			// Standard case for round one
			bonus = Tools.calculateBonus(1, 60*40);
			assertTrue(bonus >= 50);
			assertTrue(bonus < 250);
			// Edge cases for round one
			bonus = Tools.calculateBonus(1, 60*37);
			assertTrue(bonus >= 50);
			assertTrue(bonus < 200);
			bonus = Tools.calculateBonus(1, 60*26);
			assertTrue(bonus >= 50);
			assertTrue(bonus < 200);
			bonus = Tools.calculateBonus(1, 60*25);
			assertTrue(bonus >= 50);
			assertTrue(bonus < 150);
	
			// Cases for round 5
			bonus = Tools.calculateBonus(5, 60*40);
			assertTrue(bonus >= 100);
			assertTrue(bonus < 251);
			bonus = Tools.calculateBonus(5, 60*25);
			assertTrue(bonus >= 100);
			assertTrue(bonus < 200);
	
			// Cases for round 8
			bonus = Tools.calculateBonus(8, 60*40);
			assertTrue(bonus >= 100);
			assertTrue(bonus < 251);
			bonus = Tools.calculateBonus(8, 60*25);
			assertTrue(bonus >= 100);
			assertTrue(bonus < 200);
	
			// Cases for round 9
			bonus = Tools.calculateBonus(9, 60*40);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 251);
			bonus = Tools.calculateBonus(9, 60*25);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 250);
			bonus = Tools.calculateBonus(9, 60*6);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 200);
	
			// Cases for round 12 (final round)
			bonus = Tools.calculateBonus(12, 60*40);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 251);
			bonus = Tools.calculateBonus(12, 60*25);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 250);
			bonus = Tools.calculateBonus(12, 60*6);
			assertTrue(bonus >= 150);
			assertTrue(bonus < 200);
		}
	}
	
	/**
	 * @author Ji
	 * Calculates the players total score based off goods, land and bonus 
	 */
	@Test
	public void testCalculateTotalScore(){
		
		Game.setStore(new Store(0));
		Store store = Game.getStore();
		
		//case1 : initial condition
		Player player1 = new Player("player 1", 0, 0,0); //choose human
		player1.setRace(0);
		assertEquals(600, player1.calculateTotalScore());
		
		//case2 : beginner, human , river land without outfit
		Player player2 = new Player("player 2", 0, 0,0);  //choose human
		player2.setRace(0);
		player2.setQuantities(0); //choose beginner
		player2.addTile(new Tile(0,0, Tile.TileType.River)); //choose river tile when start a game. no outfit
		int i2 = 600+(500+0)+(8*store.getPrice(1)+4*store.getPrice(2)+0*0); //money + landWorth + goodWorth(foodWorth, energyWorth, oreWorth)
		assertEquals(i2,player2.calculateTotalScore());
		
		//case3 : standard, flapper , have two tiles with one energy outfit
		Player player3 = new Player("player 3", 1, 0,0); //choose flapper
		player3.setRace(1);
		player3.setQuantities(1); //choose standard
		player3.addTile(new Tile(0,0, Tile.TileType.Mountain1)); //choose mountain1 tile when start a game
		Tile t3 = new Tile(0,0, Tile.TileType.Plains); //choose plain tile with energy outfit
		t3.setOutfit(Outfit.Energy);
		player3.addTile(t3);
		int i3 = 1600+(500+0)+(500+25)+(4*store.getPrice(1)+2*store.getPrice(2)+0*store.getPrice(3));///money + 2 landWorth + goodWorth(foodWorth, energyWorth, oreWorth)
		assertEquals(i3,player3.calculateTotalScore());
		
		//case4 : test multiple players(4)
		Game g = new Game(640,480);
		Player[] player4 = new Player[4];
		for(int i=0; i<player4.length;i++){
			player4[i] = new Player("player 5", i+1, 0, 0);
			player4[i].setRace(i+1);
		}
		
		player4[0].setQuantities(0);
		player4[1].setQuantities(1);
		player4[2].setQuantities(2);
		player4[3].setQuantities(0);
		
		player4[0].addTile(new Tile(0,0, Tile.TileType.River));
		player4[1].addTile(new Tile(0,0, Tile.TileType.Mountain1));
		player4[2].addTile(new Tile(0,0, Tile.TileType.Mountain2));
		player4[3].addTile(new Tile(0,0, Tile.TileType.Plains));

		Tile[] t5= new Tile[player4.length];
		t5[0] = new Tile(0,0, Tile.TileType.River); 
		t5[1] = new Tile(0,0, Tile.TileType.Mountain1); 
		t5[2] = new Tile(0,0, Tile.TileType.Mountain2); 
		t5[3] = new Tile(0,0, Tile.TileType.Plains); 
		
		t5[0].setOutfit(Outfit.Energy);
		t5[1].setOutfit(Outfit.Food);
		t5[2].setOutfit(Outfit.Smithore);
		t5[3].setOutfit(Outfit.Energy);
		
		for(int i=0;i<player4.length;i++)
			player4[i].addTile(t5[i]);
		
		g.setPlayers(player4);
		
		int[] i5= new int[player4.length];
		i5[0] = 1600+(500+0)+(500+25)+(8*store.getPrice(1)+4*store.getPrice(2)+0*0);
		i5[1] = 1000+(500+0)+(500+30)+(4*store.getPrice(1)+2*store.getPrice(2)+0*8);
		i5[2] = 1000+(500+0)+(500+50)+(4*store.getPrice(1)+2*store.getPrice(2)+0*8);
		i5[3] = 1000+(500+0)+(500+25)+(8*store.getPrice(1)+4*store.getPrice(2)+0*0);
		
		for(int i=0;i<player4.length;i++)
			assertEquals(i5[i], player4[i].calculateTotalScore());
		
		//case5 : random event
		/*
		for(int i=0;i<player4.length;i++){
			if(g.randomEvent(player4[i]) && player4[i].calculateTotalScore()!=0)
				assertFalse(i5[i]==player4[i].calculateTotalScore());
		}
		*/
		
		//case6 : test finish turn
		int[] i6= new int[player4.length];
		for(int i=0;i<player4.length;i++)
			i6[i] = player4[i].calculateTotalScore();

		g.setCurrentPlayer(player4[0]);
		g.setTimer(780);
		//g.finishTurn(true);
		
		g.setCurrentPlayer(player4[1]);
		g.setTimer(780);
		//g.finishTurn(false);
		
		//assertTrue(player4[0].calculateTotalScore() >= i6[0]+50);
		//assertTrue(player4[1].calculateTotalScore() == i6[1]);
		
	}
	
	/**
	 * @author Jordan
	 * Changes the money the player possesses by the specified amount
	 */
	@Test
	public void testChangeMoney(){
		Player player = new Player("player 1", 0, 0,0);//player chooses human
		player.changeMoney(0);
		assertEquals(player.getMoney(), 0); //change to default money depending on race
		
		//case 1: player buys food
		player.changeMoney(600);//set initial amount of money back to 600
		Store store = new Store(0);
		int foodPrice = store.getPrice(1);
		player.changeMoney(-foodPrice);
		assertEquals(player.getMoney(),600-foodPrice);
		
		//case2: player buys a tile in auction
		int auctionPrice = 180;
		player.changeMoney(-auctionPrice);
		assertEquals(player.getMoney(),600-(auctionPrice+foodPrice));
		
		//case3: player wins a bonus
		//assume round 3 with 13 seconds left
		int bonusAmount = Tools.calculateBonus(3,13);
		player.changeMoney(bonusAmount);
		assertEquals(player.getMoney(),600-(180+30)+bonusAmount);
		
		//case4: edge case
		player.changeMoney(-1000000);
		assertEquals(player.getMoney(),0);
	}
	
}
