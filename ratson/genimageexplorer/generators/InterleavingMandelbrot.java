package ratson.genimageexplorer.generators;

import java.util.Random;

import ratson.genimageexplorer.ObservationArea;

import ratson.utils.CMath;
import ratson.utils.Complex;
import ratson.utils.FloatMatrix;

public class InterleavingMandelbrot implements FunctionFactory  {

	public class Func extends Function{
		public Complex z=new Complex();
		public Complex c=new Complex();
		public Complex zn=new Complex();
		@Override
		public
		float evaluate(double cx, double cy) {
			c.set(cx,cy);
			z.set(c);
			int iters=0;
			double r2=0;			
			while (iters<maxIters && r2<r2Max){
				int pow;
				if (iters%2==1){
					pow=power2;
				}else{
					pow=power1;
				}
				
				zn.set(z);
				for (int i=1;i<pow;++i){
					CMath.mul(zn, z, zn);
				}
				CMath.add(zn, c, z);
				r2=z.abs2();
				iters++;
			}
			if (iters>=maxIters)
				return -1.0f;

			//smooth extrapolation
			if (isSmooth){
				double dx=(ln_ln_r2max-Math.log(Math.log(r2)))/lnNM2;
				return (float)dx+(float)iters;
			}else{
				return iters;
			}		

		}
		
	}
	private int maxIters=100;
	private double r2Max;
	private int power1=2, power2=3;
	private boolean isSmooth=true;
	private double lnNM2=Math.log(power1*power2)/2;
	
	private double ln_ln_r2max;

	public void setPower1(int power1) {
		this.power1 = power1;
		lnNM2=Math.log(power1*power2)/2;
	}
	public int getPower1() {
		return power1;
	}
	public void setPower2(int power2) {
		this.power2 = power2;
		lnNM2=Math.log(power1*power2)/2;
	}
	public int getPower2() {
		return power2;
	}
	
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
	public int getMaxIters() {
		return maxIters;
	}
	
	public void setMaxIters(int maxIters) {
		this.maxIters = maxIters;
	}
	
	public void setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
	}
	public boolean isSmooth() {
		return isSmooth;
	}
	
	
	public InterleavingMandelbrot(){
		setRMax(2.0);
	}
	
	public Function get() {
		return new Func();
	}
}
