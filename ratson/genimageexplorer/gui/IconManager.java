package ratson.genimageexplorer.gui;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class IconManager {
	private String baseDir;

	public IconManager(String baseDir){
		this.baseDir = baseDir;
	}
	private HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	
	public ImageIcon getIcon(String name){
		if (name == null)
			return null;
		ImageIcon icn = icons.get(name);
		if (icn != null)
			return icn;
		icn = loadIcon(name);
		if (icn == null)
			return null;
		icons.put(name, icn);
		return icn;
	}

	private ImageIcon loadIcon(String name) {
		
		String icnPath = baseDir+name;
		java.net.URL imgURL = getClass().getResource(icnPath);
		
	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + icnPath);
	        return null;
	    }
	}
}
