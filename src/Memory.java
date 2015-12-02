
public class Memory {

	private String[] data;
	private int size = 64 * 1024;
	private int cycles;

	public Memory(int cycles) {
		this.cycles = cycles;
		this.data = new String[this.size];
	}

	public String read(int address) {
		return this.data[Math.min(this.size, address)];
	}
	
	public String[] readLine(int address, int size) {
		String[] data = new String[size];
		for (int i = 0; i < size; i++) {
			data[i] = read((address/size) * size + i);
		}
		return data;
	}

	public void write(int address, String data) {
		this.data[Math.min(this.size, address)] = data;
	}
}
