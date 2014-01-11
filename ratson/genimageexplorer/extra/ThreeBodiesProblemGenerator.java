package ratson.genimageexplorer.extra;

import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.extra.BilliardBallGenerator.Func;
import ratson.genimageexplorer.generators.Function;
import ratson.genimageexplorer.generators.FunctionFactory;
import ratson.genimageexplorer.generators.Renderer;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class ThreeBodiesProblemGenerator implements FunctionFactory {

	
	double m = 1;
	double r = 1;
	double G = 1;
	
	private double tMax = 100;
	
	
	public double getTMax() {
		return tMax;
	}
	public void setTMax(double max) {
		tMax = max;
	}

	class Func extends Function{
		double omega;
		Func(){
			omega = 0.5*Math.sqrt(m*G/(r*r*r));			
		}
		/**coordinates of stars*/
		private double getx(double t){
			return r*Math.cos(omega*t);
		}
		private double gety(double t){
			return r*Math.sin(omega*t);
		}
		@Override
		public float evaluate(double x0, double y0) {
			//three orbiting bodies 
			
			double t=0;

			double x,y, vx, vy;
			//x=0;y=0;vx=x0;vy=y0;
			x=x0;y=y0;vx=0;vy=0;
			
			double d1, d2, ax, ay;
			
			double sx, sy;
			while (true){
				
				/*calculate accelration*/
				sx = getx(t);
				sy = gety(t);
				//First star
				d1 = dist2(sx,sy,x,y);
				d2 = dist2(-sx,-sy,x,y);//second star is symetric
				
				//now calculate absolute accelerations, multiplied by 1/r:
				double a1r = m*G/d1/Math.sqrt(d1);
				double a2r = m*G/d2/Math.sqrt(d2);
				
				//and their projections

				
				ax = (sx-x)*a1r + (-sx - x)*a2r;
				ay = (sy-y)*a1r + (-sy - y)*a2r;
				
				//now integrating (using simple Euler integration)
				x+=vx*dt;
				y+=vy*dt;
				vx+=ax*dt;
				vy+=ay*dt;
				t+=dt;
				
				if (x*x+y*y>300 || t> tMax)
					break;
			}
			
			if (t>tMax)
				return -1;
			return (float) t;
		}
		
	}
	
	
	private static double dist2(double x1, double y1, double x2, double y2){
		return Utils.sqr(x1-x2)+Utils.sqr(y1-y2);
	}
	private double dt = 0.01;
	public double getDt() {
		return dt;
	}
	public void setDt(double dt) {
		this.dt = dt;
	}
	public Function get() {
		return new Func();
	}

}
