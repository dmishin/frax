package ratson.genimageexplorer.extra;

import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.generators.Renderer;
import ratson.genimageexplorer.generators.RenderingContext;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class KleinSpaceFilling extends Renderer {

	private static final double sin2pi5 = Math.sin(2*Math.PI/5);
	private static final double cos2pi5 = Math.cos(2*Math.PI/5);
	private static final double sin1pi5 = Math.sin(Math.PI/5);
	private static final double cos1pi5 = Math.cos(Math.PI/5);
	
	private static final double r1 = Math.sqrt(Math.sqrt(1.0/5.0));//horizontal coordinate of pentagon side center
	private int maxIters = 1000;
	private boolean isKlein = true;

	public KleinSpaceFilling() {
	}
	

	protected void finishRendering(ObservationArea area, FloatMatrix image, RenderingContext renderContext) {
	}

	public boolean isKleinProjection(){
		return isKlein ;
	}
	public void setKleinProjection(boolean isKlein) {
		this.isKlein = isKlein;
	}
	public int getMaxIters() {
		return maxIters;
	}
	public void setMaxIters(int maxIters) {
		this.maxIters = maxIters;
	}
	
	
	public float renderPoint(double x, double y, RenderingContext renderContrxt) {
		
		//convert from poinkare to Klein
		if (!isKlein){
			double k = 1+x*x+y*y;
			if (k>2)
				return -1;
			x=x*2/k;
			y=y*2/k;
		}
		
		
		
		//first project point from the t=1 plane to the t^2-x^2-y^2 = 1 plane.
		double d2 = 1 - Utils.sqr(x) -Utils.sqr(y);
		if (d2<=0){
			return -1f;
		}
		
		double d = Math.sqrt(d2);
		x/=d;
		y/=d;
		double t = 1/d;
		
		//perform a shift
		
		
		//converted. Now performing an algorithm
		int itrs = 0;
		double xx;
		while (itrs < maxIters){
			//first, rotate the point
			while (!(x>=-1e-10 && sin1pi5*x - cos1pi5*Math.abs(y)>=-1e-10)){
				xx = cos2pi5*x - sin2pi5*y;
				y = cos2pi5*y + sin2pi5*x;
				x=xx;
				//itrs+=1;
			}
			//check, whether the point lays in the first section of pentagon
			if (x*x-r1*r1*y*y<r1*r1 ){
				break;//we have finally get tothe unit pentagon  
			}
			//looks like we are still far away.
			//then flipping the point
			//using the matrix
			//matrix([-2*r^2-1,0,2*r*sqrt(r^2+1)],[0,1,0],[-2*r*sqrt(r^2+1),0,2*r^2+1])
			double a0 = 2*r1*r1+1;
			double a1 = 2*r1*Math.sqrt(r1*r1+1);
			
			xx = -a0*x + a1*t;
			t =  -a1*x + a0*t;
			x=xx;
			//flipping done.
			itrs+=1;
		}
		if (itrs == maxIters)
			return -1;
		
		return itrs;
	}

}
