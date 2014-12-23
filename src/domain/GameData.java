package domain;


public class GameData {

	//set up
	static int id;
	
	public int numplayers;
	public boolean isRandom;
	
	public int difficultyId;
	
	public enum difficulty{
		Beginner (0, "Beginner"),
		Intermediate (1, "Standard"),
		Expert (2, "Tournament");
		
		int id;
		String name;
		
		difficulty(int id, String name){
			this.id=id;
			this.name= name;
		}

	};
	
	public GameData(int num, boolean rand, int diffId){
		id++;
		numplayers = num;
		isRandom = rand;
		difficultyId = diffId;
	}
	
	
}
