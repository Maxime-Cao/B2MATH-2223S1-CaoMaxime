package boggle;

/**
 * This class represents a vertex in a graph. Each vertex contains a letter (a character)
 * @author Maxime Cao
 *
 */
public class Vertex {
	private final char letter;
	
	/**
	 * This constructor creates a vertex based on a letter given as an argument
	 * @param letter The letter of the vertex
	 */
	public Vertex(char letter) {
		this.letter = letter;
	}
	
	/**
	 * Get the letter of the vertex
	 * @return The letter of the vertex
	 */
	public char getVertexValue() {
		return letter;
	}

	/**
	 * Returns the vertex as a string
	 */
	@Override
	public String toString() {
		return String.format("%c",letter);
	}
}
