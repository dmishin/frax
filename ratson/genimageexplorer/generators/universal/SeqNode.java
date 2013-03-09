package ratson.genimageexplorer.generators.universal;

public class SeqNode extends NaryNode {

	public SeqNode(ExprNode[] children) {
		super(children);
	}

	public double eval() {
		double rval=0;
		for (int i = 0; i < children.length; i++) {
			rval=children[i].eval();
		}
		return rval;
	}

}
