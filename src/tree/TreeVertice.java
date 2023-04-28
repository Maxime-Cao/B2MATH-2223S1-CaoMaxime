package tree;

import java.util.Arrays;

class TreeVertice implements Comparable<TreeVertice> {
	private final char verticeValue;
	private static final int MAX_CHILDREN = 28;
    private TreeVertice[] children = new TreeVertice[0];
    private boolean isEndWord = false;
    
    public TreeVertice(char verticeValue) {
    	this.verticeValue = verticeValue;
    }
    
    public char getVerticeValue() {
    	return verticeValue;
    }

    public TreeVertice getChild(char letter) {
    	for(var child : children) {
    		if(child.getVerticeValue() == letter) {
    			return child;
    		}
    	}
		return null;
	}
	
	public TreeVertice[] getChildren() {
		return children;
	}
	
	public void addChild(char letter,TreeVertice vertice) {
		if(children.length < MAX_CHILDREN) {
			if(getChild(letter) == null) {
				int nbrChildren = children.length;
				
				children = Arrays.copyOf(children,nbrChildren+1);
				children[nbrChildren] = vertice;
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
	public int compareTo(TreeVertice v) {
		return verticeValue - v.getVerticeValue();
	}
}