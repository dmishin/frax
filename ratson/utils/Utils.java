package ratson.utils;

public class Utils {
	public final static double sqr(double x){
		return x*x;
	}
	public final static int sqr(int x){
		return x*x;
	}
	public final static float sqr(float x){
		return x*x;
	}

	public final static long sqr(long x){
		return x*x;
	}

	public static void clear(double[] arr){
		for (int i=0;i<arr.length;++i)
			arr[i]=0.0;
	}
	//////////Cube///////////////////////
	public final static double cube(double x){
		return x*x*x;
	}
	public final static float cube(float  x){
		return x*x*x;
	}
	public final static int cube(int x){
		return x*x*x;
	}
	public final static long cube(long x){
		return x*x*x;
	}

	/**modulo*/
	public static double mod(double a, double b) {
		double d=a/b;
		return (d-Math.floor(d))*b;
	}
	public static float mod(float a, float b) {
		double d=a/b;
		return (float) ((d-Math.floor(d))*b);
	}
	
	public static int mod(int a,int b){
		if (a>=0)
			return a%b;
		else
			return b+a%b;
	}
	public static long mod(long a,long b){
		if (a>=0)
			return a%b;
		else
			return b+a%b;
	}
	public static float frac(float f) {
		return (float) (f-Math.floor(f));
	}
	public static double frac(double f) {
		return f-Math.floor(f);
	}
	
	public static double asinh(double x){
		return Math.log(Math.sqrt(x*x+1)+x);
	}
	public static double acosh(double x){
		return Math.log(Math.sqrt(x*x-1)+x);
	}
	
	public static double atanh(double x){
		return 0.5*Math.log(Math.sqrt((1+x)/(1-x)));
	}
	
	/**return value of x, limited from both sides*/
	public static double limit(double x, double low, double high){
		if (x<low)
			return low;
		else if(x>high){
			return high;
		}else{
			return x;
		}
	}
	/**return value of x, limited from both sides*/
	public static float limit(float x, float low, float high){
		if (x<low)
			return low;
		else if(x>high){
			return high;
		}else{
			return x;
		}
	}
	/**return value of x, limited from both sides*/
	public static int limit(int x, int low, int high){
		if (x<low)
			return low;
		else if(x>high){
			return high;
		}else{
			return x;
		}
	}
}
