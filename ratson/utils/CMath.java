package ratson.utils;

public final class CMath {
	private CMath(){};
	
	/**c=a*b*/
	public static void mul(Complex a, Complex b, Complex c){
		double xx=a.re*b.re-a.im*b.im;
		c.im=a.re*b.im+a.im*b.re;
		c.re=xx;
	}
	/**c=a/b */
	public static void div(Complex a, Complex b, Complex c){
		double k=1.0/b.abs2();
		double xx=a.re*b.re+a.im*b.im;
		c.im=(-a.re*b.im+a.im*b.re)*k;
		c.re=xx*k;
	}
	public static void add(Complex a, Complex b, Complex c){
		c.re=a.re+b.re;
		c.im=a.im+b.im;
	}
	public static void add(Complex a1, Complex a2, Complex a3, Complex c){
		c.re=a1.re+a2.re+a3.re;
		c.im=a1.im+a2.im+a3.im;
	}
	public static void inv(Complex a, Complex b){
		double k=1.0/a.abs2();
		b.re=   a.re*k;
		b.im=  -a.im*k;
	}
	public static void exp(Complex a, Complex b){
		double e=Math.exp(a.re);
		double a_im=a.im;
		b.im=Math.sin(a_im)*e;
		b.re=Math.cos(a_im)*e;
	}
	public static void log(Complex a, Complex b){
		double re=Math.log(a.abs());
		b.im=Math.atan2(a.im, a.re);;
		b.re=re;
	}
	public static void pow(Complex a, Complex b, Complex c){
		log(a,c);
		mul(c,b,c);
		exp(c,c);
	}
	public static void powd(Complex a, double b, Complex c){
		log(a,c);
		c.re*=b;
		c.im*=b;
		exp(c,c);
	}
	public static void sqr(Complex a, Complex b){
		b.re = a.re*a.re - a.im*a.im;
		b.im = 2*a.re*a.im;
	}

	public static void sqrt(Complex x , Complex y){
		double r = x.abs();
		y.re = Math.sqrt(0.5*(r+x.re));
		y.im = Math.sqrt(0.5*(r-x.re));
	}
	
	public static void conj(Complex a, Complex b){
		b.re=a.re;
		b.im=-a.im;
	}
	
	public static double abs(Complex a){
		return a.abs();
	}
	public static double abs2(Complex a){
		return a.abs2();
	}

	public static void sin(Complex x, Complex y){
		y.re = Math.sin(x.re)*Math.cosh(x.im);
		y.im = Math.cos(x.re)*Math.sinh(x.im);
	}
	public static void cos(Complex x, Complex y){
		y.re = Math.cos(x.re)*Math.cosh(x.im);
		y.im = -Math.sin(x.re)*Math.sinh(x.im);
	}
	
	public final Complex I = new Complex(0,1);
	public final Complex mI = new Complex(0,-1);
	public final static void sub(Complex a, Complex b, Complex y) {
		y.re = a.re - b.re;
		y.im = a.im - b.im; 
	}
	
}
