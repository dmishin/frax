package ratson.genimageexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import nanoxml.XMLElement;
import ratson.genimageexplorer.ColorPicker;
import ratson.genimageexplorer.EditablePattern;
import ratson.genimageexplorer.PatternFormatException;
import ratson.utils.Utils;

public class PatternEditorControl extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public static final int MAX_K = 65536;
	private static final int PICKER_WIDTH = 6;
	private static final int PICKER_ARROW_HEIGHT = 7;
	private static final int PICKER_BOX_HEIGHT = 20;
	private static Polygon contour=new Polygon(
			new int[]{0,PICKER_WIDTH,PICKER_WIDTH,-PICKER_WIDTH,-PICKER_WIDTH},
			new int[]{0,PICKER_ARROW_HEIGHT,PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT,PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT,PICKER_ARROW_HEIGHT},
			5
			);

	
	public static void main(String[] args) {
		JFrame frm=new JFrame("Test pattern");
		frm.setSize(400, 300);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PatternEditorControl ctrl=new PatternEditorControl();
		
		ctrl.setRandomPickers(6);
		
		frm.getContentPane().add(ctrl,BorderLayout.CENTER);
		frm.setVisible(true);
	}

	BufferedImage previewImage=new BufferedImage(EditablePattern.MAX_POS,1,BufferedImage.TYPE_INT_RGB);

	private EditablePattern pattern = new EditablePattern(); 
	private int draggingPicker=-1;
	private Point dragOrigin;
	private int dragStartPos=-1;
	private boolean pickerDeleteFlag;//True, if picker will be deleted on mouseup

	public PatternEditorControl() {
		addHandlers();
	}

	private void addHandlers() {
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent e) {
				doPickerDragging(e.getPoint());
			}
			public void mouseMoved(MouseEvent e) {
			}
		});
		addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2){//if doubleclick
					Point p=e.getPoint();
					if (p.y>=10 && p.y<=30+PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT){
						int pos=x2pos(p.x);
						if (pos>=0 && pos<EditablePattern.MAX_POS)
							insertPicker(pos, new Color(pattern.getColorAt(pos)));//inserting picker of neutral color
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1){
					beginPickerDragging(e.getPoint());
				}
				if (e.getButton()==MouseEvent.BUTTON3){
					setPickerColor(getPickerByPoint(e.getPoint()));
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1){
					endPickerDragging(e.getPoint());
				}				
			}
			
		});
	}

	protected void beginPickerDragging(Point pnt) {
		int idx=getPickerByPoint(pnt);
		if (idx<0 || idx>=getPickersCount())
			return;
		if (draggingPicker!=-1)//already dragging something.. this should not be
			return;
		
		draggingPicker=idx;
		dragOrigin=pnt;
		dragStartPos=pattern.picker(idx).position;//original position of picker before dragging
	}

	/**Creates editable pattern, corresponding currently edited data
	 * 
	 * @return
	 */ 
	public void getPattern(EditablePattern pattern1){
		pattern1.setPattern(pattern);
	}

	protected void doPickerDragging(Point point) {
		if (draggingPicker==-1)
			return;
		int dx=point.x-dragOrigin.x;//offset
		int newPos=x2pos(pos2x(dragStartPos)+dx);//new position
		
		newPos=Utils.mod(newPos, EditablePattern.MAX_POS);
		pattern.picker(draggingPicker).position=newPos;
		
		draggingPicker=pattern.normalize(draggingPicker);

		pickerDeleteFlag=(pattern.getNumColors()>2) && 
			!(point.y>10 && point.y<30+PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT*2);
		pattern.updateColorTable();
		updateColorBar();
		repaint();
	}

	private void drawPicker(ColorPicker p, Graphics g){
		
		//determine horizontal position of picker
		int xc=pos2x(p.position);
		int yc=30+1;
		
		g.translate(xc, yc);
		
		g.setColor(p.color);
		g.fillPolygon(contour);
		g.setColor(Color.black);
		g.drawPolygon(contour);
		
		g.translate(-xc, -yc);

	}

	protected void endPickerDragging(Point point) {

		if (pickerDeleteFlag){
			removePicker(draggingPicker);
			updateColorBar();
			repaint();
			pickerDeleteFlag=false;
		}

		
		draggingPicker=-1;
		dragStartPos=-1;
		dragOrigin=null;
		
	}


	protected int getPickerByPoint(Point point) {
		if (point.y<10 || point.y>30+PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT)
			return -1;
		for (int i = 0; i < getPickersCount(); i++) {
			if (Math.abs(point.x-pos2x(pattern.picker(i).position))<=PICKER_WIDTH){
				return i;
			}
		}
		return -1;
	}
	
	private int getPickersCount(){
		return pattern.getNumColors();
	}
	/**Inserts new picker and returns it's index*/
	public int insertPicker(int position, Color clr){
		int rval =pattern.addColor(new ColorPicker(position,clr));//adding picker to the end of list
		updateColorBar();
		repaint();
		return rval;
	}
	

	private void overstrikePicker(ColorPicker picker, Graphics g) {
		int x=pos2x(picker.position);
		g.setColor(Color.red);
		g.translate(x, 30+PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT/2);
		g.drawLine(-PICKER_WIDTH/2, -PICKER_WIDTH/2, PICKER_WIDTH/2, PICKER_WIDTH/2);
		g.drawLine(PICKER_WIDTH/2, -PICKER_WIDTH/2, -PICKER_WIDTH/2, PICKER_WIDTH/2);
		g.translate(-x, -(30+PICKER_ARROW_HEIGHT+PICKER_BOX_HEIGHT/2));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(previewImage, 10, 10, getWidth()-10, 30, 0, 0, EditablePattern.MAX_POS, 1, this);
		Graphics2D g2=(Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < getPickersCount(); i++) {
			drawPicker(pattern.picker(i), g);
			if (pickerDeleteFlag && i==draggingPicker){
				//if currently drawn picker must be deleted, overstriking it
				overstrikePicker(pattern.picker(i),g);
			}
		}
	}
	

	private int pos2x(int position) {
		return position*(getWidth()-10*2)/EditablePattern.MAX_POS+10;
	}
	public void removePicker(int idx){
		if (pattern.getNumColors()<=2)
			return;//too few pickers. can not remove.
		pattern.remove(idx);
		updateColorBar();
	}
	
	protected void setPickerColor(int pickerIndex) {
		if (pickerIndex<0 || pickerIndex>=getPickersCount())
			return;
		Color newClr=JColorChooser.showDialog(this, "Select color", pattern.picker(pickerIndex).color);
		if (newClr!=null){
			pattern.setColor(pickerIndex,newClr);
			updateColorBar();
			repaint();
		}
	}
	public void setRandomPickers(int n){
		pattern.randomize(n);
		updateColorBar();
	}
	/*
	*/
	private void updateColorBar(){
		previewImage.setRGB(0, 0, EditablePattern.MAX_POS, 1, pattern.getColorTable(), 0, EditablePattern.MAX_POS);
	}
	
	private int x2pos(int x) {
		int bw= getWidth()-10*2;
		if (bw<=0) return 0;
		return (x-10)*EditablePattern.MAX_POS/bw;
	}

	public void reloadPattern(EditablePattern pattern1) {
		pattern.setPattern(pattern1);
		updateColorBar();
		repaint();
	}

	public void loadXML(XMLElement data) throws PatternFormatException {
		pattern.loadXML(data);
		updateColorBar();
		repaint();
	}

	public EditablePattern getPattern() {
		return pattern;
	}

	public void makeEqualSpacing() {
		pattern.makeEqualSpacing();
		updateColorBar();
		repaint();
	}

	public void randomizePattern() {
		pattern.randomize(pattern.getNumColors());
		updateColorBar();
		repaint();
	}
	
}
