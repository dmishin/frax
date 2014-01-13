package ratson.genimageexplorer.gui;

import net.n3.nanoxml.IXMLElement;

public class RendererRecord {

	public String name;
	public String decscription;
	public String className;
	public IXMLElement params;

	public RendererRecord(String name, String description, String className, IXMLElement params) {
		this.name=name;
		this.decscription=description;
		this.className=className;
		this.params=params;
	}
	

}
