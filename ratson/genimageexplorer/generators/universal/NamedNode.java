package ratson.genimageexplorer.generators.universal;

public class NamedNode extends NaryNode {

	private String name;
	
	public NamedNode(String name, ExprNode[] children){
		super(children);
		this.name=name;
	}
	
	public double eval() {
		throw new RuntimeException("Do not know how to evaluate node: "+name);
	}
	
	public String getName() {
		return name;
	}

	public ExprNode getChild(int i) {
		return children[i];
	}
	

}
