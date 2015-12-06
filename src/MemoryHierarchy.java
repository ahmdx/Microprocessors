import java.util.ArrayList;
import java.util.Arrays;

public class MemoryHierarchy {

	private int numberOfCaches; // Number of Caches

	private int L;
	private int memoryCycles; // Memory Cycles;

	// int[] S; // Array of cache capacities assuming S[0] is the capacity for
	// level 1 caches and so on.

	// int[] m; // Array of bank sizes.

	private int[] cycles; // Array of Cache Cycles.

	private String instruction = null; // The returned instruction when fetching
										// an
	// instruction from the memory.
	private String data = null; // The returned data.

	private ArrayList<Cache> caches = new ArrayList<Cache>(); // ArrayList that
																// contains all
																// the caches
	private Memory memory;

	// The constructor creates all caches and adds them to the hierarchy list,
	// then adds the main memory.
	
	private int accessCycles;
	
	public MemoryHierarchy(int numberOfCaches, int L, int S[], int m[], int cycles[], int memoryCycles) throws InvalidNumberOfBanksException {

		this.numberOfCaches = numberOfCaches;
		this.L = L;
		this.cycles = cycles;
		this.memoryCycles = memoryCycles;

		this.memory = new Memory(L, memoryCycles); // Initialize
													// memory.

		// For the first level, Create separate data and instruction caches.
		// Note that level 1 is a must.

		Cache data = new Cache(S[0], L, m[0], cycles[0]);
		Cache instructions = new Cache(S[0], L, m[0], cycles[0]);
		caches.add(data);
		caches.add(instructions);

		// For the rest of the levels, add just one cache.
		for (int i = 1; i < Math.min(numberOfCaches, 3); i++) {
			Cache cache = new Cache(S[i], L, m[i], cycles[i]);
			caches.add(cache);
		}
	}

	private boolean isData(int address) {
		return address < (64 * 1024) / 2;
	}

	//
	// Incomplete implementation
	//
	public String read(int address) {
//		int this.accessCycles = 0;
		this.accessCycles = 0;
		address = address / 2 * 2;

		int missLevel = 0;
		boolean data = isData(address);

		String res;
		if (data) {
			res = caches.get(0).read(address);
		} else {
			res = caches.get(1).read(address);
		}

		//
		// Checks if data is available in L1 caches
		// Returns the data if it was available; L1 cache hit
		//
		this.accessCycles += this.cycles[0];
		if (res != null) {
			System.out.println("READ-C: " + this.accessCycles);
			return res;
		}

		//
		// missLevel = 1; L1 cache miss
		//
		++missLevel;

		//
		// Searches other caches for the data
		//
		String[] lineContent = new String[] {};
		for (int i = 2; i < this.caches.size(); i++) {
			lineContent = this.caches.get(i).readLine(address);
			this.accessCycles += this.cycles[i - 1];
			//
			// missLevel = i; Li cache miss; i > 1
			//
			if (lineContent == null) {
				++missLevel;
			} else {
				break;
			}
		}

		//
		// Data is not available in any of the caches, read from Memory
		//
		if (missLevel == this.caches.size() - 1) {
			lineContent = this.memory.readLine(address);
			this.accessCycles += this.memoryCycles;
			System.out.print("MEM-R: ");
		}

		for (int i = missLevel - 1; i > 0; i--) {
			this.caches.get(i + 1).writeMiss(address, lineContent);
			this.accessCycles += this.cycles[i];
		}
		if (data) {
			this.caches.get(0).writeMiss(address, lineContent);
		} else {
			this.caches.get(1).writeMiss(address, lineContent);
		}
		this.accessCycles += this.cycles[0];

		System.out.println("READ-C: " + this.accessCycles);
		return lineContent[getByteOffset(address)];
	}

	public void write(int address, String content) {
		this.accessCycles = 0;
		address = address / 2 * 2;

		int missLevel = 0;
		boolean data = isData(address);

		String[] res;
		if (data) {
			res = caches.get(0).write(address, content);
		} else {
			res = caches.get(1).write(address, content);
		}
		//
		// Write hit at L1
		//
		if (res != null) {
			writeHit(1, address, res);
			System.out.println("WRITE-C: " + this.accessCycles);
			return;
		}
		this.accessCycles += this.cycles[0];
		//
		// Write miss at L1
		//
		++missLevel;
		for (int i = 2; i < this.caches.size(); i++) {
			res = this.caches.get(i).readLine(address);

			//
			// missLevel = i; Li cache miss; i > 1
			//
			if (res == null) {
				this.accessCycles += this.cycles[i - 1];
				++missLevel;
			} else {
				//
				// Write hit at Li; i > 1
				//
				writeHit(i, address, res);
				break;
			}
		}

		//
		// Write miss at all cache levels
		// Write to memory and then to other caches
		// starting at the lowest level; first before the memory
		//
		if (missLevel == this.caches.size() - 1) {
			this.memory.write(address, content);
			res = this.memory.readLine(address);
			this.accessCycles += this.memoryCycles;
//			System.out.print("MEM-R: ");
		}

		for (int i = missLevel - 1; i > 0; i--) {
			this.caches.get(i + 1).writeMiss(address, res);
			this.accessCycles += this.cycles[i];
		}
		if (data) {
			this.caches.get(0).writeMiss(address, res);
		} else {
			this.caches.get(1).writeMiss(address, res);
		}
		this.accessCycles += this.cycles[0];
		
		System.out.println("WRITE-C: " + this.accessCycles);
	}

	/**
	 * 
	 * @param level
	 *            Current level the cache hit at
	 * @param address
	 *            Address of content to write at
	 * @param lineContent
	 *            The line content to write
	 */
	public void writeHit(int level, int address, String[] lineContent) {
		System.out.println("HITL: " + level);
		this.accessCycles += this.cycles[level - 1];
		for (int i = level + 1; i < this.caches.size(); i++) {
			this.caches.get(i).writeLine(address, lineContent);
			this.accessCycles += this.cycles[i - 1];
		}
		this.memory.writeLine(address, lineContent);
		this.accessCycles += this.memoryCycles;
	}
	
	public void writeHitRec(int level, int address, String[] lineContent) {
		this.accessCycles += this.cycles[level - 1];
		if (level == this.caches.size() - 1) {
			this.memory.writeLine(address, lineContent);
			this.accessCycles += this.memoryCycles;
			return;
		}
		this.caches.get(level + 1).writeLine(address, lineContent);
		writeHitRec(level + 1, address, lineContent);
	}

	public int getByteOffset(int address) {
		String binary = Integer.toBinaryString(0x10000 | address).substring(1);

		int byteOffset = 0;
		int d = (int) (Math.log(this.L) / Math.log(2));
		if (d > 0) {
			byteOffset = Integer.parseInt(binary.substring(binary.length() - d, binary.length()), 2);
		}
		return byteOffset;
	}
	
	public void print() {
		this.memory.print();
		this.caches.get(0).print(1);
		this.caches.get(1).print(1);
		for (int i = 2; i < this.caches.size(); i++) {
			this.caches.get(i).print(i);
		}
	}
	
	public static void main(String[] args) throws InvalidNumberOfBanksException {
		MemoryHierarchy h = new MemoryHierarchy(3, 4, new int[] { 2048, 4096, 8192 }, new int[] { 1, 1, 1 },
				new int[] { 1, 4, 6 }, 10);

		// h.memory.writeLine(0, new String[] { "1", "", "b", "" });
		// mem.writeLine(4, new String[] {"2", "", "a", ""});

//		h.memory.writeLine(0, new String[] {"a", "", "B", ""});
		h.write(0, "A");
		System.out.println(h.read(0));
//		h.print();
		System.out.println();
		h.write(3, "B");
		System.out.println(h.read(3));
		System.out.println();
		h.write(12, "A");
		System.out.println(h.read(12));
		System.out.println();
		h.write(38000, "ADDI R1, R2, 8");
		System.out.println(h.read(38000));
		System.out.println();
		h.print();
	}

}
