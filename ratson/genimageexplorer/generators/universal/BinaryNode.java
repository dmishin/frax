package ratson.genimageexplorer.generators.universal;

public abstract class BinaryNode extends ExprNode {
	protected final ExprNode child1, child2;
	public BinaryNode(ExprNode c1, ExprNode  c2){
		child1=c1;
		child2=c2;
	}
}
