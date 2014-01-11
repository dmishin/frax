package ratson.genimageexplorer;

import java.io.Serializable;
import java.util.Enumeration;

import nanoxml.XMLElement;
import ratson.utils.DoubleMatrix;

@SuppressWarnings("serial")
public class ObservationArea implements Serializable{
	private int imgWidth;
	private int imgHeight;
	
	private DoubleMatrix transform=new DoubleMatrix(3,3);
	private DoubleMatrix xreal=new DoubleMatrix(3,1);
	private DoubleMatrix xscreen=new DoubleMatrix(3,1);
	private double kx;
	private double ky;
	
	protected void screenToReal(double x,double y){
		xscreen.set(0,0,x);
		xscreen.set(1,0,y);
		xscreen.set(2,0,1);
		
		xreal.mult(transform, xscreen);
		double k=xreal.get(0, 2);
		xreal.scale(1.0/k);
	}
	
	
	
	public void addTransform( DoubleMatrix tr){
		DoubleMatrix t1=new DoubleMatrix(3,3);
		t1.mult(tr, transform);
		transform.set(t1);
	}
	public void addTransformR( DoubleMatrix tr){
		DoubleMatrix t1=new DoubleMatrix(3,3);
		t1.mult(transform, tr);
		transform.set(t1);
	}
	
	private DoubleMatrix offsetMatrix(double dx, double dy){
		DoubleMatrix m=new DoubleMatrix();
		m.eye(3);
		m.set(0,2,dx);
		m.set(1,2,dy);
		return m;
	}
	private DoubleMatrix scaleMatrix(double sx, double sy){
		DoubleMatrix m=new DoubleMatrix();
		m.zeros(3);
		m.set(0,0,sx);
		m.set(1,1,sy);
		m.set(2,2,1.0);
		return m;
	}
	
	
	public ObservationArea(double x0,double y0,double x1, double y1){
		set(x0,y0,x1,y1);
	}
	public ObservationArea(){
		set(-2,-2,2,2);
	}
	public ObservationArea(ObservationArea p){
		setResolution(p.imgWidth, p.imgHeight);
		
		transform.set(p.transform);
	}
	
	public void set(double x0, double y0, double x1, double y1){
		transform.eye(3);
		addTransform(scaleMatrix(x1-x0, y1-y0));
		addTransform(offsetMatrix(x0, y0));
	}
	
	
	/**Sets resolution (in pixels) for image.
	 * When resulution is set, Scr2Abs fucntion may be used for 
	 * transforming coordinates from integer screen coordinates to absolute coordinates
	 * @param width
	 * @param height
	 */
	public void setResolution(int width, int height) {
		imgWidth=width;
		imgHeight=height;
		if (imgWidth!=0 && imgHeight!=0){
			kx=1.0/imgWidth;
			ky=1.0/imgHeight;
		}else{
			kx=0;
			ky=0;
		}
	}
	
	/**converts screen coordinates to absolute coordinates
	 * 
	 * @param screenX
	 * @return
	 */
	public void scr2abs(int screenX, int screenY, double[] absXY){
		screenToReal(screenX*kx, screenY*ky);
		absXY[0]=xreal.get(0,0);
		absXY[1]=xreal.get(1,0);
	}



	/**returns copy of transformation matrix
	 * 
	 * @return
	 */
	public DoubleMatrix getMatrix() {
		return transform.copy();
	}



	public void unZoomToBox(int x, int y, int x1, int y1) {
		double X0=kx*x,X1=kx*x1,Y0=ky*y,Y1=ky*y1;
		
		//modify transformation matrix
		
		//create additional transform matrix
		DoubleMatrix offs=offsetMatrix(X0, Y0);
		DoubleMatrix t=new DoubleMatrix(3,3);
		t.mult(offs, scaleMatrix(X1-X0, Y1-Y0));
		
		t=t.inv();
		//prepend it to the current transform matrix
		
		//was : Transf * X
		//need: Transf * T * X
		offs.mult(transform	, t);
		transform.set(offs);
	}



	public void zoomToBox(int x, int y, int x1, int y1) {
		
		double X0=kx*x,X1=kx*x1,Y0=ky*y,Y1=ky*y1;
		
		//modify transformation matrix
		
		//create additional transform matrix
		DoubleMatrix offs=offsetMatrix(X0, Y0);
		DoubleMatrix t=new DoubleMatrix(3,3);
		t.mult(offs, scaleMatrix(X1-X0, Y1-Y0));
		
		//prepend it to the current transform matrix
		
		//was : Transf * X
		//need: Transf * T * X
		offs.mult(transform	, t);
		transform.set(offs);
		
	}



	/**rotate location around center*/
	public void rotate(double angle) {
		//Was: T*x
		//new: Translate(center) * Rot(alpha) * Translate(-center) * T *x
		
		DoubleMatrix translate = offsetMatrix(-0.5, -0.5);
		DoubleMatrix rotate = rotationMatrix(angle);
		DoubleMatrix itranslate = offsetMatrix(0.5, 0.5);
		
		addTransformR(itranslate);
		addTransformR(rotate);
		addTransformR(translate);
	}



	private DoubleMatrix rotationMatrix(double angle) {
		double sn, cs;
		sn = Math.sin(angle);
		cs = Math.cos(angle);

		DoubleMatrix matrix = new DoubleMatrix();
		matrix.eye(3);
		matrix.set(0, 0, cs);
		matrix.set(0, 1, -sn);
		matrix.set(1, 0,  sn);
		matrix.set(1, 1,  cs);
		
		return matrix;
	}



	/**shifts current location by specified value in screen coordinates*/
	public void offset(int dx, int dy) {
		double dxr = -dx*kx;
		double dyr = -dy*ky;

		DoubleMatrix tr = offsetMatrix(dxr, dyr);
		addTransformR(tr);
	}
	
	/**exports contents of observation point to XML*/
	public XMLElement exportXML(){

		XMLElement root = new XMLElement();
		root.setName("location");
		XMLElement mtx = new XMLElement();
		mtx.setName("matrix");
		root.addChild(mtx);
		for (int i=0;i<3;++i){
			XMLElement row = new XMLElement();
			mtx.addChild(row);
			row.setName("row");
			for (int j =0; j<3;++j){
				XMLElement col = new XMLElement();
				row.addChild(col);
				col.setName("col");
				String content = String.valueOf(transform.get(i, j));
				col.setContent(content);
			}
		}
		return root;
	}
	public void importXML(XMLElement xml) throws XMLFormatException{
		if (!xml.getName().equals("location"))
			throw new XMLFormatException("Root node nust be <location>");
		Enumeration ch = xml.enumerateChildren();
		while (ch.hasMoreElements()){
			XMLElement child = (XMLElement)ch.nextElement();
			if (child.getName().equals("matrix")){
				importXMLMatrix(child);
				return;
			}
		}
		throw new XMLFormatException("In the <location> tag, no recognizable tags were found");
	}



	private void importXMLMatrix(XMLElement child) throws XMLFormatException {
		if (child.countChildren() != 3)
			throw new XMLFormatException("Matrix must have 3 rows");
		int i = 0;
		Enumeration rows = child.enumerateChildren();
		
		while (rows.hasMoreElements()){
			XMLElement row = (XMLElement)rows.nextElement();
			if (row.countChildren()!=3)
				throw new XMLFormatException("Row must have 3 columns");
			int j = 0;
			Enumeration els = row.enumerateChildren();
			while (els.hasMoreElements()){
				XMLElement elt = (XMLElement)els.nextElement();
				double val;
				try{
					val = Double.parseDouble(elt.getContent());					
				}catch(NumberFormatException e){
					throw new XMLFormatException("NUmber format is wrong:\n"+e.getMessage());
				}
				transform.set(i, j, val);
				++j;
			}
			++i;
		}
	}
}

