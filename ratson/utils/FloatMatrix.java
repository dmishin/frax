package ratson.utils;
import java.util.Arrays;

public final class FloatMatrix {
	private float[] data=null;
	private int width=0, height=0;
	private int[] offsets=null;
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public float get(int x,int y){
		return data[offsets[y]+x];  
	}
	public void set(int x, int y, float r){
		data[offsets[y]+x] = r;
	}
	public void resize(int sx,int sy){
		int ns = sx*sy;
		if (data==null || ns!=data.length){
			if (ns>0)
				data=new float[ns];
			if (ns==0)
				data=null;
			if (ns<0)
				throw new ArrayIndexOutOfBoundsException(ns);
			width=sx;
			height=sy;
		}
		if (offsets==null || offsets.length!=height)
			offsets=new int[height];
		
		for (int y=0;y<height;++y){
			offsets[y]=y*width;
		}
	}
	public void fill(int v){
		if (data!=null)
			Arrays.fill(data, v);
	}
	public FloatMatrix(){
		
	}
	public FloatMatrix(int szx, int szy){
		resize(szx, szy);
	}
	public float[] getData() {
		return data;
	}
}
