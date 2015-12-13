
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
	private int writes;
	private int reads;

	/**
	 * 
	 * @param L
	 *            Caches line size in bytes
	 * @param cycles
	 *            Number of cycles the Memory takes to read/write data
	 */
	public Memory(int L, int cycles) {
		this.L = L;
		this.setCycles(cycles);
		this.data = new String[this.size];
	}

	/**
	 * 
	 * @param address
	 *            Address of data to be read from Memory
	 * @return The requested byte
	 */
	public String read(int address, boolean count) {
		if (count) {
			this.reads++;
		}
		if (address % 2 != 0) {
			address--;
		}
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
		this.reads++;
		String[] data = new String[this.L];
		int addr = (address / this.L) * this.L;
		for (int i = 0; i < this.L; i++) {
			data[i] = read(addr + i, false);
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
	public String write(int address, String data, boolean count) {
		if (count) {
			this.writes++;
		}
		if (address % 2 != 0) {
			address--;
		}
		this.data[Math.min(this.size, address)] = (data == null) ? "" : data;
		return read(address, false);
	}

	/**
	 * 
	 * @param address
	 *            Where the data is to be written
	 * @param data
	 *            The requested data to write
	 */
	public String[] writeLine(int address, String[] data) {
		this.writes++;
		int addr = (address / this.L) * this.L;
		for (int i = 0; i < data.length; i++) {
			if (i % 2 == 0) {
				write(addr + i, data[i], false);
			}
		}
		for (int i = data.length; i < this.L; i++) {
			if (i % 2 == 0) {
				write(addr + i, null, false);
			}
			
		}
		
		return readLine(address);
	}
	
	public void print() {
		System.out.println("MEMORY: ---------------------- R: " + this.reads + " W: " + this.writes);
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				System.out.println(i + ": " + data[i]);
			}
		}
		System.out.println("------------------------------");
	}

	public static void main(String[] args) {
//		Memory mem = new Memory(4, 1);
//		mem.write(1, null);
//
//		mem.writeLine(2, new String[] { "a", "b", "c" });
//		
//		System.out.println(Arrays.toString(mem.readLine(2)));
	}

	public int getCycles() {
		return cycles;
	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
}
