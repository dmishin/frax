package ratson.genimageexplorer.paramvector;

public class ParamDouble extends ParamBase {
	public double value;
	@Override
	public String toString() {
		return String.format("dbl:{%g}", value);
	}
}
