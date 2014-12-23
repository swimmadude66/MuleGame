package models;



public class Store {

	private int food;
	private int energy;
	private int smithore;
	private int crystite;
	private int mules;
	
	private int foodPrice;
	private int energyPrice;
	private int smithorePrice;
	private int crystitePrice;
	private int mulesPrice;
	
	public Store(int difficulty){
		smithorePrice = 50;
		foodPrice = 30;
		energyPrice = 25;
		mulesPrice = 100;
		switch (difficulty){
		case 0:
			food = 16;
			energy = 16;
			smithore = 0;
			mules = 25;
			break;
		case 1:
		case 2:
			food = 8;
			energy = 8;
			smithore = 8;
			mules = 14;
			break;
		}
	}
	
	public int getMules(){
		return mules;
	}
	
	public Mule takeMule(Player p){
		mules--;
		return new Mule(p.getX(),p.getY());
	}
	
	public int getMulePrice(){
		return mulesPrice;
	}
	
	public void generateMules(){
		for (int i = 0; i < 2; i++){
			if (mules < 25){
				if (smithore >= 8){
					System.out.println("Generating mule!");
					smithore -= 8;
					mules++;
				}
			}
		}
	}
	
	public int getPrice(int resource){
		if (resource == 1)
			return foodPrice;
		else if (resource == 2)
			return energyPrice;
		else if (resource == 3)
			return smithorePrice;
		else if (resource == 4)
			return crystitePrice;
		return 0;
	}
	
	public void clearResource(int resource){
		if (resource == 1)
			food = 0;
		else if (resource == 2)
			energy = 0;
		else if (resource == 3)
			smithore = 0;
		else if (resource == 4)
			crystite = 0;
	}
	
	public void changePrice(int resource, int dprice){
		if (resource == 1)
			foodPrice += dprice;
		else if (resource == 2)
			energyPrice += dprice;
		else if (resource == 3)
			smithorePrice += dprice;
		else if (resource == 4)
			crystitePrice += dprice;
	}
	
	public void setPrice(int resource, int price){
		if (resource == 1)
			foodPrice = price;
		else if (resource == 2)
			energyPrice = price;
		else if (resource == 3)
			smithorePrice = price;
		else if (resource == 4)
			crystitePrice = price;
	}
	
	public void changeResource(int resource, int amount){
		if (resource == 1)
			food += amount;
		else if (resource == 2)
			energy += amount;
		else if (resource == 3)
			smithore += amount;
		else if (resource == 4)
			crystite += amount;
	}
	
	public int getResource(int resource){
		if (resource == 1)
			return food;
		if (resource == 2)
			return energy;
		if (resource == 3)
			return smithore;
		if (resource == 4)
			return crystite;
		return 0;
	}
	
	public void setResource(int resource, int amount){
		if (resource == 1)
			food += amount;
		else if (resource == 2)
			energy+= amount;
		else if (resource == 3)
			smithore+= amount;
		else if (resource == 4)
			crystite+= amount;
	}
	
}
