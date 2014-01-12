package ratson.genimageexplorer.extra.bounce;

import java.util.Random;

public class BallSimulator {
	int numBalls;
	double nearestCollision;
	double elapsedTime;
	public SimulatedBall[] balls;
	public Wall[] walls;
	private double timeAfterCollision;
	//private int collider1,collider2;
	
	
	public double getElapsedTime() {
		return elapsedTime;
	}
	
	public void resetElapsedTime(){
		elapsedTime=0;
	}
	public BallSimulator(){
		numBalls=0;
		balls=new SimulatedBall[0];
		setWalls(0,0,10,10);
		timeAfterCollision=0;
		nearestCollision=Double.NaN;
		
	}
	public void setBallsNum(int n){
		numBalls=n;
		balls=new SimulatedBall[n];
		for (int i=0;i<n;++i)
			balls[i]=new SimulatedBall(new Ball());

	}
	public void setWalls(double d, double e, double f, double g){
		walls=new Wall[4];
		walls[1]=new Wall(d,0,0,-1);
		walls[0]=new Wall(0,e,-1,0);
		walls[2]=new Wall(0,f,-1,0);
		walls[3]=new Wall(g,0,0,-1);
		//walls[4]=new Wall(20,-2,-1,1);
	}
	
	public void recalcCollisions(){
		nearestCollision=Double.NaN;
		
		for (int i = 0; i < balls.length; i++) {
			balls[i].collisionTime=Double.NaN;
			balls[i].collisionTarget=-1;
		}
		
		for (int i=0;i<numBalls;++i){
			for (int j=i+1;j<numBalls;++j){
				double t=balls[i].ball.collisionTime(balls[j].ball);
				if (!Double.isNaN(t)){
					//if there is a collision
					balls[i].updateCollisionTime(t, j);
					balls[j].updateCollisionTime(t, i);
					if (t<nearestCollision || Double.isNaN(nearestCollision))
						nearestCollision=t;
				}
			}
		}
		//TODO collisions with walls
		for (int i = 0; i < walls.length; i++) {
			for (int j = 0; j < balls.length; j++) {
				double t=balls[j].ball.collisionTime(walls[i]);
				if (!Double.isNaN(t)){
					balls[j].updateCollisionTime(t, -i-1);//negative numbers are walls
					if (t<nearestCollision || Double.isNaN(nearestCollision))
						nearestCollision=t;
				}
			}
		}
	}
	/**updates collision times with ball #idx
	 * 
	 * @param idx index of ball
	 */
	public void updateCollisions(int idx){
		SimulatedBall b0=balls[idx];
		for (int i = 0; i < balls.length; i++) {
			if (i!=idx){
				double ct=balls[i].ball.collisionTime(b0.ball);
				
				if (!Double.isNaN(ct)){//if there will be collision
					b0.updateCollisionTime(ct,i);
					balls[i].updateCollisionTime(ct,idx);
					
					if (ct<nearestCollision){
						nearestCollision=ct;
					}
				}
			}
		}
	}
	public void simulate(double dt){
		simulate(dt,null);
	}
	public void simulate(double dt,BilliardEvent evtHandler){
		timeAfterCollision+=dt;
		
		while ( !Double.isNaN(nearestCollision) && timeAfterCollision>nearestCollision){
			//moving to next collision
			timeAfterCollision-=nearestCollision;
			nextCollision(evtHandler);
		}
		
	}
	public void nextCollision(){
		nextCollision(null);
	}
	public boolean nextCollision(BilliardEvent evtHandler){
		if (Double.isNaN(nearestCollision)){
			System.err.println("There is no collision");
			if (evtHandler!=null)
				return evtHandler.onNoMoreCollisions(this);
			else
				return false;
		}
		//simulating all balls until collision time
		for (int i = 0; i < balls.length; i++) {
			balls[i].move(nearestCollision);
		}
		elapsedTime+=nearestCollision;
		//now bouncing two nearest balls
		int collider=findNextCollider();
		boolean rval=false;
		if (collider <0)
			throw new RuntimeException("Wrong ball index");
		else{
			int collideTgt=balls[collider].collisionTarget;
			if (collideTgt>=0){
				balls[collider].bounce(balls[balls[collider].collisionTarget]);
				if (evtHandler!=null)
					rval = evtHandler.onBallBounce(this, collider, collideTgt);
				else
					rval = false;
			}else{
				balls[collider].bounce(walls[-collideTgt-1]);
				if (evtHandler!=null)
					rval = evtHandler.onWallBounce(this, collider, -collideTgt-1);
				else
					rval = false;
			}
			
			//now updating the collisions, taking in account that two balls changed their directions
			//TODO do effective update
			recalcCollisions();
		}
		return rval;
	}

	/** returns index of ball that will collide next
	 * 
	 * @return integer index of ball with less collision time or -1 if all balls has no collisions predicted
	 */
	private int findNextCollider() {
		double ctime=Double.NaN;
		int idx=-1;
		for (int i = 0; i < balls.length; i++) {
			if (Double.isNaN(balls[i].collisionTime))
				continue;
			if (Double.isNaN(ctime) || balls[i].collisionTime<ctime){
				ctime=balls[i].collisionTime;
				idx=i;
			}
		}
		return idx;
	}
	public void randomFill(int n){
		setBallsNum(n);
		for (int i=0;i<n;++i){
			balls[i]=new SimulatedBall(new Ball(Math.random()*10,Math.random()*10,Math.random()-0.5,Math.random()-0.5));
		}
	}
	public void getCurrentBallPos(int idx,Ball b){
		Ball sb=balls[idx].ball;
		b.x=sb.x+sb.vx*timeAfterCollision;
		b.y=sb.y+sb.vy*timeAfterCollision;
	}
	public double energy(){
		double s=0;
		for (int i = 0; i < balls.length; i++) {
			s+=balls[i].ball.vx*balls[i].ball.vx+
			   balls[i].ball.vy*balls[i].ball.vy;
		}
		return s;
	}
	/**Simulates balls until some condition
	 * 
	 * @param condition
	 * @return elapsed time
	 */
	public double simulateUntil(BilliardEvent condition,int maxIter){
		resetElapsedTime();
		timeAfterCollision=0;
		recalcCollisions();
		int iter=0;
		
		
		while ( !Double.isNaN(nearestCollision)  && iter<maxIter){
			//moving to next collision
			boolean rval=nextCollision(condition);
			iter++;
			
			if (rval)
				return getElapsedTime();
		}
		return getElapsedTime();
			
	}
	public void setBalls(double[] b){
		if (b.length % 4 !=0){
			throw new RuntimeException("Length of vector must be quotent of 4");
		}
		if (b.length/4!=balls.length)
			setBallsNum(b.length/4);
		
		for (int i=0;i<b.length;i+=4){
			balls[i/4].ball.set(b[i],b[i+1],b[i+2],b[i+3]); 
		}
	}
	public static void main(String[] args) {
		BallSimulator bs=new BallSimulator();
		bs.setWalls(0,0,10,10);
		bs.setBallsNum(5);
		
		Random r=new Random(1002);
		
		double[] b=new double[5*4];
		for(int i=0;i<b.length;i+=4){
			b[i]=r.nextDouble()*5+1;
			b[i+1]=r.nextDouble()*5+1;
			b[i+2]=r.nextDouble()-0.5;
			b[i+3]=r.nextDouble()-0.5;
		}
		
		bs.setBalls(b);
		
		
		BilliardEvent evt=new BilliardEvent(){
			public boolean onBallBounce(BallSimulator billiard, int i, int j) {
				return false;
			}
			public boolean onWallBounce(BallSimulator billiard, int ballIdx, int wallIdx) {
				if (ballIdx == 0 & wallIdx == 1) 
					return true;
				else
					return false;
			}
			public boolean onNoMoreCollisions(BallSimulator billiard) {
				System.out.println("no more collisions!");
				return true;
			}
		};
		
		for (int i=0;i<10;++i){
			bs.setBalls(b);
			bs.setBall(0, 3, 3, 0.4+i*0.0001, 0.4);
			
			System.out.println(bs.simulateUntil(evt,10000));
		}
			
	}
	public void setBall(int idx, double x, double y, double vx, double vy){
		balls[idx].ball.set(x, y, vx, vy);
	}
}

