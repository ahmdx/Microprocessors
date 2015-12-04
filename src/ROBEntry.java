
public class ROBEntry {
	InstrType instruction;
	int dest;
	int value;
	boolean ready;
	
	public ROBEntry(InstrType instruction, int dest, int value, boolean ready) {
		this.instruction = instruction;
		this.dest = dest;
		this.value = value;
		this.ready = ready;
	}
}
