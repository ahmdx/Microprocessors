
public class FetchedObject {
	private final String data;
	private int cycles;

	public FetchedObject(String data, int cycles) {
		this.data = data;
		this.cycles = cycles;
	}

	/**
	 * 
	 * @return The number of remaining cycles
	 */
	public int getCycles() {
		return this.cycles;
	}
	
	/**
	 * 
	 * @return data
	 */
	public String getData() {
		return this.data;
	}

	/**
	 * 
	 * @return The number of remaining cycles before decrementing them
	 */
	public int nextCycle() {
		return Math.max(0, this.cycles--);
	}

	public String toString() {
		return this.data + " - " + this.cycles + " cycles";
	}
}
