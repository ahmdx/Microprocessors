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

	public MemoryHierarchy(int numberOfCaches, int L, int S[], int m[], int cycles[], int memoryCycles,
			CacheWriteHitPolicy[] policies) throws InvalidNumberOfBanksException {

		this.numberOfCaches = numberOfCaches;
		this.L = L;
		this.cycles = cycles;
		this.memoryCycles = memoryCycles;

		this.memory = new Memory(L, memoryCycles); // Initialize
													// memory.

		// For the first level, Create separate data and instruction caches.
		// Note that level 1 is a must.

		Cache data = new Cache(S[0], L, m[0], cycles[0], policies[0]);
		Cache instructions = new Cache(S[0], L, m[0], cycles[0], policies[0]);
		caches.add(data);
		caches.add(instructions);

		// For the rest of the levels, add just one cache.
		for (int i = 1; i < Math.min(numberOfCaches, 3); i++) {
			Cache cache = new Cache(S[i], L, m[i], cycles[i], policies[i]);
			caches.add(cache);
		}
	}

	private boolean isData(int address) {
		return address < (64 * 1024) / 2;
	}

	/**
	 * 
	 * @param address
	 *            The address (byte) to be read; bytes n & n + 1, where n is
	 *            even, are the same
	 * @return Fetched Object containing the data and the cycles taken to read
	 */
	public FetchedObject read(int address) {
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
			// return res;
			return new FetchedObject(res, this.accessCycles);
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

		String[] oldLine = null;
		for (int i = missLevel - 1; i > 0; i--) {
			oldLine = this.caches.get(i + 1).writeMiss(address, lineContent);
			// if (oldLine != null) {
			writeBack(i + 1, address, oldLine, false);
			// }
			this.accessCycles += this.cycles[i];
		}
		if (data) {
			oldLine = this.caches.get(0).writeMiss(address, lineContent);
		} else {
			oldLine = this.caches.get(1).writeMiss(address, lineContent);
		}
		// if (oldLine != null) {
		writeBack(1, address, oldLine, false);
		// }
		this.accessCycles += this.cycles[0];

		System.out.println("READ-C: " + this.accessCycles);
		// return lineContent[getByteOffset(address)];
		return new FetchedObject(lineContent[getByteOffset(address)], this.accessCycles);
	}

	/**
	 * 
	 * @param address
	 *            The address (byte) to write to; bytes n & n + 1, where n is
	 *            even, are the same
	 * @param content
	 *            The data to be written
	 * @return The number of cycles to write
	 */
	public int write(int address, String content) {
		this.accessCycles = 0;
		address = address / 2 * 2;

		int missLevel = 0;
		boolean data = isData(address);

		String[][] line = null;
		String[] res = null, oldLine = null;
		if (data) {
			line = caches.get(0).write(address, content);
		} else {
			line = caches.get(1).write(address, content);
		}
		if (line != null) {
			res = line[0];
			oldLine = line[1];
		}

		//
		// Write hit at L1
		//
		if (res != null) {
			writeHit(1, address, res, oldLine);
			System.out.println("WRITE-C: " + this.accessCycles);
			return this.accessCycles;
		}
		this.accessCycles += this.cycles[0];
		//
		// Write miss at L1
		//
		++missLevel;
		res = null;
		oldLine = null;
		for (int i = 2; i < this.caches.size(); i++) {
			line = this.caches.get(i).write(address, content);
			if (line != null) {
				res = line[0];
				oldLine = line[1];
			}
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
				writeHit(i, address, res, oldLine);
				break;
			}
		}

		//
		// Write miss at all cache levels
		// Write to memory and then to other caches
		// starting at the lowest level; first before the memory
		//
		if (missLevel == this.caches.size() - 1) {
			this.memory.write(address, content, true);
			res = this.memory.readLine(address);
			this.accessCycles += this.memoryCycles;
			// System.out.print("MEM-R: ");
		}

		for (int i = missLevel - 1; i > 0; i--) {
			oldLine = this.caches.get(i + 1).writeMiss(address, res);
			this.accessCycles += this.cycles[i];
			if (oldLine != null) {
				writeBack(i + 1, address, oldLine, false);
			}
		}
		if (data) {
			oldLine = this.caches.get(0).writeMiss(address, res);
		} else {
			oldLine = this.caches.get(1).writeMiss(address, res);
		}
		this.accessCycles += this.cycles[0];
		if (oldLine != null) {
			writeBack(1, address, oldLine, false);
		}
		System.out.println("WRITE-C: " + this.accessCycles);
		return this.accessCycles;
	}

	/**
	 * Called when there is a write hit at some cache level. Handles all data
	 * propagation according the cache's write hit policy
	 * 
	 * @param level
	 *            Current level the cache hit at
	 * @param address
	 *            Address of content to write at
	 * @param lineContent
	 *            The line content to write
	 * @param oldLine
	 *            The line returned from a write back cache
	 * 
	 */
	public void writeHit(int level, int address, String[] lineContent, String[] oldLine) {
		System.out.println("HITL: " + level);
		this.accessCycles += this.cycles[level - 1];

		for (int i = level + 1; i < this.caches.size(); i++) {
			if (this.caches.get(i - 1).getWriteHitPolicy() == CacheWriteHitPolicy.WriteThrough) {
				this.caches.get(i).writeLine(address, lineContent);
				this.accessCycles += this.cycles[i - 1];
			} else {
				// this.accessCycles += this.cycles[i - 1];
				writeBack(i - 1, address, this.caches.get(i).writeLine(address, oldLine)[1], false);
				return;
			}
		}
		this.memory.writeLine(address, lineContent);
		this.accessCycles += this.memoryCycles;
	}

	/**
	 * Called when there is a write hit at some cache level. Handles all data
	 * propagation according the cache's write hit policy
	 * 
	 * @param level
	 *            Current level the cache returned a data to be propagated
	 * @param address
	 *            Address of content to write at
	 * @param lineContent
	 *            The line content to write
	 * @param count
	 *            Whether to count the cycles taken at the cache level or not
	 * 
	 */
	public void writeBack(int level, int address, String[] content, boolean count) {
		if (content == null) {
			return;
		}
		if (count) {
			this.accessCycles += this.cycles[level - 1];
		}
		//
		// Write back to Memory (base case)
		//
		if (level == this.caches.size() - 1) {
			System.out.println("OK-MEM");
			this.memory.writeLine(address, content);
			this.accessCycles += this.memoryCycles;
			return;
		}
		String line[][] = null;
		line = this.caches.get(level + 1).writeLine(address, content);
		if (line != null) {
			writeBack(level + 1, address, line[1], false);
		}
		writeBack(level + 1, address, content, true);
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
		// CacheWriteHitPolicy[] policies = ;
		MemoryHierarchy h = new MemoryHierarchy(2, 4, new int[] { 2048, 4096, 8192 }, new int[] { 1, 1, 1 },
				new int[] { 1, 4, 6 }, 10, new CacheWriteHitPolicy[] { CacheWriteHitPolicy.WriteBack,
						CacheWriteHitPolicy.WriteBack, CacheWriteHitPolicy.WriteThrough });

		// h.memory.writeLine(0, new String[] {"A", "", "B", ""});

		System.out.println(h.write(0, "A") + " cycles");
		System.out.println(h.read(0));
		System.out.println();
		System.out.println(h.write(3, "B") + " cycles");
		System.out.println(h.read(3));
		System.out.println();
		// h.write(12, "A");
		// System.out.println(h.read(12));
		// System.out.println();
		// h.write(38000, "ADDI R1, R2, 8");
		// System.out.println(h.read(38000));
		// System.out.println();
		h.print();
	}

}
