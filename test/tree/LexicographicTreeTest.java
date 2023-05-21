package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTest {
	private static final String FILE_PATH = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String[] WORDS = new String[] {"aide", "as", "au", "aux",
			"bu", "bus", "but", "et", "ete"};
	private static final LexicographicTree DICT = new LexicographicTree();

	@BeforeAll
	private static void initTestDictionary() {
		for (int i=0; i<WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}
	
	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i=0; i<WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}
	
	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] {"", "aid", "ai", "aides", "mot", "e"}) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}
	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());
		
		assertEquals(0, DICT.getWords("x").size());
		
		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] {"bu", "bus", "but"}, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] {"aux", "bus", "but", "ete"}, DICT.getWordsOfLength(3).toArray());
	}

	
	
	// My tests
	// Constructor
	@Test
	void constructorBadFile(){
		// Given
		LexicographicTree dicoBad;
		
		// When
		dicoBad = new LexicographicTree("nope");
		
		// Then
		assertEquals(0, dicoBad.size());
	}
	
	@Test
	void constructorEmptyFile() {
		// Given
		LexicographicTree dico;
		
		// When
		dico = new LexicographicTree("mots/empty.txt");
		
		// Then
		assertEquals(0, dico.size());
		
	}
	
	// InsertWord
	@Test
	void insertWordNormal() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		
		// When
		dict.insertWord(word);
		words.add(word);
				
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertWordNormalWithNumbers() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		String badWord = "hel15lo";
		
		// When
		dict.insertWord(badWord);
		words.add(word);
		
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertWordNormalWithSpecialCharacters() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		String badWord = "he^^ll$o";
		
		// When
		dict.insertWord(badWord);
		words.add(word);
		
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertEmptyWord() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		String word = "";
		
		// When
		dict.insertWord(word);
		
		// Then
		assertEquals(1, dict.size());
	}

	
	
	// GetWords
	@Test
	void getWordsOfNulLength() {
		assertEquals(0, DICT.getWordsOfLength(0).size());	
	}
	
	@Test
	void getWordsOfNegativeLength() {
		assertEquals(0, DICT.getWordsOfLength(-5).size());	
	}
	
	@Test
	void getWordsOfTooHighLength() {
		assertEquals(0, DICT.getWordsOfLength(35).size());
	}
	
	
	
	@Test
	void getWordsInAlphabeticalOrdrerByPrefix() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nope";
		String word4 = "oukilest";
		String word1 = "azerbaijan";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void getWordsInAlphabeticalOrdrerByLength() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nopel";
		String word4 = "oukil";
		String word1 = "azerb";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWordsOfLength(5));
	}
	
	
	@Test
	void getWordWithUppercaseAndAccents(){
		// Given
		LexicographicTree dict = new LexicographicTree(FILE_PATH);
		List<String> words = new ArrayList<>();
		
		// When
		words = dict.getWords("artIste");
		words = dict.getWords("téléphone");
		words = dict.getWords("héberGEMent");
		
		// Then
		assertEquals(0, words.size());
	}	
}
