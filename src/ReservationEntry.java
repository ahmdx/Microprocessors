
public class ReservationEntry {
	String type;
	boolean busy;
	int vj;
	int vk;
	int qj;
	int qk;
	int dest;
	int cyclesLeft;
	byte a;
	
	public ReservationEntry(boolean busy, String type, int vj, int vk, int qj, int qk, int dest, byte a, int cyclesLeft) {
		this.busy = busy;
		this.type = type;
		this.vj = vj;
		this.vk = vk;
		this.qj = qj;
		this.qk = qk;
		this.dest = dest;
		this.a = a;
		this.cyclesLeft = cyclesLeft;
	}
}
