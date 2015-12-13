
import java.util.ArrayList;
import java.util.Scanner;

public class Processor {
	static int PCAfterLastInstruction;

    int PC;

    int[] regs;
    int[] regStatus;

    int instructionBuffer;
    int instructionsInBuffer; // Fetched Instructions

    int totalInstructions;
    int loadInstructions;
    int beqInstructions;
    int mispredictions;
    int cyclesSimulated;

    MemoryHierarchy M; // To Be done

    ArrayList<ROBEntry> ROB;

    int head;
    int tail;
    int ROBsize;
    int instructionsInROB;
    
    int maxIssuedPerCycle;

    ArrayList<String[]> fetched;
    ArrayList<Integer> fetchedAddress;
    int cyclesLeftToFetch;
    String[] instructionToBeFetched;

    ReservationEntry e; // Issued Instruction

    // Reservation Stations
    ArrayList<ReservationEntry> rs;

    int[] numCycles;
    int[] numInstrs = new int[11];
    int[] maxInstrs;

    public Processor(MemoryHierarchy M, int maxIssuedPerCycle, int instructionBuffer, int ROBsize, int[] maxInstrs, int[] numCycles) {
        PC = 32768;

        this.M = M;
        this.maxIssuedPerCycle = maxIssuedPerCycle;
        
        this.maxInstrs = maxInstrs;
        this.numCycles = numCycles;
        
        regs = new int[8];
        regStatus = new int[8];

        for (int i = 0; i < 8; i++) {
            regStatus[i] = -1;
        }
        regs[0] = 0;
        numCycles[InstrType.LW.ordinal()] = 1;
        numCycles[InstrType.SW.ordinal()] = 1;

        fetched = new ArrayList<String[]>();
        fetchedAddress = new ArrayList<Integer>();

        rs = new ArrayList<ReservationEntry>();

        this.ROBsize = ROBsize;
        ROB = new ArrayList<ROBEntry>();
        for (int i = 0; i < ROBsize; i++) {
            ROB.add(null);
        }
        head = 1;
        tail = 1;

        this.instructionBuffer = instructionBuffer;
        instructionsInBuffer = 0;
    }

    public boolean simulate() {
        // Commit Stage
    	
    	int stallCycles = 0;
        if (ROB.get(head - 1) != null && ROB.get(head - 1).ready) {
        	InstrType instruction = ROB.get(head - 1).instruction;
        	if(instruction == InstrType.LW || instruction == InstrType.ADD || instruction == InstrType.ADDI || instruction == InstrType.SUB || instruction == InstrType.MUL || instruction == InstrType.NAND) {
        		regs[ROB.get(head - 1).dest] = ROB.get(head - 1).value;
        		loadInstructions++;
        	}
        	else if(instruction == InstrType.SW) {
        		stallCycles = M.write(ROB.get(e.dest - 1).value, ""+e.vj) - 1;
        	}
        	else if(instruction == InstrType.JMP || instruction == InstrType.RET || instruction == InstrType.JALR || (instruction == InstrType.BEQ && ROB.get(head - 1).value != ROB.get(head - 1).value2)) {
        		if(instruction == InstrType.BEQ) {
        			beqInstructions++;
        			mispredictions++;
        		}
        		PC = ROB.get(head - 1).value;
        		
        		if(instruction == InstrType.JALR) {
        			ROB.get(head - 1).dest = ROB.get(head - 1).value2;
        		}
        		
        		for(int i = 0; i < 8; i++) {
        			regStatus[i] = -1;
        		}
        		
        		for(int i = 0; i < ROBsize; i++) {
        			ROB.set(i, null);
        		}
        		
        		cyclesLeftToFetch = 0;
        		rs.clear();
        		fetched.clear();
        		fetchedAddress.clear();
        		instructionsInROB = 0;
        		head = 1;
        		tail = 1;
        		totalInstructions++;
        		cyclesSimulated++;
        		if(PC >= PCAfterLastInstruction) return false;
        		return true;
        	}
        	if(instruction == InstrType.BEQ) {
        		beqInstructions++;
        	}

            if (regStatus[ROB.get(head - 1).dest] == head) {
                regStatus[ROB.get(head - 1).dest] = -1;
            }
            
            instructionsInROB--;
            ROB.set(head - 1, null);
            head++;
            if (head == ROBsize + 1) {
                head = 1;
            }
            totalInstructions++;

        }

        // Write Stage
        for (int i = 0; i < rs.size(); i++) {
            if (rs.get(i).cyclesLeft == 0) {
                ROB.get(rs.get(i).dest - 1).ready = true;

                decreaseType(rs.get(i).type); // Todo unsure
                for (int j = 0; j < rs.size(); j++) {
                    if (rs.get(j).qj == rs.get(i).dest) {
                        rs.get(j).qj = -1;
                    }
                    if (rs.get(j).qk == rs.get(i).dest) {
                        rs.get(j).qk = -1;
                    }
                }
                rs.remove(i);
                break;
            }
        }

        // Execute Stage
        for (int i = 0; i < rs.size(); i++) {
            if (rs.get(i).qj == -1 && rs.get(i).qk == -1) {
                execute(rs.get(i));
            }
        }

        //Issue Stage
        e = null; // Will Contain Issued Instruction
        int issuedInstructions = 0;
        while (issuedInstructions < maxIssuedPerCycle && fetched.size() > 0 && Issue(fetched.get(0))) {
            fetched.remove(0);
            instructionsInBuffer--;
            instructionsInROB++;
            issuedInstructions++;
            if (e != null) { // If there is an instruction to be issued
                tail++;
                if (tail == ROBsize + 1) {
                    tail = 1;
                }
                e.pc = fetchedAddress.remove(0);
                rs.add(e);
            }
        }

        // Fetch Stage
        if(cyclesLeftToFetch == 0) fetch();
        
        if(cyclesLeftToFetch > 0) {
        	cyclesLeftToFetch--;
        	if(cyclesLeftToFetch == 0) {
        		fetched.add(instructionToBeFetched);
        		if(instructionToBeFetched[0] == "BEQ") {
        			int imm = Integer.parseInt(instructionToBeFetched[3]);
        			if(imm < 0) PC = PC + imm;
        		}
        	}
        }

        cyclesSimulated++;
        if(stallCycles == 0);
        	//System.out.println("Cycle: " + cyclesSimulated);
        else {
        	//System.out.println("Cycles: " + cyclesSimulated + " " + (cyclesSimulated+stallCycles));
        	cyclesSimulated += stallCycles;
        }
		boolean allAfterLastInstruction = true;
		for(int i = 0; i < rs.size(); i++) {
			if(rs.get(i).pc < PCAfterLastInstruction) {
				allAfterLastInstruction = false;
				break;
			}
		}
		if(PC >= PCAfterLastInstruction && allAfterLastInstruction) return false;
        return true;
        /*System.out.println();
        printAll();
        System.out.println();*/
    }

    public int computeResult(InstrType type, int vj, int vk, int a, int pc) {

        if (type == InstrType.ADDI) {
            return vj + a;
        }
        else if (type == InstrType.ADD) {
            return vj + vk;
        }
        else if (type == InstrType.SUB) {
            return vj - vk;
        }
        else if (type == InstrType.MUL) {
            return vj * vk;
        }
        else if (type == InstrType.NAND) {
            return ~(vj & vk);
        }
        else if (type == InstrType.LW) {
        	return vj + a;
        }
        else if (type == InstrType.SW) {
        	return vj + a;
        }
        else if (type == InstrType.JMP) {
        	return pc+2 + vj + a;
        }
        else if (type == InstrType.BEQ) {
        	if (vj == vk)
        		return pc+2 + a;
        	else
        		return pc+2;
        }
        else if (type == InstrType.JALR) {
        	return vk;
        }
        else if (type == InstrType.RET) {
        	return vj;
        }
        
        return 0;
    }

    public void fetch() {
    	FetchedObject fetchedObject = M.read(PC);
    	String instr = fetchedObject.getData();
    	if(instr == null) instr = "ADD R7 R7 R0";
    	instructionToBeFetched = instr.split(" ");
    	cyclesLeftToFetch = fetchedObject.getCycles();
    	fetchedAddress.add(PC);
    	PC+= 2;
    }

    public boolean Issue(String[] strInstr) {
        if (instructionsInROB == ROBsize) {
            return false;
        }

        Instruction instr = new Instruction(strInstr);
        int idx = instr.getInstrType().ordinal();
        if (numInstrs[idx] < maxInstrs[idx]) {

            int rd = instr.getRegA();
            int rs = instr.getRegB();
            int rt = instr.getRegC();
            int addr = instr.getImm(); // ADDi is an exception ? and check -1 bug

            int vj = 0;
            int vk = 0;
            int qj = -1;
            int qk = -1;

            if (rs != 0) {
                if (regStatus[rs] != -1) { // If not busy
                    int h = regStatus[rs];
                    if (ROB.get(h-1).ready) {
                        vj = ROB.get(h-1).value;
                    } else {
                        qj = h;
                    }
                } else {
                    vj = regs[rs];
                }
            }

            if (rt != 0) {
                if (regStatus[rt] != -1) { // If not busy
                    int h = regStatus[rt];
                    if (ROB.get(h-1).ready) {
                        vk = ROB.get(h-1).value;
                    } else {
                        qj = h;
                    }
                } else {
                    vk = regs[rt];
                }
            }

            boolean busy = true;
            int dest = tail;
            numInstrs[idx]++;

            int cycles = numCycles[instr.getInstrType().ordinal()];
            e = new ReservationEntry(busy, instr.getInstrType(), vj, vk, qj, qk, dest, addr, cycles);

            if (instr.getInstrType() != InstrType.SW) {
                regStatus[rd] = head;
            }
            ROBEntry sd = new ROBEntry(instr.getInstrType(), rd, 0, false); // FIx -1 thingy
            ROB.set(tail - 1, sd);
            return true;
        }

        return false;
    }

    public void decreaseType(InstrType type) {
        numInstrs[type.ordinal()]--;
    }

    public boolean canExecute(ReservationEntry e) {
        if (e.qj == -1 && e.qk == -1) {
            return true;
        }
        return false;
    }

    public void execute(ReservationEntry e) {
    	if(e.type == InstrType.LW && e.step1LoadStore) {
    		boolean allStoresHaveDifferentAddress = true;
    		for(int i = 0; i < ROBsize; i++) {
    			if(ROB.get(i) != null && (e.dest - 1 != i) && ROB.get(i).instruction.equals(InstrType.SW) && ROB.get(e.dest - 1).value == ROB.get(i).value) {
    				allStoresHaveDifferentAddress = false;
    				break;
    			}
    		}
    		if(allStoresHaveDifferentAddress) e.cyclesLeft--;
    	}
    	else {
    		e.cyclesLeft--;
	        if(e.cyclesLeft == 0) {
	        	if (e.type == InstrType.JALR) {
	        		ROB.get(e.dest - 1).value2 = e.pc+1;
	        		ROB.get(e.dest - 1).value = computeResult(e.type, e.vj, e.vk, e.addr, e.pc);
	        	}
	        	else if (e.type == InstrType.BEQ) {
	        		if(e.addr < 0) ROB.get(e.dest - 1).value2 = e.pc + 2 + e.addr;
	        		else ROB.get(e.dest - 1).value2 = e.pc + 2;
	        		ROB.get(e.dest - 1).value = computeResult(e.type, e.vj, e.vk, e.addr, e.pc);
	        	}
	        	else if(e.type == InstrType.LW) {
	        		ROB.get(e.dest - 1).value = computeResult(e.type, e.vj, e.vk, e.addr, e.pc);
	        		e.step1LoadStore = true;
	        		
	        		if(e.type == InstrType.LW) {
	        			FetchedObject fetchedObject = M.read(ROB.get(e.dest - 1).value);
	        			e.cyclesLeft = fetchedObject.getCycles();
	        			ROB.get(e.dest - 1).value = Integer.parseInt(fetchedObject.getData());
	        		}
	        	}
	        	else { // Everything else
	        		ROB.get(e.dest - 1).value = computeResult(e.type, e.vj, e.vk, e.addr, e.pc);
	        	}
	        }
    	}
    }

    public void printAll() {
        System.out.println("        ROB       ");
        System.out.println("  Type Dest Value Ready");
        for (int i = 0; i < ROBsize; i++) {
            System.out.print((i + 1) + " ");
            if (ROB.get(i) != null) {
                System.out.format("%-5s", ROB.get(i).instruction);
                System.out.format("%-5d", ROB.get(i).dest);
                System.out.format("%-6d", ROB.get(i).value);
                System.out.format("%-5b", ROB.get(i).ready);
            }
            System.out.println();
        }
        System.out.println("        Reservation Stations       ");
        System.out.println("Op   Vj Vk Qj Qk Dest A   CyclesLeft PC");
        for (int i = 0; i < rs.size(); i++) {
            System.out.format("%-5s", rs.get(i).type);
            if (rs.get(i).vj != -1) {
                System.out.format("%-3d", rs.get(i).vj);
            } else {
                System.out.print("   ");
            }
            if (rs.get(i).vk != -1) {
                System.out.format("%-3d", rs.get(i).vk);
            } else {
                System.out.print("   ");
            }
            if (rs.get(i).qj != -1) {
                System.out.format("%-3d", rs.get(i).qj);
            } else {
                System.out.print("   ");
            }
            if (rs.get(i).qk != -1) {
                System.out.format("%-3d", rs.get(i).qk);
            } else {
                System.out.print("   ");
            }
            System.out.format("%-5d", rs.get(i).dest);
            System.out.format("%-4d", rs.get(i).addr);
            System.out.format("%-11d", rs.get(i).cyclesLeft);
            System.out.print(rs.get(i).pc);
            System.out.println();
        }
        System.out.println("          Registers status            ");
        System.out.println("R0 R1 R2 R3 R4 R5 R6 R7");
        for (int i = 0; i < 8; i++) {
            if (regStatus[i] != -1) {
                System.out.format("%-3d", regStatus[i]);
            } else {
                System.out.print("   ");
            }
        }
    }

    public static void main(String[] args) throws InvalidNumberOfBanksException {
    		ProgramParser programParser = new ProgramParser();
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the number of cache Levels");
            int numCacheLevels = sc.nextInt();
            
            int[] S = new int[Math.min(3, numCacheLevels)];
            int[] m = new int[Math.min(3, numCacheLevels)];
            CacheWriteHitPolicy[] cacheWriteHitPolicy = new CacheWriteHitPolicy[Math.min(3, numCacheLevels)];    
            System.out.println("Enter the Line Size L of the cache(s)");
            int L = sc.nextInt();
            for(int i = 1; i <= Math.min(3, numCacheLevels); i++) {
            	System.out.println("Enter S, M and the writing policy (0 for WriteBack, 1 for WriteThrough) of cache #" + i + " (seperated by spaces)");
            	S[i-1] = sc.nextInt();
            	m[i-1] = sc.nextInt();
            	int t = sc.nextInt();
            	if(t == 0) cacheWriteHitPolicy[i-1] = CacheWriteHitPolicy.WriteBack;
            	else cacheWriteHitPolicy[i-1] = CacheWriteHitPolicy.WriteThrough;
            }
            
            int[] cycles = new int[Math.min(3, numCacheLevels)];
            for(int i = 1; i <= Math.min(3, numCacheLevels); i++) {
            	System.out.println("Enter the access time (in cycles) of Cache #" + i);
            	cycles[i-1] = sc.nextInt();   	
            }
            System.out.println("Enter the main memory access time");
            int memoryCycles = sc.nextInt();
            
            MemoryHierarchy M = new MemoryHierarchy(numCacheLevels, L, S, m, cycles, memoryCycles, cacheWriteHitPolicy);
            
            System.out.println("Enter the pipeline width");
            int pipelineWidth = sc.nextInt();
            
            System.out.println("Enter the size of the instruction buffer (queue)");
            int insturctionBuffer = sc.nextInt();
            
            int[] maxInstrs = new int[11];
            System.out.println("Enter the number of reservation stations for each of the following instructions (seperated by spaces)");
            System.out.println("LW, SW, JMP, BEQ, JALR, RET, ADD, SUB, ADDI, NAND, MUL");
            maxInstrs[InstrType.LW.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.SW.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.JMP.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.BEQ.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.JALR.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.RET.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.ADD.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.SUB.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.ADDI.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.NAND.ordinal()] = sc.nextInt();
            maxInstrs[InstrType.MUL.ordinal()] = sc.nextInt();
            
            System.out.println("Enter the number of ROB Entries Available");
            int ROBsize = sc.nextInt();
            
            int[] numCycles = new int[11];
            System.out.println("Enter the number of cycles needed by each of the following (seperated by spaces)");
            System.out.println("JMP, BEQ, JALR, RET, ADD, SUB, ADDI, NAND, MUL");
            numCycles[InstrType.JMP.ordinal()] = sc.nextInt();
            numCycles[InstrType.BEQ.ordinal()] = sc.nextInt();
            numCycles[InstrType.JALR.ordinal()] = sc.nextInt();
            numCycles[InstrType.RET.ordinal()] = sc.nextInt();
            numCycles[InstrType.ADD.ordinal()] = sc.nextInt();
            numCycles[InstrType.SUB.ordinal()] = sc.nextInt();
            numCycles[InstrType.ADDI.ordinal()] = sc.nextInt();
            numCycles[InstrType.NAND.ordinal()] = sc.nextInt();
            numCycles[InstrType.MUL.ordinal()] = sc.nextInt();
            
            Processor p = new Processor(M, pipelineWidth, insturctionBuffer, ROBsize, maxInstrs, numCycles);
            
            //M.getMemory().write(32768, "ADD R7 R7 R0", false);
            System.out.println("Enter the starting address of your program (.ORG \"StartingAddress\")");
            sc.nextLine();
            String org = sc.nextLine();
            int startingPC = 32768;
            String[] stringsORG = programParser.match(org);
            if(stringsORG != null && stringsORG[0].equals(".ORG")) {
            	startingPC+= Integer.parseInt(stringsORG[1]);
            }
            
            System.out.println("Enter the program");
            System.out.println("Finish by Entering a blank line");
            String s = sc.nextLine();
            while(!s.equals("")) {
            	String[] strings = programParser.match(s);
            	if(strings == null) {
            		System.out.println("Invalid Instruction: " + s);
            	}
            	else {
            		if(strings[0].equals(".DATA")) {
            			M.getMemory().write(Integer.parseInt(strings[2]), strings[1], false);
            		}
            		else {
		            	M.getMemory().write(startingPC, String.join(" ", programParser.match(s)), false);
		            	startingPC+= 2;
            		}
            	}
            	s = sc.nextLine();
            }
            PCAfterLastInstruction = startingPC;
            while(p.simulate());
            
            double amat;
    		double level1Misses = p.M.getCaches().get(0).getMisses() + p.M.getCaches().get(1).getMisses();
    		double level1Accesses = p.M.getCaches().get(0).getAccesses() + p.M.getCaches().get(1).getAccesses();
        	if(p.M.getCaches().size() == 2) { // 1 Cache Level
        		amat = 
        		(double) level1Misses/ level1Accesses * p.M.getMemory().getCycles();
        	}
        	else if(p.M.getCaches().size() == 3) { // 2 Cache Levels
            	amat =
            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getCycles() +
            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getMemory().getCycles();
        	}
        	else { // 3 Cache Levels
            	amat = 
            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getCycles() +
            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getCaches().get(3).getCycles() +
            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getCaches().get(3).getMisses() / p.M.getCaches().get(3).getAccesses() * p.M.getMemory().getCycles();
        	}
        	double cpiLoad = amat * p.loadInstructions / p.totalInstructions;
        	double cpiBranch = amat * p.mispredictions / p.totalInstructions;
        	double cpi = 1.0 + cpiLoad + cpiBranch;
        	double ipc = 1.0 / cpi;
        	
        	System.out.println();
        	System.out.println("The IPC: " + ipc);
            System.out.println("Execution Time: " + p.cyclesSimulated + " cycles");
            for(int i = 0; i < p.M.getCaches().size(); i++) {
            	if(i == 0) System.out.println("The Hit Ratio of the 1st Cache: " + (double) (p.M.getCaches().get(0).getAccesses()+p.M.getCaches().get(1).getAccesses()-p.M.getCaches().get(0).getMisses()-p.M.getCaches().get(1).getMisses()) / (p.M.getCaches().get(0).getAccesses()+p.M.getCaches().get(1).getAccesses()) );
            	else if(i == 2) System.out.println("The Hit Ratio of the 2nd Cache: " + (double) (p.M.getCaches().get(2).getAccesses()-p.M.getCaches().get(2).getMisses()) / p.M.getCaches().get(2).getAccesses());
            	else if(i == 3) System.out.println("The Hit Ratio of the 3rd Cache: " + (double) (p.M.getCaches().get(3).getAccesses()-p.M.getCaches().get(3).getMisses()) / p.M.getCaches().get(3).getAccesses());
            }
            System.out.println("The global AMAT: " + amat + " cycles");
            if(p.beqInstructions == 0) {
            	System.out.println("The branch misprediction percentage is not available (No Branch Instructions)");
            }
            else {
            	System.out.println(p.mispredictions * 100.0 / p.beqInstructions);
            }
    }
}
