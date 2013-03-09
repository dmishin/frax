package ratson.genimageexplorer.generators;

import java.util.Comparator;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

public class NelderMeadContext extends RenderingContext {
	
	public class Slot{
		public double[] x=new double[2];
		public double f;
		void set( double x1, double y1){
			x[0]=x1;
			x[1]=y1;
		}

		void set( double[] x1 ){
			for (int i = 0; i < x1.length; i++) {
				x[i]=x1[i];
			}		
		}
		void set( double[] x1, double f1){
			f=f1;
			set( x1 );
		}
		@Override
		public String toString() {
			return String.format("[%f; %g](%g)", x[0],x[1],f);
		}
	}
	public Slot[] slots;
	public NelderMeadContext() {
		slots = new Slot[3];
		for (int i =0; i<3; ++i){
			slots[i] = new Slot();
		}
	}
	private Comparator<Slot> slotComparator = new Comparator<Slot>() {
		public int compare(Slot o1, Slot o2) {
			if (o1.f<o2.f)
				return -1;
			else
				return 1;
		}
	};
	public double[] xc=new double[2];
	public double[] xr=new double[2];
	public double[] xe=new double[2];
	public double[] xs=new double[2];
	public void sortSlots(){
		java.util.Arrays.sort(slots, slotComparator);
	}
}
