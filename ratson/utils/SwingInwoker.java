package ratson.utils;

import javax.swing.SwingUtilities;

/**Wrapper class for Runnable objects, that inwokes given object in a Swing main thread.
 * @author dim
 */
public class SwingInwoker implements Runnable {
	private Runnable runnable;
	public SwingInwoker(Runnable runMe){
		runnable = runMe;
	}
	public void run() {
		SwingUtilities.invokeLater(runnable);
	}
	Runnable GetInvokeTarget(){
		return runnable;
	}
}
