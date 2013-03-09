package ratson.genimageexplorer.generators.universal;

public abstract class NaryNode extends ExprNode {
	protected final ExprNode[] children;
	public NaryNode(ExprNode[] children){
		this.children=children;
	}
	public NaryNode (ExprNode child1){
		children=new ExprNode[]{child1};
	}
	public NaryNode (ExprNode child1, ExprNode child2){
		children=new ExprNode[]{child1, child2};
	}
	public NaryNode (ExprNode child1, ExprNode child2, ExprNode child3){
		children=new ExprNode[]{child1, child2, child3};
	}

}
