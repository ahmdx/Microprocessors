import java.util.ArrayList;


public class Memory_Hierarchy {
	
	int nCaches; //Number of Caches
	
	int L;
	int mCycles; //Memory Cycles;
	
	int [] S; //Array of cache capacities assuming S[0] is the capacity for level 1 caches and so on.
	
	int [] m; //Array of bank sizes.
	
	int [] cycles; //Array of Cache Cycles.
	
	String instruction = null; //The returned instruction when fetching an instruction from the memory.
	String data = null; //The returned data.
	
	ArrayList<Object> hierarchy = new ArrayList<>(); //ArrayList that contains all caches and main memory.

	//The constructor creates all caches and adds them to the hierarchy list, then adds the main memory.
	public Memory_Hierarchy (int nCaches) {
		
		this.nCaches = nCaches;
		
		Main_Memory x = new Main_Memory(L, mCycles); //Initialize memory.
		
		//For the first level, Create separate data and instruction caches.
		//Note that level 1 is a must.
		
		Cache data = new Cache(S[0], L, m[0], cycles[0]);
		Cache instructions = new Cache(S[0], L, m[0], cycles[0]);
		hierarchy.add(data);
		hierarchy.add(instructions);
		
		//For the rest of the levels, add just one cache.
		for (int i = 1; i < nCaches; i++) {
			Cache y = new Cache(S[i], L, m[i], cycles[i]);
			hierarchy.add(y);
			
		}
		
		hierarchy.add(x); //Add the memory to the hierarchy.
		
	}
	
	public void readAction(String address) {
		
		//To be implemented.
		
	}

}
