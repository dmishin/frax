package ratson.utils;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DoubleMatrix implements MatrixGenerator {
	private double[] data=null;
	private int width=0;
	private int height=0;

	public DoubleMatrix(){
		
	}
	public void fill(MatrixGenerator g){
		for(int y =0;y<getHeight(); ++y){
			for (int x = 0;x<getWidth();++x){
				set(y,x,g.get(y, x));
			}
		}
	}
	public DoubleMatrix( int h, int w){
		resize(h,w);
	}	

	/**Copy constructor*/
	public DoubleMatrix(DoubleMatrix a1) {
		set(a1);
	}
	public void resize(int h,int w) {
		
		int sz=w*h;
		if (sz<0) throw new ArrayIndexOutOfBoundsException("negative array size");
		if (sz!=0){
			if (data==null|| data.length!=sz)
				data=new double[sz];
		}else{
			data=null;
		}
		width=w;
		height=h;
	}
	public final int getWidth() {
		return width;
	}
	public final int getHeight() {
		return height;
	}
	public final double get( int y, int x){
		return data[x+y*width];
	}
	public final void set(int y, int x, double v){
		data[x+y*width]=v;
	}
	/** this=a
	 * 
	 * @param a new value
	 */
	public void set(DoubleMatrix a){
		resize(a.getHeight(), a.getWidth());
		for (int i = 0; i < data.length; i++) {
			data[i]=a.data[i];
		}
	}
	public DoubleMatrix copy(){
		DoubleMatrix cpy=new DoubleMatrix();
		cpy.set(this);
		return cpy;
	}
	
	/**Matrix multiplication
	 * 
	 * @param a !=this
	 * @param b !=this
	 * this=a*b
	 */
	public void mult(DoubleMatrix a,DoubleMatrix b){
		if (a.getWidth()!=b.getHeight())
			throw new ArrayIndexOutOfBoundsException(
					String.format("Matrices of incompatible size: [%dx%d]*[%dx%d]",
							a.getHeight(),a.getWidth(),b.getHeight(),b.getWidth()));
		resize(a.getHeight(), b.getWidth());
		
		for (int i=0; i<a.getHeight();++i){
			for (int j=0; j<b.getWidth();++j){
				double s=0;
				for (int k=0;k<a.getWidth();++k){
					s+=a.get(i,k)*b.get(k,j);
				}
				set(i,j,s);
			}	
		}
	}

	public void eye(int n){
		resize(n, n);
		if (n<=0) return;
		Arrays.fill(data, 0);
		for (int i=0; i<data.length;i+=width+1){
			data[i]=1;
		}
	}
	public void scale(double k){
		for (int i = 0; i < data.length; i++) {
			data[i]*=k;
		}
	}
	/**matrix division
	 * Simple, non-effective algorithm
	 * this:=a^-1*this
	 * */
	public void div(DoubleMatrix a){
		DoubleMatrix b=this;
		a=a.copy();
		if (!a.isSquare())
			throw new RuntimeException("Matrix mus be square");
		if (a.getHeight()!=b.getHeight())
			throw new RuntimeException("Matrix dimensions are not matching");
		
		int n=a.getHeight();
		for (int i=0;i<n;++i){
			//TODO Find maximal element in column instead of using first one
			double k=a.get(i,i);
			a.scaleRow(i, 1.0/k);
			b.scaleRow(i, 1.0/k);
			for (int j=0;j<n;++j){
				if (j!=i){
					double aji=a.get(j,i);
					a.lincombRows(j, i, -aji);
					b.lincombRows(j, i, -aji);
				}
			}
		}
		System.out.println(a);
		
	}
	/**this=a^-1*/
	public void setInv(DoubleMatrix a){
		eye(a.getHeight());
		div(a);
	}
	public DoubleMatrix inv(){
		DoubleMatrix m=new DoubleMatrix();
		m.setInv(this);
		return m;
	}
	public boolean isSquare() {
		return width==height;
	}

	private void scaleRow(int idx, double k){
		for (int j=0;j<width;++j)
			set(idx,j,get(idx,j)*k);
	}
	/** row(i1)=row(i1)+k*row(i2)*/
	private void lincombRows(int i1, int i2, double k){
		for (int j=0;j<width;++j)
			set(i1,j,get(i1,j)+get(i2,j)*k);
	}

	public void zeros(int n) {
		zeros(n,n);
	}

	private void zeros(int n, int n2) {
		resize(n,n2);
		if (data!=null)
			Arrays.fill(data, 0);
	}
	public String toString() {
		StringBuffer sbuf=new StringBuffer();
		DecimalFormat fmt=new DecimalFormat("#.###");
		for (int i=0;i<getHeight();++i){
			sbuf.append("[");
			for (int j=0;j<getWidth();++j){
				sbuf.append(fmt.format(get(i,j)));
				if (j!=getWidth()-1)
					sbuf.append(", ");
			}
			sbuf.append("]\n");
		}
		return sbuf.toString();
	}
	/**Fill array with uniform value*/
	public void fill(double value) {
		Arrays.fill(data, value);
	}
	/**M += k * A*/ 
	public DoubleMatrix addScale(DoubleMatrix A, double d) {
		if (getWidth() != A.getWidth() || getHeight() != A.getHeight()){
			throw new ArrayIndexOutOfBoundsException("Matrices must have same sizes");
		}
		for (int i =0;i<data.length;++i){
			data[i] += d * A.data[i];
		}
		return this;
	}
	
}

