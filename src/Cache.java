
public class Cache {

	// Cache Dimensions.
	int S; // Cache Capacity. C * L ... S is in KiloBytes.
	int L; // Line Size.
	int m; // Number of Banks.

	int C; // Number of Cache Lines.

	int lines;

	int accesses;
	int misses;

	//
	// Number of cycles to fetch the data
	//
	int cycles;

	//
	// byte displacement and index number of bits
	//
	int d, i;

	//
	// Write hit and miss policies
	// writeHit: false for Write through and true for Write back
	// writeMiss: false for Write allocate and true for Write around
	//

	private boolean writeHit;
	private boolean writeMiss;

	CacheEntry[][] content;

	public Cache(int S, int L, int m, int cycles) {

		this.S = S;
		this.L = L;
		this.m = m;
		this.cycles = cycles;

		this.C = (S * 1024) / L;

		this.lines = C / m;

		this.content = new CacheEntry[this.lines][this.m];

		this.d = (int) (Math.log(this.L) / Math.log(2));
		this.i = (int) (Math.log(this.lines) / Math.log(2));
	}

	public String read(int address) {
		String binary = Integer.toBinaryString(0x10000 | address).substring(1);

		int byteOffset = 0;
		if (this.d > 0) {
			byteOffset = Integer.parseInt(binary.substring(binary.length() - this.d, binary.length()), 2);
		}
		int index = 0;
		if (this.i > 0) {
			index = Integer.parseInt(binary.substring(binary.length() - this.d - this.i, binary.length() - this.d), 2);
		}
		int tag = Integer.parseInt(binary.substring(0, binary.length() - this.d - this.i), 2);

		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null && entry.isValid() && entry.getTag() == tag) {
				return this.content[0][j].getByte(byteOffset);
			}
		}
		return null;
	}

	/**
	 * Incomplete implementation
	 * @param address Byte address
	 * @param data Data to write
	 */
	public void write(int address, String data) {
		String binary = Integer.toBinaryString(0x10000 | address).substring(1);

		int byteOffset = 0;
		if (this.d > 0) {
			byteOffset = Integer.parseInt(binary.substring(binary.length() - this.d, binary.length()), 2);
		}
		int index = 0;
		if (this.i > 0) {
			index = Integer.parseInt(binary.substring(binary.length() - this.d - this.i, binary.length() - this.d), 2);
		}
		int tag = Integer.parseInt(binary.substring(0, binary.length() - this.d - this.i), 2);
		
		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null && entry.isValid()) {
				if (entry.getTag() == tag) {
					this.content[0][j].setByte(byteOffset, data);	
				}
			} else {
				
			}
		}
	}

	public static void main(String[] args) {
		Cache cache = new Cache(8, 4, 1, 1);
		cache.content[0][0] = new CacheEntry(0, new String[] {"a", "b", "c", "d"});
		cache.content[1][0] = new CacheEntry(0, new String[] {"a", "b", "c", "d"});
		
		cache.write(4, "test");
		System.out.println(cache.read(4));
	}
}
