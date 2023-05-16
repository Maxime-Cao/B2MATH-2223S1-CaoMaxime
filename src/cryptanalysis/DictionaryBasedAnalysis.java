package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	
	private final String cryptogram;
	private final LexicographicTree dictionnary;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		if(dict ==  null || cryptogram == null || cryptogram.isEmpty()) {
			throw new IllegalArgumentException("Please provide correct cryptogram and dictionnary");
		}
		
		this.cryptogram = cryptogram;
		this.dictionnary = dict;
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
		// Vérifier que l'alphabet contient bien 26 lettres et ne présente pas de redondances
		TreeSet<String> wordsExtracted = extractWords(cryptogram);
		
		int startingNbrWords = wordsExtracted.size();
		int currentNbrWords = startingNbrWords;
		String newAlphabet = "";
		Map<Character,Character> map1 = new HashMap<>();
		Map<Character,Character> map2 = new HashMap<>();
				
		while(!wordsExtracted.isEmpty()) {
			
			String currentExtractedWord = wordsExtracted.first().toUpperCase();
			String compatibleWord = getCompatibleWord(currentExtractedWord);
						
			if(!compatibleWord.isEmpty()) {
				newAlphabet = updateAlphabet(alphabet, newAlphabet,map1,map2,currentExtractedWord, compatibleWord);			
				applyAlphabetOnWords(wordsExtracted,newAlphabet);
			} else {
				wordsExtracted.remove(currentExtractedWord);
			}
			
			removeValidWords(wordsExtracted);
			
			if(currentNbrWords - wordsExtracted.size() > 1) {
				alphabet = newAlphabet;
			}
			
			currentNbrWords = wordsExtracted.size();
						
			System.out.println(currentExtractedWord);
			
			System.out.printf("=> Score decoded : words = %d / valid = %d / invalid = %d\n",startingNbrWords,startingNbrWords - currentNbrWords,currentNbrWords);
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
		if(text == null || alphabet == null || alphabet.length() != 26) {
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
	
	private TreeSet<String> extractWords(String text) {
		
		TreeSet<String> words = new TreeSet<>(
			    Comparator.comparing(String::length, Comparator.reverseOrder())
			        .thenComparing(Comparator.naturalOrder())
			);

		 List<String> wordsFound = Arrays.stream(text.split("\\s+"))
		            .filter(word -> word.length() > 2)
		            .collect(Collectors.toList());
		         
		 words.addAll(wordsFound);

		 return words;
	}
	
	
	private void removeValidWords(Set<String> words) {
		words.removeIf(word -> dictionnary.containsWord(word));
	}
	
	private String getCompatibleWord(String cryptogram) {
		var cryptoSeq = getWordSequence(cryptogram);
		
		for(var currentWord : dictionnary.getWordsOfLength(cryptogram.length())) {
			if(Arrays.equals(cryptoSeq,getWordSequence(currentWord))) {
				return currentWord.toUpperCase();
			}
		}
		
		return "";
	}
	
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
	
	private String updateAlphabet(String startingAlphabet,String newAlphabet,Map<Character,Character> map1,Map<Character,Character> map2,String cryptogram,String candidateWord) {
		StringBuilder updatedAlphabet = new StringBuilder(newAlphabet.isEmpty() ? startingAlphabet : newAlphabet);
		System.out.println("(Init) " + updatedAlphabet.toString());
		
		for(int i = 0; i < cryptogram.length(); i++) {
			char currentCryptoCharacter = cryptogram.charAt(i);
			char currentWordCharacter = candidateWord.charAt(i);
			
			if(!map1.containsKey(currentCryptoCharacter)) {
				
				int posFirstCharacter = startingAlphabet.indexOf(currentCryptoCharacter);
				
				int posSecondCharacter = startingAlphabet.indexOf(currentWordCharacter);
				
				if(posFirstCharacter != -1 && posSecondCharacter != -1) {
					updatedAlphabet.setCharAt(posFirstCharacter, currentWordCharacter);
					map1.put(currentCryptoCharacter, currentWordCharacter);
					
					if(map2.containsKey(currentCryptoCharacter)) {
						var valueMap2 = map2.get(currentCryptoCharacter);
						
						for(var v : map2.entrySet()) {
							if(v.getValue() == currentWordCharacter) {
								var posLetter = startingAlphabet.indexOf(v.getKey());
								if(posLetter != -1) {
									updatedAlphabet.setCharAt(posLetter, valueMap2);
									break;
								}
							}
						}
						map2.remove(currentCryptoCharacter);
					} else {
						map2.put(currentWordCharacter, currentCryptoCharacter);
						updatedAlphabet.setCharAt(posSecondCharacter, currentCryptoCharacter);
					}
				}
				
				System.out.println("(Letter crypto): " + currentCryptoCharacter);
				System.out.println("(Letter word): " + currentWordCharacter);
			}
			
			
			System.out.println(i + ") " + updatedAlphabet.toString());
		}
		
		System.out.println("(Final) " + updatedAlphabet.toString());
		
		return updatedAlphabet.toString();
	}
	
	private void applyAlphabetOnWords(TreeSet<String> wordsExtracted,String alphabet) {
		List<String> updatedWords = new ArrayList<String>();
		for (String w : wordsExtracted) {
		    String updatedWord = applySubstitution(w,alphabet);
		    updatedWords.add(updatedWord);
		}
		wordsExtracted.clear();
		wordsExtracted.addAll(updatedWords);
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
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		
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
