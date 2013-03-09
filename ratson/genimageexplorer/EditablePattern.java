package ratson.genimageexplorer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import nanoxml.XMLElement;

import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.genimageexplorer.gui.PatternEditorControl;
import ratson.genimageexplorer.gui.dialogs.PatternEditor;
import ratson.utils.FloatMatrix;
import ratson.utils.Utils;

public class EditablePattern extends ColorPattern {

	
	
	public ColorPicker picker(int index){
		return (ColorPicker)pickers.get(index);
	}

	public static final int MAX_K = 65536;
	
	private ArrayList pickers = new ArrayList();;
	private int[] colorTable = new int[MAX_POS];

	public static final int MAX_POS = 1024;

	public EditablePattern() {
		setNumColors(7);
		Color[] colors={Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta};
		for (int i=0; i<colors.length;++i)
			setItem(i, (MAX_POS*i)/colors.length, colors[i]);
	}
	
	private float offset = 0.0f;
	private float cycleSpeed = 0.1f;

	public double getCycleSpeed() {
		return cycleSpeed;
	}
	public void setCycleSpeed(double cycleSpeed) {
		this.cycleSpeed = (float) cycleSpeed;
		System.out.println(cycleSpeed);
	}
	public double getOffset() {
		return offset;
	}
	public void setOffset(double offset) {
		this.offset = (float) offset;
	}
	
	protected void renderPoints(float[] src, int position, int[] rgb) {

		int p=position;
		for (int i = 0; i < rgb.length; i++, p++) {
			if (src[p]<0)
				rgb[i]=0;
			else{
				int idx=(int) (Utils.mod(src[p]*cycleSpeed+offset,0.999999)*(colorTable.length));
				rgb[i]=colorTable[idx];
			}
		}

	}

	protected void beforeRender(FloatMatrix raw, BufferedImage rendered) {
		updateColorTable();
	}

	protected void endRender(FloatMatrix raw, BufferedImage rendered) {
	}

	public void setNumColors(int num) {
		pickers.clear();
		pickers.ensureCapacity(num);
		
		for(int i=0;i<num;++i)
			pickers.add(new ColorPicker((i*EditablePattern.MAX_POS)/num,Color.black));
	}

	public void setItem(int index, int pos, Color color) {
		if (index<0 || index >= getNumColors())
			throw new ArrayIndexOutOfBoundsException(index);
		picker(index).color=color;
		picker(index).position = pos;
	}

	public int getNumColors() {
		if (pickers==null)
			return 0;
		return pickers.size();
	}

	public void normalize(){
		//sorting array
		Collections.sort(pickers, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				ColorPicker p1=(ColorPicker)arg0, p2=(ColorPicker)arg1;
				if (p1.position<p2.position)
					return -1;
				if (p1.position>p2.position)
					return 1;
				return 0;
			}
		});
	}	
	public String exportXMLString(){
		return exportXML().toString();
	}
	public void makeEqualSpacing(){
		normalize();
		for(int i =0 ;i<getNumColors();++i){
			picker(i).position = (i*EditablePattern.MAX_POS)/getNumColors();
		}
		updateColorTable();
	}
	public void doEdit(){
		PatternEditor editor = new PatternEditor(null, this);
		editor.setVisible(true);

	}
	public void loadXML(XMLElement root) throws PatternFormatException{
		if (!root.getName().equals("pattern"))
			throw new PatternFormatException(root.toString());
		Enumeration children = root.enumerateChildren();
		
		pickers.clear();
		
		while (children.hasMoreElements()){
			XMLElement child = (XMLElement)children.nextElement();
			if (child.getName().equals("color")){
				String pos = child.getStringAttribute("pos");
				String val = child.getStringAttribute("value");
				if (pos == null || val==null)
					throw new PatternFormatException(child.toString());

				Color clr;
				int iPos = -1;
				int rgb;
				try{
					iPos = Integer.parseInt(pos);
					rgb = Integer.parseInt(val,16);
				}catch(NumberFormatException e){
					throw new PatternFormatException("Bad color format:"+e.getMessage());
				}
				clr = new Color(rgb,false);
				pickers.add(new ColorPicker(iPos, clr));				
			}
		}
		if (pickers.size()<=1)
			throw new PatternFormatException("Pattern contains less than 2 colors.");
		pickers.trimToSize();
		normalize();
		
		updateColorTable();
		
	}
	public XMLElement exportXML(){
		XMLElement root=new XMLElement();
		root.setName("pattern");
		for (int i =0; i<getNumColors();++i){
			XMLElement clr = new XMLElement();
			clr.setName("color");
			clr.setAttribute("pos", String.valueOf(getPos(i)));
			String color= String.format("%06x", new Object[]{
					new Integer(getColor(i).getRGB() & 0xFFFFFF)
					});
			clr.setAttribute("value", color);
			root.addChild(clr);
		}
		return root;
	}

	public Color getColor(int i) {
		return picker(i).color;
	}

	public int getPos(int i) {
		return picker(i).position;
	}

	/**merges values of two bytes
	 * 
	 * @param c1 byte value 0..255
	 * @param c2  --//--
	 * @param k integer in range 0..MAX_K, describing percent of c1 and c2 values. MAX_K=65536
	 */
	public static int merge(int c1, int c2, int k){
		return (c1*k+c2*(MAX_K - k))/MAX_K;
	}

	/**merges two colors in 24-bit RGB format
	 * 
	 * @param c1
	 * @param c2
	 * @param k
	 * @return
	 */
	public static int merge_rgb(int c1, int c2, int k){
		int r=merge(c1 & 0xFF, c2 & 0xFF, k);
		c1=c1>>8;
		c2=c2>>8;
		int g=merge(c1 & 0xFF, c2 & 0xFF, k);
		c1=c1>>8;
		c2=c2>>8;
		int b=merge(c1 & 0xFF, c2 & 0xFF, k);
		return r | (g<<8) | (b<<16);
	}
	public void updateColorTable(){
		if (getNumColors()<1)
			return;
		for (int i=0;i<getNumColors()-1;++i){
			//i is index of picker
			int p0=picker(i).position;
			int p1=picker(i+1).position;
			int dp=p1-p0;
			for (int x=p0;x<p1;++x){
				int k=((x-p0)*MAX_K)/dp;
				
				int clr=merge_rgb(picker(i+1).color.getRGB(),picker(i).color.getRGB(),k);
				
				colorTable[x]=clr;
			}
		}
		int p0=picker(pickers.size()-1).position;
		int p1=picker(0).position+EditablePattern.MAX_POS;
		int dp=p1-p0;
		for (int x=p0;x<p1;++x){
			int k=((x-p0)*MAX_K)/dp;
			
			int clr=merge_rgb(
					picker(0).color.getRGB(),
					picker(getNumColors()-1).color.getRGB(),
					k);
			
			colorTable[x%EditablePattern.MAX_POS]=clr;
		}

	}

	public int getColorAt(int pos) {
		return colorTable[Utils.mod(pos,EditablePattern.MAX_POS)];
	}

	public void setPattern(EditablePattern pattern) {
		setNumColors(pattern.getNumColors());
		for (int i =0 ;i<getNumColors(); ++i){
			setItem(i, pattern.getPos(i), pattern.getColor(i));
		}
		updateColorTable();
	}

	public void remove(int idx) {
		if (getNumColors()>2)
			pickers.remove(idx);
		updateColorTable();
	}

	public void setColor(int pickerIndex, Color newClr) {
		picker(pickerIndex).color = newClr;
		updateColorTable();
	}

	public void randomize(int n) {
		pickers.clear();
		for (int i=0;i<n;++i){
				pickers.add (new ColorPicker(
					(i*EditablePattern.MAX_POS+EditablePattern.MAX_POS/2)/(n),
					new Color((float)Math.random(),(float)Math.random(),(float)Math.random())
					)
				);
		}
		updateColorTable();
	}

	public int[] getColorTable() {
		return colorTable;
	}

	
	
	public int addColor(ColorPicker picker) {
		pickers.add(picker);
		return normalize(getNumColors()-1);//and updating it's position
	}

	/**Takes the picker with index idx, repositions it so that pickers become sorted and returns it's new index
	 * 
	 * @param idx index of picker, needed to be repositionsed
	 * @return new index
	 */ 
	public int normalize(int idx) {
		while (idx>0 && picker(idx).position<picker(idx-1).position){
			swapPickers(idx,idx-1);
			idx--;
		}
		while (idx<getNumColors()-1 && picker(idx).position>picker(idx+1).position){
			swapPickers(idx,idx+1);
			idx++;
		}
		return idx;
	}
	private void swapPickers(int i, int j) {
		Object buf=pickers.get(i);
		pickers.set(i, pickers.get(j));
		pickers.set(j,buf);
	}
	@Override
	public int renderPoint(float v) {
		if (v == AbstractGenerator.BLACK_VALUE)
			return 0;
		else{
			int idx=(int) (Utils.mod(v*cycleSpeed+offset,0.999999)*(colorTable.length));
			return colorTable[idx];
		}
	}


}
