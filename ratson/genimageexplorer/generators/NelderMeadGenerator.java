package ratson.genimageexplorer.generators;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import ratson.genimageexplorer.ObservationArea;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class NelderMeadGenerator extends AbstractGenerator {
	double x0=0.0, y0=-1, x1=0.0, y1=1; //initial two points
	double eps=0.0001;
	private int maxIter=1000;
	private double alpha=1.05;
	private double gamma=0.3;
	private double beta=2.2;
	private double delta=0.6;
	private boolean isMovingAllPoints = false; //if true,all initial points are moved
	private boolean isReturningSteps=true;//If true - return number of steps, else return final value
	private int targetFunctionIndex = 0;
	
	public boolean getReturningSteps(){
		return isReturningSteps;
	}
	public void setReturningSteps( boolean v){
		isReturningSteps = v;
	}
	
	public int getTargetFunctionIndex() {
		return targetFunctionIndex;
	}
	public void setTargetFunctionIndex(int targetFunctionIndex) {
		this.targetFunctionIndex = targetFunctionIndex;
	}
	public boolean getMoveAllPoints(){
		return isMovingAllPoints;
	}
	public void setMoveAllPoints( boolean v){
		isMovingAllPoints = v;
	}
	
	public int getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}
	public double getX0() {
		return x0;
	}
	public void setX0(double x0) {
		this.x0 = x0;
	}
	public double getX1() {
		return x1;
	}
	public void setX1(double x1) {
		this.x1 = x1;
	}
	public double getY0() {
		return y0;
	}
	public void setY0(double y0) {
		this.y0 = y0;
	}
	public double getY1() {
		return y1;
	}
	public void setY1(double y1) {
		this.y1 = y1;
	}
	public double getEps() {
		return eps;
	}
	public void setEps(double eps) {
		this.eps = eps;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getBeta() {
		return beta;
	}
	
	public void setBeta(double beta) {
		this.beta = beta;
	}
	public double getGamma() {
		return gamma;
	}
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	public double getDelta() {
		return delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	
	private static void lincomb(double[]a, double ka, double[]b, double kb, double[]c){
		for (int i = 0; i < c.length; i++) {
			c[i] = a[i]*ka+b[i]*kb;
		}
	}
	
	
	public NelderMeadGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void finishRendering(ObservationArea area, FloatMatrix image,
			RenderingContext renderContext) {
		
		

	}
	@Override
	protected RenderingContext prepareRendering(ObservationArea area) {
		return new NelderMeadContext();
	}

	@Override
	public float renderPoint(double x, double y, RenderingContext context) {
		// TODO Auto-generated method stub
		NelderMeadContext ctx = (NelderMeadContext) context;
		if (isMovingAllPoints){
			ctx.slots[0].set(x,y);
			ctx.slots[0].f = func(ctx.slots[0].x);
	
			ctx.slots[1].set(x0+x,y0+y);
			ctx.slots[1].f = func(ctx.slots[1].x);
	
			ctx.slots[2].set(x1+x,y1+y);
			ctx.slots[2].f = func(ctx.slots[2].x);
		}else{
			ctx.slots[0].set(x,y);
			ctx.slots[0].f = func(ctx.slots[0].x);
	
			ctx.slots[1].set(x0,y0);
			ctx.slots[1].f = func(ctx.slots[1].x);
	
			ctx.slots[2].set(x1,y1);
			ctx.slots[2].f = func(ctx.slots[2].x);
		}
		int iter = 0;
		while (iter < maxIter){
			iter ++;
			ctx.sortSlots();
			NelderMeadContext.Slot sh,sg,sl;
			sh = ctx.slots[2];
			sg = ctx.slots[1];
			sl = ctx.slots[0];
			if ( exitCondition( sl.x))
				break;
			
			//calculate center
			lincomb(ctx.slots[0].x, 0.5, ctx.slots[1].x, 0.5, ctx.xc);
			
			//calculate reflect
			lincomb(ctx.xc, 1+alpha, sh.x, -alpha, ctx.xr );
			double fr = func( ctx.xr); 
			if (fr<sl.f){
//	            #expand
				lincomb(ctx.xr, beta, ctx.xc, 1-beta, ctx.xe);
				double fe = func( ctx.xe);
				if (fe < fr){
//	                #expand success
					ctx.slots[2].set(ctx.xe, fe);
				}else{
//	                show("expand fail, use flip")
					ctx.slots[2].set(ctx.xr, fr);
				}
			}else if( fr < sg.f){
				// 	#not so good, but at least better than.
				ctx.slots[2].set( ctx.xr, fr);
			}else{
//	        else: #too bad (worse that fg )
				if ( fr< sh.f){//at least, not worse than xh?
					//swap xh and xr
					double[] xx = sh.x;
					double ff = sh.f;
					
					sh.x = ctx.xr;
					sh.f = fr;
					
					ctx.xr = xx;
					fr = ff;
				}
//	            #now xr is worse than xh in any case
//	            #try contract
				lincomb(sh.x, gamma, ctx.xc, 1-gamma, ctx.xs);
				double fs = func( ctx.xs );
				if (fs < sh.f){
//	                #contract success, use it
					ctx.slots[2].set(ctx.xs, fs);
				}else{
//	                #contract fail.
//	                #last resort: global shrink
					for (int i=1; i<3;++i){
						lincomb( ctx.slots[i].x, delta, sl.x, 1-delta, ctx.slots[i].x);
						ctx.slots[i].f = func( ctx.slots[i].x);
					}
				}
			}
		}
		if (iter >= maxIter){
			return -1;
		}else{
			if (isReturningSteps){
				return iter;
			}else{
				return (float) (ctx.slots[0].x[0]);
			}
		}
	}
	
	
	private boolean exitCondition(double[] x){
		switch( targetFunctionIndex ){
		case 0:
		case 3:
			return Math.abs(Math.abs(x[0])-1)+Math.abs(x[1]) < eps;
		case 1:
		case 2:
			return Math.abs( x[0])+Math.abs(x[1]) < eps;
		case 4: //nelder-mead
			return Math.abs(x[0]-1)+Math.abs(x[1]-1) < eps;
		}
		return true;
	}
	private double func( double[] xy){
		//function with two minimums
		double x = xy[0];
		double y = xy[1];
		switch( targetFunctionIndex ){
		case 0:
			return 
				(Utils.sqr(x-1)+Utils.sqr(y))*
				(Utils.sqr(x+1)+Utils.sqr(y));
		case 3:
			return 
			(Utils.sqr(Utils.sqr(x-1))+Utils.sqr(y))*
			(Utils.sqr(x+1)+Utils.sqr(Utils.sqr(y)));

		case 1:
			return x*x+y*y;
		case 2:
			return Utils.sqr(Utils.sqr(x))+Utils.sqr(y);
		case 4:
			return Utils.sqr(1-x) + 100*Utils.sqr(y-x*x);
		}
		throw new RuntimeException( "Incorrect target fucntion index");
	}

	public static void main(String[] args) {
		NelderMeadContext ctx = new NelderMeadContext();
		NelderMeadGenerator gen = new NelderMeadGenerator();
		gen.renderPoint(0.1, 0.3, ctx);
	}
}
