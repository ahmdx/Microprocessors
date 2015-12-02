import java.util.ArrayList;


public class Processor {
	int PC;
	
	short[] regs;
	int[] regStatus;
	
	int insturctionBuffer; 
	int instructionsInBuffer; // Fetched Instructions
	
	int cyclesSimulated; 
	
	Battee5aMemory B; // To Be done
	
	ArrayList<ROBEntry> ROB; 

	int head;
	int tail;
	int ROBsize;
	int instructionsInROB;
	
	ArrayList<String []> fetchedInstructions;
	
	ReservationEntry e; // Issued Instruction
	
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
	
	
	public Processor() {
		PC = 0;
		
		regs = new short[8];
		regStatus = new int[8];

		for(int i = 0; i < 8; i++) {
			regStatus[i] = -1;
		}
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
		
		e = null; // will contain issued instruction
		for(int i = 0; i < instructionsInBuffer; i++) {
			if(Issue(fetchedInstructions.get(i))) {
				fetchedInstructions.remove(i);
				instructionsInBuffer--;
				instructionsInROB++;
				break;
			}
		}
		
		
		int toRemove = -1;
		for(int i = 0; i < rs.size(); i++) {
			if(rs.get(i).cyclesLeft == 0) {
				if(toRemove == -1) toRemove = i;
			}
			else if (rs.get(i).busy) {
				execute(rs.get(i));
			}
		}
		if(toRemove != -1) {
			e.rob.ready = true;
			decreaseType(rs.get(toRemove).type);
			if(rs.get(toRemove).dest != -1) {
				regs[rs.get(toRemove).dest] = rs.get(toRemove).rob.value;
			}
			rs.remove(toRemove);
		}
		
		if(e != null) { // If there is an instruction to be issued
			if (e.qj == -1 && e.qk == -1 && (e.dest == -1 || regStatus[e.dest] == -1)) {
				e.busy = true;
				if(e.dest != -1) regStatus[e.dest] = tail;
			}
			regStatus[e.dest] = tail;
			short value = computeResult(e.type, e.vj, e.vk, e.a);
			ROB.set(tail-1, new ROBEntry(e.type, e.dest, value, false));
			e.rob = ROB.get(tail - 1);
			tail++; if(tail == ROBsize+1) tail = 1;
			rs.add(e);
		}
		
		cyclesSimulated++;
	}
	
	public short computeResult(String type, int vj, int vk, short a) {
		if(type.equals("ADD"))
			return (short) (regs[vj] + regs[vk]);
		if(type.equals("SUB"))
			return (short) (regs[vj] - regs[vk]);
		if(type.equals("NAND"))
			return (short) ~(regs[vj] & regs[vk]);
		if(type.equals("MUL"))
			return (short) (regs[vj] * regs[vk]);
		if(type.equals("ADDI"))
			return (short) (regs[vj] + a);
		return 0;
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
			case "LW": if(loadInstructions < maxLoadInstructions) { add(instruction); loadInstructions++; return true; } break;
			case "SW": if(storeInstructions < maxStoreInstructions) { add(instruction); storeInstructions++; return true; } break;
			case "JMP": if(jumpInstructions < maxJumpInstructions) { add(instruction); jumpInstructions++; return true; } break;
			case "BEQ": if(beqInstructions < maxBeqInstructions) { add(instruction); beqInstructions++; return true; } break;
			case "JALR": if(jalrInstructions < maxJalrInstructions) { add(instruction); jalrInstructions++; return true; } break;
			case "RET": if(returnInstructions < maxReturnInstructions) { add(instruction); returnInstructions++; return true; } break;
			case "ADD": if(addInstructions < maxAddInstructions) { add(instruction); addInstructions++; return true; } break;
			case "SUB": if(subInstructions < maxSubInstructions) { add(instruction); subInstructions++; return true; } break;
			case "ADDI": if(addiInstructions < maxAddiInstructions) { add(instruction); addiInstructions++; return true; } break;
			case "NAND": if(nandInstructions < maxNandInstructions) { add(instruction); nandInstructions++; return true; } break;
			case "MUL": if(mulInstructions < maxMulInstructions) { add(instruction); mulInstructions++; return true; } break;
		}
		return false;
	}
	
	public void decreaseType(String type) {
		switch(type) {
			case "LW": loadInstructions--; break;
			case "SW": storeInstructions--; break;
			case "JMP": jumpInstructions--; break;
			case "BEQ": beqInstructions--; break;
			case "JALR": jalrInstructions--; break;
			case "RET": returnInstructions--; break;
			case "ADD": addInstructions--; break;
			case "SUB": subInstructions--; break;
			case "ADDI": addiInstructions--; break;
			case "NAND": nandInstructions--; break;
			case "MUL": mulInstructions--; break;
		}
	}
	
	public boolean canExecute(ReservationEntry e) {
		if(e.qj == -1 && e.qk == -1) {
			return true;
		}
		return false;
	}
	
	public void execute(ReservationEntry e) {
		e.cyclesLeft--;
	}
	
	public void add(String []instruction) {
		String type = instruction[0];
		
		if(type.equals("ADD") || type.equals("SUB") || type.equals("NAND") || type.equals("MUL")) {
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = Integer.parseInt(instruction[3].substring(1));
			int dest = Integer.parseInt(instruction[1].substring(1));
			int qj = -1;
			int qk = -1;

			if(regStatus[vj] != -1) { // IF not blank
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != -1) { // IF not blank
				qk = regStatus[vk];
				vk = -1;
			}

			boolean busy = false;
			byte a = 0;
			if(type.equals("ADD"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, addCycles);
			else if(type.equals("SUB"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, subCycles);
			else if(type.equals("NAND"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, nandCycles);
			else if(type.equals("MUL"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, mulCycles);
		}
		else if(type.equals("LW") || type.equals("ADDI")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = Integer.parseInt(instruction[1].substring(1)); 
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != -1) {
				qk = regStatus[vk];
				vk = -1;
			}
			if(type.equals("LW"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, loadCycles);
			else if(type.equals("ADDI"))
				e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, addiCycles);
		}
		else if(type.equals("SW")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = Integer.parseInt(instruction[2].substring(1));
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != -1) {
				qk = regStatus[vk];
				vk = -1;
			}
			e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, storeCycles);
		}
		else if(type.equals("JMP")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, jumpCycles);
		}
		else if(type.equals("BEQ")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = Integer.parseInt(instruction[2].substring(1));
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = Byte.parseByte(instruction[3]);
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			if(regStatus[vk] != 0) {
				qk = regStatus[vk];
				vk = -1;
			}
			e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, beqCycles);
		}
		else if(type.equals("JALR")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[2].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = Integer.parseInt(instruction[1].substring(1));
			byte a = 0;
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, jalrCycles);
		}
		else if(type.equals("RET")) {
			boolean busy = false;
			int vj = Integer.parseInt(instruction[1].substring(1));
			int vk = -1;
			int qj = -1;
			int qk = -1;
			int dest = -1;
			byte a = 0;
			if(regStatus[vj] != -1) {
				qj = regStatus[vj];
				vj = -1;
			}
			e = new ReservationEntry(busy, type, vj, vk, qj, qk, dest, a, returnCycles);
		}
	}
}
