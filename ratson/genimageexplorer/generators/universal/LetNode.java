package ratson.genimageexplorer.generators.universal;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class LetNode extends BinaryNode {

	public LetNode(ExprNode variable, ExprNode expression) {
		super(variable, expression);
		if (!(variable instanceof VarNode)){
			throw new RuntimeException("Not a variable");
		}
	}

	
	
	public double eval() {
		double rval=child2.eval();
		((VarNode)child1).setValue(rval);
		return rval;
	}



}
