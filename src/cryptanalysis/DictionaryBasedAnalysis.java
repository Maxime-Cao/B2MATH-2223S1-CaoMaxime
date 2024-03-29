package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tree.LexicographicTree;

/**
 * This class allows you to decrypt a text by finding the substitution alphabet used to encrypt it. It can also be used to apply a substitution alphabet to a text
 * @author Maxime Cao
 *
 */
public class DictionaryBasedAnalysis {
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	
	private final String cryptogram;
	private final LexicographicTree dictionnary;
	private List<String> currentCompatibleWords;
	private int currentCompatibleWordsSize;


	/**
	 * This constructor initializes an object of the DictionaryBasedAnalysis class based on a starting cryptogram (the ciphertext) and a dictionary required for decryption
	 * @param cryptogram The starting cryptogram (ciphertext)
	 * @param dict The dictionary
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		
		if(dict ==  null || cryptogram == null || cryptogram.isEmpty()) {
			throw new IllegalArgumentException("Please provide correct cryptogram and dictionnary");
		}
		
		this.cryptogram = cryptogram;
		this.dictionnary = dict;
		this.currentCompatibleWords = new ArrayList<>();
		this.currentCompatibleWordsSize = 0;
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an approximated decoding alphabet.
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		alphabet = alphabet.toUpperCase();
		if(!isCorrectAlphabet(alphabet)) {
			throw new IllegalArgumentException("Please provide correct text and correct alphabet");
		}
		// Extract words of more than two letters from the text
		List<String> wordsExtracted = extractWords(cryptogram);
		
		if(!wordsExtracted.isEmpty()) {
			List<String> currentWords = new ArrayList<>(wordsExtracted);
			String currentExtractedWord;
			
			Set<String> alreadyConsultedWords = new HashSet<>();
			Set<String> alphabetConsulted = new HashSet<>();
			
			//If the alphabet is different from the letters A to Z in order, we apply a first substitution to the word list
			if(!alphabet.equals(LETTERS)) {
				removeValidWords(currentWords, alphabet);
			}
			
			while(!currentWords.isEmpty()) {
				
				currentExtractedWord  = currentWords.get(0);
				
				if(!alreadyConsultedWords.contains(currentExtractedWord)) {
					
								
					String newAlphabet = "";
					
					String compatibleWord = getCompatibleWord(currentExtractedWord);
					
					if(!compatibleWord.isEmpty()) {
						newAlphabet = updateAlphabet(alphabet,applySubstitution(currentExtractedWord, alphabet), compatibleWord);
						if(!alphabetConsulted.contains(newAlphabet)) {
							alphabetConsulted.add(newAlphabet);
							//I initialize a new list that includes all the words from the beginning and, by applying a substitution with the new alphabet to each of the words, I see if I obtain a list that contains less invalid words than the currentWords list. If so, currentWords becomes newWords
							var newWords = new ArrayList<>(wordsExtracted);
							removeValidWords(newWords,newAlphabet);
							if(newWords.size() < currentWords.size()) {
								alphabet = newAlphabet;
								currentWords = newWords;
							}
						}
					}
					
					alreadyConsultedWords.add(currentExtractedWord);
				
				}
				
				currentWords.remove(currentExtractedWord);
			}
		}
		return alphabet;
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * @param text A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		if(!isCorrectAlphabet(alphabet)) {
			throw new IllegalArgumentException("Please provide correct text and correct alphabet");
		}
		
		StringBuilder substituedWord = new StringBuilder("");
		text = text.toUpperCase();
		alphabet = alphabet.toUpperCase();
		
		for(int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);
			int posLetterInAlphabet = LETTERS.indexOf(currentChar);
			
			if(posLetterInAlphabet != -1) {
				substituedWord.append(alphabet.charAt(posLetterInAlphabet));
			} else if(currentChar == '\n' || currentChar == ' ') {
				substituedWord.append(currentChar);
			}
		}
		return substituedWord.toString();
	}
	
	/*
	 * PRIVATE METHODS
	 */
	/**
	 * Compares two substitution alphabets.
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	/**
	 * Load the text file pointed to by pathname into a String.
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Extracts all words (uninterrupted sequences of letters) from a text
	 * @param text The text from which the words are extracted
	 * @return A list of Strings containing the words extracted from the text
	 */
	private List<String> extractWords(String text) {	
		String[] splittedText = text.split("\\s+");
		List<String> words = new ArrayList<>();
		
		for(String word : splittedText) {
			if(!words.contains(word) && word.length() > 2) {
				words.add(word);
			}
		}
	    
	    Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String word1, String word2) {
                return Integer.compare(word2.length(), word1.length());
            }
        });

	    return words;
	}
	
	/**
	 * Delete from a list of words all the words that are valid (present in the dictionary) after applying a substitution alphabet
	 * @param words The list of words
	 * @param alphabet The substitution alphabet
	 */
	private void removeValidWords(List<String> words,String alphabet) {
		words.removeIf(word -> dictionnary.containsWord(applySubstitution(word, alphabet).toLowerCase()));
	}
	
	/**
	 * Finds a word in the dictionary that is compatible with our cipher word
	 * @param cryptogram The cipher word
	 * @return A compatible word if any, otherwise an empty string
	 */
	private String getCompatibleWord(String cryptogram) {
		
		int cryptogramLength = cryptogram.length();

	    if (currentCompatibleWordsSize != cryptogramLength) {
	        currentCompatibleWords = dictionnary.getWordsOfLength(cryptogramLength);
	        currentCompatibleWordsSize = cryptogramLength;
	    }

	    var cryptoSeq = getWordSequence(cryptogram);

		for(var currentWord : currentCompatibleWords) {
			currentWord = currentWord.replaceAll("[^a-z]", "");
			if(Arrays.equals(cryptoSeq,getWordSequence(currentWord))) {
				return currentWord.toUpperCase();
			}
		}
		
		return "";
	}
	
	/**
	 * Provides the sequence of letters in a word (letter frequency and order of appearance)
	 * @param word The word from which to extract the sequence of letters
	 * @return An array containing the sequence of letters in the word
	 */
	private Object[] getWordSequence(String word) {
		  Map<Character,String> seqMap = new LinkedHashMap<Character,String>();
		    
		    for(int i = 0; i < word.length(); i++) {
		        char currentCharacter = word.charAt(i);
		        if(seqMap.containsKey(currentCharacter)) {
		            String currentValue = seqMap.get(currentCharacter);
		            seqMap.put(currentCharacter,String.format("%s%d",currentValue,i));
		        } else {
		        	seqMap.put(currentCharacter,String.format("%d",i));
		        }
		    }
		    return seqMap.values().toArray();
	}
	
	/**
	 * Update an alphabet based on a cipher word and a candidate word
	 * @param alphabet Starting alphabet
	 * @param cryptogram Cipher word
	 * @param candidateWord Candidate word
	 * @return The updated alphabet
	 */
	private String updateAlphabet(String alphabet,String cryptogram,String candidateWord) {
		Map<Character,Character> substitutions = new HashMap<>();
		StringBuilder newAlphabet = new StringBuilder(alphabet);
		
		for(int i = 0; i < alphabet.length(); i++) {
			char currentLetter = alphabet.charAt(i);
			int indexLetterInCrypto = cryptogram.indexOf(currentLetter);
			if(indexLetterInCrypto == -1) {
				substitutions.put(currentLetter, currentLetter);
			} else {
				substitutions.put(currentLetter,candidateWord.charAt(indexLetterInCrypto));
			}
		}
		
		for(var currentSubstitution : substitutions.entrySet()) {
			char currentKey = currentSubstitution.getKey();
			char currentValue = currentSubstitution.getValue();
			// For example, if key = E and value = E
			if(currentKey == currentValue) {
				char newValue = currentValue;
				while(newValue != '\0') {
					newValue = getKeyWithLetterValue(newValue, substitutions);
					
					if(newValue != '\0') {
						currentValue = newValue;
					}
				}
				substitutions.put(currentKey,currentValue);
			}
			if(currentKey != currentValue) {
				newAlphabet.setCharAt(alphabet.indexOf(currentKey),currentValue);
			}
		}
				
		return newAlphabet.toString();
	}
	
	/**
	 * Find a substitution in a character map where the value is equal to a given letter
	 * @param letter The letter to search for
	 * @param substitutions The substitution map
	 * @return The letter that points to the letter you're looking for, or \0 if no match was found
	 */
	private char getKeyWithLetterValue(char letter,Map<Character,Character> substitutions) {
		for(var currentSubstitution : substitutions.entrySet()) {
			if(currentSubstitution.getKey() != letter && currentSubstitution.getValue() == letter) {
				return currentSubstitution.getKey();
			}
		}
		return '\0';
	}
	
	/**
	 * Determines whether an alphabet is correct
	 * @param alphabet The alphabet to check
	 * @return True if the alphabet is valid, false otherwise
	 */
	private static boolean isCorrectAlphabet(String alphabet) {
		if(alphabet.length() != 26) {
			return false;
		}
		
		List<Character> letters = new ArrayList<Character>();
		
		for(int i = 0; i < alphabet.length(); i++) {
			char currentLetter = alphabet.charAt(i);
			
			if(!letters.contains(currentLetter) && currentLetter >= 65 && currentLetter <= 90) {
				letters.add(currentLetter);
			} else {
				return false;
			}
		}
		
		return true;
	}
	 
    /*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();
		
		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 *  Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		String startAlphabet = LETTERS;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		long startTime = System.currentTimeMillis();
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		System.out.println("Analysis duration : " + (System.currentTimeMillis() - startTime) / 1000.0);
		
		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();
		
		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}
