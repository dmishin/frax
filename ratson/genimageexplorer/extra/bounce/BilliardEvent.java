package ratson.genimageexplorer.extra.bounce;

public abstract class BilliardEvent {
	/**Called when two balls bounce
	 * should return true to stop simulation or false to stopn it*/
	public abstract boolean onBallBounce(BallSimulator billiard, int i, int j);
	/**Called when ball and wall bounce
	 * should return true to stop simulation or false to stopn it*/
	public abstract boolean onWallBounce(BallSimulator billiard, int ballIdx, int wallIdx);
	public abstract boolean onNoMoreCollisions(BallSimulator billiard);
}
