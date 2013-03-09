package ratson.genimageexplorer.gui.mousetools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
	

public abstract class AbstractMouseTool {
	protected Point dragOrigin;
	private boolean dragging=false;
	protected Point currentPos;
	protected int height=0;
	protected int width=0;
	private int mouseButton;

	public boolean isDragging() {
		return dragging;
	}
	
	public AbstractMouseTool(){
	}
	
	/**Draws tool data*/
	public abstract void drawTool(Graphics g);
	
	/**returns clipping area, used to restore the component*/
	public abstract Shape getClip();
	
	public void startDragging(Point p){
		dragOrigin = p;
		currentPos = p;
		dragging = true;
	}
	public void endDragging(Point p){
		dragOrigin = null;
		currentPos = p;
		dragging = false;
	}
	public void mouseMoved(Point p){
		currentPos = p;
	}

	public void mouseDragged(Point p) {
		currentPos = p;
	}

	public void setComponentSize(int imgH, int imgW) {
		height = imgH;
		width=imgW;
	}

	/**Stes mouse button. @see MouseEvent for constant value
	 * 
	 * @param button
	 */  
	public void setMouseButton(int button) {
		mouseButton = button;
	}
	
	/**Index of mouse button
	 * 
	 * @return
	 */
	public int getMouseButton() {
		return mouseButton;
	}
	
	
}
