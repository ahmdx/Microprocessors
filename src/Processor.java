
import java.util.ArrayList;
import java.util.Scanner;

public class Processor {

    int PC;

    int[] regs;
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

    ArrayList<String[]> fetched;

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

    public Processor(int ROBsize) {
        PC = 0;

        regs = new int[8];
        regStatus = new int[8];

        for (int i = 0; i < 8; i++) {
            regStatus[i] = -1;
        }
        regs[0] = 0;

        fetched = new ArrayList<String[]>();

        rs = new ArrayList<ReservationEntry>();

        this.ROBsize = ROBsize;
        ROB = new ArrayList<ROBEntry>();
        for (int i = 0; i < ROBsize; i++) {
            ROB.add(null);
        }
        head = 1;
        tail = 1;

        B = new Battee5aMemory();
        insturctionBuffer = 4;
        instructionsInBuffer = 0;
    }

    public void simulate() {
        // Commit Stage
        if (ROB.get(head - 1) != null && ROB.get(head - 1).ready) {
            regs[ROB.get(head - 1).dest] = ROB.get(head - 1).value;
            regStatus[ROB.get(head - 1).dest] = -1;
            instructionsInROB--;
            ROB.set(head - 1, null);
            head++;
            if (head == ROBsize + 1) {
                head = 1;
            }

        }
        
        // Write Stage
        for (int i = 0; i < rs.size(); i++) {
            if (rs.get(i).cyclesLeft == 0) {
                ROB.get(rs.get(i).dest - 1).ready = true;

                //decreaseType(rs.get(i).type);
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
        if (fetched.size() > 0 && Issue(fetched.get(0))) {
        	fetched.remove(0);
        	instructionsInBuffer--;
        	instructionsInROB++;
        }
        
        if (e != null) { // If there is an instruction to be issued
            int value = computeResult(e.type, e.vj, e.vk, e.addr);
            ROB.set(tail - 1, new ROBEntry(e.type, e.dest, value, false));
            tail++;
            if (tail == ROBsize + 1) {
                tail = 1;
            }
            rs.add(e);
        }
        
        // Fetch Stage
        fetchAll();

        cyclesSimulated++;
        System.out.println("Cycle: " + cyclesSimulated);
        System.out.println();
        printAll();
        System.out.println();
    }

    public int computeResult(InstrType type, int vj, int vk, int a) {
        if (type.equals("ADD")) {
            return (int) (regs[vj] + regs[vk]);
        }
        if (type.equals("SUB")) {
            return (int) (regs[vj] - regs[vk]);
        }
        if (type.equals("NAND")) {
            return (int) ~(regs[vj] & regs[vk]);
        }
        if (type.equals("MUL")) {
            return (int) (regs[vj] * regs[vk]);
        }
        if (type.equals("ADDI")) {
            return (int) (regs[vj] + a);
        }
        return 0;
    }

    public void fetchAll() {
        while (instructionsInBuffer < insturctionBuffer) {
            if (PC / 2 == B.Instructions.size()) {
                break;
            }
            fetched.add(ProgramParser.match(B.Instructions.get(PC / 2)));
            instructionsInBuffer++;
            PC += 2;
        }
    }

    public boolean Issue(String[] instruction) {
        if (instructionsInROB == ROBsize) {
            return false;
        }
        switch (instruction[0]) {
            case "LW":
                if (loadInstructions < maxLoadInstructions) {
                    add(instruction);
                    loadInstructions++;
                    return true;
                }
                break;
            case "SW":
                if (storeInstructions < maxStoreInstructions) {
                    add(instruction);
                    storeInstructions++;
                    return true;
                }
                break;
            case "JMP":
                if (jumpInstructions < maxJumpInstructions) {
                    add(instruction);
                    jumpInstructions++;
                    return true;
                }
                break;
            case "BEQ":
                if (beqInstructions < maxBeqInstructions) {
                    add(instruction);
                    beqInstructions++;
                    return true;
                }
                break;
            case "JALR":
                if (jalrInstructions < maxJalrInstructions) {
                    add(instruction);
                    jalrInstructions++;
                    return true;
                }
                break;
            case "RET":
                if (returnInstructions < maxReturnInstructions) {
                    add(instruction);
                    returnInstructions++;
                    return true;
                }
                break;
            case "ADD":
                if (addInstructions < maxAddInstructions) {
                    add(instruction);
                    addInstructions++;
                    return true;
                }
                break;
            case "SUB":
                if (subInstructions < maxSubInstructions) {
                    add(instruction);
                    subInstructions++;
                    return true;
                }
                break;
            case "ADDI":
                if (addiInstructions < maxAddiInstructions) {
                    add(instruction);
                    addiInstructions++;
                    return true;
                }
                break;
            case "NAND":
                if (nandInstructions < maxNandInstructions) {
                    add(instruction);
                    nandInstructions++;
                    return true;
                }
                break;
            case "MUL":
                if (mulInstructions < maxMulInstructions) {
                    add(instruction);
                    mulInstructions++;
                    return true;
                }
                break;
        }
        return false;
    }

    public void decreaseType(String type) {
        switch (type) {
            case "LW":
                loadInstructions--;
                break;
            case "SW":
                storeInstructions--;
                break;
            case "JMP":
                jumpInstructions--;
                break;
            case "BEQ":
                beqInstructions--;
                break;
            case "JALR":
                jalrInstructions--;
                break;
            case "RET":
                returnInstructions--;
                break;
            case "ADD":
                addInstructions--;
                break;
            case "SUB":
                subInstructions--;
                break;
            case "ADDI":
                addiInstructions--;
                break;
            case "NAND":
                nandInstructions--;
                break;
            case "MUL":
                mulInstructions--;
                break;
        }
    }

    public boolean canExecute(ReservationEntry e) {
        if (e.qj == -1 && e.qk == -1) {
            return true;
        }
        return false;
    }

    public void execute(ReservationEntry e) {
        e.cyclesLeft--;
    }

    public void parseInstr(String[] instruction) {

    }

    public void add(String[] instruction) {
        Instruction i = new Instruction(instruction);

        int rd = i.getRegA();
        int rs = i.getRegB();
        int rt = i.getRegB();
        int addr = i.getImm(); // ADDi is an exception ? and check -1 bug

        int vj = 0;
        int vk = 0;
        int qj = -1;
        int qk = -1;

        if (rs != 0) {
            if (regStatus[rs] != -1) { // If not busy
                int h = regStatus[rs];
                if (ROB.get(h).ready) {
                    vj = ROB.get(h).value;
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
                if (ROB.get(h).ready) {
                    vk = ROB.get(h).value;
                } else {
                    qj = h;
                }
            } else {
                vk = regs[rt];
            }
        }

        boolean busy = true;
        int dest = head;

        e = new ReservationEntry(busy, i.getInstrType(), vj, vk, qj, qk, dest, addr, addCycles);

        if (i.getInstrType() != InstrType.SW) {
            regStatus[rd] = head;
        }
        ROBEntry sd = new ROBEntry(i.getInstrType(), rd, 0, false); // FIx -1 thingy
        ROB.set(tail - 1, sd);

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
        System.out.println("Op   Vj Vk Qj Qk Dest A   CyclesLeft");
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
            System.out.print(rs.get(i).cyclesLeft);
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Processor p = new Processor(4);
        p.addCycles = 2;
        p.maxAddInstructions = 1;
        p.addiCycles = 1;
        p.maxAddiInstructions = 1;
        p.B.Instructions.add("ADDI R3, R5, -40");
        p.insturctionBuffer = 4;
        p.simulate();
        p.simulate();
        p.simulate();
        p.simulate();
        p.simulate();
        p.simulate();
    }
}
