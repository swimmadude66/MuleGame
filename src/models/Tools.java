package models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import ui.Sprite;

public abstract class Tools {
	
	public static int foodRequirement(int round){
		return (3 + (round-1)/3);
	}
	
	public static void drawArrow(Graphics g, int x, int y){
		g.drawLine(x,y,x-4,y+4);
		g.drawLine(x,y,x+4,y+4);
		g.drawLine(x-4,y+4,x-2,y+4);
		g.drawLine(x+4,y+4,x+2,y+4);
		g.drawLine(x+2,y+4,x+2,y+8);
		g.drawLine(x-2,y+4,x-2,y+8);
		g.drawLine(x-2,y+8,x+2,y+8);
	}
	
	public static int calculateBonus(int round, long timeLeft){
		Random rand = new Random();
		int timeBonus = 50;
		if (timeLeft/60 > 12)
			timeBonus = 100;
		if (timeLeft/60 > 25)
			timeBonus = 150;
		if (timeLeft/60 > 37)
			timeBonus= 200;
		int bonus = (50 + (((round-1)/4)*50)) + rand.nextInt(timeBonus);
		if (bonus > 250)
			bonus = 250;
		return bonus;
	}
	
	public static Sprite colorSprite(Sprite sprite, Color color) {
		BufferedImage image = new BufferedImage(sprite.spriteWidth(), sprite.spriteHeight(), BufferedImage.TYPE_INT_ARGB);
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster rasterOld = sprite.getSprite().getRaster();
        WritableRaster rasterNew = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
            	int[] oldPixels = rasterOld.getPixel(xx, yy, (int[]) null);
                int[] pixels = rasterNew.getPixel(xx, yy, (int[]) null);
                if (oldPixels[0] > 244 && oldPixels[1] > 244 && oldPixels[2] > 244){
                	pixels[0] = color.getRed();
                	pixels[1] = color.getGreen();
                	pixels[2] = color.getBlue();
                } else {
                	pixels[0] = (oldPixels[0] + color.getRed())/2;
	                pixels[1] = (oldPixels[1] + color.getGreen())/2;
	                pixels[2] = (oldPixels[2] + color.getBlue())/2;
                }
                pixels[3] = oldPixels[3];
                rasterNew.setPixel(xx, yy, pixels);
            }
        }
        return new Sprite(image);
    }
	
	public static BufferedImage colorImage(BufferedImage image, Color color) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = color.getRed();
                pixels[1] = color.getGreen();
                pixels[2] = color.getBlue();
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }
	
	public static int dist(GameObject a, GameObject b){
		return pointDistance(a.getX(), a.getY(), b.getX(), b.getY());
	}
	
	public static int centerDist(GameObject a, GameObject b){
		return pointDistance(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
	}
	
	public static int pointDistance(int x1, int y1, int x2, int y2){
		return (int)Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	public static void drawTimeBar(Graphics g, int x, int y, int width, float percent){
		g.fillRect(x,y,(int)(width * percent),20);
	}
	
	public static boolean checkCollision(GameObject a, GameObject b){
		int xdist = Math.abs((int)(b.getCenterX() - a.getCenterX()));
		int ydist = Math.abs((int)(b.getCenterY() - a.getCenterY()));
		if ((xdist < a.getWidth()/2+b.getWidth()/2) && (ydist < a.getHeight()/2+b.getHeight()/2))
			return true;
		return false;
	}
}
