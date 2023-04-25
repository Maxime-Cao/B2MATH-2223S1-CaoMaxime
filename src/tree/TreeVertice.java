package tree;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class TreeVertice {
    private final SortedMap<Character, TreeVertice> children = new TreeMap<>();
    private boolean isEndWord = false;

    public TreeVertice getChild(char letter) {
		return children.get(letter);
	}
	
	public Set<Entry<Character, TreeVertice>> getEntries() {
		return children.entrySet();
	}
	
	public void addChild(char letter,TreeVertice vertice) {
		if(letter != ' ' && vertice != null) {
			children.put(letter, vertice);
		}
	}

	public void setEndWord(boolean isEndWord) {
		this.isEndWord = isEndWord;
	}
	
	public boolean isEndWord() {
		return isEndWord;
	}
}