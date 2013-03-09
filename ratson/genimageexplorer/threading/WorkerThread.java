package ratson.genimageexplorer.threading;

public abstract class WorkerThread implements Runnable{
	
	private int progress;
	private boolean stopRequested;
	private String name;
	private Thread thread;
	public WorkerThread(){
		progress = 0;
		stopRequested = false;
		name = "unnamed";
	}
	protected void setProgress(int p){
		progress = p;
	}
	public int getProgress(){
		return progress;
	}
	public void requestStop(){
		stopRequested = true;
	}
	public boolean isStopRequested() {
		return stopRequested;
	}
	public abstract void run();
	public String getName() {
		return name;
	}
	public void setThreadRef(Thread thrd) {
		thread = thrd;
	}
}
