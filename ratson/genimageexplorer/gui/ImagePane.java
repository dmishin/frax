package ratson.genimageexplorer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

import ratson.genimageexplorer.gui.mousetools.AbstractMouseTool;

public class ImagePane extends JPanel {
	private Image img=null;
	private int imgH=-1;
	private int imgW=-1;
	
	private double zoom = 1.0;
	

	private AbstractMouseTool mouseTool = null;

	public void setMouseTool(AbstractMouseTool tool){
		mouseTool = tool;
		if (mouseTool!=null){
			mouseTool.setComponentSize(imgH, imgW);
		}
		repaint();
	}
	
	public ImagePane(BufferedImage displayedImage) {
		
		setImage(displayedImage);
		setHandlers();
	}

	public ImagePane() {
		setHandlers();
	}

	public void setImage(Image i){
		img=i;
		
		if (i==null){
			imgW=-1;
			imgH=-1;
		}else{
			//get image dimensions
			imgH=i.getHeight(this);
			imgW=i.getWidth(this);
		}
		updateComponentSize();
		if (mouseTool!=null){
			mouseTool.setComponentSize(imgH, imgW);
		}
		if (isDisplayable()){
			repaint();
		}
	}
	
	private void updateComponentSize() {
		if (imgW!=-1 && imgH!=-1){
			Dimension d=new Dimension((int)(imgW*zoom),(int)(imgH*zoom));			
			setMinimumSize(d);
			setPreferredSize(d);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.scale(zoom, zoom);
		if (imgW>-1 && imgH>-1){
			//set zoom
			synchronized(img){
				g.drawImage(img, 0, 0, imgW, imgH, 0, 0, imgW, imgH, this);
			}
			
		}
		if (mouseTool != null){
			mouseTool.drawTool(g);
		}
	}
	public void setZoom(double zoom){
		this.zoom = zoom;
		updateComponentSize();
		if (isDisplayable()){
			repaint();
		}
	}
	public double getZoom() {
		return zoom;
	}
	private Point screen2local(Point screen){
		return new Point((int)(screen.x/zoom), (int)(screen.y/zoom));
	}
	private void setHandlers(){
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if (mouseTool!=null){
					mouseTool.setMouseButton(e.getButton());
					mouseTool.startDragging(screen2local(e.getPoint()));
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (mouseTool!=null){
					//hideMouseTool();
					e.getButton();
					mouseTool.setMouseButton(e.getButton());
					mouseTool.endDragging(screen2local(e.getPoint()));
					repaint();
				}
			}});
		
		addMouseMotionListener(new MouseMotionListener(){

			
			public void mouseDragged(MouseEvent e) {
				if (mouseTool!=null){
					mouseTool.setMouseButton(e.getButton());
					mouseTool.mouseDragged(screen2local(e.getPoint()));
					repaint();
				}
			}

			public void mouseMoved(MouseEvent e) {
				if (mouseTool!=null){
					mouseTool.setMouseButton(0);
					mouseTool.mouseMoved(screen2local(e.getPoint()));
				}
			}});
	}


	protected void hideMouseTool() {
		Shape hideShape = mouseTool.getClip();
		Graphics g = getGraphics();
		g.setClip(hideShape);
		paintComponent(g);
		g.setClip(null);
	}

	
}
