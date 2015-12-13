
public class CacheLevel {
	int s;
	int m;
	float hit_rate;
	float miss_rate;
	int access_cycles;
	int write_back;
	
	public CacheLevel(int s, int m, float hit_rate, float miss_rate, int access_cycles, int write_back){
		this.s = s;
		this.m = m;
		this.hit_rate = hit_rate;
		this.miss_rate = miss_rate;
		this.access_cycles = access_cycles;
		this.write_back = write_back;
	}
	
	public CacheLevel(){
	}
	
	public String toString(){
		return "" + this.s + m + Float.toString(hit_rate) + Float.toString(miss_rate) + Integer.toString(access_cycles); 
	}

}
