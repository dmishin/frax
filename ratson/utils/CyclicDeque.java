package ratson.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.lang.Iterable;
/**A fast container of limited capacity*/ 
public class CyclicDeque <E> implements Iterable<E>{
    private E[] data;
    private int actualSize;
    @SuppressWarnings("unchecked")
	public CyclicDeque (int cap){
        data = (E[])(new Object[cap]);
        nextPos = 0;
        actualSize = 0;
    }
    private int nextPos;
    
    /**Puts an element to  the container.
     * 
     * @param e value to put
     * @return value, pushed out from container (null isf copntainer was not full)
     */
    public E push(E e){
    	E rval = data[nextPos]; 
        data[nextPos]=e;
        nextPos += 1;
        if (nextPos >= data.length){
            nextPos = 0;
        }
        if (actualSize != data.length){
            actualSize+=1;
        }
        return rval;
    }
    /**remove all values from array*/
    public void clear(){
    	nextPos = 0;
    	Arrays.fill(data, null);
    	actualSize = 0;
    }
    /**Get value by index. 0 is the last one*/
    public E get(int idx){
        int i = nextPos - 1 - idx;
        if (i<0)
            i += data.length;
        return data[i];
    }
    public int size(){
        return actualSize;
    }
    public int getCapacity(){
    	return data.length;
    }
    public boolean isFull(){
    	return data.length == actualSize;
    }
    public static void main(String[] args){
    	CyclicDeque<Integer> d = new CyclicDeque<Integer>(13);
        for (int i =0;i<100;++i){
            d.push(i);
        }
        d.push(10);
        d.push(20);
        d.push(30);
        for (int i: d){
            System.out.println(i);
        }
    }
    private class DeqIter implements Iterator<E>{
        private int curPos = 0;
        public boolean hasNext(){
            return curPos < actualSize;
        }
        public E next(){
            E rval = get(curPos);
            curPos+=1;
            return rval;
        }
        public void remove(){
            throw new RuntimeException("Remove not supported");
        }
    };
    public Iterator<E> iterator(){
        return new DeqIter();
    }
    public E getHead(){
    	return get(0);
    }
    public E getTail(){
    	return get(actualSize - 1);
    }
}