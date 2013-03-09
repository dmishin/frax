package ratson.genimageexplorer.gui.mousetools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;

public abstract class PanMouseTool extends AbstractMouseTool{

	private static final Color COLOR_ORIG = new Color(255,255,0,128);
	private static final Color COLOR_NEW = new Color(0,255,255,128);
	private static final int BOX_SIZE=60;
	
	public void drawTool(Graphics g) {
		if (dragOrigin == null || currentPos == null)
			return;
		g.setColor(COLOR_ORIG);
		g.fillRect(dragOrigin.x-BOX_SIZE, dragOrigin.y-BOX_SIZE,
				BOX_SIZE*2, BOX_SIZE*2);
		g.setColor(COLOR_NEW);
		g.fillRect(currentPos.x-BOX_SIZE, currentPos.y-BOX_SIZE,
				BOX_SIZE*2, BOX_SIZE*2);
	}

	public Shape getClip() {
		// TODO Auto-generated method stub
		return null;
	}
	public void endDragging(Point p) {
		panned (p.x-dragOrigin.x, p.y-dragOrigin.y);
		super.endDragging(p);
	}
	
	public abstract void panned(int dx, int dy);

}
