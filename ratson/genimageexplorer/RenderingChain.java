package ratson.genimageexplorer;

import java.awt.image.BufferedImage;

import ratson.genimageexplorer.generators.FunctionFactory;
import ratson.genimageexplorer.generators.Renderer;
import ratson.genimageexplorer.generators.RendererException;
import ratson.utils.FloatMatrix;

public class RenderingChain {
	private Renderer renderer = new Renderer();
	private FloatMatrix rawImage;
	private ColorPattern colorizer;
	private BufferedImage image;
	
	public RenderingChain(){
		colorizer = new SmoothColorPattern();
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#setGenerator(ratson.genimageexplorer.generators.AbstractGenerator)
	 */
	public void setFunction(FunctionFactory f) {
		renderer.setFunction(f);
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#getImage()
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#render(ratson.genimageexplorer.ObservationPoint, java.lang.Runnable, java.lang.Runnable)
	 */
	public void render(ObservationArea area, Runnable onFinish, Runnable onProgress) throws RendererException{
		renderer.render(area, rawImage, onFinish);
	}

	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#setResolution(int, int)
	 */
	public void setResolution(int w, int h){
		if (w==0 || h ==0){
			rawImage = null;
			image = null;
		}
		//Create buffered image
		if (w!= getWidth() || h!= getHeight()){
			image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		}
		//create corresponding raw data
		int scale = colorizer.getDownscale();
		if (    rawImage == null ||
				rawImage.getWidth()!=w*scale ||
				rawImage.getHeight() != h*scale){
			rawImage = new FloatMatrix(w*scale, h*scale);
		}
		//done.
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#getWidth()
	 */
	public int getWidth(){
		if (image == null)
			return 0;
		else
			return image.getWidth();
				
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#getHeight()
	 */
	public int getHeight(){
		if (image == null)
			return 0;
		else
			return image.getHeight();			
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#GetRawImage()
	 */
	public FloatMatrix GetRawImage() {
		return rawImage;
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#colorize()
	 */
	public void colorize() {
		colorizer.render(rawImage, image);
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#getColorizer()
	 */
	public ColorPattern getColorizer() {
		return colorizer;
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#getGenerator()
	 */
	public FunctionFactory getFunction() {
		return renderer.getFunction();
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#setPattern(ratson.genimageexplorer.ColorPattern)
	 */
	public void setPattern(ColorPattern pattern) {
		this.colorizer = pattern;
	}
	/* (non-Javadoc)
	 * @see ratson.genimageexplorer.AbstractRendererChain#ensureConsistency()
	 */
	public boolean ensureConsistency() {
		if (colorizer == null)
			return false;
		if (getFunction() == null)
			return false;
		if (image == null)
			return false;
		//check the size of raw image
		int scale = colorizer.getDownscale();
		int w = image.getWidth();
		int h = image.getHeight();
		
		rawImage.resize(w*scale, h*scale);
		return true;
	}
	public Renderer getRenderer() {
		return renderer;
	}
	
	
}
