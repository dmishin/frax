package ratson.genimageexplorer.gui;

import java.util.Enumeration;

import nanoxml.XMLElement;

public class PatternRecord {
	public String name;
	public String description;
	public String className;
	public XMLElement params;
	public PatternRecord(XMLElement elt){
		if (!elt.getName().equals("pattern"))
			throw new RuntimeException("<pattern> node expected, but found "+elt.getName());
		Enumeration children = elt.enumerateChildren();
		while (children.hasMoreElements()){
			XMLElement e = (XMLElement)children.nextElement();
			
			if (e.getName().equals("name")){
				name = e.getContent();
				continue;
			}
			if (e.getName().equals("description")){
				description = e.getContent();
				continue;
			}
			if (e.getName().equals("class")){
				className = e.getContent();
				continue;
			}
			if (e.getName().equals("parameters")){
				params = e;
				continue;
			}
		}
		if (name == null)
			throw new RuntimeException("Pattern renderer name was not specified");
		if (className == null)
			throw new RuntimeException("Pattern renderer class name was not specified");
	}
}
