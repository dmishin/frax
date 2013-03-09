package ratson.genimageexplorer.extra.bounce;

public class SimulatedBall {
	public Ball ball;
	public int collisionTarget;
	
	public double collisionTime;
	
	public SimulatedBall(Ball ball2) {
		this.ball=ball2;
		collisionTarget=-1;
		collisionTime=Double.NaN;
	}

	public void move (double dt){
		if (!Double.isNaN(collisionTime))
			collisionTime-=dt;
		ball.move(dt);
	}

	public void updateCollisionTime(double ct, int target) {
		if (Double.isNaN(ct))
			return;
		if (Double.isNaN(collisionTime) || ct<collisionTime){
			collisionTime=ct;
			collisionTarget=target;
		}
		
	}

	public void bounce(SimulatedBall b2) {
		ball.bounce(b2.ball);
		collisionTime=Double.NaN;
		b2.collisionTime=Double.NaN;
	}
	public void bounce(Wall w) {
		ball.bounce(w);
		collisionTime=Double.NaN;
		
	}
	
	
}
