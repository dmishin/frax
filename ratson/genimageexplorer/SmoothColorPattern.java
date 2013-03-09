package ratson.genimageexplorer;

import java.awt.Component.BaselineResizeBehavior;
import java.awt.image.BufferedImage;

import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

/**Smooth but slow solor stripe pattern, defined by sines
 * 
 * @author dim
 *
 */
public class SmoothColorPattern extends ColorPattern {

	private float k=0.05f;
	private float b=0.0f;
	protected int[] patternData=new int[256];
	
	
	public void setK(double k){
		this.k=(float) k;
	}
	
	public SmoothColorPattern(){
		double xr,xg,xb;
		int ir,ib,ig;
		int irgb;
		for (int i=0;i<patternData.length;++i){
			double v=i/(double)patternData.length*Math.PI*2;

			xr=Math.sin(v)*0.5+0.5;
			xb=Math.sin(v+Math.PI*2/3.0)*0.5+0.5;
			xg=Math.sin(v+Math.PI*4/3.0)*0.5+0.5;
			
			ir=(int) Math.floor(xr*255);
			ig=(int) Math.floor(xg*255);
			ib=(int) Math.floor(xb*255);
			
			irgb=ir | (ig<<8) | (ib<<16);
			patternData[i]=irgb;
		}
	}
	
	protected void renderPoints(float[] src, int position, int[] rgb) {
		int p=position;
		for (int i = 0; i < rgb.length; i++, p++) {
			if (src[p]<0)
				rgb[i]=0;
			else{
				int idx=(int) (Utils.mod(src[p]*k+b,0.999999)*(patternData.length));
				rgb[i]=patternData[idx];
			}
		}


	}


	public double getK() {
		return k;
	}
	


	public void setShift(double f) {
		this.b=(float) f;
	}
	public double getShift() {
		return b;
	}

	protected void beforeRender(FloatMatrix raw, BufferedImage rendered) {
	}

	protected void endRender(FloatMatrix raw, BufferedImage rendered) {
	
	}

	@Override
	public int renderPoint(float v) {
		if (v == AbstractGenerator.BLACK_VALUE)
			return 0;
		else{
			int idx=(int) (Utils.mod(v*k+b,0.999999)*(patternData.length));
			return patternData[idx];
		}
	}

}
