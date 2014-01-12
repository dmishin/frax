package ratson.utils;

import java.util.Arrays;

public final class IntMatrix {
	private int[] data=null;
	private int width=0, height=0;
	private int[] offsets=null;
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public int get(int x,int y){
		return data[offsets[y]+x];  
	}
	public void set(int x, int y, int v){
		data[offsets[y]+x] = v;
	}
	public void resize(int sx,int sy){
		int ns = sx*sy;
		if (data==null || ns!=data.length){
			if (ns>0)
				data=new int[ns];
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
	public IntMatrix(){
		
	}
	public IntMatrix(int szx, int szy){
		resize(szx, szy);
	}
	public int[] getData() {
		return data;
	}
}
