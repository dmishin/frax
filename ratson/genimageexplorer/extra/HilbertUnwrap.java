package ratson.genimageexplorer.extra;

import java.awt.image.renderable.RenderContext;

import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.genimageexplorer.generators.RenderingContext;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class HilbertUnwrap extends AbstractGenerator {

	private double x0, y0, z0, r02;
	
	public HilbertUnwrap(){
		x0=0.3;
		y0=0.4;
		z0=0.36;
		r02=Utils.sqr(0.24);
	}
	

	protected void finishRendering(ObservationArea area, FloatMatrix image, RenderingContext renderContext) {
	}

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
	public double getZ0() {
		return z0;
	}
	public void setZ0(double z0) {
		this.z0 = z0;
	}
	public double getR() {
		return Math.sqrt(r02);
	}
	public void setR(double r) {
		r02 = r*r;
	}
	
	public float renderPoint(double X, double Y, RenderingContext renderContrxt) {
		HilbertUnwrapContext ctx = (HilbertUnwrapContext)renderContrxt;
		if (X<0 || X>1 ||Y<0 ||Y>1)
			return -1;
		
		ctx.hilb3_point(hilb_index(X, Y, 30), 30);

		double r2=Utils.sqr(ctx.x-x0)+Utils.sqr(ctx.y-y0)+Utils.sqr(ctx.z-z0);
		
		if (r2>r02) return -1;
		return (float)Math.pow( (1.0/(1-r2/r02)), 0.333);
		
	}
	protected RenderingContext prepareRendering(ObservationArea area) {
		return new HilbertUnwrapContext();
	}


	private double hilb_index(double x, double y, int lvl){
		if (lvl==0) return x;
		if ( x<0.5 && y<0.5 )
			return hilb_index(y*2, x*2, lvl-1)*0.25;

		if ( x<0.5 && y>=0.5)
			return 0.25 + hilb_index(x*2, y*2-1, lvl-1)*0.25;

		if (x>=0.5 && y<0.5)
			return  0.75 + hilb_index(1-y*2, 2-x*2, lvl-1)*0.25;

		if (x>=0.5 && y>=0.5)
			return 0.5 + hilb_index(x*2-1, y*2-1, lvl-1)*0.25;
		
		throw new RuntimeException("hilb-index: coordinate is invalid");
	}
	

	
	


}
