package ratson.genimageexplorer.extra;

import ratson.genimageexplorer.generators.RenderingContext;


class HilbertUnwrapContext extends RenderingContext{
	public double x,y,z;
	public HilbertUnwrapContext(){
		super();
	}
	public void setxyz(double x1, double y1, double z1, double k) {
		x=x1*k;y=y1*k;z=z1*k;
	}
	/**Calculates position of point on 3d hilbert curve*/
	public void hilb3_point(double n, int lvl){
		if (lvl==0){
			x=0;y=n;z=0;
			return;
		}
		if ( n > 0.5){
		  hilb3_point(1 - n,lvl);
		  y=1-y;
		  return;
		}

		n=n*8;// %n <= 4 now
	    hilb3_point(n-Math.floor(n),lvl-1);

		if (n<=1){
		  setxyz(y,z,x,0.5);
		  return;
		}

		if (n<=2){
		  setxyz(x+1, z, y, 0.5);
		  return;
		}

		if (n<=3){
		  setxyz(x+1, z, y+1, 0.5);
		  return;
		}

		if (n<=4){
		  setxyz(1 - x, y, 2- z, 0.5);
		  return;
		}

	}

}
