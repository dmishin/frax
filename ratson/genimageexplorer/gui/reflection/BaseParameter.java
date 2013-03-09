package ratson.genimageexplorer.gui.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseParameter {
	private String name;
	protected Method getter, setter;
	private Object object;
	public String getName(){
		return name;
	}
	public BaseParameter(String nm, Object obj, Class type, String getterName, String setterName) throws SecurityException, NoSuchMethodException{
		name=nm;
		object = obj;
		getter = obj.getClass().getMethod(getterName, null);
		if (getter.getReturnType() != type){
			throw new RuntimeException("Method has wrong return type");
		}
		setter = obj.getClass().getMethod(setterName, new Class[]{type});
	}
	
	public Object get(){
		if (getter == null)
			return null;
		try {
			return getter.invoke(object, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	public void set(Object val){
		if (setter  == null){
			System.err.println("No setter is present");
			return;
		}
		try {
			setter.invoke(object, new Object[]{val});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	public String toString() {
		return name+"{get="+getter+"; set="+setter+"}";
	}
	
}
