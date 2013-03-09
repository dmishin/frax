package ratson.genimageexplorer;

import java.util.LinkedList;

public class NavigationHistory {
	class HistoryItem{
		
	}
	private LinkedList<HistoryItem> history = new LinkedList<HistoryItem>();
	
	public void clear(){
		history.clear();
	}
	
}
