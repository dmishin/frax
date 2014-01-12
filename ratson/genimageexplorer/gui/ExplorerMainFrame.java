package ratson.genimageexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import ratson.genimageexplorer.ColorPattern;
import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.RenderingChain;
import ratson.genimageexplorer.XMLFormatException;
import ratson.genimageexplorer.generators.FunctionFactory;
import ratson.genimageexplorer.generators.Renderer;
import ratson.genimageexplorer.generators.RendererException;
import ratson.genimageexplorer.gui.dialogs.ResolutionSetterDlg;
import ratson.genimageexplorer.gui.mousetools.AbstractMouseTool;
import ratson.genimageexplorer.gui.mousetools.PanMouseTool;
import ratson.genimageexplorer.gui.mousetools.ProportionalZoomMouseTool;
import ratson.genimageexplorer.gui.mousetools.RotateMouseTool;
import ratson.genimageexplorer.gui.mousetools.ZoomMouseTool;
import ratson.genimageexplorer.gui.reflection.GUIBuilderException;
import ratson.genimageexplorer.gui.reflection.SwingXMLGuiBuilder;
import ratson.utils.SwingInwoker;

public class ExplorerMainFrame extends JFrame {
	
	private static final int MAX_IMG_SIZE = 10000;

	private AbstractMouseTool[] mouseTools = new AbstractMouseTool[4];
	private IconManager icons = new IconManager("/images/");
	private String defRenderer = "Mandelbrot";
	private String defPattern = "Editable";
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		try {
		    UIManager.setLookAndFeel(
		        UIManager.getSystemLookAndFeelClassName());
		    //To turn off the painting of JSlider's value label under the GTK L&F (or Synth for
		    //that matter), add the following line of code to your application:
		    UIManager.put("Slider.paintValue", Boolean.FALSE);
		} catch (UnsupportedLookAndFeelException ex) {
		  System.out.println("Unable to load native look and feel");
		}
		ExplorerMainFrame emf=new ExplorerMainFrame();
		emf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		emf.setVisible(true);
		emf.setSize(600,480);
	}
	
	private ObservationArea location=new ObservationArea();
	private RenderingChain renderer = new RenderingChain();
	
	private PatternRendererThread patternRenderer;

	private ImagePane imagePanel;
	private JToolBar toolbar;
	
	private Action actnResolution;
	private Action actnRerender;
	private Action actnNavigateOrigin;
	private Action actnFileSaveImage;
	private Action actnFileExit;

	private Box rendererSettingsPanel;

	private Box patternSettingsPanel;

	private JProgressBar progressIndicator;

	private Thread patternRendererThread;

	private Thread renderThread;
	SwingXMLGuiBuilder guiBuilder=new SwingXMLGuiBuilder();

	private LinkedList<RendererRecord> registeredRenderers = new LinkedList<RendererRecord>();
	private LinkedList<PatternRecord> registeredPatterns = new LinkedList<PatternRecord>();

	private AbstractAction actnStop;

	private AbstractAction actnSaveLoc;

	private AbstractAction actnLoadLoc;
	private AbstractAction actnRenderHQ;

	private Box progressBox;

	private AbstractAction actnZoomIn;

	private AbstractAction actnZoomOut;

	private AbstractAction actnZoom100;

	private AbstractAction actnZoom200;

	private AbstractAction actnZoom300;
	
	private void createMouseTools(){
		mouseTools[0]=new ZoomMouseTool(){
			public void selected(Rectangle rect) {
				if (getMouseButton() == MouseEvent.BUTTON1)
					doZoom(rect);
				if (getMouseButton() == MouseEvent.BUTTON3)
					doUnZoom(rect);
			}
		};
		mouseTools[1]=new RotateMouseTool(){
			public void rotated(double angle) {
				doRotate(angle);
			}
		};
		mouseTools[2]=new PanMouseTool(){
			public void panned(int dx, int dy) {
				doPan(dx, dy);
			}
		};
		mouseTools[3] = new ProportionalZoomMouseTool(){
			public void selected(Rectangle rect) {
				if (getMouseButton() == MouseEvent.BUTTON1)
					doZoom(rect);
				if (getMouseButton() == MouseEvent.BUTTON3)
					doUnZoom(rect);
			}
		};
	}
	protected void doPan(int dx, int dy) {
		location.setResolution(renderer.GetRawImage().getWidth(),renderer.GetRawImage().getHeight());
		location.offset(dx, dy);
		render();
	}
	protected void doRotate(double angle) {
		location.setResolution(renderer.GetRawImage().getWidth(),renderer.GetRawImage().getHeight());
		location.rotate(angle);
		render();
	}
	public ExplorerMainFrame(){
		super("FRAX - generated image explorer");

		createMouseTools();
		initControls();
		loadPrefs();
		bindEvents();
		createMenus();
		
		
		patternRenderer=new PatternRendererThread(renderer, new Runnable(){
			public void run() {
				imagePanel.repaint();
			}
		});
		
		patternRendererThread=new Thread(patternRenderer,"Pattern rendering thread");
		patternRendererThread.start();
		
		//load default pattern and renderer
		loadPattern(defPattern);
		loadRenderer(defRenderer);
		
	}

	/**Load renderer by name*/
	private void loadRenderer(String rendererName) {
		for (RendererRecord rcd: registeredRenderers) {
			if (rcd.name.equals(rendererName)){
				loadRenderer(rcd);
				return;
			}
		}
		System.err.println("No such renderer:"+rendererName);
	}
	private void loadPattern(String patternName) {
		for (PatternRecord rcd: registeredPatterns) {
			if (rcd.name.equals(patternName)){
				loadPattern(rcd);
				return ;
			}
		}
		System.err.println("No such renderer:"+patternName);
	}
	/**exports current location and fractal settings*/
	private XMLElement exportLocation(){
		XMLElement root = new XMLElement();
		root.setName("view");
		
		root.addChild(location.exportXML());
		
		return root;
	}
	
	/**Imports location from XML file*/
	private void importLocation(XMLElement root) throws XMLFormatException{
		if (!root.getName().equals("view"))
			throw new XMLFormatException("Root element must be <view>");
		Enumeration children = root.enumerateChildren();
		while (children.hasMoreElements()){
			XMLElement child = (XMLElement)children.nextElement();
			if (child.getName().equals("location")){
				location.importXML(child);
				continue;
			}
			
		}
	}
	
	private void doSaveLocation(){
		JFileChooser fc=new JFileChooser();
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		
		try {
		    // Save as XML
			File file = fc.getSelectedFile();
			System.out.println("Saving current location in XML format");
			Writer fw = new FileWriter(file);
			exportLocation().write(fw);
			fw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to save file.\n"+e.getMessage());
		}
	}
	private void doLoadLocation(){
		JFileChooser fc=new JFileChooser();
		if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		try {
		    // Load from XML
			File file = fc.getSelectedFile();
			System.out.println("Reading location from XML format");
			Reader rd = new FileReader(file);
			XMLElement xml = new XMLElement();
			xml.parseFromReader(rd);
			rd.close();
			
			importLocation(xml);
			
			render();//when location loaded, rendering it;
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to read file.\n"+e.getMessage());
		} catch (XMLFormatException e) {
			JOptionPane.showMessageDialog(this, "Failed to parse location XML.\n"+e.getMessage());
		}
	}
	
	private void loadPrefs() {
		//detach old controls
		setResolution(400, 400);


		//reading renderers description
		XMLElement xml = new XMLElement();
		try {
			
			InputStream data = getClass().getResourceAsStream("/renderers.xml");
			Reader fs = new InputStreamReader(data,"UTF-8");
			xml.parseFromReader(fs);
			fs.close();
			data.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!xml.getName().equals("package")){
			System.err.println("Error parsing xml, package expected");
			return;
		}
		Enumeration children = xml.enumerateChildren();
		while(children.hasMoreElements()){
			XMLElement child = (XMLElement) children.nextElement();
			if (child.getName().equals("renderer")){
				registerRenderer(child);
				continue;
			}
			if (child.getName().equals("pattern")){
				registerPattern(child);
				continue;
			}

		}

		

	}


	private void registerPattern(XMLElement child) {
		try{
			PatternRecord rcd = new PatternRecord(child);
			registeredPatterns.add(rcd);
		}catch(Exception e){
			System.err.println("Error registering pattern renderer:\n"+e.getMessage());
		}
	}

	private void registerRenderer(XMLElement xml) {
		String name=null,description=null, className = null;
		XMLElement params=null;
		
		Enumeration children = xml.enumerateChildren();
		while (children.hasMoreElements()){
			XMLElement child = (XMLElement)children.nextElement();
			if (child.getName().equals("name")){
				name=child.getContent();
				continue;
			}
			if (child.getName().equals("description")){
				description=child.getContent();
				continue;
			}
			if (child.getName().equals("class")){
				className=child.getContent();
				continue;
			}
			if (child.getName().equals("parameters")){
				params=child;
				continue;
			}
			System.err.println(child);
		}
		if (name==null || className==null){
			System.err.println("Failed to register renderer, because no class name specified");
		}
		RendererRecord rcd= new RendererRecord(name,description,className,params);
		registeredRenderers.add(rcd);
	}


	@SuppressWarnings("serial")
	class SetMouseToolAction extends AbstractAction{
		AbstractMouseTool tool;
		public SetMouseToolAction(String name, AbstractMouseTool tool){
			this(name, tool, null);
		}
		public SetMouseToolAction(String name, AbstractMouseTool tool, String iconName){
			super(name,icons.getIcon(iconName));
			this.tool=tool;
		}
		public void actionPerformed(ActionEvent e) {
			imagePanel.setMouseTool(tool);
		}
		
	}
	class LoadRendererAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		private RendererRecord rendererRcd;

		public LoadRendererAction(RendererRecord ren){
			super(ren.name, icons.getIcon("mandel-16.png"));
			this.rendererRcd = ren;
		}

		public void actionPerformed(ActionEvent e) {
			loadRenderer(rendererRcd);
		}
		
	}
	class LoadPatternAction extends AbstractAction{
		private PatternRecord patternRcd;
		
		public LoadPatternAction (PatternRecord rcd){
			super(rcd.name, icons.getIcon("pattern-16.png"));
			patternRcd=rcd;
		}
		public void actionPerformed(ActionEvent e) {
			loadPattern(patternRcd);
		}
	}
	
	private void createMenus() {
		JMenuBar mnu=new JMenuBar();
		JMenu fileMenu=new JMenu("File");
		fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
		{//File menu creation
			fileMenu.add(actnFileSaveImage);
			fileMenu.add(actnSaveLoc);
			fileMenu.add(actnLoadLoc);
			fileMenu.add(new JSeparator());
			fileMenu.add(actnFileExit);
			mnu.add(fileMenu);
		}
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
		{
			viewMenu.add(actnZoomIn);
			viewMenu.add(actnZoomOut);
			viewMenu.add(new JSeparator());
			viewMenu.add(actnZoom100);
			viewMenu.add(actnZoom200);
			viewMenu.add(actnZoom300);
			mnu.add(viewMenu);
		}
		
		
		JMenu renderMenu=new JMenu("Render");
		renderMenu.setMnemonic(java.awt.event.KeyEvent.VK_R);
		{
			renderMenu.add(actnRerender);
			renderMenu.add(new JSeparator());
			renderMenu.add(actnResolution);
			
			JMenu thrds=new JMenu("Threads");
			{
				thrds.add(new SetThreadsAction("1 thread",1));
				thrds.add(new SetThreadsAction("2 threads",2));
				thrds.add(new SetThreadsAction("3 threads",3));
				thrds.add(new SetThreadsAction("4 threads",4));
				thrds.add(new SetThreadsAction("5 threads",5));
				thrds.add(new SetThreadsAction("6 threads",6));
			}
			renderMenu.add(thrds);
			
			mnu.add(renderMenu);
			renderMenu.add(actnRenderHQ);
		}
		
		JMenu navigationMenu=new JMenu("Navigation");
		navigationMenu.setMnemonic(java.awt.event.KeyEvent.VK_N);
		{
			navigationMenu.add(actnNavigateOrigin);
			
			mnu.add(navigationMenu);
		}
		
		JMenu renderersMenu=new JMenu("Renderers");
		{
			for (RendererRecord rrec: registeredRenderers) {
				renderersMenu.add(new LoadRendererAction(rrec));
			}

		}
		mnu.add(renderersMenu);

		JMenu patternsMenu = new JMenu("Patterns");
		patternsMenu.setMnemonic(java.awt.event.KeyEvent.VK_P);
		{
			for (PatternRecord rcd : registeredPatterns) {
				patternsMenu.add(new LoadPatternAction(rcd));
			}
		}
		mnu.add(patternsMenu);
		
		JMenu helpMenu=new JMenu("Help");
		helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
		{
			mnu.add(helpMenu);
		}

		setJMenuBar(mnu);
		
	}

	//loading specified renderer from XML
	public void loadRenderer(RendererRecord rendererRcd) {
		System.out.println("Loading renderer");
		System.out.println(rendererRcd.name);
		Constructor c;
		try {
			Class functionClass = Class.forName(rendererRcd.className);
			c = functionClass.getConstructor(null);
			FunctionFactory func = (FunctionFactory)c.newInstance(null);
			//generator is created, setting it
			
			renderer.setFunction(func);
			
			int numThreads = Runtime.getRuntime().availableProcessors();
			System.out.println("Setting number of threads to the system default: "+numThreads);
			renderer.getRenderer().setNumThreads( numThreads);//setting number of threads, equal to number of processors

			location.set(-2, -2, 2, 2);
			//detaching old controls
			
			rendererSettingsPanel.setVisible(false);
			rendererSettingsPanel.removeAll();
			
			Box b=Box.createVerticalBox();
			b.setBorder(new TitledBorder(rendererRcd.name));
			rendererSettingsPanel.add(b);
			guiBuilder.build(func, b, rendererRcd.params, null);
			
			rendererSettingsPanel.setVisible(true);

			//pack();

			
			render();
			
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Exceprion during class loading:");
			System.err.println(e.getTargetException().getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Generator class not found");
		} catch (GUIBuilderException e) {
			System.err.println("Failed to create GUI controls for renderer:");
			System.err.println(e.getMessage());
		}

	}
	//loading specified renderer from XML
	public void loadPattern(PatternRecord patternRcd) {
		System.out.println("Loading pattern");
		System.out.println(patternRcd.name);
		try {
			Class<ColorPattern> patternClass = (Class<ColorPattern>) Class.forName(patternRcd.className);
			Constructor<ColorPattern> c = patternClass.getConstructor(null);
			ColorPattern pattern = (ColorPattern )c.newInstance(null);
			//pattern is created, setting it
			
			renderer.setPattern(pattern);

			//detaching old controls
			patternSettingsPanel.setVisible(false);
			patternSettingsPanel.removeAll();
			
			Box b=Box.createVerticalBox();
			b.setBorder(new TitledBorder(patternRcd.name));
			patternSettingsPanel.add(b);
			guiBuilder.build(pattern, b, patternRcd.params,new Runnable(){
				public void run() {
					wakePatternRenderer();
				}
			});
			
			patternSettingsPanel.setVisible(true);
			//pack();

			wakePatternRenderer();
			
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Exceprion during class loading:");
			System.err.println(e.getTargetException().getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Generator class not found");
		} catch (GUIBuilderException e) {
			System.err.println("Failed to create GUI controls for renderer:");
			System.err.println(e.getMessage());
		}

	}

	/** action for setting different number of rendering threads*/
	@SuppressWarnings("serial")
	private class SetThreadsAction extends AbstractAction{
		private int numThreads;

		public SetThreadsAction(String name ,int numThreads){
			super(name);
			this.numThreads = numThreads;
		}
		public void actionPerformed(ActionEvent e) {
			renderer.getRenderer().setNumThreads(numThreads);
			System.out.println("Setted number of threads to "+numThreads);
		}
	}

	private void bindEvents(){
		//adding hotkeys
		
	}
	
	@SuppressWarnings("serial")
	private void createActions(){
		actnResolution=new AbstractAction("Set resolution",icons.getIcon("resolution-32.png")){
			public void actionPerformed(ActionEvent e) {
				doSetResolution();
			}
		};
		actnRerender=new AbstractAction("Recalculate", icons.getIcon("redraw-32.png")){
			public void actionPerformed(ActionEvent e) {
				render();
			}
		};
		actnNavigateOrigin=new AbstractAction("Origin", icons.getIcon("center-32.png")){
			public void actionPerformed(ActionEvent e) {
				location.set(-2,-2,2,2);
				render();
			}
		};
		actnFileSaveImage=new AbstractAction("Save image", icons.getIcon("save-32.png")){
			public void actionPerformed(ActionEvent e) {
				doSaveImage();
			}
		};
		actnFileExit=new AbstractAction("Exit"){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		actnStop=new AbstractAction("Stop", icons.getIcon("stop-32.png")){
			public void actionPerformed(ActionEvent e) {
				if (renderer.getFunction() != null){
					renderer.getRenderer().stopRendering();
					
					System.out.println("Stopping rendering process");
				}
			}
		};
		actnSaveLoc = new AbstractAction("Save location"){
			public void actionPerformed(ActionEvent e) {
				doSaveLocation();
			}
		};
		actnLoadLoc = new AbstractAction("Load location"){
			public void actionPerformed(ActionEvent e) {
				doLoadLocation();
			}
		};
		actnRenderHQ =new AbstractAction("High quality render"){
			public void actionPerformed(ActionEvent e) {
				HQRenderGUI hqGui = new HQRenderGUI(renderer.getRenderer(),renderer.getColorizer(), location);
				hqGui.setLocationRelativeTo(ExplorerMainFrame.this);
				hqGui.setVisible(true);
			}
		};
		actnZoomIn = new AbstractAction("Zoom in"){
			public void actionPerformed(ActionEvent e) {
				imagePanel.setZoom(imagePanel.getZoom()*1.1);
			}
		};
		actnZoomOut = new AbstractAction("Zoom out"){
			public void actionPerformed(ActionEvent e) {
				imagePanel.setZoom(imagePanel.getZoom()/1.1);
			}
		};
		actnZoom100 = new AbstractAction("Zoom 1:1"){
			public void actionPerformed(ActionEvent e) {
				imagePanel.setZoom(1);
			}
		};
		actnZoom200 = new AbstractAction("Zoom 2:1"){
			public void actionPerformed(ActionEvent e) {
				imagePanel.setZoom(2);
			}
		};
		actnZoom300 = new AbstractAction("Zoom 3:1"){
			public void actionPerformed(ActionEvent e) {
				imagePanel.setZoom(3);
			}
		};

	}
	/**saves current image to file*/
	protected void doSaveImage() {
		//final String[] exts={"png"};
		
		//FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", exts);
		
		JFileChooser fc=new JFileChooser();
		
		
		//fc.setFileFilter(filter);
		
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		String fname=fc.getSelectedFile().getName();
		
		
		try {
		    // Save as PNG
			File file = fc.getSelectedFile();
			System.out.println("Saving file in PNG format");
			ImageIO.write(renderer.getImage(), "png", file);
		} catch (IOException e) {
			System.err.println("Failed to save image "+fname);
			System.err.println(e.getLocalizedMessage());
		}
	}
	protected void doSetResolution() {
		ResolutionSetterDlg rsd=new ResolutionSetterDlg(this, "Set new resolution","New resolution for displayer image");
		if (rsd.doModal(renderer.GetRawImage().getWidth(), renderer.GetRawImage().getHeight())){
			setResolution(rsd.getResolutionX(), rsd.getResolutionY());
		}
	}
	protected void doUnZoom(Rectangle box) {
		location.setResolution(renderer.GetRawImage().getWidth(),renderer.GetRawImage().getHeight());
		location.unZoomToBox(box.x,box.y,box.x+box.width,box.y+box.height);
		render();
	}
	
	protected void doZoom(Rectangle box) {
		location.setResolution(renderer.GetRawImage().getWidth(),renderer.GetRawImage().getHeight());
		location.zoomToBox(box.x,box.y,box.x+box.width,box.y+box.height);
		render();
	}

	private void initControls(){
		createActions();
		Container c=getContentPane();
		progressIndicator=new JProgressBar(0,100);
		progressIndicator.setToolTipText("Rendering progress");
		
		imagePanel=new ImagePane();
		JScrollPane scrPane=new JScrollPane(imagePanel);
		
		c.add(scrPane,BorderLayout.CENTER);
		
		JPanel settingsBoxContainer=new JPanel();
		Box settingsBox=Box.createVerticalBox();
		
		settingsBoxContainer.add(settingsBox);
		c.add(new JScrollPane(settingsBoxContainer),BorderLayout.WEST);
		
		settingsBox.add(new JLabel("Settings"));

		progressBox = Box.createHorizontalBox();
		progressBox.add(new JButton(actnStop));
		progressBox.add(progressIndicator);
		progressBox.setVisible(false);
		c.add(progressBox, BorderLayout.SOUTH);
		
		
		//GUI container for generator
		rendererSettingsPanel=Box.createVerticalBox();
		settingsBox.add(rendererSettingsPanel);

		//GUI container for colorizer
		patternSettingsPanel=Box.createVerticalBox();
		settingsBox.add(patternSettingsPanel);
		
		toolbar = new JToolBar("Main");
		{
			toolbar.add(actnRerender).setToolTipText("Recalculate");
			toolbar.add(actnFileSaveImage).setToolTipText("Save as PNG");;
			toolbar.add(actnNavigateOrigin).setToolTipText("Origin");;
			toolbar.add(actnResolution).setToolTipText("Set resolution");;
			toolbar.addSeparator();
			toolbar.add(new SetMouseToolAction("Zoom",mouseTools[0], "zoom-nonprop-32.png")).
				setToolTipText("Free zoom");
			toolbar.add(new SetMouseToolAction("Proportional zoom",mouseTools[3], "zoom-prop-32.png")).
				setToolTipText("Proportional zoom");
			toolbar.add(new SetMouseToolAction("Rotate", mouseTools[1], "rotate-32.png")).
					setToolTipText("Rotate");
			toolbar.add(new SetMouseToolAction("Pan", mouseTools[2], "move-32.png")).
				setToolTipText("Pan");
		}
		c.add(toolbar, BorderLayout.NORTH);
	}
	

	private void render(){
		//check rendering chain
		if (!renderer.ensureConsistency()){
			JOptionPane.showMessageDialog(null, "Unable to build rendering chain");
			System.out.println("Failed to render");
			return;
		}
		
		System.out.println("Rendering data in parallel");
		
		//disable user interface
		enableUI(false);
		progressIndicator.setValue(0);
		progressBox.setVisible(true);
		
		final Runnable onFinish=new SwingInwoker(new Runnable(){
			public void run(){
				enableUI(true);
				progressBox.setVisible(false);
				wakePatternRenderer();
				System.out.println(String.format("Finished rendering in %dms.", renderer.getRenderer().getTimeElapsed() ));
			}
		});
		//		wrap these handlers to make them run in a Swing main thread
		final Runnable onProgress=new SwingInwoker(new Runnable(){
			public void run() {
				//called in this thread when rendering status bar needs to be updated
				progressIndicator.setValue(renderer.getRenderer().getProgress());
			}
		});

		//starting wait thread
		renderThread = new Thread("Renderer watch"){
			public void run() {
				try{
					renderer.render(location, onFinish, onProgress);
				}catch(RendererException e){
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		renderThread.start();
	}


	/**notifies patter renderer thread to do it's dirty work
	 * 
	 *
	 */
	protected void wakePatternRenderer() {
		synchronized(patternRenderer){
			if (patternRenderer!=null)
			patternRenderer.notify();
		}
	}

	/**enables or disables user interface*/
	protected void enableUI(boolean b) {
		imagePanel.setEnabled(b);
		patternSettingsPanel.setEnabled(b);
		rendererSettingsPanel.setEnabled(b);
		
	}

	public void setResolution(int w, int h){
		if (w<=0 || h<=0)
			throw new RuntimeException("impossible image size");
		
		//resizing raw image
		try{
			//good time to do garbage collection
			renderer.setResolution(0,0);
			System.gc();
			renderer.setResolution(w, h);
			//resizing display iamge
			
		}catch (OutOfMemoryError e){
			JOptionPane.showMessageDialog(this, "Out of memory.\nTry to increase java heap size", "Out of memory", JOptionPane.ERROR_MESSAGE);
			return;
		}

		imagePanel.setImage(renderer.getImage());

		//pack();
	}
	
}
