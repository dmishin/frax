package ratson.genimageexplorer.extra.bounce;

public class Wall {
	
	double nx;
	double ny;
	double dp;
	
	public void set(double x,double y,double dx,double dy){
		double l=Math.sqrt(dx*dx+dy*dy);
		nx=-dy/l;
		ny=dx/l;
		
		dp=x*nx+y*ny;
	}
	
	public Wall(){
		set(0,0,0,1);
	}
	
	public Wall(double x0, double y0, double dx, double dy) {
		set(x0,y0,dx,dy);
	}

	double dist(double x0,double y0){
		return Math.abs(x0*nx+y0*ny-dp);
	}

	public double normProject(double vx, double vy) {
		return nx*vx+ny*vy;
	}
}
