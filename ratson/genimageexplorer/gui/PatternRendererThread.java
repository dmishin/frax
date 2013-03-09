package ratson.genimageexplorer.gui;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import ratson.genimageexplorer.AbstractRendererChain;
import ratson.genimageexplorer.ColorPattern;
import ratson.utils.FloatMatrix;

/**class that performs asynchronous pattern rendering
 * When the thread is woken, it applies pattern, requests image update and sleeps again.
 * 
 * Make this thread a demon? Looks like a good idea
 * @author dim
 *
 */
public class PatternRendererThread implements Runnable{

	private Runnable uiUpdater;
	private AbstractRendererChain renderer;
	
	public PatternRendererThread(AbstractRendererChain renderer, Runnable onFinish){
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
