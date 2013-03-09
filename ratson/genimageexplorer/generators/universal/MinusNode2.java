package ratson.genimageexplorer.generators.universal;

public class MinusNode2 extends BinaryNode {
	
	public MinusNode2(ExprNode c1, ExprNode c2) {
		super(c1, c2);
	}

	public double eval() {
		return child1.eval()-child2.eval();
	}

}
