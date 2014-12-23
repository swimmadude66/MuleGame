package ui;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Sprite{
	
	private BufferedImage[] sprite;
	private int frames;
	private double imageSingle;
	private double imageSpeed;
	private boolean animated;

	public Sprite(BufferedImage image){
		sprite = new BufferedImage[1];
		sprite[0] = image;
		frames = 1;
		imageSingle = 0;
		imageSpeed = 1;
		animated = true;
	}
	
	public Sprite(String fname, boolean included){
		this(fname, included, 1);
	}

	public Sprite(File file){
		try{
			sprite = new BufferedImage[1];
			sprite[0] = ImageIO.read(file);
			frames = 1;
			imageSingle = 0;
			imageSpeed = 1;
		} catch (IOException ioe){
			System.err.println("Could not load sprite from file.");
		}
	}

	public Sprite(String fname, boolean included, int frames){
		BufferedImage strip = null; //new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		try{
			if (included)
				strip = ImageIO.read(getClass().getResource(fname));
			else
				strip = ImageIO.read(new File(fname));
		} catch (IOException ioe){
			System.err.println("Failed to load sprite. "+fname+", "+included);
		}

		if (frames <= 1)
			frames = 1;
		
		int width = strip.getWidth()/frames;
		this.frames = frames;
		sprite = new BufferedImage[frames];
		int height = strip.getHeight();

		int dx = 0;
		int ind = 0;
		while (dx < strip.getWidth()){
			sprite[ind] = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics g = sprite[ind].getGraphics();
			g.drawImage(strip,0,0,width,height,dx,0,dx+width,height,null);
			dx += width;
			ind += 1;
		}

		imageSingle = 0;
		animated = true;
	}
	
	public void setImage(int frame, BufferedImage image){
		sprite[frame] = image;
	}

	public void addFrame(Sprite toAdd){
		BufferedImage[] newSprite = new BufferedImage[frames+1];
		System.arraycopy(sprite,0,newSprite,0,frames);
		newSprite[frames] = toAdd.getSprite();
		sprite = newSprite;
		frames++;
		imageSingle = 0;
	}

	public void setImageSingle(int is){
		imageSingle = is;
		animated = false;
	}

	public void setImageSpeed(double speed){
		imageSpeed = speed;
		imageSingle = 0;
	}

	public boolean getAnimated(){
		return animated;
	}

	public void setAnimated(boolean animated){
		this.animated = animated;
	}

	public BufferedImage getSprite(){
		return sprite[0];
	}

	public BufferedImage getSprite(int frame){
		return sprite[frame];
	}

	public int spriteWidth(){
		return sprite[(int)imageSingle].getWidth();
	}

	public int spriteHeight(){
		return sprite[(int)imageSingle].getHeight();
	}

	public void drawSprite(Graphics g, int x, int y){
		drawSprite(g,x,y,spriteWidth(),spriteHeight(),(int)imageSingle);
	}

	public void drawSprite(Graphics g, int x, int y, int imageSingle){
		drawSprite(g,x,y,spriteWidth(),spriteHeight(),(int)imageSingle);
	}

	public void drawSprite(Graphics g, int x, int y, int width, int height){
		drawSprite(g,x,y,width,height,(int)imageSingle);
	}
	
	public int getImageSingle(){
		return (int)imageSingle;
	}

	public void drawSprite(Graphics g, int x, int y, int width, int height, int single){
		if (animated)
			imageSingle += imageSpeed;
		if (imageSingle >= frames)
			imageSingle -= frames;
		g.drawImage(getSprite(single), x, y, (int)(x+width), (int)(y+height), 0, 0, spriteWidth(), spriteHeight(), null);
	}

}