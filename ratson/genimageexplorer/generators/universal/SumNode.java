package ratson.genimageexplorer.generators.universal;

public class SumNode extends NaryNode {

	
	public SumNode(ExprNode child1, ExprNode child2) {
		super(child1, child2);
		// TODO Auto-generated constructor stub
	}


	public SumNode(ExprNode[] children) {
		super(children);
		// TODO Auto-generated constructor stub
	}

	public double eval() {
		double s=0;
		for (int i = 0; i < children.length; i++) {
			s+=children[i].eval();
		}
		return s;
	}
}
