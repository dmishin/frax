package ratson.genimageexplorer.generators.universal;

public class DivNode extends BinaryNode {

	public DivNode(ExprNode c1, ExprNode c2) {
		super(c1, c2);
	}

	public double eval() {
		return child1.eval()/child2.eval();
	}


}
