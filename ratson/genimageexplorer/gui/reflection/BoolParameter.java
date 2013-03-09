package ratson.genimageexplorer.gui.reflection;

public class BoolParameter extends BaseParameter {

	public BoolParameter(String nm, Object obj, String getterName, String setterName) throws SecurityException, NoSuchMethodException {
		super(nm, obj, boolean.class, getterName, setterName);
	}
	public boolean getBool(){
		Object v = get();
		if (v instanceof Boolean){
			return ((Boolean)v).booleanValue();
		}
		System.err.println("Method returned not  boolean:"+v);
		return false;
	}
	public void setBool(boolean b){
		set(new Boolean(b));
	}

}
