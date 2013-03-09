package ratson.genimageexplorer.gui.reflection;

public class IntParameter extends BaseParameter {

	public IntParameter(String nm, Object obj, String getterName, String setterName) throws SecurityException, NoSuchMethodException {
		super(nm, obj, int.class, getterName, setterName);
	}
	
	public int getInt(){
		Object v = get();
		if (v instanceof Integer)
			return ((Integer)v).intValue();
		System.err.println("Method returned not integer value:"+v);
		return 0;
	}
	public void setInt(int v){
		set(new Integer(v));
	}

}
