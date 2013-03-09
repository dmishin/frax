package ratson.genimageexplorer;

import java.awt.image.BufferedImage;

import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.genimageexplorer.generators.RendererException;
import ratson.utils.FloatMatrix;

public interface AbstractRendererChain {

	public abstract void setGenerator(AbstractGenerator generator);

	public abstract BufferedImage getImage();

	public abstract void render(ObservationArea area, Runnable onFinish,
			Runnable onProgress) throws RendererException;

	public abstract void setResolution(int w, int h);

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract FloatMatrix GetRawImage();

	public abstract void colorize();

	public abstract ColorPattern getColorizer();

	public abstract AbstractGenerator getGenerator();

	public abstract void setPattern(ColorPattern pattern);

	/**Checks the rendring chain is consistent*/
	public abstract boolean ensureConsistency();

}