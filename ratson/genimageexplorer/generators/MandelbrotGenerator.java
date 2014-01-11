package ratson.genimageexplorer.generators;

public class MandelbrotGenerator implements FunctionFactory {
	protected int maxIters=100;
	protected double r2Max;
	protected boolean isSmooth=true;
	
	protected double ln_ln_r2max;
	protected static final double ln2=Math.log(2);

	
	/**fly-away radius. Should be at least 2.0 for correct calculation
	 * Values, bigger than 2.0 used only for smoother flyaway speed approximation. 
	 * The bigger this value, the slower calculation (though complexity grows extremely slowly)
	 * */
	public void setRMax(double r){
		r2Max=r*r;
		ln_ln_r2max=Math.log(Math.log(r2Max));		
	}
	public double getRMax() {
		return Math.sqrt(r2Max);
	}
	
	public void setMaxIters(int maxIters) {
		this.maxIters = maxIters;
	}
	public int getMaxIters() {
		return maxIters;
	}
	
	public void setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
	}
	public boolean isSmooth() {
		return isSmooth;
	}

	public MandelbrotGenerator(){
		setRMax(2.0);
	}
	
	class Func extends Function{
		@Override
		float evaluate(double cx, double cy) {
			double x=cx,y=cy,x2,y2,xx;
			int iters=0;
			double r2=0;
			while (iters<maxIters && r2<r2Max){
				x2=x*x;
				y2=y*y;
				r2=x2+y2;
				
				xx=x2-y2+cx;
				y=2*x*y+cy;
				
				x=xx;
				
				iters++;
			}
			if (iters>=maxIters)
				return -1.0f;		
			//smooth extrapolation
			if (isSmooth){
				double dx=(ln_ln_r2max-Math.log(Math.log(r2)))/ln2;
				return (float)dx+(float)iters;
			}else{
				return iters;
			}		
		}
	}
	private Func funcInstance = new Func();

	public Function get() {
		return funcInstance;
	}

}
