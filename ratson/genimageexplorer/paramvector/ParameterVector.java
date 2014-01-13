package ratson.genimageexplorer.paramvector;

import java.io.Serializable;
import java.util.ArrayList;


/*The vector, containing several named parameters of different types*/
public class ParameterVector implements Serializable{
	public ParamBase[] params;
	
	public ParameterVector(int size){
		params = new ParamInt[size];
	}
	
	public int size(){ return params.length; }
	public ParamBase get(int idx){ return params[idx]; };
	public String getName(int idx){ return get(idx).getName(); };
	
}
