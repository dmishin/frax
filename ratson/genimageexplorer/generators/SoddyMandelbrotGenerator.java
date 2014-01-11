package ratson.genimageexplorer.generators;

import ratson.genimageexplorer.ObservationArea;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class SoddyMandelbrotGenerator implements FunctionFactory{


	private int maxIter=200;
	private boolean circleMode = false;
	private double x0=0;
	private double y0=0;
	
	public double getX0() {
		return x0;
	}
	public void setX0(double x0) {
		this.x0 = x0;
	}
	public double getY0() {
		return y0;
	}
	public void setY0(double y0) {
		this.y0 = y0;
	}
	
	
	public void setCircleMode(boolean circleMode) {
		this.circleMode = circleMode;
	}
	public boolean getCircleMode() {
		return circleMode;
	}
	public int getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}
	private final static double SQ3=Math.sqrt(3);
	private boolean inCircle(double x, double y){
		return x*x+y*y < 1;
	}
		
	private boolean inTriangle(double x, double y){
		double r;
		
		r = x*x+y*y;
		if (r>1)
			return false;
		
		r=Utils.sqr(x-1)+Utils.sqr(y-SQ3);
		if (r<3) return false;
		r=Utils.sqr(x-1)+Utils.sqr(y+SQ3);
		if (r<3) return false;
		r=Utils.sqr(x+2)+Utils.sqr(y    );
		if (r<3) return false;
		return true;
	}
	
	class Func extends Function{

		@Override
		public
		float evaluate(double X, double Y) {
			int iters = 0;
			double xx;
			
			double x=x0;
			double y=y0;
			
			double sn;
			double cs;

			while (iters<maxIter && 
					(circleMode&&inCircle(x, y) || (!circleMode)&&inTriangle(x, y))){
				//first, rotate
				
				if (x>=0 && SQ3*x>=Math.abs(y)){
					//if in first triangle
					//do nothing
					sn=0;
					cs=1;
				}else{
					if (y>0){
						//rotate -2*pi/3
						cs=-0.5;
						sn=-SQ3*0.5;
					}else{
						//rotate 2*pi/3
						cs=-0.5;
						sn=+SQ3*0.5;
					}	
				}
				xx = x*cs-y*sn;
				y  = y*cs+x*sn;
				x = xx;
				
				
				//rotation done.
				//now shift
				//z1 =  -2*sqrt(3)+1-12/(z-2*sqrt(3)-1)
				x -= 1;
				
				//INVERT at 0
				double d = x*x+y*y;
				
				x = x/d;
				y=  y/d;
				
				//shift by 2/sqrt(3)
				
				x+=1/SQ3;
				//invert again
				d = x*x+y*y;
				x = x/d;
				y=  y/d;
				
				//shift back
				x+=1;
				
				//rotate back
				xx = x*cs+y*sn;
				y  = y*cs-x*sn;
				x = xx;

				
				//mandelbrot shift
				
				x+=X;
				y+=Y;
				
				iters++;
			}
			if (iters>=maxIter)
				return -1;
			return iters;
			
		}
		
	}
	public Function get() {
		return new Func();
	}
}
