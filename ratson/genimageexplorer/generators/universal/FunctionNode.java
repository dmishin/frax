package ratson.genimageexplorer.generators.universal;

public class FunctionNode extends UnaryNode {

	private String funcname;
	private UnaryFunctor functor;
	
	public FunctionNode(String funcname, ExprNode child) throws ScriptSyntaxException {
		super(child);
		this.funcname=funcname;
		if (funcname.equals("sin")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.sin(x);
				}
			};
			return;
		}
		if (funcname.equals("cos")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.cos(x);
				}
			};
			return;
		}
		if (funcname.equals("log")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.log(x);
				}
			};
			return;
		}
		if (funcname.equals("exp")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.exp(x);
				}
			};
			return;
		}
		if (funcname.equals("tan")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.tan(x);
				}
			};
			return;
		}
		if (funcname.equals("asin")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.asin(x);
				}
			};
			return;
		}
		if (funcname.equals("acos")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.acos(x);
				}
			};
			return;
		}
		if (funcname.equals("atan")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.atan(x);
				}
			};
			return;
		}
		if (funcname.equals("abs")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.abs(x);
				}
			};
			return;
		}
		if (funcname.equals("floor")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.floor(x);
				}
			};
			return;
		}
		if (funcname.equals("sinh")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.sinh(x);
				}
			};
			return;
		}
		if (funcname.equals("cosh")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.cosh(x);
				}
			};
			return;
		}
		if (funcname.equals("sqrt")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return Math.sqrt(x);
				}
			};
			return;
		}
		if (funcname.equals("sqr")){
			functor=new UnaryFunctor(){
				public double eval(double x){
					return x*x;
				}
			};
			return;
		}

		throw new ScriptSyntaxException("Unknown function name: "+funcname);




	}

	private FunctionNode(ExprNode child, String name, UnaryFunctor functor) {
		super(child);
		funcname=name;
		this.functor=functor;
	}


	public double eval() {
		return functor.eval(child.eval());
	}

	private static final String[] funcNames={"sin","cos","log","exp","sinh","cosh","asin","acos","abs"};
	
	public static boolean isFunctionName(String name){
		for (int i = 0; i < funcNames.length; i++) {
			if (funcNames[i].compareTo(name)==0)
				return true;
		} 
		return false;
	}
}
