package ratson.genimageexplorer.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ratson.genimageexplorer.ColorPattern;
import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.SmoothColorPattern;
import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.genimageexplorer.generators.MandelBarGenerator;
import ratson.genimageexplorer.generators.RendererException;

public class HQRenderGUI extends JFrame {

	
	AbstractGenerator generator;
	ColorPattern pattern;
	
	ImagePane rendered;
	JProgressBar progress;
	JTextField fldWidth, fldHeight;
	private JComboBox cbAA;
	private JButton btnRender;
	private JButton btnSave;
	private ObservationArea area;
	private BufferedImage image;
	protected boolean isRendering;
	private Integer imageWidth;
	private Integer imageHeight;
	private AAOption aaOption;
	private JButton btnClose;
	private JButton btnStop;
	private Box box_progress;
	public HQRenderGUI( AbstractGenerator generator, ColorPattern pattern, ObservationArea area){
		super("HQ render");
		this.generator = generator;
		this.pattern = pattern;
		this.area = area;
		
		this.setSize(800,600);
		initControls();
		setActions();
	}
	
	
	private void setActions() {
		btnRender.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doRender();
			}});
		
		btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doSave();
			}});
		btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}});
		btnStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				generator.stopRendering();
			}});
	}


	protected void doSave() {
		JFileChooser fc=new JFileChooser();
		
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		String fname=fc.getSelectedFile().getName();
		
		
		try {
		    // Save as PNG
			File file = fc.getSelectedFile();
			System.out.println("Saving file in PNG format");
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to save image "+fname+"\n"+
				e.getLocalizedMessage());
		}

	}


	protected void doRender() {
		if (!readParameters())
			return;
		if (isRendering){
			JOptionPane.showMessageDialog(this, "Already rendering");
			return;
		}
		
		if (image == null ||
				imageWidth != image.getWidth() ||
				imageHeight != image.getHeight()){
			setResolution(imageWidth, imageHeight);
		}
		
		final Runnable onFinish = new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				isRendering = false;
				progress.setValue(100);
				box_progress.setVisible(false);
			}
		};
		final Runnable onProgress = new Runnable(){
			public void run() {
				progress.setValue(generator.getProgress());
				rendered.repaint();
			}
		};
		
		box_progress.setVisible(true);
		progress.setValue(0);
		isRendering = true;
		Thread hqRendererThread = new Thread(new Runnable(){
			public void run() {
				try {
					generator.renderHQ(area, image, pattern, aaOption.aa, onFinish, onProgress);
				} catch (RendererException e) {
					isRendering = false;
					JOptionPane.showMessageDialog(HQRenderGUI.this, String.format("Renderer reported error:\n%s", e.getMessage()));
				}
			}
			
		}, "HQ renderer");
		hqRendererThread.start();
			
	}


	private boolean readParameters() {
		try{
			imageWidth = Integer.valueOf(fldWidth.getText());
			imageHeight = Integer.valueOf(fldHeight.getText());
			aaOption = (AAOption) cbAA.getSelectedItem();
			
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Wrong number format:\n"+e.getMessage());
			return false;
		}catch (Exception e){
			JOptionPane.showMessageDialog(this, "Exception reading data:\n"+e.getMessage());
			return false;			
		}
		return true;
	}


	private void setResolution(int w, int h){
		image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		rendered.setImage(image);
	}
	
	private void initControls() {
		Container c = getContentPane();	
	
		Dimension btnMaxDim = new Dimension(80,35); 
		
		
		fldWidth = new JTextField("400");
		fldWidth.setMaximumSize(btnMaxDim);
		
		fldHeight = new JTextField("400");
		fldHeight.setMaximumSize(btnMaxDim);
		
		cbAA = new JComboBox(aaModes);
		cbAA.setMaximumSize(btnMaxDim);
	
		btnRender = new JButton("Render");
		btnRender.setMaximumSize(btnMaxDim);
		btnRender.setMnemonic('r');
		
		btnSave = new JButton("Save");
		btnSave.setMaximumSize(btnMaxDim);
		btnSave.setMnemonic('s');
		
		btnClose = new JButton("Close");
		btnClose.setMaximumSize(btnMaxDim);

		btnStop = new JButton("Stop");
		btnStop.setMaximumSize(btnMaxDim);

		
		progress = new JProgressBar(0,100);
		rendered = new ImagePane();
		
		
		Box params_box = Box.createHorizontalBox();
		params_box.add(new JLabel("Width:"));
		params_box.add(fldWidth);
		params_box.add(new JLabel("Height:"));
		params_box.add(fldHeight);
		
		params_box.add(new JLabel("Anti-alias:"));
		params_box.add(cbAA);

		Box button_box = Box.createHorizontalBox();
		button_box.add(btnSave);
		button_box.add(btnClose);
		button_box.add(btnRender);
		
		
		Box main_box = Box.createVerticalBox();
		
		main_box.add(params_box);
		main_box.add(new JScrollPane(rendered));
		
		box_progress = Box.createHorizontalBox();
		box_progress.add(btnStop);
		box_progress.add(progress);
		
		main_box.add(box_progress);
		box_progress.setVisible(false);
		
		main_box.add(button_box);
		
		c.add(main_box);
	}

	class AAOption{
		public String text;
		public int aa;
		public AAOption(int aaValue, String txt ){
			this.aa=aaValue;
			this.text = txt;
		}
		@Override
		public String toString() {
			if (text != null)
				return text;
			else
				return String.format("%d x %d", aa,aa);
		}
	}
	private Object[] aaModes = new Object[]{
		new AAOption(1,"None"),
		new AAOption(2,null),
		new AAOption(3,null),
		new AAOption(4,null),
		new AAOption(5,null),
		new AAOption(8,null),
	};
	public static void main(String[] args) {
		AbstractGenerator generator = new MandelBarGenerator();
		ColorPattern pattern = new SmoothColorPattern();
		ObservationArea area = new ObservationArea(-1,-1,1,1);
		
		HQRenderGUI frame = new HQRenderGUI(generator, pattern,area);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(800,600);
		frame.setVisible(true);
	}
}
