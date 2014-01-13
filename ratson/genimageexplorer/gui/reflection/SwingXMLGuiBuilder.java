package ratson.genimageexplorer.gui.reflection;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import javax.print.attribute.standard.JobHoldUntil;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import net.n3.nanoxml.*;

public class SwingXMLGuiBuilder {
	/**Creates GUI controls for given object, using specified XML description
	 * 
	 * XML must contain single <controls> tag
	 * @param onUpdate Executed, when the value is updated. Code is executed in the thread, called update
	 * @throws GUIBuilderException 
	 */
	public void build(Object object, Container location, IXMLElement xml, Runnable onUpdate) throws GUIBuilderException{
		if (!xml.getName().equals("parameters") && !xml.getName().equals("group")){
			throw new GUIBuilderException("Top level tag if XML must be <controls>. Parse failed.");
		}
		Enumeration children = xml.enumerateChildren();
		while (children.hasMoreElements()){
			IXMLElement child = (IXMLElement) children.nextElement();
			String childName = child.getName();
			
			if (childName.equals("int")){
				buildInt(object, location, child, onUpdate);
				continue;
			}
			if (childName.equals("double")){
				buildDouble(object, location, child, onUpdate);
				continue;
			}
			if (childName.equals("boolean")){
				buildBoolean(object, location, child, onUpdate);
				continue;
			}
			if (childName.equals("option")){
				buildOption(object, location, child, onUpdate);
				continue;
			}
			if (childName.equals("push")){
				buildPush(object, location, child, onUpdate);
				continue;
			}
			if (childName.equals("group")){
				buildGroup(object, location, child, onUpdate);
				continue;
			}
			//unknown tag: skipping it
			System.err.println("Unknown tag in a description:");
			System.err.println(child);
			
		}
	}

	private void buildGroup(Object object, Container location, IXMLElement child, Runnable onUpdate) throws GUIBuilderException {
		Box b=Box.createVerticalBox();
		String name = child.getAttribute("name", null);
		if (name!=null){
			b.setBorder(new TitledBorder(name));
		}else{
			b.setBorder(new BevelBorder(BevelBorder.LOWERED));
			
		}
		location.add(b);
		build(object, b, child,onUpdate);
	}

	private void buildPush(final Object object, Container location, IXMLElement child, final Runnable onUpdate) {
		String name =child.getContent();
		if (name == null) name ="";

		String handler=child.getAttribute("action", null);
		if (handler == null) return ;
		Method handlerMethod=null;
		try {
			handlerMethod = object.getClass().getMethod(handler, null);
			
		} catch (SecurityException e) {
			e.printStackTrace();
			return ;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return ;
		}
		final Method finHMethod =handlerMethod; 

		//now creating the GUI
		JButton btn=new JButton(name);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					finHMethod.invoke(object, null);
					call(onUpdate);
				}catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
			}
		});
		location.add(btn);
	}

	private static void call(Runnable r){
		if (r!=null) r.run();
	}
	private StringParameter buildOption(Object object, Container location, IXMLElement node, final Runnable onUpdate) throws GUIBuilderException {
		// TODO Auto-generated method stub
		String name=null;
		LinkedList options = new LinkedList();
		Enumeration children = node.enumerateChildren();
		while(children.hasMoreElements()){
			IXMLElement child = (IXMLElement) children.nextElement();
			if (child.getName().equals("title"))
				name = child.getContent();
			if (child.getName().equals("value"))
				options.add(child.getContent());
		}
		//read contents.
		String[] gs=getGetterAndSetter(node);
		final StringParameter param;
		try {
			param = new StringParameter(name,object,gs[0],gs[1]);
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
		//parameter created. Now creating combo
		if (name != null){
			JLabel title=new JLabel(name);
			location.add(title);
		}

		Object[] items = new Object[options.size()];
		int i=0;
		for (Iterator iOption = options.iterator(); iOption.hasNext();++i) {
			items[i] = (String) iOption.next();
		}
		
		final JComboBox combo=new JComboBox(items);
		combo.setSelectedItem(param.getString());
		combo.setEditable(false);
	
		
		//setting dimension to fit vertival box
		Dimension d=combo.getPreferredSize();
		d.width=Integer.MAX_VALUE;
		combo.setMaximumSize(d);
		
		combo.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				String itm  = (String) combo.getSelectedItem();
				param.setString(itm);
				call(onUpdate);
			}
		});
		location.add(combo);			

		return param;
	}

	private BoolParameter buildBoolean(Object object, Container location, IXMLElement child, Runnable onUpdate) throws GUIBuilderException {
		String name =child.getContent();
		if (name == null) name ="";

		String[] gs=getGetterAndSetter(child);
		BoolParameter param;
		try {
			param = new BoolParameter(name,object,gs[0],gs[1]);
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}

		//now creating the GUI
		createBoolFlag(location, param, onUpdate);

		return param;
	}

	private void createBoolFlag(Container where, final BoolParameter param, final Runnable onUpdate) {

		final JCheckBox check=new JCheckBox(param.getName(),param.getBool());
		
		//setting dimension to fit vertival box
		Dimension d=check.getPreferredSize();
		d.width=Integer.MAX_VALUE;
		check.setMaximumSize(d);
		
		check.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				param.setBool(check.isSelected());
			}
		});
		//creating listener
		check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				param.setBool(check.isSelected());
				call(onUpdate);
			}});
		where.add(check);			
	}

	private DoubleParameter buildDouble(Object object, Container location, IXMLElement child, Runnable onUpdate) throws GUIBuilderException {
		String name =child.getContent();
		if (name == null) name ="";

		String[] gs=getGetterAndSetter(child);
		DoubleParameter param;
		try {
			param = new DoubleParameter(name,object,gs[0],gs[1]);
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
		//determine control type
		String ctype = child.getAttribute("control", null);
		if (ctype == null){
			ctype = "field";
		}
		
		//determine min max values
		double min = readDoubleAttr("min",child, Double.NaN);
		double max = readDoubleAttr("max",child, Double.NaN);

		//now creating the GUI
		if (ctype.equals("field")){
			createDoubleField(location, param ,min,max, onUpdate);
		}else if (ctype.equals("slider")){
			createDoubleSlider(location, param, min, max, onUpdate);
		}
		
		return param;
	}

	private DoubleParameter createDoubleField(Container where, final DoubleParameter param, final double min, final double max, final Runnable onUpdate) {
		JLabel title=new JLabel(param.getName());
		where.add(title);

		JButton btnX2 = new JButton("+5%");
		JButton btnD2 = new JButton("-5%");
		
		final JTextField fld=new JTextField(String.valueOf(param.getDouble()));

		Box buttonBox = Box.createHorizontalBox();
		

		
		//setting dimension to fit vertival box
		Dimension d=fld.getPreferredSize();
		d.width=Integer.MAX_VALUE;
		fld.setMaximumSize(d);
		
		Dimension btnSize=new Dimension(d.height, d.height);
		btnX2.setMaximumSize(btnSize);
		btnD2.setMaximumSize(btnSize);
		

		buttonBox.add(fld);
		buttonBox.add(btnX2);
		buttonBox.add(btnD2);

		where.add(buttonBox);
		
		//set listeners
		fld.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e) {update();}
			public void removeUpdate(DocumentEvent e) {update();}
			private void update(){
				double v = str2double(fld.getText());
				if (!Double.isNaN(v))
					param.setDouble(limit(v,min,max));
				call(onUpdate);
			}
			public void changedUpdate(DocumentEvent e) {}
			
		});
		//creating listener
		fld.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				double v = str2int(fld.getText());
				if (!Double.isNaN(v))
					param.setDouble(limit(v,min,max));
				call(onUpdate);
			}});
		
		
		btnX2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				double v = param.getDouble() * 1.05;
				//param.setDouble(v);
				fld.setText(String.valueOf(v));
			}
		});
		btnD2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				double v = param.getDouble() / 1.05;
				//param.setDouble(v);
				fld.setText(String.valueOf(v));
			}
		});
		
		return param;
		
	}

	/**Limits value of variable according to specified values.
	 * 
	 * @param v
	 * @param min When NaN, limit is not applied
	 * @param max
	 * @return
	 */
	private static double limit(double v, double min, double max) {
		if (!Double.isNaN(min))
			v = Math.max(v,min);
		if (!Double.isNaN(max))
			v = Math.min(v,max);
		return v;
	}

	private void createDoubleSlider(Container where, final DoubleParameter param, final double min, final double max, final Runnable onUpdate) {

		JLabel title=new JLabel(param.getName());
		
		where.add(title);
		
		final JTextField fld=new JTextField(String.valueOf(param.getDouble()));
		
		//setting dimension to fit vertival box
		Dimension d=fld.getPreferredSize();
		d.width=Integer.MAX_VALUE;
		fld.setMaximumSize(d);
		fld.setEditable(false);		
		
		
		
		//adding control to container
		where.add(fld);
		
		//creating slider
		final JSlider sld=new JSlider(0,1024);
		if (!Double.isNaN(min) && !Double.isNaN(max)){
			double v=param.getDouble();
			sld.setValue((int)((v-min)/(max-min)*1024));
		}else{
			sld.setValue(sld.getMaximum()/2);
		}
		
		
		sld.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				if (!Double.isNaN(min) && !Double.isNaN(max)){
					double newV=sld.getValue()/(double)1024 *(max-min)+min;
					param.setDouble(newV);
					call(onUpdate);
				}else{
					double dx=(sld.getValue()-1024/2)/(double)(1024/2);//+-1
					
					double newV=Math.pow(2.0, dx)*param.getDouble();
					param.setDouble(newV);
					call(onUpdate);
					sld.setValue(1024/2);
				}
				fld.setText(String.valueOf(param.getDouble()));
			}
		});
		where.add(sld);

	}

	private IntParameter buildInt(Object object, Container location, IXMLElement child, Runnable onUpdate) throws GUIBuilderException {
		String name =child.getContent();
		if (name == null) name ="";

		String[] gs=getGetterAndSetter(child);
		
		IntParameter param;
		try {
			param = new IntParameter(name, object, gs[0],gs[1] );
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}

		//determine control type
		String ctype = child.getAttribute("control", null);
		if (ctype == null){
			ctype = "field";
		}
		
		//determine min max values
		int min = readIntAttr("min",child, Integer.MIN_VALUE);
		int max = readIntAttr("max",child, Integer.MAX_VALUE);

		//now creating the GUI
		if (ctype.equals("field")){
			createIntField(location, param ,min,max, onUpdate);
		}else if (ctype.equals("slider")){
			createIntSlider(location, param, min, max, onUpdate);
		}
		return param;
		
	}

	private void createIntSlider(Container location, Object object, int min, int max, Runnable onUpdate) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	private static int str2int(String s){
		try{
			return Integer.valueOf(s).intValue();
		}catch (NumberFormatException e){
			return 0;
		}
	}
	private static double str2double(String s){
		try{
			return Double.valueOf(s).doubleValue();
		}catch (NumberFormatException e){
			return Double.NaN;
		}
	}

	
	private IntParameter createIntField(Container where, final IntParameter param, final int min, final int max, final Runnable onUpdate) {
		JLabel title=new JLabel(param.getName());
		
		where.add(title);
		
		final JTextField fld=new JTextField(String.valueOf(param.getInt()));
		
		//setting dimension to fit vertival box
		Dimension d=fld.getPreferredSize();
		d.width=Integer.MAX_VALUE;
		fld.setMaximumSize(d);
				
		fld.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e) {update();}
			public void removeUpdate(DocumentEvent e) {update();}
			private void update(){
				int v = str2int(fld.getText());
				v=Math.max(Math.min(v,max),min);
				param.setInt(v);
				call(onUpdate);
			}
			public void changedUpdate(DocumentEvent e) {}
			
		});
		//creating listener
		fld.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int v = str2int(fld.getText());
				v=Math.max(Math.min(v,max),min);
				param.setInt(v);
				call(onUpdate);
			}});
		where.add(fld);	
		
		return param;
	}

	private double readDoubleAttr(String attrName, IXMLElement node, double defValue) throws GUIBuilderException {
		String val = node.getAttribute(attrName, null);
		if (val == null)
			return defValue;
		try{
			return str2double(val);
		}catch(NumberFormatException e){
			throw new GUIBuilderException("Parameter must be numeric");
		}
	}

	private int readIntAttr(String attrName, IXMLElement node, int defValue) throws GUIBuilderException {
		String val = node.getAttribute(attrName, null);
		if (val == null)
			return defValue;
		try{
			return str2int(val);
		}catch(NumberFormatException e){
			throw new GUIBuilderException("Parameter must be numeric");
		}
	}

	/**returns array of 2 strings, containing getter and setter name*/
	private String[] getGetterAndSetter(IXMLElement elt) throws GUIBuilderException{
		String getter = elt.getAttribute("get", null);
		String setter = elt.getAttribute("set", null);
		String basename = elt.getAttribute("name", null);
		if (getter == null && setter ==null){
			if (basename == null){
				throw new GUIBuilderException("Getter, setter and basename are not specified. Can not create interface");
			}
			getter = "get"+basename;
			setter = "set"+basename;
		}
		if (getter==null)
			throw new GUIBuilderException("Can not determine getter name for "+elt);
		if (setter==null)
			throw new GUIBuilderException("Can not determine setter name for "+elt);
		
		return new String[]{getter, setter};
		
	}
}
