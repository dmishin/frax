package ratson.genimageexplorer.extra;

import java.util.Random;

import ratson.genimageexplorer.extra.bounce.BallSimulator;
import ratson.genimageexplorer.extra.bounce.BilliardEvent;
import ratson.genimageexplorer.generators.Function;
import ratson.genimageexplorer.generators.FunctionFactory;

public class BilliardBallGenerator implements FunctionFactory {

	
	public BilliardBallGenerator(){
		ballNum=4;
		seed=0;
		exitCondition=new BilliardEvent(){
			public boolean onBallBounce(BallSimulator billiard, int i, int j) {
				return false;
			}
			public boolean onNoMoreCollisions(BallSimulator billiard) {
				return true;
			}
			public boolean onWallBounce(BallSimulator billiard, int ballIdx, int wallIdx) {
				return ballIdx==0 && wallIdx==1;
			}
		};

	}
	
	private int ballNum;
	private int seed;
	private int maxIter=100000;
	
		
	private BilliardEvent exitCondition; 
	private double[] initialBalls=null;	

	class Func extends Function{

		private BallSimulator simulator;
		Func(){
			simulator = new BallSimulator(); 
			simulator.setBallsNum(ballNum);
			simulator.setWalls(-1.1, -1.1, 11.1, 11.1);
		
			initialBalls=new double [ballNum*4];
			Random r=new Random(seed);
			
			for (int i=0;i<ballNum;++i){
				int p=i*4;
				initialBalls[p]=r.nextDouble()*10;
				initialBalls[p+1]=r.nextDouble()*10;
				initialBalls[p+2]=r.nextDouble()-0.5;
				initialBalls[p+3]=r.nextDouble()-0.5;
			}
			
			
		}
		@Override
		public float evaluate(double x, double y) {
			simulator.setBalls(initialBalls);
			
			simulator.setBall(0, 5, 5, x, y);
			return (float) simulator.simulateUntil(exitCondition, maxIter);
		}
	}
	


	public int getBallNum() {
		return ballNum;
	}
	public void setBallNum(int ballNum) {
		this.ballNum = ballNum;
	}
	public int getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}
	
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}
	



	public Function get() {
		return new Func();
	}






}

