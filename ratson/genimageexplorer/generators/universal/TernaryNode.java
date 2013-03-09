package ratson.genimageexplorer.generators.universal;

public abstract class TernaryNode extends ExprNode {

	ExprNode  child1, child2, child3;
	public TernaryNode(ExprNode c1, ExprNode c2, ExprNode c3){
		child1=c1;
		child2=c2; child3 = c3;
	}

}
