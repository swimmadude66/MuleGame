package ui;

import javax.swing.JFrame;

public class MuleGame {

	public static final int VERSION = 1000;

	public static void main(String[] args){
		System.out.println("Initializing...");
		
		JFrame frame = new JFrame("M.U.L.E.");
		int frameWidth = 640;
		int frameHeight = 480;
		frame.setSize(frameWidth+frame.getContentPane().getWidth(),frameHeight+frame.getContentPane().getHeight());
		frame.setResizable(false);
		frame.setLocation(240,60);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new Game(frameWidth, frameHeight));

		frame.setVisible(true);

		System.out.println("Running...");
	}
	
}
