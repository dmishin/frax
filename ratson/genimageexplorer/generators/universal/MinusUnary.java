package ratson.genimageexplorer.generators.universal;

public class MinusUnary extends UnaryNode {
	public MinusUnary(ExprNode c) {
		super(c);
	}
	public double eval() {
		return - child.eval();
	}
}
