package ratson.genimageexplorer.generators;

import java.awt.image.BufferedImage;

import ratson.genimageexplorer.ColorPattern;
import ratson.genimageexplorer.ObservationArea;
import ratson.utils.FloatMatrix;

public abstract class AbstractGenerator {

	public static final float BLACK_VALUE = -1;

	private int numThreads = 1;
	
	protected boolean doStop=false;
	
	/*Forces renderer to stop processing*/
	public void stopRendering(){
		doStop = true;
	}
	
	public void setNumThreads(int numThreads) {
		if (numThreads<=0){
			throw new RuntimeException("Number of threads can not be 0 or less");
		}
		this.numThreads = numThreads;
	}
	public int getNumThreads() {
		return numThreads;
	}
	
	private RenderingContext[] runningContexts;

	private long timeElapsed = 0;

	public long getTimeElapsed() {
		return timeElapsed;
	}
	/**Returns current rendering progress for ll contexts*/
	public int getProgress(){
		if (runningContexts == null)
			return 0;
		int s=0;
		for (int i = 0; i < runningContexts.length; i++) {
			s+=runningContexts[i].getProgress();
		}
		if (runningContexts.length == 0)
			return 0;
		else
			return s/runningContexts.length;
	}

	/**Calculate value of single point. Point-by-point calculation may introduce slight overhead for
	 * simple calculated images. If you wish to avoid it, redefine "render" method
	 * @param x
	 * @param y
	 * @param context TODO
	 * @return
	 */
	public abstract float renderPoint(double x, double y, RenderingContext context);
		
	/**returns new render context. Return null, if renderer does not uses context*/
	protected RenderingContext prepareRendering(ObservationArea area)
	{
		RenderingContext context = new RenderingContext();
		return context;
	}

	
	protected abstract void finishRendering(ObservationArea area, FloatMatrix image, RenderingContext renderContext);
	
	private class RenderRunner extends Thread{
		private int shift;//first line to render
		private int offset;//how much to skip between lines
		private ObservationArea loc;
		private FloatMatrix image;
		private RenderingContext context; 

		public RenderRunner (ObservationArea loc, FloatMatrix image, RenderingContext context, int shift, int offset){
			super("Renderer#"+offset);
			this.shift=shift;
			this.offset=offset;
			this.loc=loc;
			this.image=image;
			this.context = context;
		}
		
		public void run() {
			if (context == null)
				throw new RuntimeException("Context is 0");
			renderInterlaced(loc, image,context,shift, offset);
		}
	}
	
	class RenderRunnerHQ extends Thread{
		private int y0;
		private int dy;
		private int progress = 0;
		private ObservationArea area;
		private RenderingContext context;
		private ColorPattern pattern;
		private BufferedImage image;
		private int aa;
		public RenderRunnerHQ(ObservationArea area,RenderingContext context,BufferedImage image, ColorPattern pattern, int antiAlias, int y0, int dy){
			this.y0 = y0;
			this.dy = dy;
			this.area = area;
			this.progress = 0;
			this.context = context;
			this.pattern = pattern;
			this.image = image;
			this.aa = antiAlias;
		}
		public int getProgress() {
			return progress;
		}
		@Override
		public void run() {
			if (context == null)
				throw new RuntimeException("Context is 0");
			renderHQInterlaced(area, image, pattern,context, aa, y0, dy);
		}
		
	}
	

	/**Renders image, using multithreaded rendering. This method waits until rendering is finished
	 * 
	 * @param area area to render
	 * @param image destination
	 * @throws RendererException 
	 */
	public final void render(ObservationArea area, FloatMatrix image,  Runnable onFinish, Runnable onProgress) throws RendererException{
		if (runningContexts!=null){
			throw new RendererException("rendering process is already running");
		}
		doStop = false;
		
		area = new ObservationArea(area);//create copy of data;
		int w=image.getWidth();
		int h=image.getHeight();
		area.setResolution(w, h);		

		//create and initialize contexts
		runningContexts = new RenderingContext[numThreads];
		
		for (int i=0;i<numThreads;++i){
			runningContexts[i] = prepareRendering(area);
		}
		//now start rendering

		//creating and starting renderer threads
		final RenderRunner runners[]=new RenderRunner[numThreads];
		for(int i=0;i<numThreads;++i){
			runners[i]=new RenderRunner(new ObservationArea(area),image,runningContexts[i], i, numThreads);
			runners[i].start();
		}
		//all renderers created and started.

		long timeStart = System.currentTimeMillis();
		//joining to all runners to wait their completion

		long timeout = 500;
		//now waiting until thay all stop
		//waiting for first active thread
		boolean renderFinished;
		try{
			do{
				renderFinished = true;
				for (int i=0;i<runners.length;++i){
					if (runners[i].isAlive()){
						runners[i].join(timeout);
						//call onProgress
						onProgress.run();
						renderFinished = false;
					}
				}
			}while(!renderFinished);
		}catch (InterruptedException e){
			e.printStackTrace();//TODO do smething with InterruptedException
		}
		onFinish.run();
		timeElapsed = System.currentTimeMillis() - timeStart;
		
		runningContexts = null;
	}


	public final void renderHQ(ObservationArea area, BufferedImage image, ColorPattern pattern,  int antiAlias, Runnable onFinish, Runnable onProgress) throws RendererException{
		if (runningContexts!=null){
			throw new RendererException("rendering process is already running");
		}
		doStop = false;
		
		area = new ObservationArea(area);//create copy of data;
		int w=image.getWidth();
		int h=image.getHeight();
		area.setResolution(w, h);		

		//create and initialize contexts
		runningContexts = new RenderingContext[numThreads];
		
		for (int i=0;i<numThreads;++i){
			runningContexts[i] = prepareRendering(area);
		}
		//now start rendering

		//creating and starting renderer threads
		final RenderRunnerHQ runners[]=new RenderRunnerHQ[numThreads];
		for(int i=0;i<numThreads;++i){
			runners[i]=new RenderRunnerHQ(
					new ObservationArea(area),runningContexts[i], image, pattern, antiAlias, i, numThreads
					);
			runners[i].start();
		}
		//all renderers created and started.

		long timeStart = System.currentTimeMillis();
		//joining to all runners to wait their completion

		long timeout = 500;
		//now waiting until thay all stop
		//waiting for first active thread
		boolean renderFinished;
		try{
			do{
				renderFinished = true;
				for (int i=0;i<runners.length;++i){
					if (runners[i].isAlive()){
						runners[i].join(timeout);
						//call onProgress
						onProgress.run();
						renderFinished = false;
					}
				}
			}while(!renderFinished);
		}catch (InterruptedException e){
			e.printStackTrace();//TODO do smething with InterruptedException
		}
		onFinish.run();
		timeElapsed = System.currentTimeMillis() - timeStart;
		
		runningContexts = null;
	}

	
	//used for rendering in parallel
	protected void renderInterlaced(ObservationArea area, FloatMatrix image, RenderingContext context, int shift, int offset) {
		int w=image.getWidth();
		int h=image.getHeight();
		
		double[] xy=new double[2];
		
		for (int iy=shift;iy<h;iy+=offset){
			for (int ix=0;ix<w;++ix){
				area.scr2abs(ix, iy, xy);
				image.set(ix, iy, renderPoint(xy[0],xy[1], context));					
			}
			if (doStop)
				break;
			//accounting rendering progress
			context.setProgress((iy*100)/h);
		}
		finishRendering(area, image, context);
	}

	private static double merge(double x0, double x1, double p){
		return x0*(1.0-p)+x1*p;
	}
	//used for rendering in parallel
	protected void renderHQInterlaced(ObservationArea area, BufferedImage image, ColorPattern pattern, RenderingContext context, int antiAlias, int shift, int offset) {
		int w=image.getWidth();
		int h=image.getHeight();
		
		double[] xy00=new double[2];
		double[] xy01=new double[2];
		double[] xy10=new double[2];
		double[] xy11=new double[2];
		
		int[] colorRow = new int[w];
		int sr, sg, sb;
		
		for (int iy=shift;iy<h;iy+=offset){
			for (int ix=0;ix<w;++ix){
				area.scr2abs(ix, iy, xy00);
				area.scr2abs(ix+1, iy+1, xy11);
				area.scr2abs(ix+1, iy, xy10);
				area.scr2abs(ix, iy+1, xy01);
				
				sr = 0;
				sg = 0;
				sb = 0;
				for (int dx = 0;dx<antiAlias;dx++){
					for (int dy = 0; dy<antiAlias; dy++){
						double p = (double)dx/(double)antiAlias;
						double q = (double)dy/(double)antiAlias;
						
						double x = merge(merge(xy00[0],xy10[0],p),merge(xy01[0],xy11[0],p),q) ;
						double y = merge(merge(xy00[1],xy10[1],p),merge(xy01[1],xy11[1],p),q) ;
						
						int rgb = renderAbsPoint(x,y, context, pattern);
						
						sr += rgb & 0xFF;
						rgb = rgb >> 8;
						sg += rgb & 0xFF;
						rgb = rgb >> 8;
						sb += rgb & 0xFF;
					}
				}
				int aanum = antiAlias*antiAlias;
				sr = sr / aanum;
				sg = sg / aanum;
				sb = sb / aanum;
				
				colorRow[ix] = sr | (sg<<8) | (sb<<16);
			}
			image.setRGB(0, iy, w, 1, colorRow, 0, w);
			if (doStop)
				break;
			//accounting rendering progress
			context.setProgress((iy*100)/h);
		}
		finishRendering(area, null, context);
	}
	
	
	/** Direct rendering to the pattern, without promediate data*/
	private int renderAbsPoint(double x, double y, RenderingContext context, ColorPattern pattern) {
		float v = renderPoint(x, y, context);
		return pattern.renderPoint(v);
	}

}
