package ratson.genimageexplorer.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParseException;
import net.n3.nanoxml.XMLWriter;
import ratson.genimageexplorer.EditablePattern;
import ratson.genimageexplorer.PatternFormatException;
import ratson.genimageexplorer.gui.PatternEditorControl;
import ratson.utils.NanoXML;

@SuppressWarnings("serial")
public class PatternEditor extends JDialog{
	
	PatternEditorControl editor;
	EditablePattern pattern;
	IXMLElement systemPatterns;
	IXMLElement userPatterns;
	IXMLElement currentPattern;
	private JComboBox<ListItem> patternList;
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnOK;
	private JButton btnDel;
	private JButton btnEqual;
	private JButton btnRandom;
	
	public PatternEditor(Frame owner, EditablePattern ptrn){
		super(owner,"Pattern editor",true);
		if (ptrn == null)
			throw new NullPointerException("Pattern to edit was not specified");
		pattern = ptrn;
		
		currentPattern = ptrn.exportXML();
		
		initControls();
		editor.reloadPattern(ptrn);


		readPatternFiles();
		populatePatternList();
		
		bindEvents();
	}
	private void saveUserPatterns(){
		if (userPatterns == null || userPatterns.getChildrenCount() == 0)
			return;//nothing to save
		try{
			System.out.println("SAving user patterns");
			FileOutputStream fow = new FileOutputStream("user-patterns.xml");
			OutputStreamWriter osw = new OutputStreamWriter(fow, "UTF-8");
			XMLWriter xw = new XMLWriter(osw);
			xw.write(userPatterns);
			osw.close();
			fow.close();
		}catch (IOException e){
			JOptionPane.showMessageDialog(null, "Failed to save user patterns file.\n"+e.getMessage());
		}
	}
	private void readPatternFiles(){
		try {
			systemPatterns = null;
			InputStream sysPatternStream = getClass().getResourceAsStream("/patterns.xml");
			Reader rdr;
			rdr = new InputStreamReader(sysPatternStream, "UTF-8");
			systemPatterns = readFile(rdr);
			rdr.close();
			
		} catch (UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, "Failed to load system pattern file.\n"+e.getMessage());
		} catch (PatternFormatException e) {
			JOptionPane.showMessageDialog(null, "System pattern file is corrupted.\n"+e.getMessage());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to load system pattern file.\n"+e.getMessage());
		}
		try {

			userPatterns = null;
			File userPatternsFile = new File("user-patterns.xml");
			if (userPatternsFile.exists()){
				FileInputStream userPatternStream = new FileInputStream(userPatternsFile);
				Reader rdr = new InputStreamReader(userPatternStream, "UTF-8");
				userPatterns = readFile(rdr);
				rdr.close();
			}
			
		} catch (UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, "Failed to load user pattern file.\n"+e.getMessage());
		} catch (PatternFormatException e) {
			JOptionPane.showMessageDialog(null, "System pattern file is corrupted.\n"+e.getMessage());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to load system pattern file.\n"+e.getMessage());
		}
	
	}
	private void bindEvents() {
		
		btnOK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editor.getPattern(pattern);
				setVisible(false);
				saveUserPatterns();

			}});
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				saveUserPatterns();

			}});
		btnDel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doDeleteSelected();
			}
		});
		btnEqual.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editor.makeEqualSpacing();
			}
		});
		btnRandom.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editor.randomizePattern();
			}});
		patternList.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (true/*e.getStateChange()==ItemEvent.SELECTED*/){
					ListItem itm =(ListItem)e.getItem();
					try {
						editor.loadXML(itm.data);
					} catch (PatternFormatException e1) {
						System.err.println(e1.getMessage());
					}
				}
			}
		});
		
		btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				IXMLElement elt = editor.getPattern().exportXML();
				//Request name from user
				String newName = JOptionPane.showInputDialog(null, "Enter the name of the pattern", "Pattern name");
				if (newName != null){
					elt.setAttribute("name", newName);
					if (userPatterns == null) {
						userPatterns = new XMLElement();
						userPatterns.setName("patterns");
					}
					userPatterns.addChild(elt);
					populatePatternList();
				}
			}
		});


		
	}
	protected void doDeleteSelected() {
		// TODO Auto-generated method stub
		
	}
	private IXMLElement readFile(String name) throws PatternFormatException{
		try {
			Reader r;
			r = new InputStreamReader(new FileInputStream(name),"UTF-8");
			IXMLElement element = readFile(r);
			r.close();
			return element;
		} catch (UnsupportedEncodingException e) {
			throw new PatternFormatException("XML contains unsupported characters");
		} catch (FileNotFoundException e) {
			throw new PatternFormatException("XML file not found");
		} catch (IOException e) {
			throw new PatternFormatException("XML file read IO error");
		}
	}
	private IXMLElement readFile(Reader file) throws PatternFormatException{
		IXMLElement element;
		try {
			element = NanoXML.parseStream(file);
		} catch (XMLParseException e) {
			throw new PatternFormatException("XML is incorrect");
		} catch (IOException e) {
			throw new PatternFormatException("File is not readable");
		} catch (IllegalAccessException e) {
			throw new PatternFormatException("File is not readable");
		} catch (XMLException e) {
			throw new PatternFormatException("XML is incorrect");
		}
		if (!element.getName().equals("patterns"))
			throw new PatternFormatException("Top level element must be <patterns>");
		return element;
	}
	
	private void initControls(){
		Container c = getContentPane();
		////////////create controls
		editor =new PatternEditorControl();
		patternList = new JComboBox<ListItem>();
		btnSave = new JButton("Save");
		btnOK = new JButton("OK");
		btnCancel = new JButton("Cancel");
		btnDel = new JButton("X");
		btnEqual = new JButton("Equal spacing");
		btnRandom = new JButton("Random pattern");
		
		//////////laying out controls
		Box topBox1 = Box.createHorizontalBox();
		topBox1.setBorder(new TitledBorder("Patterns library"));
		topBox1.add(patternList);
		topBox1.add(btnDel);
		topBox1.add(btnSave);
		
		Box topBox2 = Box.createHorizontalBox();
		topBox2.add(Box.createHorizontalGlue());
		topBox2.add(btnRandom);
		topBox2.add(btnEqual);
		
		
		Box bottomBox1 = Box.createHorizontalBox();
		bottomBox1.add(btnOK);
		bottomBox1.add(btnCancel);
		
		Box mainBox = Box.createVerticalBox();
		mainBox.add(topBox1);
		mainBox.add(topBox2);
		mainBox.add(editor);
		mainBox.add(bottomBox1);
		
		
		c.setLayout(new BorderLayout());
		c.add(mainBox);
		
		/////////////set sizes
		Dimension maxVSize = new Dimension(Integer.MAX_VALUE, 24);
		topBox1.setMaximumSize(maxVSize);
		
		pack();
		setSize(new Dimension(400,250));
		
	}
	
	class ListItem{
		public String name;
		public IXMLElement data;
		public ListItem(String n, IXMLElement d){
			name = n;
			data = d;
		}
		public String toString() {
			return name;
		}
	}
	private void populatePatternList(IXMLElement source) {
		Enumeration e = source.enumerateChildren();
		while (e.hasMoreElements()){
			IXMLElement c = (IXMLElement)e.nextElement();
			if (c.getName().equals("pattern"));{
				String name = c.getAttribute("name", null);
				if (name==null) name = "unnamed";
				patternList.addItem(new ListItem(name, c));
			}
		}
	
	}

	private void populatePatternList() {
		patternList.setEnabled(false);
		patternList.removeAllItems();
		patternList.addItem(new ListItem("<current>", currentPattern));
		if (systemPatterns != null)
			populatePatternList(systemPatterns);
		if (userPatterns != null)
			populatePatternList(userPatterns );
		patternList.setSelectedIndex(0);
		patternList.setEnabled(true);
	}
	
	
	public static void main(String[] args) throws PatternFormatException {
		EditablePattern p = new EditablePattern();
		p.randomize(3);
		PatternEditor edt = new PatternEditor(null, p);
		edt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		edt.readFile("patterns.xml");
		edt.setVisible(true);
		System.out.println(p.exportXMLString());
	}

}
