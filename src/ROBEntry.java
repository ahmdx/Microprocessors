
public class ROBEntry {
	InstrType instruction;
	int dest;
	int value;
	int value2;
	boolean ready;
	int pc;
	
	public ROBEntry(InstrType instruction, int dest, int value, boolean ready) {
		this.instruction = instruction;
		this.dest = dest;
		this.value = value;
		this.ready = ready;
		
		this.value2 = -1;
	}
}
