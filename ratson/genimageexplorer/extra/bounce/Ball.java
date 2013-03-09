package ratson.genimageexplorer.extra.bounce;

public class Ball {
	public double x,y,vx,vy;
	public Ball(){
		x=0;y=0;
		vx=0;
		vy=0;
	}
	public Ball(double x,double y,double vx, double vy){
		this.x=x;
		this.y=y;
		this.vx=vx;
		this.vy=vy;
	}
	void move(double dt){
		x=x+dt*vx;
		y=y+dt*vy;
	}
	
	double getV(){
		return Math.sqrt(vx*vx+vy*vy);
	}
	
	double collisionTime(Wall w){
		
		//projecting ball's z and v vectors on a direction, normal to wall
		double pv=w.normProject(vx,vy);
		double rval=(w.normProject(x, y)-w.dp)/(-pv) - Math.abs(getRadius()/pv);
		
		if (Double.isInfinite(rval))
			return Double.NaN;
		if (rval<0) return Double.NaN;
		else return rval;
	}		
	public double getRadius() {
		return 1;
	}
	double collisionTime(Ball b){
		
		double dx=x-b.x,
			dy=y-b.y,
			dvx=vx-b.vx,
			dvy=vy-b.vy;
		
		double v2=dvx*dvx+dvy*dvy;
		
		double vz=dx*dvx+dy*dvy;
		double v_z=-dx*dvy+dy*dvx;
		
		double l2=dx*dx+dy*dy;
		
		double D=v2*4 - v_z*v_z;
		
		if (D<=0) return Double.NaN;//no collision at all
		double t=(-vz-Math.sqrt(D))/v2;
		
		if (Double.isInfinite(t))
			return Double.NaN;
		
		if (t>=0) 
			return t;
		else 
			return Double.NaN;//collisions in past are not counted;
	}
	void bounce(Ball b){
		//performs bouncing of wo balls
		double k;
		double dx=x-b.x,
		dy=y-b.y,
		dvx=vx-b.vx,
		dvy=vy-b.vy;

		k=-(dx*dvx+dy*dvy)/(dx*dx+dy*dy);
		vx+=k*dx;
		vy+=k*dy;
		
		b.vx-=k*dx;
		b.vy-=k*dy;
	
		
		//System.out.println(Math.sqrt(Math.pow(b.y-y, 2)+Math.pow(b.x-x, 2)));
		
	}
	void bounce(Wall w){
		double p=w.normProject(vx, vy)*2;
		vx-=p*w.nx;
		vy-=p*w.ny;
		
	}
	public static void main(String[] args) {
		Ball b1=new Ball();
		Ball b2=new Ball(10,3,-1,0);
		System.out.println(b1.collisionTime(b2));
	}
	public void set(double x, double y, double vx, double vy) {
		
		this.x=x;
		this.y=y;
		this.vx=vx;
		this.vy=vy;
		
	}
}
