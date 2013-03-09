package ratson.genimageexplorer;

import java.awt.image.BufferedImage;
import ratson.utils.FloatMatrix;

public abstract class ColorPattern {
	//protected FloatMatrix raw;
	//protected BufferedImage rendered;
	

	//public final void setSource(FloatMatrix rawImage) {
//		raw=rawImage;
//	}

//	public final void setDest(BufferedImage displayedImage) {
		//rendered=displayedImage;
	//}
	public final void render(FloatMatrix raw, BufferedImage rendered){
		beforeRender(raw,rendered);
		int h=raw.getHeight();
		int w=raw.getWidth ();
		int[] buff=new int[w];
		for (int y=0;y<h;++y){
			renderPoints(raw.getData(),y*raw.getWidth(),buff);
			synchronized(rendered){
				rendered.setRGB(0, y, w, 1, buff, 0, w);
			}
		}
		endRender(raw,rendered);
	}

	protected abstract void endRender(FloatMatrix raw, BufferedImage rendered);

	protected abstract void beforeRender(FloatMatrix raw, BufferedImage rendered);

	protected abstract void renderPoints(float[] src, int position, int[] rgb);

	
	/**returns downscale coefficient (for anti-aliasing)*/
	public int getDownscale() {
		return 1;
	}

	public abstract int renderPoint(float v);

}
