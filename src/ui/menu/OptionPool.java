package ui.menu;

import java.util.ArrayList;

public class OptionPool<T>{

	private ArrayList<T> options;
	private boolean[] taken;
	private int size;
	
	public OptionPool(int size){
		this.size = size;
		options = new ArrayList<T>(size);
		taken = new boolean[size];
		for (int i = 0; i < size; i++)
			taken[i] = false;
	}
	
	public void addOption(T option){
		options.add(option);
	}
	
	public T getOption(int i){
		return options.get(i);
	}
	
	public boolean optionRemains(int i){
		return !taken[i];
	}
	
	public T returnOption(int i){
		taken[i] = false;
		return options.get(i);
	}
	
	public void takeOption(int i){
		taken[i] = true;
	}
	
	public int size(){
		return size;
	}
	
}
