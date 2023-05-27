package tree;

import java.util.Arrays;

/**
 * This class can be used to represent a node in a lexicographic tree
 * @author Maxime Cao
 *
 */
class TreeVertex implements Comparable<TreeVertex> {
	private final char vertexValue;
	private static final int MAX_CHILDREN = 28;
    private TreeVertex[] children = null;
    private boolean isEndWord = false;
    
    /**
     * This constructor is used to build a node of a lexicographic tree based on a character
     * @param vertexValue The character
     */
    public TreeVertex(char vertexValue) {
    	this.vertexValue = vertexValue;
    }
    
    /**
     * Get the node value (its character)
     * @return The node value
     */
    public char getVertexValue() {
    	return vertexValue;
    }

    /**
     * Get a child node of the current node (if it exists) based on its associated character
     * @param letter Child node character
     * @return Child node (if any), otherwise null
     */
    public TreeVertex getChild(char letter) {
    	if(children != null) {
	    	for(var child : children) {
	    		if(child.getVertexValue() == letter) {
	    			return child;
	    		}
	    	}
    	}
		return null;
	}
	
    /**
     * Get an array of all the children of the current node
     * @return An array of all the children of the current node
     */
	public TreeVertex[] getChildren() {
		return children;
	}
	
	/**
	 * Adds a child node to the current node if it doesn't already exist
	 * @param letter  The character of the node to be added, used to determine whether the current node already has the node you want to add
	 * @param vertex The node to add to the current node
	 */
	public void addChild(char letter,TreeVertex vertex) {
		
		if(children == null) {
			children = new TreeVertex[0];
		}
		
		if(children.length < MAX_CHILDREN) {
			if(getChild(letter) == null) {
				int nbrChildren = children.length;
				
				children = Arrays.copyOf(children,nbrChildren+1);
				children[nbrChildren] = vertex;
				Arrays.sort(children);
			}
		}
	}

	/**
	 * Specifies whether the current node is a word node or not
	 * @param isEndWord True if the current node is a word node, false otherwise
	 */
	public void setEndWord(boolean isEndWord) {
		this.isEndWord = isEndWord;
	}
	
	/**
	 * Determines whether the current node is a word node or not
	 * @return True if the current node is a word node, false otherwise
	 */
	public boolean isEndWord() {
		return isEndWord;
	}

	/**
	 * Redefinition of the compareTo method, used to compare two nodes of a lexicographic tree
	 */
	@Override
	public int compareTo(TreeVertex v) {
		return vertexValue - v.getVertexValue();
	}
}