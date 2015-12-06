
public class FetchedObject {
	private String data;
	private int cycles;
	
	public FetchedObject(String data, int cycles) {
		this.data = data;
		this.cycles = cycles;
	}
	
	public String toString() {
		return this.data + " - " + this.cycles + " cycles";
	}
}
