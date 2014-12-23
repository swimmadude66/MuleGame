package models;

public enum Outfit{
	Energy(2,25,50),
	Food(1,30,25),
	Smithore(3,50,75);
	
	protected int worth;
	public int cost;
	public int id;
	
	Outfit(int id, int worth, int cost){
		this.id = id;
		this.worth = worth;
		this.cost = cost;
	}
}
