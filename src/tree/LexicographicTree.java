package tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LexicographicTree {
	
	private final TreeVertex root;
	private int treeSize;
	
	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		this(null);
	}
	
	/**
	 * Constructor : creates a lexicographic tree populated with words 
	 * @param filename A text file containing the words to be inserted in the tree 
	 */
	public LexicographicTree(String filename) {
		this.root = new TreeVertex(' ');
		treeSize = 0;
		if(filename != null) {
			initializeTree(filename);
		}
	}
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Returns the number of words present in the lexicographic tree.
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return treeSize;
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * @param word A word
	 */
	public void insertWord(String word) {
		boolean isNewWord = false;
		TreeVertex currentVertex = root;
		TreeVertex vertexFound;
		char currentCharacter;
		for(int i = 0; i < word.length();i++) {
			currentCharacter = word.charAt(i);
			if(isAcceptedCharacter(currentCharacter)) {
				vertexFound = currentVertex.getChild(currentCharacter);
				if(vertexFound == null) {
					vertexFound = new TreeVertex(currentCharacter);
					currentVertex.addChild(currentCharacter, vertexFound);
					isNewWord = true;
				}
				currentVertex = vertexFound;
			}
		}
		currentVertex.setEndWord(true);
		
		if(isNewWord) {
			treeSize++;
		}
	}
	/**
	 * Determines if a word is present in the lexicographic tree.
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		TreeVertex currentVertex = root;
		for(char currentCharacter : word.toCharArray()) {
			TreeVertex vertexFound = currentVertex.getChild(currentCharacter);
			if(vertexFound == null) {
				return false;
			}
			currentVertex = vertexFound;
		}
		return currentVertex.isEndWord();
	}
	
	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix.
	 * If 'prefix' is an empty string, all words are returned.
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		List<String> words = new ArrayList<>();
		TreeVertex currentVertex = root;
		if(!prefix.isEmpty()) {
			for(char currentChar : prefix.toCharArray()) {
				currentVertex = currentVertex.getChild(currentChar);
				if(currentVertex == null) {
					return words;
				}
			}
		}
		getAllWords(words,currentVertex,prefix);
		return words;
	}

	/**
	 * Returns an alphabetic list of all words of a given length.
	 * If 'length' is lower than or equal to zero, an empty list is returned.
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		List<String> words = new ArrayList<>();
		
		if(length > 0) {
			getAllWordsOfLength(words,root,"",length);
		}
		
		return words;
	}

	/*
	 * PRIVATE METHODS
	 */
	
	private void initializeTree(String filename) {
		try(Scanner scan = new Scanner(new File(filename))) {
			while(scan.hasNextLine()) {
				String word = scan.nextLine();
				insertWord(word);
			}
		} catch(FileNotFoundException ex) {
			System.out.println("Your file does not exist");
		}
		
	}
	
	private boolean isAcceptedCharacter(char characterToVerify) {
		return characterToVerify == 39 || characterToVerify == 45 || (characterToVerify >= 97 && characterToVerify <=122);
	}
	
	private void getAllWords(List<String> words,TreeVertex vertex,String prefix) {
		if(vertex.isEndWord()) {
			words.add(prefix);
		}
		for(var child : vertex.getChildren()) {
			getAllWords(words, child, prefix+child.getVertexValue());
		}
	}
	
	private void getAllWordsOfLength(List<String> words,TreeVertex vertex,String prefix,int length) {
		if(vertex.isEndWord() && prefix.length() == length) {
			words.add(prefix);
		} else {
			for(var child : vertex.getChildren()) {
				getAllWordsOfLength(words, child, prefix+child.getVertexValue(),length);			}
		}
	}
	
	/*
	 * TEST FUNCTIONS
	 */
		
	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char)('a' + (int)(number % radix)) + word;
			number = number / radix;
		} while(number != 0);
		return word;
	}
	
	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;
		
		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();
		
		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine();
				    boolean found = dico.containsWord(word);
				    if (!found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine() + "xx";
				    boolean found = dico.containsWord(word);
				    if (found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory()/MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory()/MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory()/MB);
			}
		}
	}
	
	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");
		
		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}
}