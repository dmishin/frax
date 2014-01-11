package ratson.genimageexplorer.generators.universal;

import ratson.genimageexplorer.generators.Function;
import ratson.genimageexplorer.generators.FunctionFactory;
import ratson.genimageexplorer.generators.janino.ScriptReference;
import ratson.genimageexplorer.gui.dialogs.EditScriptDialog;

public class UniversalGenerator implements FunctionFactory {


	int maxIter;


	protected ScriptReference script;
	
	public int getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}
	public void editScript(){
		EditScriptDialog esd=new EditScriptDialog(null, true);
		esd.setScript(script);
		esd.setVisible(true);
		

	}
	public void setWholeFormula(String formula) {
		script.script = formula;
		script.changed = true;
	}
	

	public UniversalGenerator() {
		//setTestFormula2();
		setWholeFormula(
				"(fractal \n"+
				
				"  (init (;\n"+
				"    (let xn x) (let yn y) \n"+
				"   )) \n"+
				
				"  (while (- 4 (+ x2 y2) ) )\n"+
				
				"  (iterate (;\n"+
				"     (let x2 (* xn xn)) \n"+
				"     (let y2 (* yn yn)) \n"+
				"     (let yn (+ y (* 2 xn yn))) \n"+
				"     (let xn (+ x (- x2 y2)))\n"+
				"  )) \n"+
				
				"  (return I)\n"+
				
				"  (infreturn -1)\n"+
				")"
		);
		maxIter=100;
	}
	
	public Function get() {
		try {
			UniversalRendererContext func = new UniversalRendererContext(this);
			func.setFormula(script.getScript());
			return func;
		} catch (ScriptSyntaxException e) {
			System.err.println("Parsing failed");
			System.err.println(e);
		}
		return new Function() {
			@Override
			public float evaluate(double x, double y) {
				return 0;
			}
		}; 
	}
}
