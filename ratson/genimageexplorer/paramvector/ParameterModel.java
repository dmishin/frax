package ratson.genimageexplorer.paramvector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class ParameterModel implements Serializable{
	HashMap<String, Integer> parameterIndices=new HashMap<String, Integer>();
	LinkedList<String> paramNames = new LinkedList<String>();
	
	public int addParameter(String name){
		if (parameterIndices.containsKey(name))
			throw new RuntimeException("Parameter already exists");
		parameterIndices.put(name, new Integer(size()));
		paramNames.add(name);
		return size() - 1;
	}
	
	public int getParameter(String name){
		Integer v = parameterIndices.get(name);
		if (v == null)
			throw new RuntimeException("No such parameter:"+name);
		return v.intValue();
	}
	
	public int size(){
		return paramNames.size();
	}
	public ParameterVector createVector(){
		ParameterVector vec = new ParameterVector(size());
		
		for (Iterator<String> iPar = paramNames.iterator(); iPar.hasNext();) {
			String name =  iPar.next();
			
		}
		return vec;
	}
	
}
