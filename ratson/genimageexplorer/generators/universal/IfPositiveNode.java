package ratson.genimageexplorer.generators.universal;

public class IfPositiveNode extends TernaryNode {

	public IfPositiveNode(ExprNode condition, ExprNode c2, ExprNode c3) {
		super(condition, c2, c3);
	}

	public double eval() {
		double v = child1.eval();
		if (v>0){
			return child1.eval();
		}else{
			return child2.eval();
		}
	}
}
