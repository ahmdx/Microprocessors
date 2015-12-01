
public class Cache {
	
	//Cache Dimensions.
	int S; //Cache Capacity. C * L ... S is in KiloBytes.
	int L; //Line Size.
	int m; //Number of Banks.
	
	int C; //Number of Cache Lines.
	
	int linesPerBank;
	
	int accesses;
	int misses;
	
	int cycles;
	
	//Building Cache.
	String [][] content;
	
	public Cache(int S, int L, int m, int cycles) {
		
		this.S = S;
		this.L = L;
		this.m = m;
		this.cycles = cycles;
		
		this.C = (S * 1024) / L;
		
		this.linesPerBank = C / m;
		
		this.content = new String [m][linesPerBank];
	}

}
