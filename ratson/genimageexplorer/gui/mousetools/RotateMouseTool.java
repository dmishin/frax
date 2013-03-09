package ratson.genimageexplorer.gui.mousetools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public abstract class RotateMouseTool extends AbstractMouseTool {

	private static final Color TOOL_COLOR = new Color(255,255,0,128);
	
	public void drawTool(Graphics g) {
		if (dragOrigin == null)
			return;
		if (currentPos == null)
			return;
			
		g.setColor(TOOL_COLOR);
		
			
		int dx = width/2;
		int dy = height/2;
		
		int arcSize = Math.min(dx, dy)/2;
		
		final double ka = 180/Math.PI;
		int a1 = (int)(getAngle(dragOrigin)*ka);
		int a2=  (int)(getAngle(currentPos)*ka);
		g.fillArc(dx-arcSize, dy-arcSize, arcSize*2, arcSize*2, 
				a1, a2-a1);
	}
	
	private double getAngle(Point pt){
		int dx = pt.x-width/2;
		int dy = pt.y-height/2;
		if (dx==0 && dy ==0)
			return 0;
		return Math.atan2(-dy, dx);
	}
	public void endDragging(Point p) {
		rotated(getAngle(p)-getAngle(dragOrigin));
		super.endDragging(p);
	}

	public Shape getClip() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract void rotated(double angle);
	

}
