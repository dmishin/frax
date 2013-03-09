package ratson.genimageexplorer.generators.universal;


public class FractalNode extends ExprNode {

	private ExprNode exprInit, exprIterate, exprWhile, exprReturn, exprInfReturn;
	private FractalNode(ExprNode init, ExprNode iter, ExprNode whil, ExprNode ret, ExprNode infret){
		exprInit=init;
		exprIterate=iter;
		exprWhile=whil;
		exprReturn=ret;
		exprInfReturn=infret;
	}
	public FractalNode(ExprNode[] children) throws ScriptSyntaxException {
		for (int i=0;i<children.length;++i){
			if (!(children[i] instanceof NamedNode)){
				throw new ScriptSyntaxException("Wrong subnode in fractal description:"+children[i].getClass().getName());
			}
			NamedNode c=(NamedNode )children[i];
			
			if (c.getName().equals("init")){
				if (exprInit!=null)
					throw new ScriptSyntaxException("Duplicate init in fractal description");
				exprInit=c.getChild(0);
				continue;
			}
			if (c.getName().equals("iterate")){
				if (exprIterate!=null)
					throw new ScriptSyntaxException("Duplicate iterate in fractal description");
				exprIterate=c.getChild(0);
				continue;
			}
			if (c.getName().equals("while")){
				if (exprWhile!=null)
					throw new ScriptSyntaxException("Duplicate while in fractal description");
				exprWhile=c.getChild(0);
				continue;
			}
			if (c.getName().equals("return")){
				if (exprReturn!=null)
					throw new ScriptSyntaxException("Duplicate return in fractal description");
				exprReturn=c.getChild(0);
				continue;
			}
			if (c.getName().equals("infreturn")){
				if (exprInfReturn!=null)
					throw new ScriptSyntaxException("Duplicate infreturn in fractal description");
				exprInfReturn=c.getChild(0);
				continue;
			}
			//ignoring other types
		}
		
	}

	public double eval() {
		throw new RuntimeException("should not be evaluated");
	}
	public ExprNode getExprInfReturn() {
		return exprInfReturn;
	}
	public ExprNode getExprInit() {
		return exprInit;
	}
	public ExprNode getExprIterate() {
		return exprIterate;
	}
	public ExprNode getExprWhile() {
		return exprWhile;
	}
	public ExprNode getExprReturn() {
		return exprReturn;
	}

	

}
