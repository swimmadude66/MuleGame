package ui.menu;

/**
 * An interface for any object that will be recieving events directly from an Input.
 */
public interface InputReceiver {
	public void pressLeft();
	public void releaseLeft();
	public void pressRight();
	public void releaseRight();
	public void pressUp();
	public void releaseUp();
	public void pressDown();
	public void releaseDown();
	public void pressA();
	public void releaseA();
	public void pressB();
	public void releaseB();
	public void pressStart();
}
