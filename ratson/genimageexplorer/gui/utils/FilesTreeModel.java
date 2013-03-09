package ratson.genimageexplorer.gui.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FilesTreeModel implements TreeModel {
	private TreeNode rootNode;
	private FileFilter filter = null;
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public void fireTreeChanged(){
		for (TreeModelListener lst : listeners) {
			//lst.treeStructureChanged(new TreeModelEvent());
		}
		rootNode.reset();
	}
	
	public FilesTreeModel(String baseDir, FileFilter filter){
		rootNode = new TreeNode(new File(baseDir), null);
		this.filter=filter;
	}

	public FilesTreeModel(String baseDir, final String extension){
		rootNode = new TreeNode(new File(baseDir), null);

		filter = new FileFilter(){
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String fname = f.getName();
				int idxDot = fname.lastIndexOf('.');
				if (idxDot == -1)
					return false;
				String ext = fname.substring(idxDot +1 );				
				return ext.compareToIgnoreCase(extension) == 0;
			}
		};
	}
	
	public class TreeNode{
		@Override
		public String toString() {
			if (isLeaf()){
				return file.getName();
			}else{
				return "["+file.getName()+"]("+getNumChildren()+")";
			}
		}
		
		public TreeNode(File f, TreeNode parent){
			file = f;
			this.parent = parent;
			isUpdated = false;
		}
		public void reset(){
			isUpdated = false;
			children = null;
		}
		private boolean isUpdated;
		
		private File file;
		private TreeNode parent;
		
		private ArrayList<TreeNode> children=null;
		public boolean isLeaf(){
			updateChildren();
			return children == null;
		}
		public int getNumChildren(){
			updateChildren();
			if (children == null)
				return 0;
			else
				return children.size();
		}
		public TreeNode getChild(int idx){
			updateChildren();
			return children.get(idx);
		}
		private void updateChildren(){
			if (isUpdated)
				return;
			if (!file.isDirectory()){
				children = null; 
			}else{
				File[] childFiles = file.listFiles();
				children = new ArrayList<TreeNode>();
				
				for (int i = 0; i < childFiles.length; i++) {
					if (filter != null && filter.accept(childFiles[i]))
					children.add(new TreeNode(childFiles[i], this));
				}
			}
			isUpdated = true;
		}

		public File getFile() {
			return file;
		}
	}
	
	public void addTreeModelListener(TreeModelListener l) {	
		listeners.add(l);
	}

	public Object getChild(Object o, int idx) {
		TreeNode node = (TreeNode)o;
		if (idx >=0 && idx < node.getNumChildren()){
			return node.getChild(idx);
		}else{
			return null;
		}
	}

	public int getChildCount(Object o) {
		TreeNode node = (TreeNode)o;
		return node.getNumChildren();
	}

	public int getIndexOfChild(Object p, Object c) {
		TreeNode parent = (TreeNode)p;
		TreeNode child = (TreeNode)c;
		for (int i =0; i< parent.getNumChildren(); ++i){
			if (parent.getChild(i)==child){
				return i;
			}
		}
		return -1;
	}

	public Object getRoot() {
		return rootNode;
	}

	public boolean isLeaf(Object o) {
		return ((TreeNode)o).isLeaf();
	}

	public void removeTreeModelListener(TreeModelListener arg0) {
		listeners.remove(arg0);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
