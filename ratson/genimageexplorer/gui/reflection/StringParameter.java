package ratson.genimageexplorer.gui.reflection;

public class StringParameter extends BaseParameter {

	public StringParameter(String nm, Object obj, String getterName, String setterName) throws SecurityException, NoSuchMethodException {
		super(nm, obj, String.class, getterName, setterName);
	}
	public String getString(){
		Object v = get();
		if (v instanceof String)
			return (String)v;
		System.err.println("method returned not string:"+v);
		return "";
	}
	public void setString(String s){
		set(s);
	}

}
