package ratson.genimageexplorer.gui;

import javax.swing.SwingUtilities;

import ratson.genimageexplorer.RenderingChain;

/**class that performs asynchronous pattern rendering
 * When the thread is woken, it applies pattern, requests image update and sleeps again.
 * 
 * Make this thread a demon? Looks like a good idea
 * @author dim
 *
 */
public class PatternRendererThread implements Runnable{

	private Runnable uiUpdater;
	private RenderingChain renderer;
	
	public PatternRendererThread(RenderingChain renderer, Runnable onFinish){
		uiUpdater = onFinish;
		this.renderer = renderer;
	}
	public void run() {
		
		try{
		while (true){
			//infinite working loop
			synchronized (this) {
				this.wait();
			}
			//now perform rendering
			renderer.colorize();
			//request UI update
			SwingUtilities.invokeLater( uiUpdater);
		}
		}catch(InterruptedException e){
			
		}
		
	}

}
