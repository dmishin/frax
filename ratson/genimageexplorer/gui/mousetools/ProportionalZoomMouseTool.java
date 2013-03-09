package ratson.genimageexplorer.gui.mousetools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class ProportionalZoomMouseTool extends AbstractMouseTool {

	private Rectangle selected = null;
	protected final static Color ZOOM_COLOR = new Color(255,255,255,128);
	
	public void mouseDragged(Point p) {
		super.mouseDragged(p);
		
		//update selection
		if (width==0 || height ==0){
			selected = null;
		}else{

			int dx = Math.abs(dragOrigin.x - currentPos.x);
			int dy = Math.abs(dragOrigin.y - currentPos.y);
			
			int dx1 = (dy * width) / height; 
			int dy1 = (dx * height) / width;
			
			dx = Math.min(dx, dx1);
			dy = Math.min(dy, dy1);
			
			//left top point pf proportionally selected region
			// cx - dx/2 = (x1+x0-dx)/2
			int x0 = Math.min(dragOrigin.x , currentPos.x);
			int y0 = Math.min(dragOrigin.y , currentPos.y);
			
			selected.setBounds(x0, y0, dx, dy);
		}
		
	}
	
	
	public void drawTool(Graphics g) {
		if (selected != null){
			g.setColor(ZOOM_COLOR);
			g.fillRect(selected.x, selected.y,	selected.width, selected.height);
		}
	}

	public Shape getClip() {
		return selected;
	}
	
	/**must be called, when the dragging of tool finished*/
	public void endDragging(Point p) {
		if (selected != null)
			selected(selected);
		selected = null;
		super.endDragging(p);
	}
	
	public void startDragging(Point p) {
		super.startDragging(p);
		selected = new Rectangle();
	}


	public abstract void selected(Rectangle rect);

	


}
