package ratson.genimageexplorer.extra.bounce;

import java.awt.Graphics;

public interface AnimatedItem {
	public abstract void initAnimation();
	public abstract void animateStep(long dtMs);
	public abstract void drawFrame(Graphics g);
}
