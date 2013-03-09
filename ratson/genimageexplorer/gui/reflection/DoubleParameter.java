package ratson.genimageexplorer.gui.reflection;

public class DoubleParameter extends BaseParameter {

	public DoubleParameter(String nm, Object obj,  String getterName, String setterName) throws SecurityException, NoSuchMethodException {
		super(nm, obj, double.class, getterName, setterName);
	}
	public double getDouble(){
		Object v = get();
		if (v instanceof Double){
			return ((Double)v).doubleValue();
		}
		System.err.println("Method returned not Double:"+v);
		return 0.0;
	}
	public void setDouble(double v){
		set(new Double(v));
	}

}
