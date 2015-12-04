
/**
 *
 * @author andrewmagdy
 */

/*

 Type 1
 op regA, regB, Imm

 Type 2
 op regA, Imm

 Type 3 
 op regA, regB

 Type 4
 op regA
    
 Type 5
 op regA, regB, regC
 */
public enum InstrType {

    LW(1), SW(1), BEQ(1),
    JMP(2), ADDI(2),
    JALR(3),
    RET(4),
    ADD(5), SUB(5), NAND(5), MUL(5);

    private int type;

    public int getType() {
        return type;
    }

    InstrType(int type) {
        this.type = type;
    }
}
