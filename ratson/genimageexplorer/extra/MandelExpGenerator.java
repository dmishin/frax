package ratson.genimageexplorer.extra;

import ratson.genimageexplorer.generators.Function;
import ratson.genimageexplorer.generators.FunctionFactory;

public class MandelExpGenerator implements FunctionFactory{

	private int maxIter = 100;
	public int getMaxIters() {
		return maxIter;
	}
	public void setMaxIters(int maxIter) {
		this.maxIter = maxIter;
	}
	double r2max = 100;
	public double getR2Max() {
		return r2max;
	};
	public void setR2Max(double r2max) {
		this.r2max = r2max;
	}
	class Func extends Function{
		@Override
		public float evaluate(double x0, double y0) {
			double x=0, y=0;
			int iter = 0;
			double r2=x*x+y*y;
			while (r2<r2max && iter < maxIter){
				//x1 = e^x - x + c


				
				/*//sin
				double x1 = Math.sin(x)*Math.cosh(y)+x0;
				double y1 = -Math.cos(x)*Math.sinh(y)+y0;
				*/

				
				/*// quite pretty
				double er = Math.exp(x);
				double x1 = 0.5*(er+1/er)*Math.cos(y)+x0;
				double y1 = 0.5*(er-1/er)*Math.sin(y)+y0;
				 */
				/*//e^x
				double er = Math.exp(x);
				double x1 = er*Math.cos(y)+x0;
				double y1 = er*Math.sin(y)+y0;
				*/

				//e^x-x
				double er = Math.exp(x);
				double x1 = er*Math.cos(y)-x+x0;
				double y1 = er*Math.sin(y)-y+y0;
				
				
				x=x1;
				y=y1;
				r2=x*x+y*y;
				iter++;
			}
			if (iter>=maxIter)
				return -1;
			return iter;
		}
		
	}
	public Function get() {
		return new Func();
	}
}
