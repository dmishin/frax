package ratson.genimageexplorer.threading;

import java.util.Iterator;
import java.util.LinkedList;

public class WorkManager {
	private LinkedList< WorkerThread > workers;
	
	public void addWorker(WorkerThread worker){
		workers.add(worker);
	}
	
	public void run(){
		for (Iterator<WorkerThread> iWorker = workers.iterator(); iWorker.hasNext();) {
			WorkerThread worker = iWorker.next();
			
			Thread thrd =new Thread(worker,worker.getName()); 
			worker.setThreadRef(thrd);
		}
	}
}
