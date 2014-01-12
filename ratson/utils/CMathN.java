package ratson.utils;

public final class CMathN {
	public static Complex sin(Complex x){
		Complex y = new Complex();
		CMath.sin(x, y);
		return y;
	}
	public static Complex cos(Complex x){
		Complex y = new Complex();
		CMath.cos(x, y);
		return y;
	}
	public static Complex exp(Complex x){
		Complex y = new Complex();
		CMath.exp(x, y);
		return y;
	}
	public static Complex log(Complex x){
		Complex y = new Complex();
		CMath.log(x, y);
		return y;
	}
	public static Complex conj(Complex x){
		return new Complex(x.re, -x.im);
	}

	public static Complex sqrt(Complex x){
		Complex y = new Complex();
		CMath.sqrt(x, y);
		return y;
	}
	public static Complex sqr(Complex x){
		Complex y = new Complex();
		CMath.sqr(x, y);
		return y;
	}
	public static Complex powc(Complex x, Complex a){
		Complex y = new Complex();
		CMath.pow(x, a, y);
		return y;
	}
	public static Complex powd(Complex x, double a){
		Complex y = new Complex();
		CMath.powd(x, a, y);
		return y;
	}

	public static Complex mul(Complex a, Complex b){
		Complex y = new Complex();
		CMath.mul(a,b,y);
		return y;
	}
	public static Complex mul(Complex a, double b){
		return new Complex(a.re*b, a.im*b);
	}
	public static Complex div(Complex a, Complex b){
		Complex y = new Complex();
		CMath.div(a,b,y);
		return y;
	}
	public static Complex inv(Complex x){
		Complex y = new Complex();
		CMath.inv(x,y);
		return y;
	}
	public static Complex add(Complex a, Complex b){
		Complex y = new Complex();
		CMath.add(a,b,y);
		return y;
	}
	public static Complex sub(Complex a, Complex b){
		Complex y = new Complex();
		CMath.sub(a,b,y);
		return y;
	}
}
