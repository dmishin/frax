package ratson.genimageexplorer.gui.mousetools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class ZoomMouseTool extends AbstractMouseTool {

	protected final static Color ZOOM_COLOR = new Color(255,255,255,128);
	public void drawTool(Graphics g) {
		if (dragOrigin!=null && currentPos!=null){
			int x0,y0,w,h;
			x0=Math.min(dragOrigin.x, currentPos.x);
			y0=Math.min(dragOrigin.y, currentPos.y);
			w = Math.abs(dragOrigin.x - currentPos.x);
			h = Math.abs(dragOrigin.y - currentPos.y);
			g.setColor(ZOOM_COLOR);
			g.fillRect(x0,y0,w,h);
		}
	}

	/**returns area, affected by mouse tool.
	 * Can return null, in this case whole area needs redrawing
	 */
	public Shape getClip() {
		return new Rectangle(dragOrigin.x, dragOrigin.y, currentPos.x-dragOrigin.x, currentPos.y-dragOrigin.y);
	}

	/**called, when user selected the area*/
	public abstract void selected(Rectangle rect);

	/**must be called, when the dragging of tool finished*/
	public void endDragging(Point p) {
		Rectangle rect = new Rectangle(dragOrigin.x, dragOrigin.y, currentPos.x-dragOrigin.x, currentPos.y-dragOrigin.y);
		selected(rect);
		super.endDragging(p);
	}
}
