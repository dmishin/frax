package ratson.genimageexplorer.generators.universal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.sun.org.apache.xpath.internal.operations.Plus;

import ratson.genimageexplorer.ObservationArea;
import ratson.genimageexplorer.generators.AbstractGenerator;
import ratson.genimageexplorer.generators.RenderingContext;
import ratson.genimageexplorer.generators.janino.ScriptReference;
import ratson.genimageexplorer.gui.dialogs.EditScriptDialog;
import ratson.utils.FloatMatrix;

public class UniversalGenerator extends AbstractGenerator {


	private int maxIter;


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

	protected void finishRendering(ObservationArea area, FloatMatrix image, RenderingContext renderContext) {
		//do nothing
	}



	public float renderPoint(double x, double y, RenderingContext renderContrxt) {
		UniversalRendererContext rc =(UniversalRendererContext)renderContrxt;
		
		if (!rc.isParsedOK())
			return -1;
		
		rc.varX.setValue(x);
		rc.varY.setValue(y);
		
		
		rc.init.eval();
		int iter=0;
		do {
			rc.varIter.setValue(iter);
			rc.repeat.eval();
			iter++;
		} while (rc.condition.eval()>0 && iter<maxIter);
		
		if (iter<maxIter)
			return (float) rc.finish.eval();
		else
			return (float) rc.finishUnreached.eval();
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
	

	protected RenderingContext prepareRendering(ObservationArea area) {
		UniversalRendererContext context = new UniversalRendererContext();
		try {
			context.setFormula(script.getScript());
		} catch (ScriptSyntaxException e) {
			System.err.println("Parsing failed");
		}
		return context;
	}
}
