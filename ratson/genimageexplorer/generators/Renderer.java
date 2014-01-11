package ratson.genimageexplorer.generators;

import java.awt.image.BufferedImage;

import ratson.genimageexplorer.ColorPattern;
import ratson.genimageexplorer.ObservationArea;
import ratson.utils.FloatMatrix;

public final class Renderer {

	public static final float BLACK_VALUE = -1;

	private int numThreads = 1;
	private FunctionFactory functionMaker=null;
	private Function[] functions=null;
	
	protected boolean doStop=false;
	
	public Renderer(FunctionFactory f){
		functionMaker = f;
	}
	
	public Renderer() {
		functionMaker = null;
	}
	
	public FunctionFactory getFunction(){
		return functionMaker;
	}
	public void setFunction(FunctionFactory f){
		functionMaker = f;
	}

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
	
	private long timeElapsed = 0;

	public long getTimeElapsed() {
		return timeElapsed;
	}
	/**Returns current rendering progress for ll contexts*/
	public int getProgress(){
		return 0;
		//TODO: stub
	}

	private class RenderRunner extends Thread{
		private int shift;//first line to render
		private int offset;//how much to skip between lines
		private ObservationArea loc;
		private FloatMatrix image;
		private Function function; 

		public RenderRunner (ObservationArea loc, FloatMatrix image, Function function, int shift, int offset){
			super("Renderer#"+offset);
			this.shift=shift;
			this.offset=offset;
			this.loc=loc;
			this.image=image;
			this.function = function;			
		}
		
		public void run() {
			if (function == null)
				throw new RuntimeException("Context is 0");
			renderInterlaced(loc, image,function,shift, offset);
		}
	}
	
	class RenderRunnerHQ extends Thread{
		private int y0;
		private int dy;
		private int progress = 0;
		private ObservationArea area;
		private Function function;
		private ColorPattern pattern;
		private BufferedImage image;
		private int aa;
		public RenderRunnerHQ(ObservationArea area,Function function,BufferedImage image, ColorPattern pattern, int antiAlias, int y0, int dy){
			this.y0 = y0;
			this.dy = dy;
			this.area = area;
			this.progress = 0;
			this.function = function;
			this.pattern = pattern;
			this.image = image;
			this.aa = antiAlias;
		}
		public int getProgress() {
			return progress;
		}
		@Override
		public void run() {
			if (function == null)
				throw new RuntimeException("Context is 0");
			renderHQInterlaced(area, image, pattern,function, aa, y0, dy);
		}
		
	}
	

	/**Renders image, using multithreaded rendering. This method waits until rendering is finished
	 * 
	 * @param area area to render
	 * @param image destination
	 * @throws RendererException 
	 */
	public final void render(ObservationArea area, FloatMatrix image,  Runnable onFinish) throws RendererException{
		doStop = false;
		
		area = new ObservationArea(area);//create copy of data;
		int w=image.getWidth();
		int h=image.getHeight();
		area.setResolution(w, h);		

		//create and initialize contexts
		createFunctionPool();
		//now start rendering

		//creating and starting renderer threads
		final RenderRunner runners[]=new RenderRunner[numThreads];
		for(int i=0;i<numThreads;++i){
			runners[i]=new RenderRunner(new ObservationArea(area),image,functions[i], i, numThreads);
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
						//onProgress.run();
						renderFinished = false;
					}
				}
			}while(!renderFinished);
		}catch (InterruptedException e){
			e.printStackTrace();//TODO do smething with InterruptedException
		}
		onFinish.run();
		timeElapsed = System.currentTimeMillis() - timeStart;
		
		functions = null;
	}


	private void createFunctionPool() throws RendererException
	{
		if (functions!=null){
			throw new RendererException("Rendering process is already running");
		}
		functions = new Function[numThreads];
		for (int i=0;i<numThreads;++i){
			functions[i] = functionMaker.get();
		}
	}
	public final void renderHQ(ObservationArea area, BufferedImage image, ColorPattern pattern,  int antiAlias, Runnable onFinish, Runnable onProgress) throws RendererException{
		doStop = false;
		
		area = new ObservationArea(area);//create copy of data;
		int w=image.getWidth();
		int h=image.getHeight();
		area.setResolution(w, h);		

		//create and initialize contexts
		createFunctionPool();
		//now start rendering

		//creating and starting renderer threads
		final RenderRunnerHQ runners[]=new RenderRunnerHQ[numThreads];
		for(int i=0;i<numThreads;++i){
			runners[i]=new RenderRunnerHQ(
					new ObservationArea(area),functions[i], image, pattern, antiAlias, i, numThreads
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
		
		functions = null;
	}

	
	//used for rendering in parallel
	protected void renderInterlaced(ObservationArea area, FloatMatrix image, Function function, int shift, int offset) {
		int w=image.getWidth();
		int h=image.getHeight();
		
		double[] xy=new double[2];
		
		for (int iy=shift;iy<h;iy+=offset){
			for (int ix=0;ix<w;++ix){
				area.scr2abs(ix, iy, xy);
				image.set(ix, iy, function.evaluate(xy[0],xy[1]));					
			}
			if (doStop)
				break;
			//accounting rendering progress
			//TODO
			//function.setProgress((iy*100)/h);
		}
	}

	private static double merge(double x0, double x1, double p){
		return x0*(1.0-p)+x1*p;
	}
	//used for rendering in parallel
	protected void renderHQInterlaced(ObservationArea area, BufferedImage image, ColorPattern pattern, Function function, int antiAlias, int shift, int offset) {
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
						
						int rgb = pattern.renderPoint(function.evaluate(x, y));
						
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
			//function.setProgress((iy*100)/h);
		}
	}
	
	
}
