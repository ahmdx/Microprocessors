
public class MainMemory {
	
	int L;
	
	String [] memory;
	
	public MainMemory (int L, int cycles) {
		
		this.L = L;
		memory = new String [64 * 1024];
	}

}