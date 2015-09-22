package objects;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class OpticObject {

	
	private BufferedImage image;
	private int xPos;
	private int yPos;
	private int height;
	private boolean snapToCenter = false;
	private boolean drawPrincipleRays = false;
	
	/**
	 * Creates a new optical object, with the image desired. The x and y coordinates 
	 * defign the upper left hand corner of the image. The height of the Optical object is also necissary,
	 * and does not have to be the same as the image height itself. 
	 * @param img The image to represent this object.
	 * @param x The x of the left of the image.
	 * @param y The y of the top of the image.
	 * @param h The height of the object. The image will be scaled appropriately.
	 */
	public OpticObject(BufferedImage img, int x, int y, int h){
		image = img;
		xPos = x;
		yPos = y;
		height = h;
	}
	
	/**
	 * Sets this object to have a new image.
	 * @param img
	 */
	public void setImage(BufferedImage img){
		image = img;
	}
	
	/**
	 * Gets the proper image to represent this object. It is scalled according to this object's height.
	 * @return The image.
	 */
	public Image getScaledImage(){
		return image.getScaledInstance((int)(image.getWidth(null)*((double)height/image.getHeight(null))), height, Image.SCALE_DEFAULT);
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	/**
	 * Changes the height of the object.
	 * @param h The desired height.
	 */
	public void setHeight(int h){
		height = h;
	}
	
	/**
	 * Gets this object's height.
	 * @return
	 */
	public int getHeight(){
		return height;
	}
	
	public void setX(int x){
		xPos = x;
	}
	
	public void setY(int y){
		yPos = y;
	}
	
	public int getX(){
		return xPos+((int)(image.getWidth(null)*((double)height/image.getHeight(null))))/2;
	}
	
	public int getY(){
		return yPos;
	}
	
	public Rectangle getBounds(){
		return new Rectangle(getX(), yPos, ((int)(image.getWidth(null)*((double)height/image.getHeight(null)))), height);
	}
	
	public int getMiddleX(){
		return xPos;
	}

	public boolean shouldSnapToCenter() {
		return snapToCenter;
	}

	public void setSnapToCenter(boolean snap) {
		snapToCenter = snap;
	}

	public boolean shouldDrawPrincipleRays() {
		return drawPrincipleRays;
	}

	public void setDrawPrincipleRays(boolean shouldDraw) {
		drawPrincipleRays = shouldDraw;
	}
}
