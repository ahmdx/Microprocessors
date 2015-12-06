import java.util.Arrays;
import java.util.Random;

public class Cache {

	// Cache Dimensions.
	private int S; // Cache Capacity. C * L ... S is in KiloBytes.
	private int L; // Line Size.
	private int m; // Number of Banks.

	private int C; // Number of Cache Lines.

	//
	// Lines per bank
	//
	private int lines;

	private float accesses;
	private float misses;

	//
	// Number of cycles to fetch the data
	//
	private int cycles;

	//
	// byte displacement and index number of bits
	//
	private int d, i;

	private CacheWriteHitPolicy writeHitPolicy;
	// private CacheWriteMissPolicy writeMissPolicy;

	private CacheEntry[][] content;

	public Cache(int S, int L, int m, int cycles) throws InvalidNumberOfBanksException {
		this.S = S;
		this.L = L;
		this.m = m;
		this.cycles = cycles;
		this.writeHitPolicy = CacheWriteHitPolicy.WriteThrough;
		// this.writeHitPolicy = CacheWriteHitPolicy.WriteBack;
		// this.writeMissPolicy = CacheWriteMissPolicy.WriteAllocate;

		this.C = S / L;

		if (C < m) {
			throw new InvalidNumberOfBanksException(
					"Number of banks cannot be greater than the number of cache entries");
		}

		this.lines = C / m;

		this.content = new CacheEntry[this.lines][this.m];

		this.d = (int) (Math.log(this.L) / Math.log(2));
		this.i = (int) (Math.log(this.lines) / Math.log(2));
	}

	public Cache(int S, int L, int m, int cycles, CacheWriteHitPolicy hitPolicy, CacheWriteMissPolicy missPolicy) {
		this.S = S;
		this.L = L;
		this.m = m;
		this.cycles = cycles;
		this.writeHitPolicy = hitPolicy;
		// this.writeMissPolicy = missPolicy;

		this.C = (S * 1024) / L;

		this.lines = C / m;

		this.content = new CacheEntry[this.lines][this.m];

		this.d = (int) (Math.log(this.L) / Math.log(2));
		this.i = (int) (Math.log(this.lines) / Math.log(2));
	}

	public CacheWriteHitPolicy getWriteHitPolicy() {
		return writeHitPolicy;
	}

	private int[] getCacheAddress(int address) {
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
		return new int[] { tag, index, byteOffset };
	}

	private void setDirtyBit(CacheEntry entry) {
		if (this.writeHitPolicy == CacheWriteHitPolicy.WriteBack) {
			entry.setDirty(true);
		}
	}

	public void print(int level) {
		System.out.println("CACHE: ------------------------ LEVEL: " + level + " HIT %: " + (1 - this.misses/this.accesses) * 100);
		for (int i = 0; i < this.content.length; i++) {
			for (int j = 0; j < this.content[i].length; j++) {
				if (this.content[i][j] != null) {
					System.out.println(this.content[i][j].toString());
				}
			}
		}
	}

	public String read(int address) {
		this.accesses++;
		int[] addr = getCacheAddress(address);
		int tag = addr[0];
		int index = addr[1];
		int byteOffset = addr[2];

		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null && entry.getTag() == tag) {
				return entry.getByte(byteOffset);
			}
		}
		this.misses++;
		return null;
	}

	public String[] readLine(int address) {
		this.accesses++;
		int[] addr = getCacheAddress(address);
		int tag = addr[0];
		int index = addr[1];

		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null && entry.getTag() == tag) {
				return entry.getData();
			}
		}
		this.misses++;
		return null;
	}

	public String[] write(int address, String data) {
		this.accesses++;
		int[] addr = getCacheAddress(address);
		int tag = addr[0];
		int index = addr[1];
		int byteOffset = addr[2];

		// boolean found = false;
		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null) {
				if (entry.getTag() == tag) {
					// found = true;
					entry.setByte(byteOffset, data);
					setDirtyBit(entry);
					return entry.getData();
				}
			}
		}
		this.misses++;
		return null;
	}

	public String[] writeLine(int address, String[] data) {
		this.accesses++;
		int[] addr = getCacheAddress(address);
		int tag = addr[0];
		int index = addr[1];

		// boolean found = false;
		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null) {
				if (entry.getTag() == tag) {
					// found = true;
					entry.setData(data);
					setDirtyBit(entry);
					return entry.getData();
				}
			}
		}
		this.misses++;
		return null;
	}

	public void writeMiss(int address, String[] data) {
		this.accesses++;
		int[] addr = getCacheAddress(address);
		int tag = addr[0];
		int index = addr[1];

		boolean found = false;
		for (int j = 0; j < this.m; j++) {
			CacheEntry entry = this.content[index][j];
			if (entry != null) {
				if (entry.getTag() == tag) {
					found = true;
					entry.setData(data);
					setDirtyBit(entry);
				}
			} else {
				this.misses++;
				this.content[index][j] = new CacheEntry(tag, data);
				setDirtyBit(this.content[index][j]);
				found = true;
			}
		}

		//
		// All entries are valid in a set and the tags are different, remove a
		// random entry
		//
		if (!found) {
			this.misses++;
			Random rand = new Random();
			int randomEntry = rand.nextInt(m);
			this.content[index][randomEntry] = new CacheEntry(tag, data);
			setDirtyBit(this.content[index][randomEntry]);
		}

	}

	public static void main(String[] args) {
		// Cache cache = new Cache(8, 4, 1, 1);
		// cache.content[0][0] = new CacheEntry(0, new String[] { "a", "b", "c",
		// "d" });
		// cache.content[1][0] = new CacheEntry(0, new String[] { "a", "b", "c",
		// "d" });

		// System.out.println(Arrays.toString(cache.getCacheAddress(4)));

		// cache.write(4, "test");
		// System.out.println(cache.read(4));
	}

}
