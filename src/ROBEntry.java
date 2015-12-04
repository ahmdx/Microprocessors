
public class ROBEntry {
	String type;
	int dest;
	short value;
	boolean ready;
	
	public ROBEntry(String type, int dest, short value, boolean ready) {
		this.type = type;
		this.dest = dest;
		this.value = value;
		this.ready = ready;
	}
}
