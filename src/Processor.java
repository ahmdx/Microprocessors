import java.util.ArrayList;


public class Processor {
	int PC;
	
	short[] regs;
	int[] regStatus;
	
	int insturctionBuffer;
	int instructionsInBuffer;
	
	int cyclesSimulated;
	
	Battee5aMemory B;
	
	ArrayList<ROBEntry> ROB;
	int head;
	int tail;
	int ROBsize;
	int instructionsInROB;
	
	ArrayList<String []> fetchedInstructions;
	
	// Reservation Stations
	ArrayList<ReservationEntry> rs;
	
	int loadCycles;
	int storeCycles;
	int jumpCycles;
	int beqCycles;
	int jalrCycles;
	int returnCycles;
	int addCycles;
	int addiCycles;
	int subCycles;
	int nandCycles;
	int mulCycles;
	
	int loadInstructions;
	int storeInstructions;
	int jumpInstructions;
	int beqInstructions;
	int jalrInstructions;
	int returnInstructions;
	int addInstructions;
	int addiInstructions;
	int subInstructions;
	int nandInstructions;
	int mulInstructions;
	
	int maxLoadInstructions;
	int maxStoreInstructions;
	int maxJumpInstructions;
	int maxBeqInstructions;
	int maxJalrInstructions;
	int maxReturnInstructions;
	int maxAddInstructions;
	int maxAddiInstructions;
	int maxSubInstructions;
	int maxNandInstructions;
	int maxMulInstructions;
	
	ArrayList<String []> writtenInstructions;
	
	public Processor() {
		PC = 0;
		
		regs = new short[8];
		regStatus = new int[8];
		regs[0] = 0;
		
		ROB = new ArrayList<ROBEntry>();
		for(int i = 0; i < ROBsize; i++) {
			ROB.add(null);
		}
		head = 1;
		tail = 1;
		
		B = new Battee5aMemory();
		insturctionBuffer = 4;
		instructionsInBuffer = 0;
	}
	
	public void simulate() {
		fetchAll();
		
		for(int i = 0; i < instructionsInBuffer; i++) {
			if(Issue(fetchedInstructions.get(i))) {
				fetchedInstructions.remove(i);
				instructionsInBuffer--;
				break;
			}
		}
		
		for(int i = 0; i < rs.size(); i++) {
			execute(rs.get(i));
		}
		
		cyclesSimulated++;
	}
	
	public void fetchAll() {
		while(instructionsInBuffer < insturctionBuffer) {
			if(PC / 2 == B.Instructions.size()) break;
			fetchedInstructions.add(ProgramParser.match(B.Instructions.get(PC / 2)));
			PC += 2;
			instructionsInBuffer++;
		}
	}
	
	public boolean Issue(String []instruction) {
		if(instructionsInROB == ROBsize) return false;
		switch(instruction[0]) {
			case "LW": if(loadInstructions < maxLoadInstructions) { add(instruction); return true; } break;
			case "SW": if(storeInstructions < maxStoreInstructions) { add(instruction); return true; } break;
			case "JMP": if(jumpInstructions < maxJumpInstructions) { add(instruction); return true; } break;
			case "BEQ": if(beqInstructions < maxBeqInstructions) { add(instruction); return true; } break;
			case "JALR": if(jalrInstructions < maxJalrInstructions) { add(instruction); return true; } break;
			case "RET": if(returnInstructions < maxReturnInstructions) { add(instruction); return true; } break;
			case "ADD": if(addInstructions < maxAddInstructions) { add(instruction); return true; } break;
			case "SUB": if(subInstructions < maxSubInstructions) { add(instruction); return true; } break;
			case "ADDI": if(addiInstructions < maxAddiInstructions) { add(instruction); return true; } break;
			case "NAND": if(nandInstructions < maxNandInstructions) { add(instruction); return true; } break;
			case "MUL": if(mulInstructions < maxMulInstructions) { add(instruction); return true; } break;
		}
		return false;
	}
	
	public boolean canExecute(ReservationEntry e) {
		if(e.qj == -1 && e.qk == -1) {
			return true;
		}
		return false;
	}
	
	public void execute(ReservationEntry e) {
		if(!e.busy) {
			if(canExecute(e)) {
				e.busy = true;
				ROB.set(tail-1, new ROBEntry(e.type, e.dest, (short) -1, false));
				tail++;
				if(tail == ROBsize+1) {
					tail = 1;
				}
			}
		}
		else {
			if(e.cyclesLeft > 0) {
				e.cyclesLeft--;
			}
			else {
				
			}
		}
	}
	
	public void add(String []instruction) {
		String type = instruction[0];
		
		if(type.equals("ADD") || type.equals("SUB") || type.equals("NAND") || type.equals("MUL")) {
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = Integer.parseInt(instruction[3].substring(1));
			int dest = Integer.parseInt(instruction[1].substring(1));
			int qj = -1;
			int qk = -1;
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != 0) {
				qk = regStatus[vk];
				vk = -1;
			}
			boolean busy = false;
			byte a = 0;
			if(type.equals("ADD"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, addCycles));
			else if(type.equals("SUB"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, subCycles));
			else if(type.equals("NAND"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, nandCycles));
			else if(type.equals("MUL"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, mulCycles));
		}
		else if(type.equals("LW") || type.equals("ADDI")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = Integer.parseInt(instruction[1].substring(1)); 
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != 0) {
				qk = regStatus[vk];
				vk = -1;
			}
			if(type.equals("LW"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, loadCycles));
			else if(type.equals("ADDI"))
				rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, addiCycles));
		}
		else if(type.equals("SW")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = Integer.parseInt(instruction[2].substring(1));
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != 0) {
				qk = regStatus[vk];
				vk = -1;
			}
			rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, storeCycles));
		}
		else if(type.equals("JMP")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, jumpCycles));
		}
		else if(type.equals("BEQ")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = Integer.parseInt(instruction[2].substring(1));
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != 0) {
				qk = regStatus[vk];
				vk = -1;
			}
			rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, beqCycles));
		}
		else if(type.equals("JALR")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = Integer.parseInt(instruction[1].substring(1));
			byte a = 0;
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, jalrCycles));
		}
		else if(type.equals("RET")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = 0;
			if(regStatus[vj] != 0) {
				qj = regStatus[vj];
				vj = -1;
			}
			rs.add(new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, returnCycles));
		}
	}
}
