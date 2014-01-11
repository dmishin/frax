package ratson.genimageexplorer.generators;

public abstract class Function {
	abstract float evaluate(double x, double y);
	void evaluate(double[] x, double[] y, float[] f){
		if(x.length != y.length || x.length != f.length) throw new RuntimeException("Inconsistent sizes");
		for(int i=0;i<x.length;++i){
			f[i]=evaluate(x[i],y[i]);
		}
	}
}
