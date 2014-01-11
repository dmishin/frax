package ratson.genimageexplorer.extra;

import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.generators.Renderer;
import ratson.genimageexplorer.generators.MandelbrotGenerator;
import ratson.genimageexplorer.generators.RenderingContext;
import ratson.utils.CMath;

public class CollatzFractal extends MandelbrotGenerator {
	
	public CollatzFractal (){
		super();
		setRMax(100);
	}
	private boolean mandelbrotMode = false;
	public void setMandelbrotMode(boolean mandelbrotMode) {
		this.mandelbrotMode = mandelbrotMode;
	}
	public boolean isMandelbrotMode() {
		return mandelbrotMode;
	}

	protected RenderingContext prepareRendering(ObservationArea area) {
		
		RenderingContext rc = super.prepareRendering(area);
		//rc.setUserData(new ratson.utils.Complex[3]);
		return rc;
	}
	
	public float renderPoint(double X, double Y, RenderingContext context) {
		double x,y;
		int iters = 0;
		double x2=0,x2_prev=0;
		double fx, fy;
		double x1,y1;
		if (!mandelbrotMode){
			x=X;y=Y;
			
			do{
				fx = Math.cos(Math.PI*x)*Math.cosh(Math.PI*y);
				fy = -Math.sin(Math.PI*x)*Math.sinh(Math.PI*y);
				
				
				x1=0.25*(1+4*x-fx-2*(fx*x-fy*y));
				y1=0.25*( +4*y-fy-2*(fx*y+fy*x));
				x=x1;
				y=y1;
				iters++;
				x2_prev=x2;
				x2=x*x+y*y;
			}while (x2<r2Max && iters<maxIters);
		}else{
			x=0;y=0;
			do{
				fx = Math.cos(Math.PI*x)*Math.cosh(Math.PI*y);
				fy = -Math.sin(Math.PI*x)*Math.sinh(Math.PI*y);
				
				
				x1=0.25*(1+4*x-fx-2*(fx*x-fy*y));
				y1=0.25*( +4*y-fy-2*(fx*y+fy*x));
				x=x1 + X;
				y=y1 + Y;
				iters++;
				x2_prev=x2;
				x2=x*x+y*y;
			}while (x2<this.r2Max && iters<maxIters);
		}
		
		if (iters>=maxIters)
			return -1;
		if (isSmooth){
			//return (float) (iters + Math.log(r2Max/x2_prev)/Math.log(x2/x2_prev));
			double ll_x2_prev = Math.log(Math.log(x2_prev));
			double ll_x2      = Math.log(Math.log(x2     ));
			return (float) (iters + (ln_ln_r2max - ll_x2_prev)/(ll_x2 - ll_x2_prev) );
			
			
		}
		return iters;		
		
	}

}
