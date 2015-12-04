
//
// Memory is split into two parts, the first one holds the data and the second part holds instructions
// Memory has size of 64KB and is addressed using 16 bits (byte-addressable)
//

import java.util.Arrays;

public class Memory {

	private String[] data;
	private int size = 64 * 1024;
	private int cycles;
	private int L;

	/**
	 * 
	 * @param L
	 *            Caches line size in bytes
	 * @param cycles
	 *            Number of cycles the Memory takes to read/write data
	 */
	public Memory(int L, int cycles) {
		this.L = L;
		this.cycles = cycles;
		this.data = new String[this.size];
	}

	/**
	 * 
	 * @param address
	 *            Address of data to be read from Memory
	 * @return The requested byte
	 */
	public String read(int address) {
		return this.data[Math.min(this.size, address)];
	}

	/**
	 * 
	 * @param address
	 *            Address of data to be read from Memory
	 * @return The requested L bytes where L is the size of the cache line in
	 *         bytes
	 */
	public String[] readLine(int address) {
		String[] data = new String[this.L];
		int addr = (address / this.L) * this.L;
		for (int i = 0; i < this.L; i++) {
			data[i] = read(addr + i);
		}
		return data;
	}

	/**
	 * 
	 * @param address
	 *            Where the data is to be written
	 * @param data
	 *            The requested data to write
	 */
	public String write(int address, String data) {
		this.data[Math.min(this.size, address)] = (data == null) ? "" : data;
		return read(address);
	}

	/**
	 * 
	 * @param address
	 *            Where the data is to be written
	 * @param data
	 *            The requested data to write
	 */
	public String[] writeLine(int address, String[] data) {
		int addr = (address / this.L) * this.L;
		for (int i = 0; i < data.length; i++) {
			write(addr + i, data[i]);
		}
		for (int i = data.length; i < this.L; i++) {
			write(addr + i, null);
		}
		
		return readLine(address);
	}
	
	public void print() {
		System.out.println("MEMORY: ----------------------");
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				System.out.println(i + ": " + data[i]);
			}
		}
		System.out.println("------------------------------");
	}

	public static void main(String[] args) {
		Memory mem = new Memory(4, 1);
		mem.write(1, null);

		mem.writeLine(2, new String[] { "a", "b", "c" });
		
		System.out.println(Arrays.toString(mem.readLine(2)));
	}
}
