package ratson.genimageexplorer.generators.universal;

public class ConstNode extends ExprNode {
	double value;
	public ConstNode(double v){
		value=v;
	}
	public double eval() {
		return value;
	}

}
