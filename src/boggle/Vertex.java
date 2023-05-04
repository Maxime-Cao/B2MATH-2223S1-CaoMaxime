package boggle;

public class Vertex {
	private final char letter;
	
	public Vertex(char letter) {
		this.letter = letter;
	}
	
	public char getVertexValue() {
		return letter;
	}

	@Override
	public String toString() {
		return String.format("%c",letter);
	}
	
	

}
