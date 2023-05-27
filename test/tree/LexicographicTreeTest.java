package tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* ---------------------------------------------------------------- */
/*
 * Constructor
 * @author Lawal Benjamin (merci à toi)
 */
public class LexicographicTreeTest {
	private static final String[] WORDS = new String[] { "aide", "as", "au", "aux", "bu", "bus", "but", "et", "ete" };
	private static final LexicographicTree DICT = new LexicographicTree();

	private LexicographicTree tree;

	@BeforeAll
	private static void initTestDictionary() {
		for (int i = 0; i < WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}

	@BeforeEach
	private void setUp() {
		this.tree = new LexicographicTree();
	}

	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void constructor_File() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
		assertNotNull(dict);
		assertEquals(327956, dict.size());
	}

	@Test
	void constructor_FileNotFound() {
		LexicographicTree dict = new LexicographicTree("mots/aaaaaaaaaaaaaaaaaaaaaaaaaa.txt");
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i = 0; i < WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}

	@Test
	void containsWord_General() {
		initTestDictionary();
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] { "", "aid", "ai", " ", "mot", "e" }) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}

	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());
		assertEquals(0, DICT.getWords("x").size());
		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] { "bu", "bus", "but" }, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, DICT.getWordsOfLength(3).toArray());
	}

	// -------------------------------------------------------------------------------------------------------------

	// region insertWord
	@Test
	void insertWordsWithThreeDifferentStartWord() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(4, tree.size());
		List<String> words = tree.getWords("");
		assertEquals(4, words.size());
		assertTrue(words.contains("test"));
		assertTrue(words.contains("soda"));
		assertTrue(words.contains("sodonium"));
		assertTrue(words.contains("coca"));
	}

	@Test
	void insertNullWord() {
		assertThrows(NullPointerException.class, () -> tree.insertWord(null));
	}

	@Test
	void insertWordWithTab() {
		tree.insertWord("						");
		assertEquals(0, tree.size());
	}

	@Test
	void insertWordWithSpaceBetweenTwoPartOfWord() {
		tree.insertWord("test soda");
		tree.insertWord("test soda aindqsld _ lqnsdlksqnd");
		assertEquals(2, tree.size());
	}

	@Test
	void insertWordWithUnauthorizedCharacter() {
		tree.insertWord("testé");
		tree.insertWord("testà");
		tree.insertWord("test3");
		tree.insertWord("test=:;µ$wù$ù$ù$^)àç!è§'");
		assertEquals(2, tree.size());
	}

	@Test
	void insertTwoSameWord() {
		tree.insertWord("test");
		tree.insertWord("test");
		assertEquals(1, tree.size());
	}

	@Test
	void insertWordWithCaracterePlus() {
		tree.insertWord("test+");
		assertEquals(1, tree.size());

	}

	@Test
	void insertWord1500Words() {
		add1500WordsInTree(tree);
		assertEquals(1500, tree.size());
		contains1500WordUtils(tree);
	}
//endregion

	// region getSize
	@Test
	void getSizeEmptyTree() {
		assertEquals(0, tree.size());
	}

	@Test
	void getSizeTree() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(4, tree.size());
	}

	@Test
	void getSize1500Words() {
		add1500WordsInTree(tree);
		assertEquals(1500, tree.size());
	}
//endregion

	// region containsWord

	@Test
	void containsEmptyTree() {
		assertFalse(tree.containsWord("test"));
	}

	@Test
	void containsWord() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertTrue(tree.containsWord("soda"));
		assertFalse(tree.containsWord("sod"));
		assertTrue(tree.containsWord("sodonium"));
	}

	@Test
	void containsWord1500Words() {
		add1500WordsInTree(tree);
		assertTrue(contains1500WordUtils(tree));
	}

	@Test
	void containPrefixButIsNotAWord() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertFalse(tree.containsWord("sod"));
		assertFalse(tree.containsWord("co"));
	}

	@Test
	void containWordWithEmptyString() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertFalse(tree.containsWord(""));
	}

	@Test
	void containWordWithNull() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertThrows(NullPointerException.class, () -> tree.containsWord(null));
	}
	// endregion

	// region getWords
	@Test
	void getWordsEmptyTree() {
		assertEquals(0, tree.getWords("test").size());
	}

	@Test
	void getWords() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(2, tree.getWords("sod").size());
		assertEquals(1, tree.getWords("coca").size());
		assertEquals(1, tree.getWords("coc").size());
		assertEquals(0, tree.getWords("qo").size());
	}

	@Test
	void getWords1500Words() {
		add1500WordsInTree(tree);
		assertEquals(1500, tree.getWords("").size());
		assertTrue(contains1500WordUtils(tree));
	}

	@Test
	void getWordsWithNull() {
		assertThrows(NullPointerException.class, () -> tree.getWords(null));
	}

	@Test
	void getNonexistentWords() {
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWords("qo").size());
		assertEquals(0, tree.getWords("sodas").size());
	}

	@Test
	void getNonexistentWordMoreShorterThanWordInTree() {
		tree.insertWord("tests");
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(2, tree.getWords("so").size());
		assertEquals(2, tree.getWords("test").size());
	}

	@Test
	void getWordsWithEmptyString() {
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(5, tree.getWords("").size());
		String[] words = { "coca", "soda", "sodonium", "test", "tests" };
		assertArrayEquals(words, tree.getWords("").toArray());
	}
//endregion

	// region getWordsOfLength
	@Test
	void getWordsOfLengthEmptyTree() {
		assertEquals(0, tree.getWordsOfLength(4).size());
	}

	@Test
	void getWordsOfLength() {
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(3, tree.getWordsOfLength(4).size());
		assertEquals(1, tree.getWordsOfLength(8).size());
		assertEquals(0, tree.getWordsOfLength(3).size());
	}

	@Test
	void getWordOfNullLength() {
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWordsOfLength(0).size());
		assertEquals(new ArrayList<>(), tree.getWordsOfLength(0));
	}

	@Test
	void getWordsOfLengthGreaterThanWordInTree() {
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWordsOfLength(100000000).size());
		assertEquals(new ArrayList<>(), tree.getWordsOfLength(100000000));
	}

	@Test
	void getWordsOfNegativeLength() {
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWordsOfLength(-10).size());
		assertEquals(new ArrayList<>(), tree.getWordsOfLength(-10));
	}

	// endregion

	// region Utils methods
	private void add1500WordsInTree(LexicographicTree tree2) {
		String path = "test/resources/dictionnaire_FR_sans_accents.txt";
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				tree2.insertWord(line);
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture du fichier : " + e.getMessage());
		}
	}

	private boolean contains1500WordUtils(LexicographicTree tree2) {
		boolean result = true;
		String path = "test/resources/dictionnaire_FR_sans_accents.txt";
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!tree2.containsWord(line)) {
					result = false;
				}
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture du fichier : " + e.getMessage());
		}
		return result;
	}

	// endregion

}