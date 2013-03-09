package ratson.genimageexplorer.paramvector;

public class ParamInt extends ParamBase{
	public int value;
	@Override
	public String toString() {
		return String.format("int:{%d}", value);
	}
}
