import java.util.ArrayList;

public class MemoryHierarchy {

	int numberOfCaches; // Number of Caches

	// int L;
	// int memoryCycles; // Memory Cycles;

	// int[] S; // Array of cache capacities assuming S[0] is the capacity for
	// level 1 caches and so on.

	// int[] m; // Array of bank sizes.

	// int[] cycles; // Array of Cache Cycles.

	String instruction = null; // The returned instruction when fetching an
								// instruction from the memory.
	String data = null; // The returned data.

	ArrayList<Object> hierarchy = new ArrayList<>(); // ArrayList that contains
														// all caches and main
														// memory.

	// The constructor creates all caches and adds them to the hierarchy list,
	// then adds the main memory.
	public MemoryHierarchy(int numberOfCaches, int L, int S[], int m[], int cycles[], int memoryCycles) {

		this.numberOfCaches = numberOfCaches;

		Memory mainMemory = new Memory(memoryCycles); // Initialize
																// memory.

		// For the first level, Create separate data and instruction caches.
		// Note that level 1 is a must.

		Cache data = new Cache(S[0], L, m[0], cycles[0]);
		Cache instructions = new Cache(S[0], L, m[0], cycles[0]);
		hierarchy.add(data);
		hierarchy.add(instructions);

		// For the rest of the levels, add just one cache.
		for (int i = 1; i < Math.min(numberOfCaches, 3); i++) {
			Cache cache = new Cache(S[i], L, m[i], cycles[i]);
			hierarchy.add(cache);
		}

		hierarchy.add(mainMemory); // Add the memory to the hierarchy.
	}

	//
	// Incomplete implementation
	//
	public void read(int address) {
		if (address < (64 * 1024) / 2) {
			System.out.println("Data");
		} else {
			System.out.println("Instruction");
		}
	}

	public static void main(String[] args) {
		MemoryHierarchy hierarchy = new MemoryHierarchy(1, 4, new int[] { 8 }, new int[] { 1 }, new int[] { 1 }, 12);

		hierarchy.read(55555);
	}

}
