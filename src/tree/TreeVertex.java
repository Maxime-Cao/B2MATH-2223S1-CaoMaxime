package tree;

import java.util.Arrays;

class TreeVertex implements Comparable<TreeVertex> {
	private final char vertexValue;
	private static final int MAX_CHILDREN = 28;
    private TreeVertex[] children = new TreeVertex[0];
    private boolean isEndWord = false;
    
    public TreeVertex(char vertexValue) {
    	this.vertexValue = vertexValue;
    }
    
    public char getVertexValue() {
    	return vertexValue;
    }

    public TreeVertex getChild(char letter) {
    	for(var child : children) {
    		if(child.getVertexValue() == letter) {
    			return child;
    		}
    	}
		return null;
	}
	
	public TreeVertex[] getChildren() {
		return children;
	}
	
	public void addChild(char letter,TreeVertex vertex) {
		if(children.length < MAX_CHILDREN) {
			if(getChild(letter) == null) {
				int nbrChildren = children.length;
				
				children = Arrays.copyOf(children,nbrChildren+1);
				children[nbrChildren] = vertex;
				Arrays.sort(children);
			}
		}
	}

	public void setEndWord(boolean isEndWord) {
		this.isEndWord = isEndWord;
	}
	
	public boolean isEndWord() {
		return isEndWord;
	}

	@Override
	public int compareTo(TreeVertex v) {
		return vertexValue - v.getVertexValue();
	}
}