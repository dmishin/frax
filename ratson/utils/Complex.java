package ratson.utils;

public final class Complex {
	public double re, im;
	public Complex(){
		re=0;
		im=0;
	}
	public Complex(double r, double i){
		re=r;
		im=i;
	}
	public Complex(Complex c){
		set(c);
	}
	public void set(Complex c) {
		re=c.re;
		im=c.im;
	}
	public double abs(){
		return Math.sqrt(abs2());
	}
	public double abs2() {
		return re*re+im*im;
	}
	public Complex copy(){
		return new Complex(this);
	}
	public void set(double re, double im) {
		this.re=re;
		this.im=im;
	}
	
	@Override
	public String toString() {
		double r = abs();
		if (r==0)
			return "0";
		if (Math.abs(re)<r*1e-12)
			return String.format("%gi", im);
		if (Math.abs(im)<r*1e-12)
			return String.format("%g", re);
		if (re<0)
			return String.format("%g%gi", re, im);
		else
			return String.format("%g+%gi", re, im);
	}
	
}
