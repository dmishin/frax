package ratson.genimageexplorer.generators.universal;

public class MultNode extends NaryNode {


	public MultNode(ExprNode child1, ExprNode child2) {
		super(child1, child2);
	}
	public MultNode(ExprNode[] children) {
		super(children);
	}


	public double eval() {
		double v=children[0].eval();
		for (int i=1;i<children.length;++i){
			v=v*children[i].eval();
		}
		return v;
	}

}
