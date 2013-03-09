package ratson.genimageexplorer.generators.universal;

public abstract class UnaryNode extends ExprNode {
	protected final ExprNode child;
	public UnaryNode(ExprNode child){
		this.child=child;
	}
}
