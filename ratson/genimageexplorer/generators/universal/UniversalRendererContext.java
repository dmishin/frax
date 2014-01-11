package ratson.genimageexplorer.generators.universal;

import ratson.genimageexplorer.generators.Function;


public class UniversalRendererContext extends Function {
	Parser parser = new Parser();
	ExprNode init, repeat, condition, finish;
	ExprNode finishUnreached;
	VarNode varY=new VarNode("y");
	VarNode varX=new VarNode("x");
	VarNode varIter=new VarNode("I");
	UniversalGenerator factory;

	private boolean isScriptOK;
	
	public boolean isParsedOK(){
		return isScriptOK;
	}

	public UniversalRendererContext(UniversalGenerator factory){
		this.factory = factory;
	}

	public void setFormula(String formula) throws ScriptSyntaxException{
		isScriptOK = false;
		
		parser.vars.clear();
		parser.vars.put(varX.getName(), varX);
		parser.vars.put(varY.getName(), varY);
		parser.vars.put(varIter.getName(), varIter);

		ExprNode nd= parser.parse(formula);
		if (!(nd instanceof FractalNode))
			throw new ScriptSyntaxException("Top level node must be 'fractal'");
		FractalNode f=(FractalNode) nd;
		
		init=f.getExprInit();
		if (init==null) init=new ConstNode(0);//empty op
		repeat=f.getExprIterate();
		if (repeat==null) throw new ScriptSyntaxException("'iterate' block not specified");
		condition=f.getExprWhile();
		if (condition==null) throw new ScriptSyntaxException("'while' block not specified");
		finish=f.getExprReturn();
		if (finish==null) finish=parser.getVariable("I");
		finishUnreached=f.getExprInfReturn();
		if (finishUnreached==null) finishUnreached=new ConstNode(-1);
		
		isScriptOK = true;
	}

	@Override
	public float evaluate(double x, double y) {
		
		if (!isParsedOK())
			return -1;
		
		varX.setValue(x);
		varY.setValue(y);
		
		
		init.eval();
		int iter=0;
		do {
			varIter.setValue(iter);
			repeat.eval();
			iter++;
		} while (condition.eval()>0 && iter<factory.maxIter);
		
		if (iter<factory.maxIter)
			return (float) finish.eval();
		else
			return (float) finishUnreached.eval();
	}
}
