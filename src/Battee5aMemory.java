import java.util.ArrayList;


public class Battee5aMemory {
	ArrayList<String> Instructions;
	
	public Battee5aMemory() {
		Instructions = new ArrayList<String>();
	}
	
	String getInstruction(short address) {
		return Instructions.get(address / 2);
	}
}
