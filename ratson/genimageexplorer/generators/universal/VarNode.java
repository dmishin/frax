package ratson.genimageexplorer.generators.universal;
/**variable*/
public class VarNode extends ExprNode {
	private double value;
	private String name;
	public String getName(){
		return name;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}
	public double eval() {
		return value;
	}
	
	public VarNode (String name){
		this.name=name;
	}

}
