package ratson.genimageexplorer.generators.universal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Parser {

	protected HashMap<String, VarNode> vars = new HashMap<String, VarNode>();

	public Parser() {
		super();
	}

	/**Parses inverse polish formula into expression tree
	 * (operator op1 op2 ...)
	 * var_name
	 * value
	 * @param ipFormula
	 * @return
	 * @throws ScriptSyntaxException 
	 */
	protected ExprNode parse(String ipFormula) throws ScriptSyntaxException {
		//is constant?
		ipFormula=ipFormula.trim();
		if (!(ipFormula.length()>=2 && ipFormula.charAt(0)=='(' &&
				ipFormula.charAt(ipFormula.length()-1)==')')){
			//if not braced expression
			if (isVariable(ipFormula)){
				return getVariable(ipFormula);
			}
			if (isNumeric(ipFormula)){
				return new ConstNode(Double.valueOf(ipFormula).doubleValue());
			}
			System.err.println(ipFormula);
			throw new ScriptSyntaxException("SyntaxError");
		}else{
			//braced expression
			//extract contents
			ipFormula=ipFormula.substring(1, ipFormula.length()-1);
			
			//split to tokens
			String[] tokens=splitString(ipFormula);
			
			if (tokens.length==0){
				System.err.println(ipFormula);
				throw new ScriptSyntaxException("Syntax error");
			}
			
			if (tokens[0].compareTo("+")==0){
				return new SumNode(parseChildrenTokens(tokens));
			}
			if (tokens[0].compareTo("-")==0){
				switch(tokens.length){
				case 2:
					return new MinusUnary(parse(tokens[1]));
				case 3:
					return new MinusNode2(parse(tokens[1]),parse(tokens[2]));
				default:
					System.err.println(ipFormula);
					throw new ScriptSyntaxException("Syntax error");
				}
			}
			if (tokens[0].compareTo("*")==0){
				return new MultNode(parseChildrenTokens(tokens));
			}
			if (tokens[0].compareTo("/")==0){
				switch(tokens.length){
				case 3:
					return new DivNode(parse(tokens[1]),parse(tokens[2]));
				default:
					System.err.println(ipFormula);
					throw new ScriptSyntaxException("Syntax error");
				}
			}
			if (tokens[0].compareTo("let")==0){
				switch(tokens.length){
				case 3:
					return new LetNode(parse(tokens[1]),parse(tokens[2]));
				default:
					System.err.println(ipFormula);
					throw new ScriptSyntaxException("Syntax error");
				}
			}
			if (tokens[0].compareTo("if")==0){
				switch(tokens.length){
				case 3:
					return new IfPositiveNode(parse(tokens[1]),parse(tokens[2]),parse(tokens[3]));
				default:
					System.err.println(ipFormula);
					throw new ScriptSyntaxException("Syntax error");
				}
			}
			if (tokens[0].compareTo(";")==0){
				return new SeqNode(parseChildrenTokens(tokens));
			}
			if (FunctionNode.isFunctionName(tokens[0])){
				if (tokens.length==2)
					return makeFuncNode(tokens[0], parse(tokens[1]));
				throw new ScriptSyntaxException("too many args to func");
			}
			if (tokens[0].equals("fractal")){
				return new FractalNode(parseChildrenTokens(tokens));
			}
			//unknown token? creating text node
			return new NamedNode(tokens[0], parseChildrenTokens(tokens) );
		}
		
	}

	private ExprNode[] parseChildrenTokens(String[] tokens)
			throws ScriptSyntaxException {
				ExprNode[] children=	new ExprNode[tokens.length-1];
				for(int i=0; i<children.length;++i){
					children[i]=parse(tokens[i+1]);
				}
				return children;
			}

	private String[] splitString(String ipFormula) throws ScriptSyntaxException {
		LinkedList<String> tokens = new LinkedList<String>();
		int braces=0;//level of braces
		
		//considering that there is always only one space
		int posBegin=0;
		int posEnd=0;
		
		while (posEnd<ipFormula.length()){
			if (isSpace(ipFormula.charAt(posEnd)) && braces==0){
				//space found
				String token=ipFormula.substring(posBegin,posEnd).trim();
				if (token.length()!=0)
					tokens.add(token);//adding only non-empty tokens
				posBegin=posEnd;//moving to the next token
			}
			if (ipFormula.charAt(posEnd)=='(')
				braces++;
			if (ipFormula.charAt(posEnd)==')')
				braces--;
			posEnd++;
		}
		String token=ipFormula.substring(posBegin,posEnd).trim();
		if (token.length()!=0)
			tokens.add(token);//adding only non-empty tokens
	
		//tokens parsed
		//checking braces consistency
		if (braces!=0){
			throw new ScriptSyntaxException("Braces are inconsistent: "+ipFormula);
		}
		
		String[] rval=new String[tokens.size()];
		int i=0;
		for (Iterator<String> iToken = tokens.iterator(); iToken.hasNext();++i) {
			rval[i] = iToken.next();
		}
		//returning array. temporary list is thrown away.
		return rval;
	}

	private boolean isSpace(char c) {
		switch (c){
		case ' ':
		case '\n':
		case '\r':
		case '\t':
			return true;
		}
		return false;
	}

	private UnaryNode makeFuncNode(String funcname, ExprNode child)
			throws ScriptSyntaxException {
				return new FunctionNode(funcname, child);
			}

	public ExprNode getVariableClone(String name) {
		return getVariable(name);
	}

	private boolean isNumeric(String text) {
		//TODO: quite bad...
		if (text.length()==0)
			return false;
		try{
			Double.valueOf(text).doubleValue();
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	protected VarNode getVariable(String name) {
		VarNode var = vars.get(name);
		if (var==null){
			//if no such variable yet
			var=new VarNode(name);
			vars.put(name, var);
			return var;
		}else{
			return var;
		}
	}

	private boolean isVariable(String text) {
		if (text.length()==0)
			return false;
		char c0=text.charAt(0);
		if  (!((c0>='a' && c0 <='z')||(c0>='A' && c0<='Z')||(c0=='_')))
			return false;
		for (int i=1; i<text.length();++i){
			char c=text.charAt(i);
			if  (!(
					(c>='a' && c <='z')||
					(c>='A' && c<='Z')||
					(c=='_') ||
					(c>='0' && c<='9')))
				return false;
		}
		return true;
	}

}