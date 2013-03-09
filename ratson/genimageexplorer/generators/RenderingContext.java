package ratson.genimageexplorer.generators;

/**stores per-thread information, used for image rendering.
 *  also stores information about progress
 *  Subclass it to use additional data
 * @author dim
 *
 */
public class RenderingContext {

	private int progress=0;
	private Object userData=null;
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	public Object getUserData() {
		return userData;
	}
	
	public RenderingContext(){	
	}
	public RenderingContext(Object userData){
		setUserData(userData);
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getProgress() {
		return progress;
	}
	
}
