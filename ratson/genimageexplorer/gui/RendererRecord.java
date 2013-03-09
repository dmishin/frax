package ratson.genimageexplorer.gui;

import nanoxml.XMLElement;

public class RendererRecord {

	public String name;
	public String decscription;
	public String className;
	public XMLElement params;

	public RendererRecord(String name, String description, String className, XMLElement params) {
		this.name=name;
		this.decscription=description;
		this.className=className;
		this.params=params;
	}
	

}
